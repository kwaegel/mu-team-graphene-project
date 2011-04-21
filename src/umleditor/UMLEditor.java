package umleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import com.thoughtworks.xstream.XStream;

/**
 * Displays the UML editing application and contains all major GUI items (menu bar, tabbed pane, tool bar, add-class and
 * delete buttons, etc.). Is responsible for things that are global to the application, such as maintaining add class
 * mode and copied classes. Creates new UML Class Diagrams, closes them, and loads them from files.
 */
public class UMLEditor extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -9139566399320553797L;

	private Color unselectedButtonColor = javax.swing.UIManager.getColor("Button.background");
	private Color selectedButtonColor = Color.gray;

	private HelpPanel helpPanel;

	private EditorMenuBar menuBar;
	private JToolBar toolBar;

	private JButton deleteButton;
	private JButton addClassButton;

	private JTabbedPane tabbedPane;

	private List<ClassDiagram> classDiagrams;

	private ClassNode copyNode;

	private boolean addNewClassModeEnabled;

	private TabCloseListener tabCloseListener;

	/**
	 * Constructs and opens a new UMLEditor application.
	 */
	public UMLEditor()
	{
		super("UML Editor");
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setPreferredSize(new Dimension(800, 800));
		this.setMinimumSize(new Dimension(250, 200));
		this.setLocationByPlatform(true);
		this.addWindowListener(new WindowCloseListener());

		classDiagrams = new ArrayList<ClassDiagram>();

		tabCloseListener = new TabCloseListener();

		setUpMenuBar();
		setUpToolBar();
		setUpTabbedPane();
		createNewClassDiagram();
		setUpHelpPanel();

		// Create a glass pane for temporary drawing.
		this.setGlassPane(new GlassDrawingPane());

		this.pack();
		this.setVisible(true);
	}

	/**
	 * Returns whether or not Add-Class mode is enabled. ClassDiagram checks this on mouse released and if it is
	 * enabled, adds a new node to the diagram.
	 * 
	 * @return - <code>true</code> if Add-Class mode is enabled, <code>false</code> if it is not.
	 */
	public boolean isAddNewClassModeEnabled()
	{
		return (addNewClassModeEnabled);
	}

	/**
	 * Enables Add-Class mode. Changes "Add Class" button color to indicate that it is selected and sets cursor.
	 */
	public void enableAddNewClassMode()
	{
		addNewClassModeEnabled = true;
		addClassButton.setBackground(selectedButtonColor);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}

	/**
	 * Disables Add-Class mode. Changes button background back to normal color and restores default cursor.
	 */
	public void disableAddNewClassMode()
	{
		addNewClassModeEnabled = false;
		addClassButton.setBackground(unselectedButtonColor);
		setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * Enables mode if was disabled, or disables if was enabled.
	 */
	public void toggleAddNewClassMode()
	{
		if (!addNewClassModeEnabled)
		{
			// Add-Class mode was not enabled, so enable it
			enableAddNewClassMode();
		}
		else
		{
			// Add-Class mode was already selected, this click disables it
			disableAddNewClassMode();
		}
	}

	/**
	 * Ensures that the menu items and buttons in the {@link UMLEditor} are disabled when nothing is selected.
	 */
	public void reflectUnselectedState()
	{
		deleteButton.setEnabled(false);
		menuBar.setCopyCutMode(false);
	}

	/**
	 * Enable the delete button (called when something "deletable" is selected.
	 */
	public void enableDeleteButtonState()
	{
		deleteButton.setEnabled(true);
	}

	/**
	 * Ensures the copy and cut menu items appropriately reflect state of the diagram.
	 * 
	 * @param enabled
	 *            - whether to enable or disable the menu options
	 */
	public void setCopyCutState(boolean enabled)
	{
		menuBar.setCopyCutMode(enabled);
	}

	/**
	 * Sets up the menu bar
	 */
	private void setUpMenuBar()
	{
		menuBar = new EditorMenuBar(this);
		this.add(menuBar, BorderLayout.NORTH);
	}

	/**
	 * Sets up the tool bar. The tool bar contains the buttons Add Class and Delete
	 */
	private void setUpToolBar()
	{
		toolBar = new JToolBar();

		Icon addClassIcon = new ImageIcon("icons/class.gif");
		Icon deleteIcon = new ImageIcon("icons/delete.gif");

		addClassButton = new JButton("Add Class", addClassIcon);
		addClassButton.setActionCommand("ADD");
		addClassButton.addActionListener(this);
		addClassButton.setFocusable(false);
		addClassButton.setFocusPainted(false);
		toolBar.add(addClassButton);
		addNewClassModeEnabled = false;

		deleteButton = new JButton("Delete", deleteIcon);
		deleteButton.setActionCommand("DELETE");
		deleteButton.addActionListener(this);
		deleteButton.setEnabled(false);
		toolBar.add(deleteButton);

		this.add(toolBar, BorderLayout.SOUTH);
	}

	/**
	 * Sets up state of the JTabbedPane that will be used to display diagrams.
	 */
	private void setUpTabbedPane()
	{
		tabbedPane = new JTabbedPane();
		tabbedPane.setFocusable(false);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.add(tabbedPane, BorderLayout.CENTER);
	}

	/**
	 * Prompts the user for a file to load from, then reconstructs the class diagram from that file and displays it in a
	 * new tab.
	 */
	private void loadDiagramFromFile()
	{
		JFileChooser fileLoadChooser = new JFileChooser();
		fileLoadChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileLoadChooser.setMultiSelectionEnabled(false);
		fileLoadChooser.setAcceptAllFileFilterUsed(false);
		fileLoadChooser.setFileFilter(new FileExtensionFilter());
		int userChoice = fileLoadChooser.showOpenDialog(this);

		ClassDiagram loadedDiagram = null;

		if (userChoice == JFileChooser.APPROVE_OPTION)
		{
			File fileToOpen = FileUtils.attachAppropriateExtension(fileLoadChooser.getSelectedFile());
			int loadedIndex = wasAlreadyOpen(fileToOpen);

			if (loadedIndex > -1)
			{
				tabbedPane.setSelectedIndex(loadedIndex);
			}
			else
			{
				try
				{
					FileReader fileInStream;
					BufferedReader buffInStream;

					XStream xmlStream = FileUtils.getXmlReaderWriter();

					fileInStream = new FileReader(fileToOpen);
					buffInStream = new BufferedReader(fileInStream);

					loadedDiagram = (ClassDiagram) xmlStream.fromXML(buffInStream);

					buffInStream.close();
				}
				catch (IOException e)
				{
					System.err.println("Could not open file: " + e.getMessage());
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error loading file",
							JOptionPane.WARNING_MESSAGE);
				}
				finally
				{
					// If the diagram has been loaded correctly, finish initialization.
					if (loadedDiagram != null)
					{
						JScrollPane scrollPane = new JScrollPane();
						tabbedPane.add(scrollPane);
						loadedDiagram.initAfterLoadFromFile(this, scrollPane, fileToOpen);
						addDiagramToEditor(loadedDiagram, fileToOpen.getName());
					}
				}
			}
		}
	}

	/**
	 * Initializes a new ClassDiagram.
	 */
	private void createNewClassDiagram()
	{
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.add(scrollPane);
		ClassDiagram initialDiagram = new ClassDiagram(this, scrollPane);
		addDiagramToEditor(initialDiagram, "Unsaved Diagram");
	}

	/**
	 * Adds a either a new Class Diagram or a loaded Class Diagram to the UML Editor
	 */
	private void addDiagramToEditor(ClassDiagram newDiagram, String diagramName)
	{
		classDiagrams.add(newDiagram);
		if (!classDiagrams.isEmpty())
		{
			tabbedPane.setSelectedIndex(classDiagrams.size() - 1);
			addClassButton.setEnabled(true);
			newDiagram.requestFocusOnView();
		}
		if (copyNode != null)
		{
			newDiagram.enablePastePopup();
		}
		menuBar.setDiagramBasedMenuItems(true);
		tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(),
				new TabTitleComponent(tabCloseListener, diagramName));
	}

	/**
	 * Initializes the HelpPanel to be used on this editor. Sets it to be not displayed.
	 */
	private void setUpHelpPanel()
	{
		helpPanel = new HelpPanel();
		helpPanel.setVisible(false);
	}

	/**
	 * Checks to see if the file is already open. If it is open, returns the index of the Class Diagram.
	 */
	private int wasAlreadyOpen(File file)
	{
		for (int i = 0; i < classDiagrams.size(); ++i)
		{
			if (classDiagrams.get(i).isSavedInFile(file))
			{
				return (i);
			}
		}
		return (-1);
	}

	/**
	 * Handles when user selects menu options (or uses their associated accelerators).
	 */
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (arg0.getActionCommand() == "ADD")
		{
			toggleAddNewClassMode();
		}
		else if (arg0.getActionCommand() == "DELETE")
		{
			ClassDiagram currentDiagram = getCurrentDiagram();
			if (currentDiagram != null)
			{
				currentDiagram.deleteSelectedObjects();
			}
		}
		else if (arg0.getActionCommand() == "NEW")
		{
			createNewClassDiagram();
			this.validate();
		}
		else if (arg0.getActionCommand() == "LOAD")
		{
			disableAddNewClassMode();
			loadDiagramFromFile();
			this.validate();
		}
		else if (arg0.getActionCommand() == "SAVE")
		{
			ClassDiagram currentDiagram = getCurrentDiagram();
			currentDiagram.saveToFile(false);
		}
		else if (arg0.getActionCommand() == "SAVEAS")
		{
			ClassDiagram currentDiagram = getCurrentDiagram();
			currentDiagram.saveToFile(true);
		}
		else if (arg0.getActionCommand() == "PRINT")
		{
			printClassDiagram(getCurrentDiagram());
		}
		else if (arg0.getActionCommand() == "CLOSE")
		{
			closeCurrentTab();
		}
		else if (arg0.getActionCommand() == "EXIT")
		{
			closeEditor();
		}
		else if (arg0.getActionCommand() == "CUT")
		{
			ClassDiagram currentDiagram = getCurrentDiagram();
			currentDiagram.cutNode();
		}
		else if (arg0.getActionCommand() == "COPY")
		{
			ClassDiagram currentDiagram = getCurrentDiagram();
			currentDiagram.copyNode();
		}
		else if (arg0.getActionCommand() == "PASTE")
		{
			ClassDiagram currentDiagram = getCurrentDiagram();
			currentDiagram.pasteNode();
		}
		else if (arg0.getActionCommand() == "HELP")
		{
			helpPanel.setToContentsTab();
			helpPanel.setVisible(true);
		}
		else if (arg0.getActionCommand() == "ABOUT")
		{
			helpPanel.setToAboutTab();
			helpPanel.setVisible(true);
		}
	}

	/**
	 * Provides the user with the option of printing the following diagram. User can choose layout (portrait vs.
	 * landscape) or other options (some of which may not make sense given the context), or cancel printing.
	 * 
	 * @param diagram
	 *            - UML class diagram to print
	 */
	public void printClassDiagram(ClassDiagram diagram)
	{
		PrinterJob printJob = PrinterJob.getPrinterJob();
		PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
		printJob.setPrintable(diagram);
		boolean print = printJob.printDialog(attributeSet);
		if (print)
		{
			try
			{
				printJob.print(attributeSet);
			}
			catch (PrinterException e)
			{
				e.printStackTrace();
			}
		}

		// PrinterJob printJob = PrinterJob.getPrinterJob();
		// printJob.setPrintable(getCurrentDiagram());
		// boolean print = printJob.printDialog();
		// if (print)
		// {
		// try
		// {
		// printJob.print();
		// }
		// catch (PrinterException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

	/**
	 * Goes through every opened tab and, if there are changes, asks the user if they want to save before closing. If
	 * the user either saves or discards all diagrams without canceling, will close the editor, otherwise will not
	 * close.
	 */
	private void closeEditor()
	{
		int numOpenTabs = tabbedPane.getTabCount();
		for (int i = 0; i < numOpenTabs; ++i)
		{
			boolean closedTab = closeCurrentTab();
			if (!closedTab)
			{
				break;
			}
		}

		if (classDiagrams.isEmpty())
		{
			// we removed all diagrams without the user canceling,
			// so go ahead and close the editor
			this.dispose();
		}
	}

	/**
	 * Allows deletion of any tab. Makes the ClassDiagram at index current, then deletes it.
	 * 
	 * @param tabIndex
	 *            - index in the tabbed pane of the tab to close.
	 * @return <code>true</code> if the tab was actually closed, <code>false</code> if the user canceled before closing
	 */
	public boolean closeTab(int tabIndex)
	{
		tabbedPane.setSelectedIndex(tabIndex);
		return (closeCurrentTab());
	}

	/**
	 * Closes the currently open diagram.
	 * 
	 * @return <code>true</code> if the tab was actually closed, <code>false</code> if the user canceled before closing
	 */
	public boolean closeCurrentTab()
	{
		ClassDiagram currentDiagram = getCurrentDiagram();
		int userOption = JOptionPane.NO_OPTION;
		if (currentDiagram.hasUnsavedChanges())
		{
			String name = currentDiagram.getName();
			userOption = JOptionPane.showConfirmDialog(this, "There are unsaved changes to \"" + name
					+ "\". \nDo you want to save before closing?");
		}

		if (userOption == JOptionPane.YES_OPTION)
		{
			boolean userSaved = currentDiagram.saveToFile(false);
			if (!userSaved)
			{
				userOption = JOptionPane.CANCEL_OPTION;
			}
		}

		if (userOption != JOptionPane.CANCEL_OPTION && userOption != JOptionPane.CLOSED_OPTION)
		{
			tabbedPane.remove(tabbedPane.getSelectedIndex());
			classDiagrams.remove(currentDiagram);
			if (classDiagrams.isEmpty())
			{
				addClassButton.setEnabled(false);
				deleteButton.setEnabled(false);
				menuBar.setDiagramBasedMenuItems(false);
			}
			return (true);
		}
		return (false);
	}

	/**
	 * Convenience method for use inside ClassDiagram. Gets the diagram that is currently displayed in the tabbed pane.
	 * 
	 * @return - the currently open class diagram
	 */
	private ClassDiagram getCurrentDiagram()
	{
		int currentIndex = tabbedPane.getSelectedIndex();
		if (!classDiagrams.isEmpty())
		{
			ClassDiagram openDiagram = classDiagrams.get(currentIndex);
			return (openDiagram);
		}
		return (null);
	}

	/**
	 * Returns the most recently copied node. Can be from any diagram, and may be null if no nodes were copied yet.
	 * 
	 * @return - the node that was copied
	 */
	public ClassNode getCopyNode()
	{
		return (copyNode);
	}

	/**
	 * Copies a particular node, so it will be available to paste. Notifies all class diagrams to enable paste
	 * 
	 * @param nodeToCopy
	 *            - the node to make a copy of
	 */
	public void setCopyNode(ClassNode nodeToCopy)
	{
		copyNode = new ClassNode(nodeToCopy);
		menuBar.enablePaste();
		for (int i = 0; i < classDiagrams.size(); ++i)
		{
			classDiagrams.get(i).enablePastePopup();
		}
	}

	/**
	 * Entry point for the program.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		// // Used for event debugging.
		// java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
		// {
		//
		// @Override
		// public void eventDispatched(AWTEvent event)
		// {
		// if (event.getID() == MouseEvent.MOUSE_DRAGGED)
		// {
		// System.out.println(event);
		// }
		// }
		//
		// }, AWTEvent.MOUSE_MOTION_EVENT_MASK);

		new UMLEditor();
	}

	/**
	 * Listens for Window Closing events (on the UML editor) and calls the close routine when they occur. Window Adapter
	 * provides an empty implementation of all Window listening methods, extending it is preferable to implementing
	 * WindowListener because that requires implementation of unnecessary methods.
	 */
	private class WindowCloseListener extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			closeEditor();
		}
	}

	/**
	 * Class to listen for requests to close a tab.
	 */
	private class TabCloseListener implements ActionListener
	{
		/**
		 * Called when user clicks on close button. Gets the current index and tells the {@link UMLEditor} to delete the
		 * {@link ClassDiagram} at that index.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JButton source = (JButton) e.getSource();
			TabTitleComponent tab = (TabTitleComponent) source.getParent();
			int index = tabbedPane.indexOfTabComponent(tab);
			UMLEditor.this.closeTab(index);
		}
	}

}
