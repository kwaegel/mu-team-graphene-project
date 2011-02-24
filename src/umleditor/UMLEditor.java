package umleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class UMLEditor extends JFrame implements ActionListener {

	private static final long serialVersionUID = -9139566399320553797L;
	private Color unselectedButtonColor = javax.swing.UIManager
			.getColor("Button.background");
	private Color selectedButtonColor = Color.gray;

	private JMenuBar menuBar;
	private JToolBar toolBar;

	private JButton deleteButton;
	private JButton addClassButton;

	private ClassDiagram classDiagram;

	private boolean addNewClassModeEnabled;

	public UMLEditor() {
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

	public boolean isAddNewClassModeEnabled() {
		return (addNewClassModeEnabled);
	}

	public void disableAddNewClassMode() {
		addNewClassModeEnabled = false;
		addClassButton.setBackground(unselectedButtonColor);
	}

	public void setDeleteButtonState(boolean enabled) {
		deleteButton.setEnabled(enabled);
	}

	private void setUpMenuBar() {
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

	private void setUpToolBar() {
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

	private void setUpClassDiagram() {
		classDiagram = new ClassDiagram(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand() == "ADD") {
			// in most cases, we'll get this action event because we're enabling
			// Add-Class mode, so set background appropriately
			addClassButton.setBackground(selectedButtonColor);
			if (addNewClassModeEnabled) {
				// in the rare case that the user is clicking AddClass again
				// without adding a node to unselect Add-Class mode
				// change background to unselected color
				addClassButton.setBackground(unselectedButtonColor);
			}
			// toggle Add-Class state
			addNewClassModeEnabled = !addNewClassModeEnabled;
		} else if (arg0.getActionCommand() == "DELETE") {
			
			classDiagram.deleteSelectedNode();
			
		} else if(arg0.getActionCommand() == "NEW")
		{
			clearDiagram();
			
		}else if(arg0.getActionCommand() == "EXIT")
		{
			JDialog quit = new JDialog();
			quit.setVisible(true);
			quit.setTitle("Exit");
//			JLabel text = new JLabel("Are you sure you want to quit?");
//			quit.add(text);
//			
//			ActionListener exitDialog = new ActionListener()
//			{
//				@Override
//				public void actionPerformed(ActionEvent arg0) {
//					if (arg0.getActionCommand() == "YES") 
//					{
//						System.exit(0);
//						
//					}else{ 
//						
//						//quit.close();
//						
//					}
//				}
//			};
//			JButton yes = new JButton("Yes");
//			yes.setActionCommand("YES");
//			yes.addActionListener(exitDialog);
//			JButton no = new JButton("No");
//			no.setActionCommand("NO");
//			no.addActionListener(exitDialog);
//			quit.add(yes);
//			quit.add(no);
			
			//JOptionPane.showConfirmDialog(parentComponent, message)
			
			
//			Object[] yesno = {"Yes", "No"};
//			int n = JOptionPane.showOptionDialog(quit, "Are you sure you want to quit?",
//					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, yesno, yesno[1]);
//			
//			
//			
//			JOptionPane options = new JOptionPane("Are you sure you want to quit?",
//			JOptionPane.QUESTION_MESSAGE,
//			JOptionPane.YES_NO_OPTION);
//			options.setContentPane(optionPane);
//			optionPane.addPropertyChangeListener(new PropertyChangeListener() {
//				public void propertyChange(PropertyChangeEvent e) {
//					String prop = e.getPropertyName();
//
//					if (options.isVisible() && (e.getSource() == optionPane)
//							&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
//						// If you were going to check something
//						// before closing the window, you'd do
//						// it here.
//						options.setVisible(false);
//					}
//				}
//			});
//			dialog.pack();
//			dialog.setVisible(true);

//			int value = ((Integer)options.getValue()).intValue();
//			if (value == JOptionPane.YES_OPTION) {
//			    System.exit(0);
//			} else if (value == JOptionPane.NO_OPTION) {
//				
//			}
			
			JOptionPane optionPane = new JOptionPane ("Sure you want to exit?", 
						JOptionPane.YES_NO_OPTION);
			
			optionPane.setVisible(true);

			
			
		}
	}
	//Used when New is selected in the File menu.
	//Deletes everything in the diagram
	public void clearDiagram()
	{
		while(classDiagram.getNodes().size() > 0)
		{
			classDiagram.setSelectedNode(classDiagram.getNodes().getFirst());
			classDiagram.deleteSelectedNode();
		}
	}

	/**
	 * Entry point for the program.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new UMLEditor();
	}
}
