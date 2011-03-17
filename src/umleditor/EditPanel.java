package umleditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import umleditor.NumberedTextField.FieldType;

/**
 * EditPanel -- Allows user to edit values for a particular ClassNode in the UML
 * diagram the ClassNode will be changed as it is edited -- there is no "cancel"
 * option because editing a value through the EditPanel already changed that
 * value in the class
 */
public class EditPanel extends JDialog implements FocusListener, ActionListener
{
	/**
	 * Generated id, recommended for all GUI components
	 */
	private static final long serialVersionUID = 6645450438602061200L;

	/**
	 * Class whose values this EditPanel will allow user to edit
	 */
	private ClassNode associatedNode;
	
	/**
	 * 
	 */
	//private ClassNode copyOfOriginalNode;

	/**
	 * Scroll pane for contents of Edit panel, so can have lots of fields
	 * without resizing edit window.
	 */
	private JScrollPane scrollPane;

	/**
	 * Panel which contains all other contents of EditPanel. All contents of the
	 * EditPanel must be contained in one panel to work properly with the Scroll
	 * Pane. Also makes removing components simpler, since removeAll command
	 * does not work properly for JDialogs
	 */
	private JPanel everythingPanel;

	/**
	 * Will contain the name of a new attribute to add, if user adds new
	 * attribute
	 */
	private JTextField newAttributeTextField;

	/**
	 * Will contain the name of a new method to add, if user adds new method
	 */
	private JTextField newMethodTextField;

	/**
	 * Constructs a new EditPanel that allows user to modify a particular class
	 * in the UML diagram.
	 * 
	 * @param nodeToModify
	 *            - the class this EditPanel will modify
	 */
	public EditPanel(ClassNode nodeToModify)
	{
		super();

		associatedNode = nodeToModify;
		//copyOfOriginalNode = new ClassNode();

		initialize();

		displayNodeProperties();

	}

	/**
	 * Sets up state of the JDialog
	 */
	private void initialize()
	{
		super.setTitle("Edit Class");
		super.setModalityType(ModalityType.APPLICATION_MODAL);
		super.setLocation(200, 200);
		super.setMinimumSize(new Dimension(400, 450));
		super.setResizable(false);

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		everythingPanel = new JPanel(new MigLayout("wrap 1", "0[]0", ""));
		scrollPane.setViewportView(everythingPanel);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Creates contents of everythingPanel from information in associatedNode.
	 * Called initially, and whenever an attribute or method in associatedNode
	 * is added or deleted (but not when names of existing values change)
	 */
	private void displayNodeProperties()
	{
		// remove any existing components, to start fresh
		everythingPanel.removeAll();

		displayClassName();

		addSeparator();

		displayAttributesList();

		addSeparator();

		displayMethodsList();

		addCloseButtons();
	}

	private void displayClassName()
	{
		JLabel classTitle = new JLabel("Class Name:");
		everythingPanel.add(classTitle, "gapx 3");

		String className = associatedNode.getName();
		NumberedTextField classTitleEditField = new NumberedTextField(className, 0, FieldType.ClassName);
		classTitleEditField.setPreferredSize(new Dimension(100, 25));
		Font boldFont = classTitleEditField.getFont().deriveFont(Font.BOLD);
		classTitleEditField.setFont(boldFont);
		classTitleEditField.addFocusListener(this);
		everythingPanel.add(classTitleEditField, "gapx 5");
	}

	private void addSeparator()
	{
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		everythingPanel.add(separator, "gapx 0, h 1:1:1, w 400");
	}

	private void displayAttributesList()
	{
		JLabel attributeLabel = new JLabel("Attributes:");
		everythingPanel.add(attributeLabel, "gapx 3");

		for (int i = 0; i < associatedNode.getNumAttributes(); ++i)
		{
			NumberedTextField attributeField = new NumberedTextField(associatedNode.getAttribute(i), i,
					FieldType.Attribute);
			attributeField.setPreferredSize(new Dimension(100, 25));
			attributeField.addFocusListener(this);
			everythingPanel.add(attributeField, "split 2, gapx 5");

			JButton deleteButton = new JButton("Delete");
			deleteButton.setActionCommand("DeleteAttrib" + i);
			deleteButton.addActionListener(this);
			everythingPanel.add(deleteButton, "split 2");
		}

		newAttributeTextField = new JTextField();
		newAttributeTextField.setPreferredSize(new Dimension(100, 25));
		newAttributeTextField.setActionCommand("NewAttrib");
		newAttributeTextField.addActionListener(this);
		everythingPanel.add(newAttributeTextField, "split 2, gapx 5");

		JButton newAttributeButton = new JButton("New");
		newAttributeButton.setActionCommand("NewAttrib");
		newAttributeButton.addActionListener(this);
		everythingPanel.add(newAttributeButton, "split 2");
	}

	private void displayMethodsList()
	{
		JLabel methodLabel = new JLabel("Methods:");
		everythingPanel.add(methodLabel, "gapx 3");

		for (int i = 0; i < associatedNode.getNumMethods(); ++i)
		{
			NumberedTextField methodField = new NumberedTextField(associatedNode.getMethod(i), i, FieldType.Method);
			methodField.setPreferredSize(new Dimension(100, 25));
			methodField.addFocusListener(this);
			everythingPanel.add(methodField, "split 2, gapx 5");

			JButton deleteButton = new JButton("Delete");
			deleteButton.setActionCommand("DeleteMethod" + i);
			deleteButton.addActionListener(this);
			everythingPanel.add(deleteButton, "split 2");
		}
		newMethodTextField = new JTextField();
		newMethodTextField.setPreferredSize(new Dimension(100, 25));
		newMethodTextField.setActionCommand("NewMethod");
		newMethodTextField.addActionListener(this);
		everythingPanel.add(newMethodTextField, "split 2, gapx 5");

		JButton newMethodButton = new JButton("New");
		newMethodButton.setActionCommand("NewMethod");
		newMethodButton.addActionListener(this);
		everythingPanel.add(newMethodButton, "split 2, wrap 15:push");
	}

	private void addCloseButtons()
	{
		JButton closeButton = new JButton("Close");
		closeButton.setActionCommand("Exit");
		closeButton.addActionListener(this);
		everythingPanel.add(closeButton, "align center, split, gapright 30");
		
		JButton revertButton = new JButton("Discard Changes");
		revertButton.setActionCommand("Discard");
		revertButton.addActionListener(this);
		everythingPanel.add(revertButton, "align center");
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		// do nothing
	}

	/**
	 * When a text field loses focus, update the appropriate value in the
	 * ClassNode.
	 */
	@Override
	public void focusLost(FocusEvent e)
	{
		NumberedTextField ntf = (NumberedTextField) e.getComponent();
		int componentIndex = ntf.getNumberIndex();
		FieldType type = ntf.getType();
		if (type == FieldType.Attribute)
		{
			associatedNode.setAttribute(componentIndex, ntf.getText());
		}
		else if (type == FieldType.Method)
		{
			associatedNode.setMethod(componentIndex, ntf.getText());
		}
		else // type == FieldType.ClassName
		{
			associatedNode.setName(ntf.getText());
		}
	}

	/**
	 * Handles pressed events for all buttons in the EditPanel
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String actionCommand = e.getActionCommand();
		
		if (actionCommand == "Exit")
		{
			// get rid of JDialog
			super.dispose();
		}
		else if (actionCommand == "Discard")
		{
			
		}
		else if (actionCommand.startsWith("New"))
		{
			if (actionCommand.endsWith("Attrib"))
			{
				// add new attribute to ClassNode
				String newAttribName = newAttributeTextField.getText();
				if (!newAttribName.equals(""))
					associatedNode.addAttribute(newAttribName);
			}
			else
			{
				// add new method to ClassNode
				String newMethodName = newMethodTextField.getText();
				if (!newMethodName.equals(""))
					associatedNode.addMethod(newMethodName);
			}
			// recreate display to reflect changes to ClassNode
			this.displayNodeProperties();
			// redraw the EditPanel
			this.validate();
		}
		else if (actionCommand.startsWith("Delete"))
		{
			// get index of value to delete from action command (index will be at end of command)
			int index = new Integer(actionCommand.substring(actionCommand.length() - 1));
			if (actionCommand.contains("Attrib"))
			{
				// delete attribute
				associatedNode.removeAttribute(index);
			}
			else
			{
				// delete method
				associatedNode.removeMethod(index);
			}
			// recreate display to reflect changes to ClassNode
			this.displayNodeProperties();
			// redraw the EditPanel
			this.validate();
		}
	}
}