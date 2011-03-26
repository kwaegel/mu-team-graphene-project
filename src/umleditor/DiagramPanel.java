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

	public DiagramPanel()
	{
		super();
		m_relationships = new LinkedList<Relationship>();

		RelationshipDragListener dragController = new RelationshipDragListener(m_relationships);
		this.addMouseListener(dragController);
		this.addMouseMotionListener(dragController);
	}

	public void addRelationship(Relationship r)
	{
		// m_relationships.add(r);
		this.add(r);
	}

	public void removeRelationship(Relationship r)
	{
		// m_relationships.remove(r);
		this.remove(r);
	}

	public void removeRelationships(List<Relationship> relationshipList)
	{
		m_relationships.removeAll(relationshipList);
	}

}
