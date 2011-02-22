package umleditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

public class UMLEditor extends JFrame implements ActionListener {

	private static final long serialVersionUID = -9139566399320553797L;
	JMenuBar menuBar;
	JToolBar toolBar;
	ClassDiagram classDiagram;
	boolean addNewClassModeEnabled;
	
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
	}
	
	private void setUpMenuBar()
	{
		menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		JMenuItem newOption = new JMenuItem("New");
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
		fileMenu.add(exitOption);
		menuBar.add(fileMenu);
		
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		this.add(menuBar, BorderLayout.NORTH);
		 
	}
	
	private void setUpToolBar()
	{
		toolBar = new JToolBar();
		
		JButton addClassButton = new JButton("Add Class");
		addClassButton.setActionCommand("ADD");
		addClassButton.addActionListener(this);
		toolBar.add(addClassButton);
		addNewClassModeEnabled = false;
		
		JButton deleteButton = new JButton("Delete");
		deleteButton.setEnabled(false);
		toolBar.add(deleteButton);
		
		this.add(toolBar, BorderLayout.SOUTH);
	}
	
	private void setUpClassDiagram()
	{
		classDiagram = new ClassDiagram(this);
	}


	public static void main(String[] args) {
		new UMLEditor();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand() == "ADD")
		{
			addNewClassModeEnabled = !addNewClassModeEnabled;
		}
		
	}
	
	public static void main(String[] args) 
	{
		
	}
}
