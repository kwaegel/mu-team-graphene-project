package umleditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import umleditor.NumberedTextField.FieldType;

/**
 * Allows user to edit values for a particular ClassNode in the UML diagram. The ClassNode will be changed automatically
 * as it is edited -- there is no "cancel" option because editing a value through the EditPanel already changed that
 * value in the class. There is a "revert" option which changes all values back to the point when the edit panel was
 * opened. Choosing this option and then closing the dialog has the effect of "cancel"
 */
public class EditPanel extends JDialog implements FocusListener, ActionListener, KeyListener, WindowListener
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
	 * Class node which maintains the methods, attributes and class name in associatedNode before editing began. Used to
	 * provide Discard Changes functionality and determine if changes have occurred.
	 */
	private ClassNode copyOfOriginalNode;

	/**
	 * Scroll pane for contents of Edit panel, so can have lots of fields without resizing edit window.
	 */
	private JScrollPane scrollPane;

	/**
	 * Panel which contains all other contents of EditPanel. All contents of the EditPanel must be contained in one
	 * panel to work properly with the Scroll Pane. Also makes removing components simpler, since removeAll command does
	 * not work properly for JDialogs
	 */
	private JPanel everythingPanel;

	/**
	 * Will contain the name of a new attribute to add, if user adds new attribute
	 */
	private JTextField newAttributeTextField;

	/**
	 * Will contain the name of a new method to add, if user adds new method
	 */
	private JTextField newMethodTextField;

	private ClassDiagram parentDiagram;

	/**
	 * Constructs a new EditPanel that allows user to modify a particular class in the UML diagram.
	 * 
	 * @param nodeToModify
	 *            - the class this EditPanel will modify
	 */
	public EditPanel(ClassNode nodeToModify, ClassDiagram parentDiagram)
	{
		super();

		associatedNode = nodeToModify;
		copyOfOriginalNode = new ClassNode(associatedNode);

		initialize();

		displayNodeProperties();

		this.parentDiagram = parentDiagram;
	}

	/**
	 * Sets up state of the JDialog
	 */
	private void initialize()
	{
		super.setTitle("Edit Class");
		super.setModalityType(ModalityType.APPLICATION_MODAL);
		super.setLocation(500, 100);
		super.setMinimumSize(new Dimension(400, 450));
		super.setResizable(false);

		this.addWindowListener(this);

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		everythingPanel = new JPanel(new MigLayout("wrap 1", "0[]0", ""));
		scrollPane.setViewportView(everythingPanel);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Creates contents of everythingPanel from information in associatedNode. Called initially, and whenever an
	 * attribute or method in associatedNode is added or deleted (but not when names of existing values change)
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

	/**
	 * Creates & attaches label used to display the class name and also for dragging
	 */
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
		classTitleEditField.addKeyListener(this);
		everythingPanel.add(classTitleEditField, "gapx 5");
	}

	/**
	 * Adds a JSeparator to editPanel
	 */
	private void addSeparator()
	{
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		everythingPanel.add(separator, "gapx 0, h 1:1:1, w 400");
	}

	/**
	 * Shows a list of the attributes in EditPanel. List is able to be modified
	 */
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
			attributeField.addKeyListener(this);
			attributeField.addKeyListener(this);
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

	/**
	 * Shows a list of the methods in EditPanel. List is able to be modified
	 */
	private void displayMethodsList()
	{
		JLabel methodLabel = new JLabel("Methods:");
		everythingPanel.add(methodLabel, "gapx 3");

		for (int i = 0; i < associatedNode.getNumMethods(); ++i)
		{
			NumberedTextField methodField = new NumberedTextField(associatedNode.getMethod(i), i, FieldType.Method);
			methodField.setPreferredSize(new Dimension(100, 25));
			methodField.addFocusListener(this);
			methodField.addKeyListener(this);
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

	/**
	 * Adds Close and Discard Changes buttons
	 */
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
	 * When a text field loses focus, ensure it is not empty. If it is empty, display an error message and set to
	 * default value
	 */
	@Override
	public void focusLost(FocusEvent e)
	{
		NumberedTextField ntf = (NumberedTextField) e.getComponent();
		int componentIndex = ntf.getNumberIndex();
		FieldType type = ntf.getType();
		String text = ntf.getText();

		if (text.isEmpty())
		{
			String dialogMessage;
			if (type == FieldType.Attribute)
			{
				associatedNode.setAttribute(componentIndex, "default attribute");
				ntf.setText(associatedNode.getAttribute(componentIndex));
				dialogMessage = "Attribute name cannot be blank. To delete this attribute, click Delete";
			}
			else if (type == FieldType.Method)
			{
				associatedNode.setMethod(componentIndex, "default method");
				ntf.setText(associatedNode.getMethod(componentIndex));
				dialogMessage = "Method name cannot be blank. To delete this method, click Delete";
			}
			else
			// type == FieldType.ClassName
			{
				associatedNode.setName("DefaultClass");
				ntf.setText(associatedNode.getName());
				dialogMessage = "Class name cannot be blank";
			}
			JOptionPane.showMessageDialog(this, dialogMessage);
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
			if (!associatedNode.propertiesEqual(copyOfOriginalNode))
			{
				parentDiagram.markAsChanged();
			}
			// get rid of JDialog
			super.dispose();
		}
		else if (actionCommand == "Discard")
		{
			associatedNode.setPropertiesTo(copyOfOriginalNode);
			associatedNode.updateNodePanel();
		}
		else if (actionCommand.startsWith("New"))
		{
			if (actionCommand.endsWith("Attrib"))
			{
				// add new attribute to ClassNode
				String newAttribName = newAttributeTextField.getText();
				if (newAttribName.equals(""))
				{
					JOptionPane.showMessageDialog(this, "New attribute name cannot be blank");
				}
				else
				{
					associatedNode.addAttribute(newAttribName);
				}
			}
			else
			{
				// add new method to ClassNode
				String newMethodName = newMethodTextField.getText();
				if (newMethodName.equals(""))
				{
					JOptionPane.showMessageDialog(this, "New method name cannot be blank");
				}
				else
				{
					associatedNode.addMethod(newMethodName);
				}
			}
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
		}
		// recreate display to reflect changes to ClassNode
		this.displayNodeProperties();
		// redraw the EditPanel
		this.validate();
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// do nothing
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		// do nothing
	}

	/**
	 * Handles KeyReleased events for class name, attribute and method fields so will update continuously as user types.
	 */
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			if (!associatedNode.propertiesEqual(copyOfOriginalNode))
			{
				parentDiagram.markAsChanged();
			}
			super.dispose();
		}

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
		else
		// type == FieldType.ClassName
		{
			associatedNode.setName(ntf.getText());
		}
	}

	/**
	 * When {@link EditPanel} closes, determines if the node has been changed, and, if so, sets the state in the node's
	 * parent {@link ClassDiagram}.
	 */
	@Override
	public void windowDeactivated(WindowEvent e)
	{
		if (!associatedNode.propertiesEqual(copyOfOriginalNode))
		{
			// check if the user has changed the class and if they have, notify its
			// node panel's parent diagram that changes have been made.
			parentDiagram.markAsChanged();
		}
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		// Do nothing
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		// Do nothing
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		// Do nothing
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		// Do nothing
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		// Do nothing
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		// Do nothing
	}

}