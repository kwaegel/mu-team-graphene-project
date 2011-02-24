package umleditor;

import java.awt.Graphics;
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
	}

	public void addRelationship(Relationship r)
	{
		m_relationships.add(r);
	}

	public void removeRelationship(Relationship r)
	{
		m_relationships.remove(r);
	}

	public void removeRelationships(List<Relationship> relationshipList)
	{
		m_relationships.removeAll(relationshipList);
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		for (Relationship rel : m_relationships)
		{
			rel.draw(g);
		}
	}
}
