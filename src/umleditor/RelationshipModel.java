package umleditor;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import umleditor.Relationship.RelationshipType;

public class RelationshipModel
{

	/**
	 * The type of relationship to draw.
	 */
	private RelationshipType m_type;

	/**
	 * Contains a list of class nodes to draw between.
	 */
	private List<Point> m_points;

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
	 * TODO: This is a hack. Find a way for the model not to need a reference to the controller/view.
	 */
	private transient Relationship m_relationship;

	public RelationshipModel(ClassNode first, Point firstOffset, ClassNode second, Point secondOffset,
			RelationshipType type)
	{
		this(first, second, type);
		m_firstNodeOffset = firstOffset;
		m_secondNodeOffset = secondOffset;
	}

	public RelationshipModel(ClassNode node1, ClassNode node2, RelationshipType type)
	{
		m_firstNode = node1;
		m_secondNode = node2;
		m_type = type;

		// Create the point list.
		int initialNumPoints = 2;
		m_points = new ArrayList<Point>(initialNumPoints);
		m_points.add(new Point());
		m_points.add(new Point());

		// Calculate some default control points.
		calculateDefaultPathControlPoints();

		// Calculate the default offsets.
		m_firstNodeOffset = calculateOffset(m_points.get(0), m_firstNode.getPanelBounds());
		m_secondNodeOffset = calculateOffset(m_points.get(m_points.size() - 1), m_secondNode.getPanelBounds());
	}

	/** Private Helper Methods **/

	/**
	 * Calculate some default path control points. The points chosen are the centers of the nearest two sides of the
	 * {@link ClassNode ClassNodes}.
	 */
	private void calculateDefaultPathControlPoints()
	{
		Rectangle firstBounds = m_firstNode.getPanelBounds();
		Rectangle secondBounds = m_secondNode.getPanelBounds();

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
					m_points.set(0, startPoint);
					m_points.set(m_points.size() - 1, endPoint);
					minDistence = dist;
				}
			}
		}
	}

	/** Public Methods **/

	/**
	 * Recalculate the end points of the relationship path, based on the previously stored offsets. This allows the
	 * relationship ends to maintain the same relative position to the classNodes as they are moved. TODO: make private
	 * and use events to recalculate.
	 */
	public void recalculateEndPoints()
	{
		Rectangle firstBounds = m_firstNode.getPanelBounds();
		Rectangle secondBounds = m_secondNode.getPanelBounds();

		m_points.get(0).x = firstBounds.x + m_firstNodeOffset.x;
		m_points.get(0).y = firstBounds.y + m_firstNodeOffset.y;

		int end = m_points.size() - 1;
		m_points.get(end).x = secondBounds.x + m_secondNodeOffset.x;
		m_points.get(end).y = secondBounds.y + m_secondNodeOffset.y;
	}

	/**
	 * Return a {@link java.util.Collection collection} containing the {@link ClassNode ClassNodes} this relationship is
	 * linked to.
	 */
	public Collection<ClassNode> getClassNodes()
	{
		return Arrays.asList(m_firstNode, m_secondNode);
	}

	/**
	 * Tell this relationship to remove itself from the {@link ClassNode nodes} it links together.
	 */
	public void removeFromLinkedNodes()
	{
		// TODO: Implement this.
		m_firstNode.removeRelationship(this);
		m_secondNode.removeRelationship(this);
	}

	/** Getters and Setters **/

	public Relationship getRelationship()
	{
		return m_relationship;
	}

	public void setRelationship(Relationship r)
	{
		m_relationship = r;
	}

	public RelationshipType getType()
	{
		return m_type;
	}

	public void setType(RelationshipType type)
	{
		this.m_type = type;
	}

	public List<Point> getPoints()
	{
		return m_points;
	}

	public void setPoints(List<Point> points)
	{
		this.m_points = points;
	}

	public ClassNode getFirstNode()
	{
		return m_firstNode;
	}

	public void setFirstNode(ClassNode firstNode)
	{
		this.m_firstNode = firstNode;
	}

	public ClassNode getSecondNode()
	{
		return m_secondNode;
	}

	public void setSecondNode(ClassNode secondNode)
	{
		this.m_secondNode = secondNode;
	}

	public Point getFirstNodeOffset()
	{
		return m_firstNodeOffset;
	}

	public void setFirstNodeOffset(Point firstNodeOffset)
	{
		this.m_firstNodeOffset = firstNodeOffset;
	}

	public Point getSecondNodeOffset()
	{
		return m_secondNodeOffset;
	}

	public void setSecondNodeOffset(Point secondNodeOffset)
	{
		this.m_secondNodeOffset = secondNodeOffset;
	}

	/** Static methods **/
	/**
	 * Calculate the offset between the given point and the origin of the given rectangle.
	 * 
	 * @param p
	 * @param r
	 * @return
	 */
	private static Point calculateOffset(Point p, Rectangle r)
	{
		return new Point(p.x - r.x, p.y - r.y);
	}

}
