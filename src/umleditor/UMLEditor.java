package umleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

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
	 * Enables Delete Button. The delete button becomes clickable.
	 */
	public void setDeleteButtonState(boolean enabled)
	{
		deleteButton.setEnabled(enabled);
	}

	/**
	 * Sets up the File and Help menu bars File menu contains New, Load, Close, Save, Save As, and Exit
	 */
	private void setUpMenuBar()
	{
		menuBar = new JMenuBar();

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

		JMenu editMenu = new JMenu("Edit");
		
		/*JMenuItem pasteOption = new JMenuItem("Paste");
		pasteOption.setActionCommand("PASTE");
		pasteOption.addActionListener(this);
		pasteOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		editMenu.add(pasteOption);*/

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

		this.add(menuBar, BorderLayout.NORTH);

	}

	/**
	 * Sets up the tool bar. The tool bar contains the buttons Add Class and Delete
	 */
	private void setUpToolBar()
	{
		toolBar = new JToolBar();

		addClassButton = new JButton("Add Class");
		addClassButton.setActionCommand("ADD");
		addClassButton.addActionListener(this);
		addClassButton.setFocusable(false);
		addClassButton.setFocusPainted(false);
		toolBar.add(addClassButton);
		addNewClassModeEnabled = false;

		deleteButton = new JButton("Delete");
		deleteButton.setActionCommand("DELETE");
		deleteButton.addActionListener(this);
		deleteButton.setEnabled(false);
		toolBar.add(deleteButton);

		this.add(toolBar, BorderLayout.SOUTH);
	}

	/**
	 * Sets up a scroll pane.
	 */
	private void setUpTabbedPane()
	{
		tabbedPane = new JTabbedPane();
		tabbedPane.setFocusable(false);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.add(tabbedPane, BorderLayout.CENTER);
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
		}
		tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(), new TabComponent(this, tabbedPane,
				"Unsaved Diagram"));
		addClassButton.setEnabled(true);
		initialDiagram.requestFocusOnView();
	}

	private void setUpHelpPanel()
	{
		helpPanel = new HelpPanel();
		helpPanel.setVisible(false);
	}

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
			disableAddNewClassMode();
			createNewClassDiagram();
			this.validate();
		}
		else if (arg0.getActionCommand() == "LOAD")
		{

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

	public boolean closeCurrentTab()
	{
		ClassDiagram currentDiagram = getCurrentDiagram();
		int userOption = JOptionPane.NO_OPTION;
		if (currentDiagram.isUnsaved())
		{
			userOption = JOptionPane.showConfirmDialog(this,
					"There are unsaved changes to the current diagram. \nDo you want to save before closing?");
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
	 * Used when New is selected in the File menu. Deletes everything in the diagram
	 */
	public void clearDiagram()
	{
		this.disableAddNewClassMode();
		// classDiagram = new ClassDiagram(this);
		this.validate();
	}

	private ClassDiagram getCurrentDiagram()
	{
		int currentIndex = tabbedPane.getSelectedIndex();
		ClassDiagram openDiagram = classDiagrams.get(currentIndex);
		return (openDiagram);
	}

	public ClassNode getCopyNode()
	{
		return (copyNode);
	}

	public void setCopyNode(ClassNode coppiedNode)
	{
		copyNode = new ClassNode(coppiedNode);
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

	private class WindowCloseListener extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			closeEditor();
		}
	}
}
