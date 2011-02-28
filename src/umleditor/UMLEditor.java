package umleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

public class UMLEditor extends JFrame implements ActionListener
{

	private static final long serialVersionUID = -9139566399320553797L;
	private Color unselectedButtonColor = javax.swing.UIManager.getColor("Button.background");
	private Color selectedButtonColor = Color.gray;

	private JMenuBar menuBar;
	private JToolBar toolBar;

	private JButton deleteButton;
	private JButton addClassButton;

	private ClassDiagram classDiagram;

	private boolean addNewClassModeEnabled;

	public UMLEditor()
	{
		super("UML Editor");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(800, 800));
		this.setLocationByPlatform(true);

		setUpMenuBar();
		setUpToolBar();
		setUpClassDiagram();

		this.pack();
		this.setVisible(true);
	}

	public boolean isAddNewClassModeEnabled()
	{
		return (addNewClassModeEnabled);
	}

	public void disableAddNewClassMode()
	{
		addNewClassModeEnabled = false;
		addClassButton.setBackground(unselectedButtonColor);
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
		
		fileMenu.addSeparator();
		
		JMenuItem printOption = new JMenuItem("Print...");
		fileMenu.add(printOption);
		
		fileMenu.addSeparator();
		
		JMenuItem exitOption = new JMenuItem("Exit");
		exitOption.setActionCommand("EXIT");
		exitOption.addActionListener(this);
		fileMenu.add(exitOption);
		
		menuBar.add(fileMenu);

		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);

		this.add(menuBar, BorderLayout.NORTH);

	}

	private void setUpToolBar()
	{
		toolBar = new JToolBar();

		addClassButton = new JButton("Add Class");
		addClassButton.setActionCommand("ADD");
		addClassButton.addActionListener(this);
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

	private void setUpClassDiagram()
	{
		classDiagram = new ClassDiagram(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (arg0.getActionCommand() == "ADD")
		{
			// in most cases, we'll get this action event because we're enabling
			// Add-Class mode, so set background appropriately
			addClassButton.setBackground(selectedButtonColor);
			if (addNewClassModeEnabled)
			{
				// if Add-Class mode was already selected,
				// this click unselects it, so reset background 
				// to normal color
				addClassButton.setBackground(unselectedButtonColor);
			}
			// toggle Add-Class state
			addNewClassModeEnabled = !addNewClassModeEnabled;
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
			// When save is implemented, message will be changed to: Do you want to save?
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
	}

	/**
	 * Used when New is selected in the File menu.
	 * Deletes everything in the diagram
	 */
	public void clearDiagram()
	{
		this.remove(classDiagram.getView());
		classDiagram = new ClassDiagram(this);
		this.validate();
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
