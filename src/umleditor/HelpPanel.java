package umleditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Modeless JDialog that contains two tabs: Help Contents and About. Help Contents contains information about how to use
 * the UML Editor. About tab contains information about the UML Editor. Accessing either the "Help" or the "About"
 * option from the help menu will bring up the Help Panel if it is not already open.
 */
public class HelpPanel extends JDialog
{
	/**
	 * Generated id, recommended for all GUI components
	 */
	private static final long serialVersionUID = 3093084182510159371L;

	/**
	 * Tabbed Pane that holds the Help Contents and About pages
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Constructs a new Help Panel and sets up the Contents and About tabs. Help Panel is Modeless, so will not prevent
	 * use of UML Editor while open.
	 */
	public HelpPanel()
	{
		super();
		super.setTitle("UML Editor Help");
		super.setModalityType(ModalityType.MODELESS);
		super.setLocation(500, 100);
		super.setMinimumSize(new Dimension(400, 450));

		tabbedPane = new JTabbedPane();
		addHelpContentsTab();
		addAboutTab();
		this.add(tabbedPane, BorderLayout.CENTER);
	}

	/**
	 * Sets up and attaches the Contents tab. Contents tab will contain the description of how to use the UML Editor.
	 * Found in the HelpContents.html file located in the project. This description is placed inside a JScrollPane.
	 */
	private void addHelpContentsTab()
	{
		JScrollPane scrollPane = new JScrollPane();
		final JEditorPane helpContents = new JEditorPane();
		helpContents.setEditable(false);
		URL helpURL = this.getClass().getResource("HelpContents.html");
		helpContents.setContentType("text/html");

		try
		{
			helpContents.setPage(helpURL);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		scrollPane.setViewportView(helpContents);

		helpContents.addHyperlinkListener(new HyperlinkListener()
		{
			@Override
			public void hyperlinkUpdate(HyperlinkEvent r)
			{
				try
				{
					if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
					{
						helpContents.setPage(r.getURL());
					}
				}
				catch (Exception e)
				{
				}
			}
		});

		tabbedPane.addTab("Help Contents", null, scrollPane, "Help Contents");
	}

	/**
	 * Sets up and attaches the About tab. About tab will contain general information about the project, found in the
	 * About.html file.
	 */
	private void addAboutTab()
	{
		JEditorPane about = new JEditorPane();
		about.setEditable(false);
		URL aboutURL = this.getClass().getResource("About.html");
		about.setContentType("text/html");
		try
		{
			about.setPage(aboutURL);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		tabbedPane.addTab("About", null, about, "About");
	}

	/**
	 * Opens the Contents tab in the Help Panel
	 */
	public void setToContentsTab()
	{
		tabbedPane.setSelectedIndex(0);
	}

	/**
	 * Opens the About tab in the Help Panel
	 */
	public void setToAboutTab()
	{
		tabbedPane.setSelectedIndex(1);
	}
}
