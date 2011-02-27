package umleditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class EditPanel extends JDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 108292737166911948L;

	private ClassNode associatedNode;

	private JTextField classTitleEditField;

	public EditPanel(ClassNode nodeToModify)
	{
		super();

		associatedNode = nodeToModify;

		initialize();

		displayNodeProperties();
	}

	private void initialize()
	{
		super.setTitle("Edit Class");
		super.setModalityType(ModalityType.APPLICATION_MODAL);
		super.setLocation(200, 200);
		super.setMinimumSize(new Dimension(400, 400));
		super.setLayout(new MigLayout("wrap 1", "0[]0", ""));
	}

	private void displayNodeProperties()
	{
		JLabel classTitle = new JLabel("Class Name:");
		this.add(classTitle);

		String className = associatedNode.getName();
		classTitleEditField = new JTextField(className);
		classTitleEditField.setPreferredSize(new Dimension(100, 25));
		classTitleEditField.addFocusListener(new FocusListener()
		{

			@Override
			public void focusLost(FocusEvent e)
			{

				classTitleEditField.setBackground(Color.white);
				String newClassName = classTitleEditField.getText();
				associatedNode.setName(newClassName);
			}

			@Override
			public void focusGained(FocusEvent e)
			{
				classTitleEditField.setBackground(Color.orange);
			}
		});
		this.add(classTitleEditField);

		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setPreferredSize(new Dimension(2000, 1));
		this.add(separator, "gapx 0 0");

		displayAttributesList();

		JSeparator separator2 = new JSeparator(SwingConstants.HORIZONTAL);
		separator2.setPreferredSize(new Dimension(2000, 1));
		this.add(separator2, "gapx 0 0");

		displayMethodsList();

		JButton closeButton = new JButton("Exit");
		closeButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				setVisible(false);
			}
		});
		this.add(closeButton, "align center, gapbottom 0");
	}

	private void displayAttributesList()
	{

		JLabel attributeLabel = new JLabel("Attributes:");
		this.add(attributeLabel);

		for (int i = 0; i < associatedNode.getNumAttributes(); ++i)
		{
			JTextField attributeField = new JTextField(associatedNode.getAttribute(i));
			attributeField.setPreferredSize(new Dimension(100, 25));
			this.add(attributeField);
		}
	}

	private void displayMethodsList()
	{

	}

}
