package umleditor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.vecmath.Point2i;

/**
 * Relationship defines a connection between two ClassNodes. Relationships will be maintained by the individual classes
 * they connect. They will be able to draw themselves using a reference to the ClassDiagram’s View’s graphics, and will
 * do so when there are changes to the view panel.
 */
public class Relationship
{
	private float arrowHeight = 10.0f;
	private float arrowWidth = 10.0f;

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
	 * Contains a reference to the first ClassNode (relationship comes ‘from’ this one).
	 */
	private ClassNode firstNode;

	/**
	 * Contains a reference to the second ClassNode (relationship goes ‘to’ this one).
	 */
	private ClassNode secondNode;

	Polygon m_arrow;

	Point2i start, end;
	int[] xPoints, yPoints;
	int numPoints;

	/**
	 * 
	 * @param first
	 * @param second
	 * @param type
	 */
	public Relationship(ClassNode first, ClassNode second, RelationshipType type)
	{
		this.type = type;
		firstNode = first;
		secondNode = second;

		m_arrow = new Polygon();

		calculateEndPoints();
		createArrowPoints();
	}

	private void calculateEndPoints()
	{
		Rectangle firstBounds = firstNode.getNodePanel().getBounds();
		Rectangle secondBounds = secondNode.getNodePanel().getBounds();

		start = new Point2i(firstBounds.x, firstBounds.y);
		end = new Point2i(secondBounds.x, secondBounds.y);

	}

	private void createArrowPoints()
	{
		m_arrow = new Polygon();

		if (end.x == start.x) // Handle degenerate case
		{
			int lowerY = end.y + (int) arrowHeight;

			numPoints = 3;

			// Add tip point
			m_arrow.addPoint(end.x, end.y);

			// add left point
			m_arrow.addPoint((int) (end.x - arrowWidth / 2), lowerY);

			// Add back point if needed.
			if (type == RelationshipType.Aggeration || type == RelationshipType.Composition)
			{
				m_arrow.addPoint(end.x, (int) (end.y + 2 * arrowHeight));
			}

			// Add right point.
			m_arrow.addPoint((int) (end.x + arrowWidth / 2), lowerY);
		}
		else
		{
			// Dalculate line direction vector.
			double dirX = end.x - start.x;
			double dirY = end.y - start.y;

			// Normalize
			double distence = Point2D.distance(start.x, start.y, end.x, end.y);
			dirX /= distence;
			dirY /= distence;

			// Create a perpendicular vector
			double perpX = dirY * this.arrowWidth / 2.0;
			double perpY = -dirX * this.arrowWidth / 2.0;

			// get the center point of the diamond (or center base of a triangle)
			double centerX = end.x - dirX; // Need to mult by length?
			double centerY = end.y - dirY;

			// Add tip point.
			m_arrow.addPoint(end.x, end.y);

			// Add left point.
			m_arrow.addPoint((int) (centerX - perpX), (int) (centerY - perpY));

			// Add back point if needed.
			if (type == RelationshipType.Aggeration || type == RelationshipType.Composition)
			{
				m_arrow.addPoint(end.x, (int) (end.y + 2 * arrowHeight));
			}

			// Add right point.
			m_arrow.addPoint((int) (centerX + perpX), (int) (centerY + perpY));

		}
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
	 * draw a line representing the relationship in the ClassDiagram view panel. Nature of line drawn depends on
	 * relationship type. Placement of line determined by location of NodePanels associated with first and second nodes
	 */
	public void draw(Graphics viewGraphics)
	{
		Graphics2D g2d = (Graphics2D) viewGraphics;

		// draw the main line
		g2d.drawLine(start.x, start.y, end.x, end.y);

		// Draw line end arrow.
		if (type == RelationshipType.Composition)
		{
			g2d.fillPolygon(m_arrow);
		}
		else
		{
			g2d.drawPolygon(m_arrow);
		}

		/*
		 * Vector2d normal = new Vector2d(end.x-start.x, end.y-start.y); normal.normalize();
		 * 
		 * Point2d center = new Point2d(end.x, end.y); center.scaleAdd(-arrowHeight, normal);
		 * 
		 * 
		 * // y=m*x+b
		 * 
		 * 
		 * 
		 * double m = (end.y-start.y)/(end.x-start.x); // get the slope double negRecipSlope = -1.0/m; Vector2d recipVec
		 * = new Vector2d(end.y-start.y, end.x-start.x); recipVec.negate();
		 * 
		 * // get the normal direction Vector2d dir = new Vector2d(end.x-start.x, end.y - start.y);
		 * 
		 * 
		 * // negative of the arrow height times the direction plus the tip position //Point2d center = new Point2d();
		 * center.scaleAdd(-arrowHeight, dir, new Point2d(end.x, end.y));
		 * 
		 * Point2d rightPoint = new Point2d(recipSlope,); //rightPoint.scale Point2d leftPoint = new Point2d();
		 * 
		 * 
		 * float centerX = end.x ; float centerY;
		 * 
		 * //Point2i p1 =
		 */

	}

}
