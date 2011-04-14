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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;
import umleditor.Relationship.RelationshipType;

public class RelationshipEditDialog extends JDialog implements ActionListener
{
	private boolean m_modelChanged;

	private RelationshipModel m_model;

	private ButtonGroup m_buttonGroup;

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
		setModelChanged(false);

		setLayout(new MigLayout("wrap 1"));

		createDialogComponents();

		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
				setTitle("Thwarted user attempt to close window.");
				setModelChanged(false);
				dispose();
			}
		});

		pack();
	}

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
	 * @return true of the model was changed.
	 */
	private boolean wasModelChanged()
	{
		return m_modelChanged;
	}

	private void setModelChanged(boolean modelChanged)
	{
		m_modelChanged = modelChanged;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub

	}

	/** Actions **/

	private class SaveAction extends AbstractAction
	{
		private static final long serialVersionUID = 6910525348649499302L;

		@Override
		public void actionPerformed(ActionEvent e)
		{
			RelationshipEditDialog.this.setModelChanged(true);
			RelationshipEditDialog.this.dispose();
		}
	}

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
			}

			RelationshipEditDialog.this.setModelChanged(false);
			RelationshipEditDialog.this.dispose();
		}
	}

	private class SwitchAction extends AbstractAction
	{
		private static final long serialVersionUID = 7080930509494941236L;

		@Override
		public void actionPerformed(ActionEvent e)
		{
			m_model.reverse();
		}

	}

	/** Factory methods **/

	/**
	 * Pop up a relationship modification dialog.
	 * 
	 * @param model
	 * @param parent
	 *            - the parent component for this dialog.
	 * @return
	 */
	public static boolean showEditDialog(RelationshipModel model, JComponent parent, Point dialogLocation)
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
	 * @param second
	 * @param parent
	 * @return
	 */
	public static RelationshipModel showCreationDialog(ClassNode first, ClassNode second, JComponent parent,
			Point dialogLocation)
	{
		RelationshipModel model = new RelationshipModel(first, second, RelationshipType.Association);
		boolean saveModel = showEditDialog(model, parent, dialogLocation);

		// Return the model if save was selected, else return null
		return (saveModel == true ? model : null);
	}
}
