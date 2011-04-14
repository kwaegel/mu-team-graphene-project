package umleditor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class RelationshipEditDialog extends JDialog
{
	private boolean m_modelChanged;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4774378332989799094L;

	public RelationshipEditDialog(RelationshipModel model)
	{
		setModalityType(ModalityType.DOCUMENT_MODAL);
		setTitle("Edit relationship");
		add(new JLabel("Edit a relationship"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setModelChanged(false);

		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
				setTitle("Thwarted user attempt to close window.");
				setModelChanged(true);
				setSize(200, 50);
				dispose();
			}
		});

		pack();
	}

	/**
	 * @return true of the model was changed.
	 */
	private boolean wasModelChanged()
	{
		return true;
	}

	private void setModelChanged(boolean modelChanged)
	{
		m_modelChanged = modelChanged;
	}

	public static boolean showEditDialog(RelationshipModel model)
	{
		RelationshipEditDialog dialog = new RelationshipEditDialog(model);
		dialog.setVisible(true);
		boolean wasModelChanged = dialog.wasModelChanged();
		dialog.dispose();
		return wasModelChanged;
	}

}
