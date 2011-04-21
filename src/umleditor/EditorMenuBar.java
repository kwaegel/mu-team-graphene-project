package umleditor;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * {@link JMenuBar} that holds menu items for the UML Editor. Maintains references to those {@link JMenuItem}s whose
 * state will need to change in the future based on changes in the {@link UMLEditor}, and provides methods for changing
 * them.
 */
public class EditorMenuBar extends JMenuBar
{
	/**
	 * Generated id for GUI item
	 */
	private static final long serialVersionUID = -5119021766263204155L;

	/**
	 * Menu items that will need to be enabled/disabled based on changes in the {@link UMLEditor}'s state.
	 */
	private JMenuItem closeOption;
	private JMenuItem saveOption;
	private JMenuItem saveAsOption;
	private JMenuItem printOption;
	private JMenuItem cutOption;
	private JMenuItem copyOption;
	private JMenuItem pasteOption;

	/**
	 * Maintains whether or not a node has been copied, so that when diagram-related options are enabled, the paste
	 * option will set appropriately.
	 */
	private boolean pasteEnabled;

	/**
	 * Constructs a new {@link EditorMenuBar} which displays menus in the specified {@link UMLEditor}.
	 * 
	 * @param parentEditor
	 *            - UMLEditor of which this menu will be part
	 */
	public EditorMenuBar(UMLEditor parentEditor)
	{
		super();

		pasteEnabled = false;

		createFileMenu(parentEditor);

		createEditMenu(parentEditor);

		createHelpMenu(parentEditor);
	}

	/**
	 * Sets up and attaches the file menu. File menu contains New, Load, Close, Save, Save As, and Exit
	 * 
	 * @param parentEditor
	 *            - {@link UMLEditor} that will listen to events from the menu items.
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
	 * Sets up and attaches the edit menu. Edit menu contains Cut, Copy and Paste.
	 * 
	 * @param parentEditor
	 *            - {@link UMLEditor} that will listen to events from the menu items.
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

	/**
	 * Sets up and attaches the help menu. Help menu contains the Help Contents and About options.
	 * 
	 * @param parentEditor
	 *            - {@link UMLEditor} that will listen to events from the menu items.
	 */
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

	/**
	 * Called when a node is copied in the UMLEditor. Ensures the paste menu item will always be enabled when a class
	 * diagram is open.
	 */
	public void enablePaste()
	{
		pasteEnabled = true;
		pasteOption.setEnabled(pasteEnabled);
	}

	/**
	 * Sets the state of the copy and cut menu items, which should become enabled when a {@link ClassNode} is selected,
	 * and disabled when none are selected.
	 * 
	 * @param enabled
	 *            - whether the cut and copy options should be enabled or not.
	 */
	public void setCopyCutMode(boolean enabled)
	{
		cutOption.setEnabled(enabled);
		copyOption.setEnabled(enabled);
	}

	/**
	 * Sets the state of the menu items which should only be enabled when a {@link ClassDiagram} is open in the
	 * {@link UMLEditor}.
	 * 
	 * @param enabled
	 *            - whether the menu items should be enabled or disabled.
	 */
	public void setDiagramBasedMenuItems(boolean enabled)
	{
		closeOption.setEnabled(enabled);
		saveOption.setEnabled(enabled);
		saveAsOption.setEnabled(enabled);
		printOption.setEnabled(enabled);
		cutOption.setEnabled(enabled);
		copyOption.setEnabled(enabled);
		boolean pasteState;
		if (enabled)
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
