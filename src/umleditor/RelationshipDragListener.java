package umleditor;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Handles selection testing for {@link Relationship relationships}.
 */
public class RelationshipDragListener extends MouseAdapter
{
	private ClassDiagram m_diagram;
	private List<Relationship> m_relationships;

	/**
	 * Cache of the last selected relationship for faster intersection testing.
	 */
	private Relationship m_lastSelectedRelationship;

	public RelationshipDragListener(ClassDiagram diagram, List<Relationship> relationships)
	{
		m_diagram = diagram;
		m_relationships = relationships;
	}

	/**
	 * Find the relationship that has been clicked on.
	 * 
	 * @param clickPoint
	 *            - the point clicked in the diagram panel.
	 * @return - the relationship under the click point. Null if none exists.
	 */
	private Relationship getSelectedRelationship(Point clickPoint)
	{
		// Short circuit test for fast repeated testing (such as during dragging).
		if (m_lastSelectedRelationship != null && m_lastSelectedRelationship.contains(clickPoint))
		{
			return m_lastSelectedRelationship;
		}

		for (Relationship r : m_relationships)
		{
			if (r.contains(clickPoint))
			{
				return r;
			}
		}

		return null;
	}

	/* Mouse adapter methods */

	@Override
	public void mouseClicked(MouseEvent e)
	{
		Point clickPoint = e.getPoint();
		// Add control node on double click.
		if (m_lastSelectedRelationship != null && e.getClickCount() == 2
				&& m_lastSelectedRelationship.contains(clickPoint))
		{
			m_lastSelectedRelationship.addControlPoint(clickPoint);
			m_lastSelectedRelationship.repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		Point clickPoint = e.getPoint();

		Relationship selected = getSelectedRelationship(clickPoint);

		if (m_lastSelectedRelationship != null && m_lastSelectedRelationship != selected)
		{
			m_lastSelectedRelationship.setSelected(false, null);
			m_lastSelectedRelationship.repaint();
		}

		if (selected != null)
		{
			selected.setSelected(true, clickPoint);
			selected.repaint();

			// Tell the ClassDiagram that the relationship is selected.
			m_diagram.setSelectedObject(selected);
		}

		// Store the selected relationship
		m_lastSelectedRelationship = selected;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		// Drag any selected relationships.
		if (m_lastSelectedRelationship != null)
		{
			m_lastSelectedRelationship.mouseDragged(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// If there is not a selected relationship, pass the event on to the ClassDiagram so it can check about adding a
		// new ClassNode.
		if (m_lastSelectedRelationship == null)
		{
			m_diagram.mouseReleased(e);
		}
	}

}
