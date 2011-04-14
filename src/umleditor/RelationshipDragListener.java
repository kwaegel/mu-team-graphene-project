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

	RelationshipDragListener(ClassDiagram diagram)
	{
		m_diagram = diagram;
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
			m_diagram.markAsChanged();
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		Relationship source = (Relationship) e.getSource();

		// Convert click point to diagram coordinates.
		Point clickPoint = e.getPoint();
		clickPoint = SwingUtilities.convertPoint(source, clickPoint, source.getParent());

		m_diagram.setSelectedObject(source, true);
		source.setSelected(true, clickPoint);
		source.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Relationship source = (Relationship) e.getSource();
		source.mouseDragged(e);
		m_diagram.markAsChanged();
	}
}
