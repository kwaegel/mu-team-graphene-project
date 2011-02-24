package umleditor;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Relationship defines a connection between two ClassNodes. Relationships will
 * be maintained by the individual classes they connect. They will be able to
 * draw themselves using a reference to the ClassDiagram’s View’s graphics, and
 * will do so when there are changes to the view panel.
 */
public class Relationship
{
	private int x1, x2, y1, y2;

	/**
	 * The type of relationship.
	 * 
	 */
	public enum RelationshipType
	{
		Composition, Aggeration, Relationship, Association, Generalization
	}

	/**
	 * The type of relationship to draw.
	 */
	private RelationshipType type;

	/**
	 * Contains a reference to the first ClassNode (relationship comes ‘from’
	 * this one).
	 */
	private ClassNode firstNode;

	/**
	 * Contains a reference to the second ClassNode (relationship goes ‘to’ this
	 * one).
	 */
	private ClassNode secondNode;

	/**
	 * 
	 * @param first
	 * @param second
	 * @param type
	 */
	public Relationship(ClassNode first, ClassNode second, RelationshipType type)
	{
		firstNode = first;
		secondNode = second;
		calculateEndPoints(first, second);
		this.type = type;
	}

	private void calculateEndPoints(ClassNode first, ClassNode second)
	{
		Rectangle firstBounds = first.getNodePanel().getBounds();
		Rectangle secondBounds = second.getNodePanel().getBounds();

		x1 = firstBounds.x;
		x2 = secondBounds.x;
		y1 = firstBounds.y;
		y2 = secondBounds.y;
	}

	/**
	 * @return a reference to the first node.
	 */
	public ClassNode getFirstNode()
	{
		return firstNode;
	}

	/**
	 * @return returns a reference to the second node
	 */
	public ClassNode getSecondNode()
	{
		return secondNode;
	}

	/**
	 * draw a line representing the relationship in the ClassDiagram view panel.
	 * Nature of line drawn depends on relationship type. Placement of line
	 * determined by location of NodePanels associated with first and second
	 * nodes
	 */
	public void draw(Graphics viewGraphics)
	{
		// TODO: need to draw ends depending on type.
		viewGraphics.drawLine(x1, y1, x2, y2);
	}

}
