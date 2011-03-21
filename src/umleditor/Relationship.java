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
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
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

	private static final int m_cpDrawSize = 8;

	private static final int m_selectionTolerence = 10;

	private static final Color m_nodeColor = new Color(0, 100, 255);
	private static final Color m_selectedNodeColor = Color.red;

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
	private ClassNode m_firstNode;

	/**
	 * Contains a reference to the second ClassNode (relationship goes ‘to’ this one).
	 */
	private ClassNode m_secondNode;

	private Point m_firstNodeOffset, m_secondNodeOffset;

	/**
	 * The {@link java.awt.Polygon Polygon} used to draw the end arrow of the relationship.
	 */
	Polygon m_arrow;

	// If the relationship is selected or not.
	private boolean m_selected = false;

	// Which control node, if any, is selected.
	private int m_selectedControlPointIndex = -1;

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
		m_firstNode = first;
		m_secondNode = second;

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
		m_firstNode = first;
		m_secondNode = second;

		int m_numLinePoints = 2;
		m_points = new Point[m_numLinePoints];
		m_line = new GeneralPath(GeneralPath.WIND_NON_ZERO, m_numLinePoints);

		m_arrow = new Polygon();

		m_firstNodeOffset = firstOffset;
		m_secondNodeOffset = secondOffset;
		calculatePathControlPoints();

		addControlPoint(new Point(100, 100), 1);

		createPathFromPoints();

		createArrowPoints();
		setArrowFill();
	}

	private void calculatePathControlPoints()
	{
		Rectangle firstBounds = m_firstNode.getBounds();
		Rectangle secondBounds = m_secondNode.getBounds();

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

		if (m_firstNodeOffset == null || m_secondNodeOffset == null)
		{
			m_firstNodeOffset = new Point();
			m_secondNodeOffset = new Point();
			m_firstNodeOffset.x = m_points[0].x - firstBounds.x;
			m_firstNodeOffset.y = m_points[0].y - firstBounds.y;
			m_secondNodeOffset.x = m_points[m_points.length - 1].x - secondBounds.x;
			m_secondNodeOffset.y = m_points[m_points.length - 1].y - secondBounds.y;
		}
	}

	private void recalculateEndPoints()
	{
		Rectangle firstBounds = m_firstNode.getBounds();
		Rectangle secondBounds = m_secondNode.getBounds();

		m_points[0].x = firstBounds.x + m_firstNodeOffset.x;
		m_points[0].y = firstBounds.y + m_firstNodeOffset.y;

		m_points[m_points.length - 1].x = secondBounds.x + m_secondNodeOffset.x;
		m_points[m_points.length - 1].y = secondBounds.y + m_secondNodeOffset.y;
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

	/**
	 * Create arrow points for the end of the relationship line.
	 */
	private void createArrowPoints()
	{
		m_arrow = new Polygon(); // Create a blank polygon

		// Get start and end points from the last line segment.
		Point start = m_points[m_points.length - 2];
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
		m_points[m_points.length - 2] = start;
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

	public void addControlPoint(Point clickPoint)
	{
		int halfTol = m_selectionTolerence / 2;
		Rectangle2D boundingRect = new Rectangle2D.Float(clickPoint.x - halfTol, clickPoint.y - halfTol,
				m_selectionTolerence, m_selectionTolerence);

		// Find which line segment to add the new control point on
		for (int i = 0; i < m_points.length - 1; i++)
		{
			Point segStart = m_points[i];
			Point segEnd = m_points[i + 1];

			Line2D seg = new Line2D.Float(segStart, segEnd);

			if (boundingRect.intersectsLine(seg))
			{
				this.addControlPoint(clickPoint, i + 1);
				break;
			}
		}
	}

	private void addControlPoint(Point newControlPoint, int indexOfPointAfterNewPoint)
	{
		// TODO: convert m_points to a linked list to avoid the conversion.
		List<Point> pointList = new LinkedList<Point>(Arrays.asList(m_points));

		pointList.add(indexOfPointAfterNewPoint, newControlPoint);

		m_points = pointList.toArray(new Point[0]);
	}

	/**
	 * Handle dragging the relationship control points. If the first or last control point is being dragged, ensure it
	 * remains attached to the box.
	 * 
	 * @param e
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (m_selected && m_selectedControlPointIndex >= 0)
		{
			Point dragPoint = e.getPoint();

			if (m_selectedControlPointIndex == 0)
			{
				Rectangle bounds = m_firstNode.getBounds();
				m_points[m_selectedControlPointIndex] = getClosestPointOnRectangle(dragPoint, bounds);

				m_firstNodeOffset.x = m_points[m_selectedControlPointIndex].x - bounds.x;
				m_firstNodeOffset.y = m_points[m_selectedControlPointIndex].y - bounds.y;
			}
			else if (m_selectedControlPointIndex == m_points.length - 1)
			{
				Rectangle bounds = m_secondNode.getBounds();
				m_points[m_selectedControlPointIndex] = getClosestPointOnRectangle(dragPoint, bounds);

				m_secondNodeOffset.x = m_points[m_selectedControlPointIndex].x - bounds.x;
				m_secondNodeOffset.y = m_points[m_selectedControlPointIndex].y - bounds.y;
			}
			else
			{
				m_points[m_selectedControlPointIndex] = dragPoint;
			}

			// Rebuild the path.
			createPathFromPoints();

			// If we are dragging nodes at the end of the line, also rebuild the arrows.
			if (m_selectedControlPointIndex <= 1 || m_selectedControlPointIndex >= m_points.length - 2)
			{
				createArrowPoints();
			}
		}
	}

	/**
	 * Return the point on the rectangle that is closest to the click point.
	 * 
	 * @param clickPoint
	 *            - the click point
	 * @param rect
	 *            - the {@link java.awt.Rectangle rectangle} to check.
	 * @return - the point on the rectangle nearest to the click point.
	 */
	private Point getClosestPointOnRectangle(Point clickPoint, Rectangle rect)
	{
		Point nearestPoint = new Point(clickPoint);

		int maxX = rect.x + rect.width;
		int maxY = rect.y + rect.height;

		// Snap one of the coords to the side for points inside the rectangle
		if (rect.contains(clickPoint))
		{
			float centerDeltaX = (float) rect.getCenterX() - clickPoint.x;
			float centerDeltaY = (float) rect.getCenterY() - clickPoint.y;

			if (Math.abs(centerDeltaX) > Math.abs(centerDeltaY))
			{
				// Snap left if the delta is positive, else right.
				nearestPoint.x = (centerDeltaX > 0) ? rect.x : maxX;
			}
			else
			{
				// Snap up if the delta is positive, else down.
				nearestPoint.y = (centerDeltaY > 0) ? rect.y : maxY;
			}
		}
		else
		{
			// Check x coords for points outside the rectangle
			if (clickPoint.x < rect.x)
			{
				nearestPoint.x = rect.x;
			}
			else if (clickPoint.x > maxX)
			{
				nearestPoint.x = maxX;
			}

			// Check y coords for points outside the rectangle
			if (clickPoint.y < rect.y)
			{
				nearestPoint.y = rect.y;
			}
			else if (clickPoint.y > maxY)
			{
				nearestPoint.y = maxY;
			}
		}

		return nearestPoint;
	}

	public void setSelected(boolean selected, Point clickPoint)
	{
		m_selected = selected;
		if (m_selected)
		{
			m_selectedControlPointIndex = getSelectedControlIndex(clickPoint);
		}
	}

	/**
	 * Check for a selected control point near to a click point.
	 * 
	 * @param clickPoint
	 * @return - the selected control point, or null if none is close to the click point.
	 */
	private int getSelectedControlIndex(Point clickPoint)
	{
		int tol = m_selectionTolerence * m_selectionTolerence;

		for (int i = 0; i < m_points.length; i++)
		{
			if (clickPoint.distanceSq(m_points[i]) < tol)
			{
				return i;
			}
		}

		return -1;
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
			for (int i = 0; i < m_points.length; i++)
			{
				boolean isControlPoint = (i == m_selectedControlPointIndex);
				g2d.setColor(isControlPoint ? m_selectedNodeColor : m_nodeColor);

				g2d.fillRect(m_points[i].x - offset, m_points[i].y - offset, m_cpDrawSize, m_cpDrawSize);
			}

			g2d.setColor(oldColor);
		}
	}

}
