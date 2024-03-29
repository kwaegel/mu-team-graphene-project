package umleditor;

import java.awt.Graphics2D;

/**
 * Relationship defines a connection between two ClassNodes. Relationships will
 * be maintained by the individual classes they connect. They will be able to
 * draw themselves using a reference to the ClassDiagram�s View�s graphics, and
 * will do so when there are changes to the view panel.
 */
public class Relationship
{

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
	 * Contains a reference to the first ClassNode (relationship comes �from�
	 * this one).
	 */
	private ClassNode firstNode;

	/**
	 * Contains a reference to the second ClassNode (relationship goes �to� this
	 * one).
	 */
	private ClassNode secondNode;

	/**
	 * the graphics of the View, given to the relationship by the ClassDiagram
	 * when the relationship is created, allows relationship to draw itself.
	 */
	private Graphics2D viewGraphics;

	/**
	 * Methods: getFirst(): returns getSecond(): draw():
	 * */

	public Relationship(ClassNode first, ClassNode second, RelationshipType type)
	{
		firstNode = first;
		secondNode = second;
		this.type = type;
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
	public void draw()
	{
		viewGraphics.draw3DRect(5, 10, 8, 9, true);
	}
	
}
