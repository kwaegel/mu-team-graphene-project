package umleditor;

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * The component that is attached to each tab on the tabbedPane in the {@link UMLEditor}. Displays the title of the tab
 * and the close button for the {@link ClassDiagram} displayed in that tab.
 */
public class TabTitleComponent extends JPanel// implements ActionListener
{
	/**
	 * Generated id, recommended for all GUI components
	 */
	private static final long serialVersionUID = -6628241342810672413L;

	private JLabel titleLabel;

	/**
	 * Creates a new {@link TabTitleComponent} which displays tab titles and delete buttons.
	 * 
	 * @param closeListener
	 *            - {@link ActionListener} that is invoked when the close button is pressed.
	 * @param title
	 *            - title to display for this tab
	 */
	public TabTitleComponent(ActionListener closeListener, String title)
	{
		super();
		this.setLayout(new MigLayout());

		titleLabel = new JLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(10.0f));
		this.add(titleLabel, "dock west, gapx 0 5, gapy 3");

		JButton closeButton = new JButton("X");
		closeButton.setFont(closeButton.getFont().deriveFont(8.0f));
		closeButton.setMargin(new Insets(0, 0, 0, 0));
		closeButton.setFocusable(false);
		closeButton.setFocusPainted(false);
		closeButton.addActionListener(closeListener);
		this.add(closeButton, "dock east, gapy 3");

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
}