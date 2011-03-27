package umleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
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

	private JScrollPane scrollPane;

	private ClassDiagram classDiagram;

	private ClassNode copyNode;

	private boolean addNewClassModeEnabled;

	public UMLEditor()
	{
		super("UML Editor");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(800, 800));
		this.setMinimumSize(new Dimension(250, 200));
		this.setLocationByPlatform(true);

		setUpMenuBar();
		setUpToolBar();
		setUpScrollPane();
		setUpClassDiagram();
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

	public void setDeleteButtonState(boolean enabled)
	{
		deleteButton.setEnabled(enabled);
	}

	private void setUpMenuBar()
	{
		menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");

		JMenuItem newOption = new JMenuItem("New");
		newOption.setActionCommand("NEW");
		newOption.addActionListener(this);
		fileMenu.add(newOption);

		JMenuItem loadOption = new JMenuItem("Load...");
		fileMenu.add(loadOption);

		JMenuItem saveOption = new JMenuItem("Save");
		fileMenu.add(saveOption);
		//
		// fileMenu.addSeparator();
		//
		// JMenuItem printOption = new JMenuItem("Print...");
		// fileMenu.add(printOption);

		fileMenu.addSeparator();

		JMenuItem exitOption = new JMenuItem("Exit");
		exitOption.setActionCommand("EXIT");
		exitOption.addActionListener(this);
		fileMenu.add(exitOption);

		menuBar.add(fileMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setActionCommand("HELP");
		helpMenu.addActionListener(this);
		menuBar.add(helpMenu);
		
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

		this.add(menuBar, BorderLayout.NORTH);

	}

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

	private void setUpScrollPane()
	{
		scrollPane = new JScrollPane();
		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void setUpClassDiagram()
	{
		classDiagram = new ClassDiagram(this);
	}
	
	private void setUpHelpPanel ()
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
			classDiagram.deleteSelectedNode();
		}
		else if (arg0.getActionCommand() == "NEW")
		{
			clearDiagram();
		}
		else if (arg0.getActionCommand() == "EXIT")
		{
			// When save is implemented, message will be changed to: Do you want
			// to save?
			JFrame frame = new JFrame();
			String message = "Are you sure you want to quit?";
			int answer = JOptionPane.showConfirmDialog(frame, message);
			if (answer == JOptionPane.YES_OPTION)
			{
				System.exit(0);
			}
			else if (answer == JOptionPane.NO_OPTION)
			{

			}
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
	 * Used when New is selected in the File menu. Deletes everything in the diagram
	 */
	public void clearDiagram()
	{
		this.disableAddNewClassMode();
		classDiagram = new ClassDiagram(this);
		this.validate();
	}

	public JScrollPane getScrollPane()
	{
		return (scrollPane);
	}

	public ClassNode getCopyNode()
	{
		return (copyNode);
	}

	public void setCopyNode(ClassNode coppiedNode)
	{
		copyNode = coppiedNode;
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
}
