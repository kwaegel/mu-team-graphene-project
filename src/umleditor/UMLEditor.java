package umleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

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

	private JMenuBar menuBar;
	private JToolBar toolBar;

	private JButton deleteButton;
	private JButton addClassButton;

	private JTabbedPane tabbedPane;

	private List<ClassDiagram> classDiagrams;

	private ClassNode copyNode;

	private boolean addNewClassModeEnabled;

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

		setUpMenuBar();
		setUpToolBar();
		setUpTabbedPane();
		createNewClassDiagram();
		setUpHelpPanel();

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
	 * Sets the state of the delete button
	 * 
	 * @param enabled
	 *            - whether to enable or disable the delete button
	 */
	public void setDeleteButtonState(boolean enabled)
	{
		deleteButton.setEnabled(enabled);
	}

	/**
	 * Sets up the menu bar and menus
	 */
	private void setUpMenuBar()
	{
		menuBar = new JMenuBar();

		createFileMenu();

		createEditMenu();

		createHelpMenu();

		this.add(menuBar, BorderLayout.NORTH);

	}

	/**
	 * Sets up and attaches the file menu. File menu contains New, Load, Close, Save, Save As, and Exit
	 */
	private void createFileMenu()
	{
		JMenu fileMenu = new JMenu("File");

		JMenuItem newOption = new JMenuItem("New");
		newOption.setActionCommand("NEW");
		newOption.addActionListener(this);
		newOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(newOption);

		JMenuItem loadOption = new JMenuItem("Load...");
		loadOption.setActionCommand("LOAD");
		loadOption.addActionListener(this);
		loadOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(loadOption);

		JMenuItem closeOption = new JMenuItem("Close");
		closeOption.setActionCommand("CLOSE");
		closeOption.addActionListener(this);
		closeOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(closeOption);

		fileMenu.addSeparator();

		JMenuItem saveOption = new JMenuItem("Save");
		saveOption.setActionCommand("SAVE");
		saveOption.addActionListener(this);
		saveOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(saveOption);

		JMenuItem saveAsOption = new JMenuItem("Save As...");
		saveAsOption.setActionCommand("SAVEAS");
		saveAsOption.addActionListener(this);
		fileMenu.add(saveAsOption);
		//
		// fileMenu.addSeparator();
		//
		// JMenuItem printOption = new JMenuItem("Print...");
		// fileMenu.add(printOption);

		fileMenu.addSeparator();

		JMenuItem exitOption = new JMenuItem("Exit");
		exitOption.setActionCommand("EXIT");
		exitOption.addActionListener(this);
		exitOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		fileMenu.add(exitOption);

		menuBar.add(fileMenu);
	}

	/**
	 * Sets up and attaches the edit menu. Edit menu contains Cut, Copy and Paste
	 */
	private void createEditMenu()
	{
		JMenu editMenu = new JMenu("Edit");

		JMenuItem cutOption = new JMenuItem("Cut");
		cutOption.setActionCommand("CUT");
		cutOption.addActionListener(this);
		cutOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		editMenu.add(cutOption);

		JMenuItem copyOption = new JMenuItem("Copy");
		copyOption.setActionCommand("COPY");
		copyOption.addActionListener(this);
		copyOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		editMenu.add(copyOption);

		JMenuItem pasteOption = new JMenuItem("Paste");
		pasteOption.setActionCommand("PASTE");
		pasteOption.addActionListener(this);
		pasteOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		editMenu.add(pasteOption);

		menuBar.add(editMenu);
	}

	private void createHelpMenu()
	{
		JMenu helpMenu = new JMenu("Help");

		JMenuItem helpOption = new JMenuItem("Help Contents  ");
		helpOption.setActionCommand("HELP");
		helpOption.addActionListener(this);
		helpOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpMenu.add(helpOption);
		helpMenu.addSeparator();

		JMenuItem aboutOption = new JMenuItem("About");
		aboutOption.setActionCommand("ABOUT");
		aboutOption.addActionListener(this);
		helpMenu.add(aboutOption);

		menuBar.add(helpMenu);
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
		fileLoadChooser.addChoosableFileFilter(new FileExtensionFilter());
		int userChoice = fileLoadChooser.showOpenDialog(this);

		ClassDiagram loadedDiagram = null;

		if (userChoice == JFileChooser.APPROVE_OPTION)
		{
			File f = fileLoadChooser.getSelectedFile();
			try
			{
				FileReader fileInStream;
				BufferedReader buffInStream;

				XStream xmlStream = new XStream();

				fileInStream = new FileReader(f);
				buffInStream = new BufferedReader(fileInStream);

				loadedDiagram = (ClassDiagram) xmlStream.fromXML(buffInStream);

				buffInStream.close();
			}
			catch (IOException e)
			{
				System.err.println("Could not open file: " + e.getMessage());
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error loading file", JOptionPane.WARNING_MESSAGE);
			}
			finally
			{
				// If the diagram has been loaded correctly, finish initialization.
				if (loadedDiagram != null)
				{
					JScrollPane scrollPane = new JScrollPane();
					loadedDiagram.initAfterLoadFromFile(this, scrollPane, f);
					classDiagrams.add(loadedDiagram);
					tabbedPane.add(scrollPane);
					if (!classDiagrams.isEmpty())
					{
						tabbedPane.setSelectedIndex(classDiagrams.size() - 1);
						addClassButton.setEnabled(true);
						loadedDiagram.requestFocusOnView();
					}
					tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(),
							new TabComponent(this, tabbedPane, f.getName()));
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
		ClassDiagram initialDiagram = new ClassDiagram(this, scrollPane);
		classDiagrams.add(initialDiagram);
		tabbedPane.add(scrollPane);
		if (!classDiagrams.isEmpty())
		{
			tabbedPane.setSelectedIndex(classDiagrams.size() - 1);
			addClassButton.setEnabled(true);
			initialDiagram.requestFocusOnView();
		}
		tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(), new TabComponent(this, tabbedPane,
				"Unsaved Diagram"));
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
	 * Handles when user selects menu options (or uses their associated accelerators).
	 */
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (arg0.getActionCommand() == "ADD")
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
		else if (arg0.getActionCommand() == "DELETE")
		{
			ClassDiagram currentDiagram = getCurrentDiagram();
			currentDiagram.deleteSelectedObject();
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
			currentDiagram.saveToFile(false);
		}

		if (userOption != JOptionPane.CANCEL_OPTION && userOption != JOptionPane.CLOSED_OPTION)
		{
			tabbedPane.remove(tabbedPane.getSelectedIndex());
			classDiagrams.remove(currentDiagram);
			if (classDiagrams.isEmpty())
			{
				addClassButton.setEnabled(false);
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
		ClassDiagram openDiagram = classDiagrams.get(currentIndex);
		return (openDiagram);
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
	 * Copies a particular node, so it will be available to paste.
	 * 
	 * @param nodeToCopy
	 *            - the node to make a copy of
	 */
	public void setCopyNode(ClassNode nodeToCopy)
	{
		copyNode = new ClassNode(nodeToCopy);
	}

	/**
	 * Entry point for the program.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
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
}
