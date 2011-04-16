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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Relationship defines a connection between two {@link ClassNode}s. Relationships will be maintained by the individual
 * classes they connect. They will be able to draw themselves using a reference to the ClassDiagram’s View’s graphics,
 * and will do so when there are changes to the view panel.
 */
public class Relationship extends JComponent implements ISelectable
{
	/**
	 * Generated UID for Relationship.
	 */
	private static final long serialVersionUID = -1173663450541981195L;

	/* Static constants. */
	private static final float m_arrowHeight = 12.0f;
	private static final float m_arrowWidth = 15.0f;

	private static final int m_clickDelta = 5;

	private static final int m_cpDrawSize = 8;

	private static final int m_selectionTolerence = 10;

	private static final Color m_nodeColor = new Color(0, 100, 255);
	private static final Color m_selectedNodeColor = Color.red;

	// Event publisher
	private EventPublisher m_eventPublisher;

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
		 * A dependency is represented by an arrow with a dotted line.
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

	private RelationshipModel m_model;

	private enum FillType
	{
		None, Outline, Solid
	}

	/**
	 * Specify how the polygon for the end arrow should be drawn.
	 */
	private FillType m_endFill;

	private transient Path2D.Float m_line;

	/**
	 * The {@link java.awt.Polygon Polygon} used to draw the end arrow of the relationship.
	 */
	private transient Polygon m_arrow;

	// If the relationship is selected or not.
	private transient boolean m_selected = false;

	// Which control node, if any, is selected.
	private transient int m_selectedControlPointIndex = -1;

	/**
	 * Initializer block.
	 */
	{
		m_line = new Path2D.Float();
		m_arrow = new Polygon();
		addMouseListener(RelationshipDragListener.getInstance());
		addMouseMotionListener(RelationshipDragListener.getInstance());
	}

	/***** Constructors *****/

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
		m_model = new RelationshipModel(first, firstOffset, second, secondOffset, type);

		rebuildGraphics();
	}

	/**
	 * Creates a relationship between two classes without offset information.
	 * 
	 * @param first
	 * @param second
	 * @param type
	 */
	public Relationship(ClassNode first, ClassNode second, RelationshipType type)
	{
		m_model = new RelationshipModel(first, second, type);

		rebuildGraphics();
	}

	// TODO: comments
	public Relationship(RelationshipModel model)
	{
		m_model = model;
		rebuildGraphics();
	}

	/***** Methods *****/

	/**
	 * Opens an edit dialog for this relationship.
	 */
	public void openEditDialog(Point dialogLocation)
	{
		boolean modelChanged = RelationshipEditDialog.showEditDialog(getModel(), (JComponent) this.getParent(),
				dialogLocation);

		if (modelChanged)
		{
			System.out.println("Model changed!");
		}
	}

	/**
	 * Rebuild the needed drawing components when the model updated.
	 */
	public void rebuildGraphics()
	{
		m_model.setRelationship(this);

		createPathFromPoints();
		createArrow();
		setArrowFill();

		recalculateBounds();

		fireChangeEvent();
	}

	/**
	 * Get the model.
	 */
	public RelationshipModel getModel()
	{
		return m_model;
	}

	/**
	 * Set the model.
	 * 
	 * @param model
	 *            - the new model to use.
	 */
	public void setModel(RelationshipModel model)
	{
		m_model = model;
		rebuildGraphics();
	}

	/**
	 * Tell this relationship to remove itself from the {@link ClassNode nodes} it links together.
	 */
	public void removeFromLinkedNodes()
	{
		m_model.removeFromLinkedNodes();
	}

	/**
	 * Calculate the bounding box that can contain this Relationship.
	 */
	private void recalculateBounds()
	{
		List<Point> points = m_model.getPoints();
		Rectangle newBounds = new Rectangle(points.get(0));
		int offset = m_cpDrawSize / 2;
		for (Point p : points)
		{
			// Add rectangles instead of points to ensure the control nodes draw correctly.
			newBounds.add(new Rectangle(p.x - offset, p.y - offset, m_cpDrawSize, m_cpDrawSize));
		}
		newBounds.add(m_arrow.getBounds());

		// Increase the width and height to ensure the bottom and right of the arrows are included.
		newBounds.height += 1;
		newBounds.width += 1;

		this.setBounds(newBounds);
	}

	/**
	 * Determine type of arrow fill to use.
	 */
	private void setArrowFill()
	{
		RelationshipType type = m_model.getType();

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
	private void createArrow()
	{
		m_arrow.reset();

		List<Point> points = m_model.getPoints();

		// Get start and end points from the last line segment.
		Point start = points.get(points.size() - 2);
		Point end = points.get(points.size() - 1);

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
		RelationshipType type = m_model.getType();
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
	}

	/**
	 * Create the line from a list of points.
	 * 
	 * @param pointList
	 */
	private void createPathFromPoints()
	{
		List<Point> points = m_model.getPoints();

		// Reset the path.
		m_line.reset();
		m_line.moveTo(points.get(0).x, points.get(0).y);

		for (int i = 1; i < points.size(); i++)
		{
			Point p = points.get(i);
			m_line.lineTo(p.x, p.y);
		}
	}

	/**
	 * Add a control point at the given click point.
	 * 
	 * @param clickPoint
	 */
	public void addControlPoint(Point clickPoint)
	{
		int halfTol = m_selectionTolerence / 2;
		Rectangle2D boundingRect = new Rectangle2D.Float(clickPoint.x - halfTol, clickPoint.y - halfTol,
				m_selectionTolerence, m_selectionTolerence);

		// Find which line segment to add the new control point on
		List<Point> points = m_model.getPoints();
		for (int i = 0; i < points.size() - 1; i++)
		{
			Point segStart = points.get(i);
			Point segEnd = points.get(i + 1);

			Line2D seg = new Line2D.Float(segStart, segEnd);

			if (boundingRect.intersectsLine(seg))
			{
				// Insert the new point after the current index.
				points.add(i + 1, clickPoint);
				break;
			}
		}

		fireChangeEvent();
	}

	/**
	 * Remove the currently selected control point.
	 */
	public void removeSelectedControlPoint()
	{
		List<Point> points = m_model.getPoints();

		// Don't allow the end points to be removed.
		if (m_selectedControlPointIndex > 0 && m_selectedControlPointIndex < points.size() - 1)
		{
			points.remove(m_selectedControlPointIndex);

			this.createPathFromPoints();
			this.createArrow();

			m_selectedControlPointIndex = -1;
		}
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
			// TODO: change this to work in local coordinates instead of doing a conversion.
			dragPoint = SwingUtilities.convertPoint(this, dragPoint, getParent());

			List<Point> m_points = m_model.getPoints();
			Point m_firstNodeOffset = m_model.getFirstNodeOffset();
			Point m_secondNodeOffset = m_model.getSecondNodeOffset();

			if (m_selectedControlPointIndex == 0)
			{
				Rectangle bounds = m_model.getFirstNode().getPanelBounds();
				Point closestPoint = getClosestPointOnRectangle(dragPoint, bounds);
				m_points.set(m_selectedControlPointIndex, closestPoint);

				m_firstNodeOffset.x = m_points.get(0).x - bounds.x;
				m_firstNodeOffset.y = m_points.get(0).y - bounds.y;
			}
			else if (m_selectedControlPointIndex == m_points.size() - 1)
			{
				Rectangle bounds = m_model.getSecondNode().getPanelBounds();
				Point closestPoint = getClosestPointOnRectangle(dragPoint, bounds);
				m_points.set(m_selectedControlPointIndex, closestPoint);

				int end = m_points.size() - 1;
				m_secondNodeOffset.x = m_points.get(end).x - bounds.x;
				m_secondNodeOffset.y = m_points.get(end).y - bounds.y;
			}
			else
			{
				m_points.set(m_selectedControlPointIndex, dragPoint);
			}

			// Rebuild the path.
			createPathFromPoints();

			// If we are dragging nodes at the end of the line, also rebuild the arrows.
			if (m_selectedControlPointIndex <= 1 || m_selectedControlPointIndex >= m_points.size() - 2)
			{
				createArrow();
			}

			// Recalculate the bounding box.
			recalculateBounds();

			// Repaint the relationship.
			repaint();

			fireChangeEvent();
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
	private static Point getClosestPointOnRectangle(Point clickPoint, Rectangle rect)
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

	/**
	 * Mark this relationship as selected. When selected, control nodes will be drawn to allow manipulation of the
	 * relationship path. No control nodes will be selected.
	 */
	@Override
	public void setSelected(boolean selected)
	{
		setSelected(selected, null);
	}

	/**
	 * Mark this relationship as selected. When selected, control nodes will be drawn to allow manipulation of the
	 * relationship path. The control node under the click point (if any) will also be selected.
	 * 
	 * @param selected
	 * @param clickPoint
	 */
	public void setSelected(boolean selected, Point clickPoint)
	{
		m_selected = selected;
		m_selectedControlPointIndex = -1;
		if (m_selected && clickPoint != null)
		{
			m_selectedControlPointIndex = getSelectedControlIndex(clickPoint);
		}
		repaint();

		if (selected)
		{
			fireSelectedEvent();
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
		List<Point> points = m_model.getPoints();
		for (int i = 0; i < points.size(); i++)
		{
			if (clickPoint.distanceSq(points.get(i)) < tol)
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * @return true if the relationship has a selected control point.
	 */
	public boolean isControlPointSelected()
	{
		return m_selected && (m_selectedControlPointIndex >= 0);
	}

	/**
	 * This tests if the mouse has clicked on a relationship line or arrow.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(int x, int y)
	{
		// Note: the provided point is relative to the bounding box of the component, not the parent window.
		// Convert coordinates (local-to-parent) for testing. This is a workaround until all data is stored in local
		// coordinates.
		Point loc = getLocation();
		x += loc.x;
		y += loc.y;

		Rectangle2D clickArea = new Rectangle2D.Float(x - m_clickDelta, y - m_clickDelta, m_clickDelta * 2,
				m_clickDelta * 2);

		boolean inBounds = getBounds().contains(x, y);
		boolean intersectsLine = false;
		boolean intersectsArrow = m_arrow.intersects(clickArea);

		/*
		 * Need to check line intersections by hand, as the intersects() method only checks shape containment, not line
		 * intersection. TODO: Find a more elegant way to do this.
		 */
		PathIterator lineItr = m_line.getPathIterator(null);
		float[] nextPoints = new float[6];
		float[] lastPoints = new float[6];
		while (!lineItr.isDone() && !intersectsLine)
		{
			int segmentType = lineItr.currentSegment(nextPoints);
			if (segmentType == PathIterator.SEG_LINETO)
			{
				intersectsLine = clickArea.intersectsLine(lastPoints[0], lastPoints[1], nextPoints[0], nextPoints[1]);
			}
			System.arraycopy(nextPoints, 0, lastPoints, 0, 6);
			lineItr.next();
		}

		return inBounds && (intersectsLine || intersectsArrow);
	}

	/**
	 * Draw a line representing the relationship in the ClassDiagram view panel. Nature of line drawn depends on
	 * relationship type. Placement of line determined by location of NodePanels associated with first and second nodes.
	 * 
	 * {@inheritDoc}
	 * 
	 * @param g
	 *            {@inheritDoc}
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		// Convert from local coordinates to parent coordinates.
		Point loc = this.getLocation();
		g2d.translate(-loc.x, -loc.y);

		// Enable anti-aliasing mode.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Save the previous stroke pattern;
		Stroke oldStroke = g2d.getStroke();

		RelationshipType type = m_model.getType();

		// Dependencies need to be drawn with a dashed line.
		if (type == RelationshipType.Dependency)
		{
			g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 8.0f,
					8.0f }, 5.0f));
		}

		// these two lines copy-pasted to make relationships redraw
		// These should be moved to the mouseDraged methods for better drawing performance.
		m_model.recalculateEndPoints();
		createPathFromPoints();
		createArrow();
		recalculateBounds();

		// Draw a line through all the line points.
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
			List<Point> points = m_model.getPoints();
			for (int i = 0; i < points.size(); i++)
			{
				boolean isControlPoint = (i == m_selectedControlPointIndex);
				g2d.setColor(isControlPoint ? m_selectedNodeColor : m_nodeColor);

				g2d.fillRect(points.get(i).x - offset, points.get(i).y - offset, m_cpDrawSize, m_cpDrawSize);
			}

			g2d.setColor(oldColor);
		}

		// Set the old transform back
		g2d.translate(loc.x, loc.y);
	}

	/**
	 * Set the {@link EventPublisher} to use.
	 * 
	 * @param eventPublisher
	 */

	public void setEventPublisher(EventPublisher eventPublisher)
	{
		m_eventPublisher = eventPublisher;
	}

	private void fireChangeEvent()
	{
		if (m_eventPublisher != null)
		{
			m_eventPublisher.fireChangeEvent(this);
		}
	}

	private void fireSelectedEvent()
	{
		if (m_eventPublisher != null)
		{
			m_eventPublisher.fireSelectedEvent(this);
		}
	}
}
