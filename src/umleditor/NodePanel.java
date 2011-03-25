package umleditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

/**
 * This is the visible representation of a class that appears in the UML Editor. Contains no information about the class
 * itself, but has a link to the ClassNode that does. Displays the information in this ClassNode in the appropriate
 * format. Handles interactions between the user and the class.
 * 
 */
public class NodePanel extends JPanel implements MouseListener
{
	/**
	 * Generated id, recommended for all GUI components
	 */
	private static final long serialVersionUID = 912113941232687505L;

	/**
	 * Height of top label, which displays the class name and also serves as a drag area for the NodePanel.
	 */
	private static int LABEL_AND_DRAG_Y_BOUND = 30;
	/**
	 * Height of a default (method or attribute) field. Used to ensure empty fields (which may be present during
	 * editing), or classes that have no attributes and/or methods, display correctly. This is the height that a JLabel
	 * with some title in the default font will have automatically.
	 */
	private static int DEFAULT_FIELD_HEIGHT = 16;

	/**
	 * ClassNode associated with this NodePanel. The NodePanel will display this ClassNode's name, methods and
	 * attributes.
	 */
	private ClassNode associatedNode;

	/**
	 * 
	 */
	private ClassDiagram parentDiagram;

	public NodePanel(ClassDiagram parent, ClassNode node)
	{
		super();

		parentDiagram = parent;
		associatedNode = node;
		associatedNode.attachPanel(this);

		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setLayout(new MigLayout("wrap 1, fill", "0[fill]0", ""));
		this.setBackground(Color.white);

		this.createDisplay();

		this.addMouseListener(this);
	}

	public ClassNode getClassNode()
	{
		return associatedNode;
	}

	public ClassDiagram getParentDiagram()
	{
		return parentDiagram;
	}

	public void makeUnselected()
	{
		this.setBackground(Color.white);
	}

	public void makeSelected()
	{
		this.setBackground(Color.pink);
	}

	/**
	 * recreated display from values in classNode
	 */
	public void createDisplay()
	{
		// clear everything in the class diagram
		this.removeAll();

		// set up title label / dragging label
		String className = associatedNode.getName();
		JLabel titleDragLabel = new JLabel(className, JLabel.CENTER);
		Dimension labelSize = titleDragLabel.getMinimumSize();
		titleDragLabel.setMinimumSize(new Dimension(labelSize.width + 30, LABEL_AND_DRAG_Y_BOUND));
		NodeDragListener ndl = new NodeDragListener(this);
		titleDragLabel.addMouseMotionListener(ndl);
		titleDragLabel.addMouseListener(ndl);
		this.add(titleDragLabel, "dock north");

		// add separator
		addSeparator();

		// add attributes
		for (int i = 0; i < associatedNode.getNumAttributes(); ++i)
		{
			String attributeName = associatedNode.getAttribute(i);
			JLabel attributeLabel = new JLabel(attributeName);
			// ensure that blank fields don't make node panel smaller
			// will never have a blank attribute except while editing,
			// but while editing, don't want strange effects where making
			// a field empty causes node lenght to shrink
			if (attributeName.isEmpty())
				attributeLabel.setMinimumSize(new Dimension(0, 16));
			this.add(attributeLabel, "gapx 3 3");
		}

		// add space if there are no attributes so displays properly
		if (associatedNode.getNumAttributes() == 0)
			addSpacer();

		// add separator
		addSeparator();

		// add methods
		for (int i = 0; i < associatedNode.getNumMethods(); ++i)
		{
			String methodName = associatedNode.getMethod(i);
			JLabel methodLabel = new JLabel(methodName);
			// ensure that blank fields don't make node panel smaller
			// will never have a blank attribute except while editing,
			// but while editing, don't want strange effects where making
			// a field empty causes node lenght to shrink
			if (methodName.isEmpty())
				methodLabel.setMinimumSize(new Dimension(0, DEFAULT_FIELD_HEIGHT));
			this.add(methodLabel, "gapx 3 3");
		}

		// add space if there are no attributes so displays properly
		if (associatedNode.getNumMethods() == 0)
			addSpacer();

		// add an empty JLabel, workaround for a bug in MigLayout
		// where using direction docking causes the last component
		// in the container to not have the normal gap after it
		this.add(new JLabel());
	}

	/**
	 * Adds a separator to the ClassNode
	 */
	private void addSeparator()
	{
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setForeground(Color.black);
		this.add(separator);
	}

	/**
	 * Adds a spacer the height of an empty field
	 */
	private void addSpacer()
	{
		JLabel spacer = new JLabel();
		spacer.setMinimumSize(new Dimension(0, DEFAULT_FIELD_HEIGHT));
		this.add(spacer);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		int clickCount = e.getClickCount();
		if (clickCount > 1)
		{
			EditPanel editPanel = new EditPanel(this.associatedNode);
			editPanel.setVisible(true);
		}
		else
		{
			parentDiagram.setSelectedNode(associatedNode);
			this.makeSelected();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// do nothing
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// do nothing
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		parentDiagram.setSelectedNode(associatedNode);
		this.makeSelected();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		Component comp = parentDiagram.getComponentUnder(e);

		if (comp instanceof NodePanel)
		{
			ClassNode targetNode = ((NodePanel) comp).getClassNode();
			parentDiagram.addRelationship(targetNode);
		}
	}
}
