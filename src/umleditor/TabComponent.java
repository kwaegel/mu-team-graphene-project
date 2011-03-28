package umleditor;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

public class TabComponent extends JPanel implements ActionListener
{
	/**
	 * Generated id, recommended for all GUI components
	 */
	private static final long serialVersionUID = -6628241342810672413L;

	private UMLEditor editor;

	private JLabel titleLabel;

	public TabComponent(UMLEditor parentEditor, String title)
	{
		super();
		this.setLayout(new MigLayout());

		titleLabel = new JLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(10.0f));
		this.add(titleLabel, "dock west, gapx 0 5, gapy 3");

		editor = parentEditor;
		JButton closeButton = new JButton("X");
		closeButton.setFont(closeButton.getFont().deriveFont(8.0f));
		closeButton.setMargin(new Insets(0, 0, 0, 0));
		closeButton.setFocusable(false);
		closeButton.setFocusPainted(false);
		closeButton.addActionListener(this);
		this.add(closeButton, "dock east, gapy 3");

		this.setOpaque(false);
	}

	public void setTitle(String newTitle)
	{
		titleLabel.setText(newTitle);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JTabbedPane parentPane = (JTabbedPane) this.getParent().getParent();
		if (parentPane.getTabComponentAt(parentPane.getSelectedIndex()).equals(this))
		{
			editor.closeCurrentTab();
		}
	}

}
