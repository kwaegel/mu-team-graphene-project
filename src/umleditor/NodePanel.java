package umleditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class NodePanel extends JPanel implements MouseListener
{

	private static final long serialVersionUID = 912113941232687505L;
	ClassNode classNode;
	ClassDiagram parentDiagram;

	JLabel className;

	public NodePanel(ClassNode node, ClassDiagram parent, Point creationPoint)
	{
		classNode = node;
		parentDiagram = parent;
		this.addMouseListener(this);

		// Set the size and position
		Dimension dims = new Dimension(100, 100); // Set how big the class view should be.
		Rectangle r = new Rectangle(creationPoint, dims); // Set where the node should be shown.
		setBounds(r);
		setSize(dims);

		// Set appearance and layout.
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createLineBorder(Color.blue));
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// Setup default data.
		className = new JLabel(node.getName());
		className.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(className, JLabel.CENTER);

		this.add(new JSeparator(SwingConstants.HORIZONTAL));
	}

	public void setNode(ClassNode node)
	{
		classNode = node;
	}

	public void makeUnselected()
	{
		this.setBackground(Color.white);
	}

	public void makeSelected()
	{
		this.setBackground(Color.pink);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		int clickCount = e.getClickCount();

		if (clickCount > 1)
		{
			// open edit dialog
		}
		else
		{
			parentDiagram.setSelectedNode(classNode);
			this.makeSelected();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		parentDiagram.addRelationship(this);

	}

}
