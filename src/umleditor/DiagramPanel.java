package umleditor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

	private void setAntialiasing(Graphics g, boolean enable)
	{
		Graphics2D g2d = (Graphics2D) g;

		if (enable == true)
		{

			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

			// Need to enable this for each draw call?
			// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		else
		{
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	public void addRelationship(Relationship r)
	{
		m_relationships.add(r);
		this.add(r);
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

	@Override
	protected void paintComponent(Graphics g)
	{
		setAntialiasing(g, true);

		super.paintComponent(g);
		// for (Relationship rel : m_relationships)
		// {
		// rel.repaint();
		// }
	}

}
