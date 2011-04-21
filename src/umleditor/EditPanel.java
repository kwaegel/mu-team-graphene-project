package umleditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
 * Allows user to edit values for a particular {@link ClassNode} in the UML diagram. The {@link ClassNode} will be
 * changed automatically as it is edited -- there is no "cancel" option because editing a value through the EditPanel
 * already changed that value in the class. There is a "revert" option which changes all values back to the point when
 * the edit panel was opened. Choosing this option and then closing the dialog has the effect of "cancel"
 */
public class EditPanel extends JDialog implements FocusListener, ActionListener, KeyListener
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

	/**
	 * The ClassDiagram to which the {@link ClassNode} being edited belongs.
	 */
	private ClassDiagram parentDiagram;

	/**
	 * Constructs a new {@link EditPanel} that allows user to modify a particular {@link ClassNode} in the UML diagram.
	 * 
	 * @param nodeToModify
	 *            - the {@link ClassNode} this EditPanel will modify
	 * @param parentDiagram
	 *            - the {@link ClassDiagram} to which the {@link ClassNode} being edited belongs.
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

		this.addWindowListener(new WindowDeactivationListener());

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
			everythingPanel.add(attributeField, "split 2, gapx 5");

			JButton deleteButton = new JButton("Delete");
			deleteButton.setActionCommand("DeleteAttrib" + i);
			deleteButton.addActionListener(this);
			deleteButton.addKeyListener(this);
			everythingPanel.add(deleteButton, "split 2");
		}

		newAttributeTextField = new JTextField();
		newAttributeTextField.setPreferredSize(new Dimension(100, 25));
		newAttributeTextField.setActionCommand("NewAttrib");
		newAttributeTextField.addActionListener(this);
		newAttributeTextField.addKeyListener(new EscapeListener());
		everythingPanel.add(newAttributeTextField, "split 2, gapx 5");

		JButton newAttributeButton = new JButton("New");
		newAttributeButton.setActionCommand("NewAttrib");
		newAttributeButton.addActionListener(this);
		newAttributeButton.addKeyListener(this);
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
			deleteButton.addKeyListener(this);
			everythingPanel.add(deleteButton, "split 2");
		}
		newMethodTextField = new JTextField();
		newMethodTextField.setPreferredSize(new Dimension(100, 25));
		newMethodTextField.setActionCommand("NewMethod");
		newMethodTextField.addActionListener(this);
		newMethodTextField.addKeyListener(new EscapeListener());
		everythingPanel.add(newMethodTextField, "split 2, gapx 5");

		JButton newMethodButton = new JButton("New");
		newMethodButton.setActionCommand("NewMethod");
		newMethodButton.addActionListener(this);
		newMethodButton.addKeyListener(this);
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
		closeButton.addKeyListener(this);
		everythingPanel.add(closeButton, "align center, split, gapright 30");

		JButton revertButton = new JButton("Discard Changes");
		revertButton.setActionCommand("Discard");
		revertButton.addActionListener(this);
		revertButton.addKeyListener(this);
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
		String text = ntf.getText();

		if (text.isEmpty())
		{
			displayEmptyFieldMessage(ntf);
			ntf.requestFocus();
		}
	}

	/**
	 * Displays a message stating that the property associated with this {@link NumberedTextField} cannot be blank.
	 * Called when the user attempts to make the value of the class name or some attribute or method be blank.
	 * 
	 * @param ntf
	 *            - field whose value user has attempted to set to nothing.
	 */
	private void displayEmptyFieldMessage(NumberedTextField ntf)
	{
		int componentIndex = ntf.getNumberIndex();
		FieldType type = ntf.getType();

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

	/**
	 * Request focus on component identified by the component code.
	 * 
	 * @param nextComponentCode
	 *            - code used to identify component to focus on.
	 */
	private void focusOnComponent(String nextComponentCode)
	{
		if (nextComponentCode.equals("NewAttributeField"))
		{
			newAttributeTextField.requestFocus();
		}
		else if (nextComponentCode.equals("NewMethodField"))
		{
			newMethodTextField.requestFocus();
		}
		else if (nextComponentCode.equals("CloseButton"))
		{
			// close button is second to last component
			everythingPanel.getComponent(everythingPanel.getComponentCount() - 2).requestFocus();
		}
		else if (nextComponentCode.equals("ClassNameField"))
		{
			// class name field is second component in the everything panel
			everythingPanel.getComponent(1).requestFocus();
		}
	}

	/**
	 * Removes any changes made to the Class since the EditPanel was last opened.
	 */
	private void revert()
	{
		associatedNode.setPropertiesTo(copyOfOriginalNode);
		associatedNode.updateNodePanel();
	}

	/**
	 * Handles pressed events for all buttons in the EditPanel
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String actionCommand = e.getActionCommand();
		String nextComponentToGainFocus = "";

		if (actionCommand == "Exit")
		{
			super.dispose();
		}
		else if (actionCommand == "Discard")
		{
			revert();
			nextComponentToGainFocus = "ClassNameField";
		}
		else if (actionCommand.startsWith("New"))
		{
			if (actionCommand.endsWith("Attrib"))
			{
				// add new attribute to ClassNode
				String newAttribName = newAttributeTextField.getText();
				if (newAttribName.isEmpty())
				{
					JOptionPane.showMessageDialog(this, "New attribute name cannot be blank");
				}
				else
				{
					associatedNode.addAttribute(newAttribName);
				}
				nextComponentToGainFocus = "NewAttributeField";
			}
			else
			{
				// add new method to ClassNode
				String newMethodName = newMethodTextField.getText();
				if (newMethodName.isEmpty())
				{
					JOptionPane.showMessageDialog(this, "New method name cannot be blank");
				}
				else
				{
					associatedNode.addMethod(newMethodName);
				}
				nextComponentToGainFocus = "NewMethodField";
			}
		}
		else if (actionCommand.startsWith("Delete"))
		{
			nextComponentToGainFocus = "CloseButton";
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

		// focus on next component
		if (!nextComponentToGainFocus.isEmpty())
			focusOnComponent(nextComponentToGainFocus);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// Do Nothing
	}

	/**
	 * Handles enter events for ntf's in edit panel
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getComponent() instanceof NumberedTextField && e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			// pressing enter in a text field closes
			// the edit panel, keeping current changes
			// unless the text field is empty, which is illegal
			NumberedTextField ntf = (NumberedTextField) e.getComponent();
			if (ntf.getText().isEmpty())
				displayEmptyFieldMessage(ntf);
			else
				super.dispose();
		}
	}

	/**
	 * Handles KeyReleased events for class name, attribute and method fields and all buttons. Ensures class name,
	 * attributes and methods will update continuously as user types, and hitting enter when focused on a button will
	 * behave the same as pressing that button. Also handles special escape events: pressing escape while focused on any
	 * component will revert changes and close the edit panel.
	 */
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			// if the user pressed escape, regardless of what has focus, want to
			// revert changes and exit immediately, without doing anything further
			revert();
			super.dispose();
			// don't continue with rest of method.
			return;
		}

		Component originatingComponent = e.getComponent();
		if (originatingComponent instanceof JButton && e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			// if user hits enter while button has focus,
			// same result as clicking button.
			JButton button = (JButton) originatingComponent;
			button.doClick();
		}
		else if (originatingComponent instanceof NumberedTextField)
		{
			NumberedTextField ntf = (NumberedTextField) e.getComponent();

			if (e.getKeyCode() == KeyEvent.VK_DELETE)
			{
				// delete field
				if (ntf.getType() == FieldType.Attribute)
					associatedNode.removeAttribute(ntf.getNumberIndex());
				else if (ntf.getType() == FieldType.Method)
					associatedNode.removeMethod(ntf.getNumberIndex());
				// recreate display to reflect changes to ClassNode
				this.displayNodeProperties();
				// redraw the EditPanel
				this.validate();
				// focus on close button
				focusOnComponent("CloseButton");
			}
			else
			{
				// typed something in this text box, so update the associated node appropriately
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
		}
	}

	/**
	 * Special key listener just for newAttributeTextField and newMehtodTextField, which we don't want to respond to
	 * most key events like the other fields do, but should behave consistently when escape is pressed.
	 */
	private class EscapeListener extends KeyAdapter
	{
		/**
		 * If the user pressed escape, exits without keeping any changes that have been made since the {@link EditPanel} opened.
		 */
		@Override
		public void keyReleased(KeyEvent e)
		{
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				// if the user pressed escape, revert changes and exit immediately
				revert();
				EditPanel.super.dispose();
			}
		}
	}

	/**
	 * Listens for window deactivation events (when edit panel is about to close) and checks to see if the node has been
	 * changed, if so, marks the parent {@link ClassDiagram} as changed. Window Adapter provides an empty implementation
	 * of all Window listening methods, extending it is preferable to implementing WindowListener because that requires
	 * implementation of unnecessary methods.
	 */
	private class WindowDeactivationListener extends WindowAdapter
	{
		/**
		 * When {@link EditPanel} closes, determines if the node has been changed, and, if so, sets the state in the
		 * node's parent {@link ClassDiagram}.
		 */
		@Override
		public void windowDeactivated(WindowEvent e)
		{
			if (!associatedNode.equals(copyOfOriginalNode))
			{
				// check if the user has changed the class and if they have, notify its
				// node panel's parent diagram that changes have been made.
				parentDiagram.markAsChanged();
			}
		}
	}
}