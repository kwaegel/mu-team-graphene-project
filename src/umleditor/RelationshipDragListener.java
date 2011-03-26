package umleditor;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class RelationshipDragListener extends MouseAdapter
{
	private List<Relationship> m_relationships;

	private Relationship m_lastSelectedRelationship; // Cache the last selected relationship.

	public RelationshipDragListener(List<Relationship> relationships)
	{
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

		Relationship selectedReleationship = null;
		for (Relationship r : m_relationships)
		{
			boolean isSelected = r.contains(clickPoint);

			if (isSelected)
			{
				selectedReleationship = r;
				break;
			}
		}

		return selectedReleationship;
	}

	/* Mouse adapter methods */

	@Override
	public void mouseClicked(MouseEvent e)
	{
		Point clickPoint = e.getPoint();
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
		}

		if (selected != null)
		{
			selected.setSelected(true, clickPoint);
			selected.repaint();
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
			// m_lastSelectedRelationship.repaint();
		}
	}

}
