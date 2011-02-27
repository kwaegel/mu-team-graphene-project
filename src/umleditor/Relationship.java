package umleditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 * Relationship defines a connection between two ClassNodes. Relationships will be maintained by the individual classes
 * they connect. They will be able to draw themselves using a reference to the ClassDiagram’s View’s graphics, and will
 * do so when there are changes to the view panel.
 */
public class Relationship
{
	private static final float arrowHeight = 12.0f;
	private static final float arrowWidth = 15.0f;

	/**
	 * The type of relationship.
	 * 
	 */
	public enum RelationshipType
	{
		/**
		 * A composition is represented by a filled diamond.
		 */
		Composition,
		/**
		 * An aggregation is represented by an open diamond.
		 */
		Aggregation,
		/**
		 * 
		 */
		Dependency,
		/**
		 * An association is represented by a plain (wireframe) arrow.
		 */
		Association,
		/**
		 * Generalization (or inheritance) is represented by a open triangle pointing to the more general (or parent)
		 * class.
		 */
		Generalization
	}

	private enum FillType
	{
		None, Outline, Solid
	}

	/**
	 * Specify how the polygon for the end arrow should be drawn.
	 */
	private FillType m_endFill;

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

	Polygon m_arrow; // Try CubicCurve2D?

	Point start, end;

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
		setEndFill();
	}

	private void calculateEndPoints()
	{
		Rectangle firstBounds = firstNode.getNodePanel().getBounds();
		Rectangle secondBounds = secondNode.getNodePanel().getBounds();

		// Find the center points of each edge for the first node.
		Point[] startEdges = new Point[4];
		startEdges[0] = new Point((int) (firstBounds.x + firstBounds.width / 2.0f), firstBounds.y);
		startEdges[1] = new Point(firstBounds.x + firstBounds.width, (int) (firstBounds.y + firstBounds.height / 2.0f));
		startEdges[2] = new Point((int) (firstBounds.x + firstBounds.width / 2.0f), firstBounds.y + firstBounds.height);
		startEdges[3] = new Point(firstBounds.x, (int) (firstBounds.y + firstBounds.height / 2.0f));

		// Find the center points of each edge for the second node.
		Point[] endEdges = new Point[4];
		endEdges[0] = new Point((int) (secondBounds.x + secondBounds.width / 2.0f), secondBounds.y);
		endEdges[1] = new Point(secondBounds.x + secondBounds.width,
				(int) (secondBounds.y + secondBounds.height / 2.0f));
		endEdges[2] = new Point((int) (secondBounds.x + secondBounds.width / 2.0f), secondBounds.y
				+ secondBounds.height);
		endEdges[3] = new Point(secondBounds.x, (int) (secondBounds.y + secondBounds.height / 2.0f));

		// Search for the closest two edge points to use for drawing.
		double minDistence = Float.POSITIVE_INFINITY;
		for (Point startPoint : startEdges)
		{
			for (Point endPoint : endEdges)
			{
				double dist = startPoint.distance(endPoint);
				if (dist < minDistence)
				{
					start = startPoint;
					end = endPoint;
					minDistence = dist;
				}
			}
		}
	}

	private void setEndFill()
	{
		if (type == RelationshipType.Composition || type == RelationshipType.Dependency)
		{
			m_endFill = FillType.Solid;
		}
		else if (type == RelationshipType.Generalization || type == RelationshipType.Aggregation)
		{
			m_endFill = FillType.Outline;
		}
		else if (type == RelationshipType.Association)
		{
			m_endFill = FillType.None;
		}
	}

	private void createArrowPoints()
	{
		m_arrow = new Polygon(); // Create a blank polygon

		// Calculate line direction vector.
		double dirX = end.x - start.x;
		double dirY = end.y - start.y;

		// Normalize
		double distence = Point2D.distance(start.x, start.y, end.x, end.y);
		dirX /= distence;
		dirY /= distence;

		// Create a perpendicular vector
		double perpX = dirY * arrowWidth / 2.0;
		double perpY = -dirX * arrowWidth / 2.0;

		// get the center point of the diamond (or center base of a triangle)
		double centerX = end.x - dirX * arrowHeight;
		double centerY = end.y - dirY * arrowHeight;

		// Add tip point.
		m_arrow.addPoint(end.x, end.y);

		// Add left point.
		m_arrow.addPoint((int) (centerX - perpX), (int) (centerY - perpY));

		// Add back point if needed.
		if (type == RelationshipType.Aggregation || type == RelationshipType.Composition)
		{
			m_arrow.addPoint((int) (end.x - 2 * dirX * arrowHeight), (int) (end.y - 2 * dirY * arrowHeight));
		}

		if (type == RelationshipType.Dependency)
		{
			// m_arrow.addPoint(end.x, end.y);
			m_arrow.addPoint((int) (end.x - 0.5 * dirX * arrowHeight), (int) (end.y - 0.5 * dirY * arrowHeight));
		}

		// Add right point.
		m_arrow.addPoint((int) (centerX + perpX), (int) (centerY + perpY));
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

		// Enable anti-aliasing mode.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Save the previous stroke pattern;
		Stroke oldStroke = g2d.getStroke();

		// Dependencies need to be drawn with a dashed line.
		if (type == RelationshipType.Dependency)
		{
			g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 8.0f,
					8.0f }, 5.0f));
		}

		// Draw the main line
		g2d.drawLine(start.x, start.y, end.x, end.y);

		// Restore the previous stroke pattern
		g2d.setStroke(oldStroke);

		// Draw line end arrow.
		switch (m_endFill)
		{
			case Solid:
				g2d.fillPolygon(m_arrow);
				break;
			case Outline: {
				g2d.setColor(Color.white);
				g2d.fillPolygon(m_arrow);
				g2d.setColor(Color.black);
				g2d.drawPolygon(m_arrow);
			}
				break;
			case None:// Do not draw an arrow.
				break;
		}
	}

}
