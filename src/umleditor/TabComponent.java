package umleditor;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

/**
 * The component that is attached to each tab on the tabbedPane in the {@link UMLEditor}. Displays the title of the tab and the close button for
 * the {@link ClassDiagram} displayed in that tab.
 */
public class TabComponent extends JPanel implements ActionListener
{
	/**
	 * Generated id, recommended for all GUI components
	 */
	private static final long serialVersionUID = -6628241342810672413L;

	private UMLEditor editor;
	private JTabbedPane tabbedPane;
	private JLabel titleLabel;
	private JButton closeButton;

	/**
	 * Creates a new {@link TabComponent} which displays the tab titles and delete buttons for parentTabbedPane in
	 * parentEditor.
	 * 
	 * @param parentEditor
	 * @param parentTabbedPane
	 * @param title
	 */
	public TabComponent(UMLEditor parentEditor, JTabbedPane parentTabbedPane, String title)
	{
		super();
		this.setLayout(new MigLayout());

		titleLabel = new JLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(10.0f));
		this.add(titleLabel, "dock west, gapx 0 5, gapy 3");

		editor = parentEditor;
		closeButton = new JButton("X");
		closeButton.setFont(closeButton.getFont().deriveFont(8.0f));
		closeButton.setMargin(new Insets(0, 0, 0, 0));
		closeButton.setFocusable(false);
		closeButton.setFocusPainted(false);
		closeButton.addActionListener(this);
		this.add(closeButton, "dock east, gapy 3");

		tabbedPane = parentTabbedPane;

		this.setOpaque(false);
	}

	/**
	 * Sets the title to display for this tab.
	 * 
	 * @param newTitle
	 *            - new title to display
	 */
	public void setTitle(String newTitle)
	{
		titleLabel.setText(newTitle);
	}

	/**
	 * Called when user clicks on close button. Gets the current index and tells the {@link UMLEditor} to delete the
	 * {@link ClassDiagram} at that index.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		int index = tabbedPane.indexOfTabComponent(this);
		editor.closeTab(index);
	}
}
