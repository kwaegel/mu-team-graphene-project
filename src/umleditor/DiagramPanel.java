package umleditor;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

public class DiagramPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7972706309909597588L;

	private LinkedList<Relationship> m_relationships;
	private RelationshipDragListener m_dragController;

	public DiagramPanel()
	{
		super();
		m_relationships = new LinkedList<Relationship>();

		m_dragController = new RelationshipDragListener(m_relationships);
		this.addMouseListener(m_dragController);
		this.addMouseMotionListener(m_dragController);
	}

	public void addRelationship(Relationship r)
	{
		m_relationships.add(r);
		// "external" constraint prevents MigLayout from changing the bounds of the relationship.
		this.add(r, "external");
	}

	public void removeRelationship(Relationship r)
	{
		m_relationships.remove(r);
		this.remove(r);
	}

	public void removeRelationships(List<Relationship> relationshipList)
	{
		m_relationships.removeAll(relationshipList);
	}
}
