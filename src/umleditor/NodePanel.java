package umleditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class NodePanel extends JPanel implements MouseListener
{

	private static final long serialVersionUID = 912113941232687505L;

	private ClassNode associatedNode;
	private ClassDiagram parentDiagram;

	public NodePanel(ClassDiagram parent, ClassNode node)
	{
		super();

		parentDiagram = parent;
		associatedNode = node;
		associatedNode.attachPanel(this);

		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setLayout(new MigLayout("wrap 1", "0[fill]0", ""));
		this.setMinimumSize(new Dimension(100, 1));
		this.createDisplay();
		this.addMouseListener(this);
	}

	public ClassNode getClassNode()
	{
		return associatedNode;
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

		// add class name
		String className = associatedNode.getName();
		JLabel titleLabel = new JLabel(className);
		this.add(titleLabel, "alignx center, gapx 15:push 15:push");

		// add separator
		addSeparator();
		
		// add attributes
		for (int i = 0; i < associatedNode.getNumAttributes(); ++i)
		{
			String attributeName = associatedNode.getAttribute(i);
			JLabel attributeLabel = new JLabel(attributeName);
			this.add(attributeLabel, "gapx 3");
		}
		
		// add separator
		addSeparator();

		// add methods
		for (int i = 0; i < associatedNode.getNumMethods(); ++i)
		{
			String methodName = associatedNode.getMethod(i);
			JLabel methodLabel = new JLabel(methodName);
			this.add(methodLabel, "gapx 3");
		}
	}
	
	/**
	 * Adds a separator to the ClassNode
	 */
	private void addSeparator()
	{
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		this.add(separator, "gapx 0, gapy 0");
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
