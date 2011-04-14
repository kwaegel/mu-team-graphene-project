package umleditor;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class EditorMenuBar extends JMenuBar
{
	/**
	 * Generated id for GUI item
	 */
	private static final long serialVersionUID = -5119021766263204155L;
	
	private JMenuItem closeOption;
	private JMenuItem saveOption;
	private JMenuItem saveAsOption;
	private JMenuItem printOption;
	private JMenuItem cutOption;
	private JMenuItem copyOption;
	private JMenuItem pasteOption;
	
	private boolean pasteEnabled;
	
	public EditorMenuBar (UMLEditor parentEditor)
	{
		super ();
		
		pasteEnabled = false;
		
		createFileMenu(parentEditor);

		createEditMenu(parentEditor);

		createHelpMenu(parentEditor);
	}
	
	/**
	 * Sets up and attaches the file menu. File menu contains New, Load, Close, Save, Save As, and Exit
	 */
	private void createFileMenu(UMLEditor parentEditor)
	{
		JMenu fileMenu = new JMenu("File");

		JMenuItem newOption = new JMenuItem("New");
		newOption.setActionCommand("NEW");
		newOption.addActionListener(parentEditor);
		newOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(newOption);

		JMenuItem loadOption = new JMenuItem("Load...");
		loadOption.setActionCommand("LOAD");
		loadOption.addActionListener(parentEditor);
		loadOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(loadOption);

		closeOption = new JMenuItem("Close");
		closeOption.setActionCommand("CLOSE");
		closeOption.addActionListener(parentEditor);
		closeOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(closeOption);

		fileMenu.addSeparator();

		saveOption = new JMenuItem("Save");
		saveOption.setActionCommand("SAVE");
		saveOption.addActionListener(parentEditor);
		saveOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(saveOption);

		saveAsOption = new JMenuItem("Save As...");
		saveAsOption.setActionCommand("SAVEAS");
		saveAsOption.addActionListener(parentEditor);
		fileMenu.add(saveAsOption);

		fileMenu.addSeparator();

		printOption = new JMenuItem("Print...");
		printOption.setActionCommand("PRINT");
		printOption.addActionListener(parentEditor);
		printOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(printOption);

		fileMenu.addSeparator();

		JMenuItem exitOption = new JMenuItem("Exit");
		exitOption.setActionCommand("EXIT");
		exitOption.addActionListener(parentEditor);
		exitOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		fileMenu.add(exitOption);

		this.add(fileMenu);
	}

	/**
	 * Sets up and attaches the edit menu. Edit menu contains Cut, Copy and Paste
	 */
	private void createEditMenu(UMLEditor parentEditor)
	{
		JMenu editMenu = new JMenu("Edit");

		cutOption = new JMenuItem("Cut");
		cutOption.setActionCommand("CUT");
		cutOption.addActionListener(parentEditor);
		cutOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		editMenu.add(cutOption);

		copyOption = new JMenuItem("Copy");
		copyOption.setActionCommand("COPY");
		copyOption.addActionListener(parentEditor);
		copyOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		editMenu.add(copyOption);

		pasteOption = new JMenuItem("Paste");
		pasteOption.setActionCommand("PASTE");
		pasteOption.addActionListener(parentEditor);
		pasteOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		pasteOption.setEnabled(pasteEnabled);
		editMenu.add(pasteOption);

		this.add(editMenu);
	}

	private void createHelpMenu(UMLEditor parentEditor)
	{
		JMenu helpMenu = new JMenu("Help");

		JMenuItem helpOption = new JMenuItem("Help Contents  ");
		helpOption.setActionCommand("HELP");
		helpOption.addActionListener(parentEditor);
		helpOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpMenu.add(helpOption);
		helpMenu.addSeparator();

		JMenuItem aboutOption = new JMenuItem("About");
		aboutOption.setActionCommand("ABOUT");
		aboutOption.addActionListener(parentEditor);
		helpMenu.add(aboutOption);

		this.add(helpMenu);
	}
	
	public void enablePaste()
	{
		pasteEnabled = true;
		pasteOption.setEnabled(pasteEnabled);
	}
	
	public void toggleCopyCutMode(boolean enabled)
	{
		cutOption.setEnabled(enabled);
		copyOption.setEnabled(enabled);
	}

	public void toggleDiagramBasedMenuItems(boolean enabled)
	{
		closeOption.setEnabled(enabled);
		saveOption.setEnabled(enabled);
		saveAsOption.setEnabled(enabled);
		printOption.setEnabled(enabled);
		cutOption.setEnabled(enabled);
		copyOption.setEnabled(enabled);
		boolean pasteState;
		if(enabled)
		{
			pasteState = pasteEnabled;
		}
		else
		{
			pasteState = false;
		}
		pasteOption.setEnabled(pasteState);
	}
}
