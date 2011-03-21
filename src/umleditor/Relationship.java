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
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Relationship defines a connection between two ClassNodes. Relationships will be maintained by the individual classes
 * they connect. They will be able to draw themselves using a reference to the ClassDiagram’s View’s graphics, and will
 * do so when there are changes to the view panel.
 */
public class Relationship
{
	/* Static constants. */
	private static final float m_arrowHeight = 12.0f;
	private static final float m_arrowWidth = 15.0f;

	private static final int m_clickDelta = 5;

	private static final int m_cpDrawSize = 6;

	/**
	 * The type of relationship.
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

	/* Member variables. */

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
	 * Contains a list of class nodes to draw between.
	 */
	private Point[] m_points;

	private GeneralPath m_line;

	/**
	 * Contains a reference to the first ClassNode (relationship comes ‘from’ this one).
	 */
	private ClassNode firstNode;

	/**
	 * Contains a reference to the second ClassNode (relationship goes ‘to’ this one).
	 */
	private ClassNode secondNode;

	Point firstNodeOffset, secondNodeOffset;

	/**
	 * The {@link java.awt.Polygon Polygon} used to draw the end arrow of the relationship.
	 */
	Polygon m_arrow;

	// If the relationship is selected or not.
	private boolean m_selected = false;

	// Which control node, if any, is selected.
	private Point m_selectedControlPoint = null;

	private int m_selectionTolerence = 5;

	/* Methods */

	/**
	 * Creates a relationship between two classes without offset information.
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

		int m_numLinePoints = 2;
		m_points = new Point[m_numLinePoints];
		m_line = new GeneralPath(GeneralPath.WIND_NON_ZERO, m_numLinePoints);

		m_arrow = new Polygon();

		calculatePathControlPoints();

		createPathFromPoints();

		createArrowPoints();
		setArrowFill();
	}

	/**
	 * Creates a relationship between two classes using offset information.
	 * 
	 * @param first
	 * @param firstOffset
	 * @param second
	 * @param secondOffset
	 */
	public Relationship(ClassNode first, Point firstOffset, ClassNode second, Point secondOffset, RelationshipType type)
	{
		this.type = type;
		firstNode = first;
		secondNode = second;

		int m_numLinePoints = 2;
		m_points = new Point[m_numLinePoints];
		m_line = new GeneralPath(GeneralPath.WIND_NON_ZERO, m_numLinePoints);

		m_arrow = new Polygon();

		firstNodeOffset = firstOffset;
		secondNodeOffset = secondOffset;
		calculatePathControlPoints();

		addControlPoint(new Point(100, 100), 1);

		createPathFromPoints();

		createArrowPoints();
		setArrowFill();
	}

	private void calculatePathControlPoints()
	{
		Rectangle firstBounds = firstNode.getBounds();
		Rectangle secondBounds = secondNode.getBounds();

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
					m_points[0] = startPoint;
					m_points[m_points.length - 1] = endPoint;
					minDistence = dist;
				}
			}
		}

		// Add a center control point for testing.
		int deltaX = (m_points[m_points.length - 1].x - m_points[0].x) / 2;
		int deltaY = (m_points[m_points.length - 1].y - m_points[0].y) / 2;
		Point centerPoint = new Point(m_points[0].x + deltaX, m_points[0].y + deltaY);
		this.addControlPoint(centerPoint, 1);

		if (firstNodeOffset == null || secondNodeOffset == null)
		{
			firstNodeOffset = new Point();
			secondNodeOffset = new Point();
			firstNodeOffset.x = m_points[0].x - firstBounds.x;
			firstNodeOffset.y = m_points[0].y - firstBounds.y;
			secondNodeOffset.x = m_points[m_points.length - 1].x - secondBounds.x;
			secondNodeOffset.y = m_points[m_points.length - 1].y - secondBounds.y;
		}
	}

	private void recalculateEndPoints()
	{
		Rectangle firstBounds = firstNode.getBounds();
		Rectangle secondBounds = secondNode.getBounds();

		m_points[0].x = firstBounds.x + firstNodeOffset.x;
		m_points[0].y = firstBounds.y + firstNodeOffset.y;

		m_points[m_points.length - 1].x = secondBounds.x + secondNodeOffset.x;
		m_points[m_points.length - 1].y = secondBounds.y + secondNodeOffset.y;
	}

	/**
	 * Determine type of arrow fill to use.
	 */
	private void setArrowFill()
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

	// FIXME: this does not redraw correctly when the class is being dragged.
	private void createArrowPoints()
	{
		m_arrow = new Polygon(); // Create a blank polygon

		// Copy start and end data from line arrays.
		Point start = m_points[0];
		Point end = m_points[m_points.length - 1];

		// Calculate line direction vector.
		double dirX = end.x - start.x;
		double dirY = end.y - start.y;

		// Normalize
		double distence = Point2D.distance(start.x, start.y, end.x, end.y);
		dirX /= distence;
		dirY /= distence;

		// Create a perpendicular vector
		double perpX = dirY * m_arrowWidth / 2.0;
		double perpY = -dirX * m_arrowWidth / 2.0;

		// get the center point of the diamond (or center base of a triangle)
		double centerX = end.x - dirX * m_arrowHeight;
		double centerY = end.y - dirY * m_arrowHeight;

		// Add tip point.
		m_arrow.addPoint(end.x, end.y);

		// Add left point.
		m_arrow.addPoint((int) (centerX - perpX), (int) (centerY - perpY));

		// Add back point if needed.
		if (type == RelationshipType.Aggregation || type == RelationshipType.Composition)
		{
			m_arrow.addPoint((int) (end.x - 2 * dirX * m_arrowHeight), (int) (end.y - 2 * dirY * m_arrowHeight));
		}

		if (type == RelationshipType.Dependency)
		{
			// m_arrow.addPoint(end.x, end.y);
			m_arrow.addPoint((int) (end.x - 0.5 * dirX * m_arrowHeight), (int) (end.y - 0.5 * dirY * m_arrowHeight));
		}

		// Add right point.
		m_arrow.addPoint((int) (centerX + perpX), (int) (centerY + perpY));

		// Copy points back to line array
		m_points[0] = start;
		m_points[m_points.length - 1] = end;
	}

	/**
	 * Create the line from a list of points.
	 * 
	 * @param pointList
	 */
	private void createPathFromPoints()
	{
		m_line.reset();
		m_line.moveTo(m_points[0].x, m_points[0].y);

		for (int i = 1; i < m_points.length; i++)
		{
			Point p = m_points[i];
			m_line.lineTo(p.x, p.y);
		}
	}

	private void addControlPoint(Point newControlPoint, int indexOfPointAfterNewPoint)
	{
		// TODO: convert m_points to a linked list to avoid the conversion.
		List<Point> pointList = new LinkedList<Point>(Arrays.asList(m_points));

		pointList.add(indexOfPointAfterNewPoint, newControlPoint);

		m_points = pointList.toArray(new Point[0]);
	}

	public void setSelected(boolean selected, Point clickPoint)
	{
		m_selected = selected;
		if (m_selected)
		{
			m_selectedControlPoint = getSelectedControlPoint(clickPoint);
		}
	}

	/**
	 * Check for a selected control point near to a click point.
	 * 
	 * @param clickPoint
	 * @return - the selected control point, or null if none is close to the click point.
	 */
	private Point getSelectedControlPoint(Point clickPoint)
	{
		int tol = m_selectionTolerence * m_selectionTolerence;

		for (Point checkPoint : m_points)
		{
			if (checkPoint.distanceSq(clickPoint) < tol)
			{
				return checkPoint;
			}
		}
		return null;
	}

	/**
	 * Check if the click point is near to the relationship line or end arrow.
	 * 
	 * @param clickPoint
	 * @return
	 */
	public boolean intersectsEpsilon(Point clickPoint)
	{
		Rectangle2D clickArea = new Rectangle2D.Float(clickPoint.x - m_clickDelta, clickPoint.y - m_clickDelta,
				m_clickDelta * 2, m_clickDelta * 2);

		return m_line.intersects(clickArea) || m_arrow.intersects(clickArea);
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

		// these two lines copy-pasted to make relationships redraw
		recalculateEndPoints();
		createPathFromPoints();
		createArrowPoints();

		// Draw a line through all the line points.
		// g2d.drawPolyline(m_xLinePoints, m_yLinePoints, m_numLinePoints);
		g2d.draw(m_line);

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

		// If the relationship is selected, draw a handle at each control node
		if (m_selected)
		{
			Color oldColor = g2d.getColor();

			int offset = m_cpDrawSize / 2;
			for (Point controlPoint : m_points)
			{
				boolean isControlPoint = (controlPoint == m_selectedControlPoint);
				g2d.setColor(isControlPoint ? Color.red : Color.green);

				g2d.fillRect(controlPoint.x - offset, controlPoint.y - offset, m_cpDrawSize, m_cpDrawSize);
			}

			g2d.setColor(oldColor);
		}
	}

}
