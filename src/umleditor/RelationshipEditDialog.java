package umleditor;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;
import umleditor.Relationship.RelationshipType;

/**
 * Allows user to modify a relationship. User will be able to choose a new type of relationship or switch the direction
 * of the relationship. Changes occur immediately, although there is an option to "Cancel" which will close the window
 * and revert any changes. Pressing "Done" closes the window without reverting the changes.
 * 
 */
public class RelationshipEditDialog extends JDialog implements ActionListener
{
	private boolean m_modelChanged;

	private RelationshipModel m_model;

	private static ButtonGroup m_buttonGroup;

	private RelationshipModel m_backupModel;

	private static final long serialVersionUID = 4774378332989799094L;

	public RelationshipEditDialog(RelationshipModel model)
	{
		m_model = model;

		// Create a backup of the model
		m_backupModel = new RelationshipModel(m_model);

		setModalityType(ModalityType.DOCUMENT_MODAL);
		setTitle("Edit relationship");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);

		setModelChanged(false);

		setLayout(new MigLayout("wrap 1"));

		super.setModalityType(ModalityType.APPLICATION_MODAL);
		createDialogComponents();

		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
				dispose();
			}
		});

		pack();
	}

	/**
	 * Constructs the Edit Dialog window. Adds buttons allowing the user to switch relationship direction and
	 * relationship type.
	 */
	private void createDialogComponents()
	{
		// Create radio buttons.
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
		m_buttonGroup = new ButtonGroup();
		RelationshipType[] types = RelationshipType.values();
		for (RelationshipType type : types)
		{
			JRadioButton typeButton = new JRadioButton(type.toString());
			typeButton.setActionCommand(type.toString());
			typeButton.addActionListener(this);

			typeButton.setSelected(type == m_model.getType());

			m_buttonGroup.add(typeButton);
			buttonPanel.add(typeButton);
		}
		add(buttonPanel);

		// Create switch direction button
		JButton switchButton = new JButton(new SwitchAction());
		switchButton.setText("Switch direction");
		add(switchButton, "center");

		// Create save/cancel buttons
		JPanel saveCancelPanel = new JPanel(new GridLayout());
		JButton saveButton = new JButton(new SaveAction());
		saveButton.setText("Done");
		saveCancelPanel.add(saveButton, "tag ok");

		JButton cancelButton = new JButton(new CancelAction());
		cancelButton.setText("Cancel");
		saveCancelPanel.add(cancelButton, "tag cancel");

		add(saveCancelPanel);
	}

	/**
	 * @return true if the model was changed.
	 */
	private boolean wasModelChanged()
	{
		return m_modelChanged;
	}

	/**
	 * Sets the m_modelChanged to true if the model was changed.
	 */
	private void setModelChanged(boolean modelChanged)
	{
		m_modelChanged = modelChanged;
	}

	/**
	 * Changes the relationship type based on the ActionEvent and tells the Relationship Edit Dialog that it was
	 * changed.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String typeName = m_buttonGroup.getSelection().getActionCommand();
		RelationshipType newType = RelationshipType.valueOf(typeName);
		m_model.setType(newType);
		RelationshipEditDialog.this.setModelChanged(true);
	}

	/** Actions **/

	/**
	 * Saves the changes the user has made if there was any and then closes the Relationship Edit Dialog.
	 */
	private class SaveAction extends AbstractAction
	{
		private static final long serialVersionUID = 6910525348649499302L;

		@Override
		public void actionPerformed(ActionEvent e)
		{
			RelationshipEditDialog.this.dispose();
		}
	}

	/**
	 * Reverts the relationship to its previous state if the model was changed and then closes the Relationship Edit
	 * Dialog.
	 */
	private class CancelAction extends AbstractAction
	{
		private static final long serialVersionUID = 1653570971716173190L;

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (wasModelChanged())
			{
				// Restore state from backup
				m_model.setModelState(m_backupModel);
				m_model.getRelationship().repaint();
			}

			RelationshipEditDialog.this.setModelChanged(false);
			RelationshipEditDialog.this.dispose();
		}
	}

	/**
	 * Switches the direction the arrow is pointing in the relationship and then tells the Relationship Edit Dialog it
	 * was changed.
	 */
	private class SwitchAction extends AbstractAction
	{
		private static final long serialVersionUID = 7080930509494941236L;

		@Override
		public void actionPerformed(ActionEvent e)
		{
			m_model.reverse();
			RelationshipEditDialog.this.setModelChanged(true);
		}

	}

	/** Factory methods **/

	/**
	 * Pop up a relationship modification dialog.
	 * 
	 * @param model
	 *            - the {@link RelationshipModel model} to edit.
	 * @param dialogLocation
	 *            - the {@link Point} to create the edit dialog at.
	 * @return true if the model has been modified
	 */
	public static boolean showEditDialog(RelationshipModel model, Point dialogLocation)
	{
		RelationshipEditDialog dialog = new RelationshipEditDialog(model);
		dialog.setLocation(dialogLocation);
		dialog.setVisible(true);
		boolean wasModelChanged = dialog.wasModelChanged();
		dialog.dispose();
		return wasModelChanged;
	}

	/**
	 * Pop up a dialog to create a new relationship model.
	 * 
	 * @param first
	 *            - the first {@link ClassNode} to link together.
	 * @param second
	 *            - the second {@link ClassNode} to link together.
	 * @param dialogLocation
	 *            - the {@link Point} to create the edit dialog at.
	 * @return - the new {@link RelationshipModel} that has been created. Will be null if cancel was clicked.
	 */
	public static RelationshipModel showCreationDialog(ClassNode first, ClassNode second, Point dialogLocation)
	{
		RelationshipModel model = new RelationshipModel(first, second, RelationshipType.Association);
		boolean saveModel = showEditDialog(model, dialogLocation);

		// Return the model if save was selected, else return null
		return (saveModel == true ? model : null);
	}
}
