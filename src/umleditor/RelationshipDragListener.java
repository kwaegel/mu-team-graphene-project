package umleditor;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

/**
 * Handles selection testing for {@link Relationship relationships}.
 */
public class RelationshipDragListener extends MouseAdapter
{
	private ClassDiagram m_diagram;
	
	private static RelationshipDragListener m_instance = null;

	/**
	 * Cache of the last selected relationship for faster intersection testing.
	 */
	private Relationship m_lastSelectedRelationship;

	private RelationshipDragListener(ClassDiagram diagram)
	{
		m_diagram = diagram;
	}
	
	public static RelationshipDragListener getRelationshipListener(ClassDiagram diagram)
	{
		
		if(m_instance == null)
		{
			m_instance = new RelationshipDragListener(diagram);
		}
		return (m_instance);
	}

	/* Mouse adapter methods */

	@Override
	public void mouseClicked(MouseEvent e)
	{
		Relationship source = (Relationship) e.getSource();

		// Convert click point to diagram coordinates.
		Point clickPoint = e.getPoint();
		clickPoint = SwingUtilities.convertPoint(source, clickPoint, source.getParent());

		// Add control point on double click.
		if (e.getClickCount() == 2)
		{
			source.addControlPoint(clickPoint);
			source.repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		Relationship source = (Relationship) e.getSource();

		// Convert click point to diagram coordinates.
		Point clickPoint = e.getPoint();
		clickPoint = SwingUtilities.convertPoint(source, clickPoint, source.getParent());

		if (m_lastSelectedRelationship != null && m_lastSelectedRelationship != source)
		{
			m_lastSelectedRelationship.setSelected(false, null);
			m_lastSelectedRelationship.repaint();
		}

		source.setSelected(true, clickPoint);
		source.repaint();
		m_diagram.setSelectedObject(source);

		m_lastSelectedRelationship = source;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Relationship source = (Relationship) e.getSource();
		source.mouseDragged(e);
	}
}
