package umleditor;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class NodeDragListener extends MouseInputAdapter
{
	private NodePanel panel;
	private Point previousMouseLoc;

	public NodeDragListener(NodePanel parentPanel)
	{
		panel = parentPanel;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// forward event to node panel
		panel.dispatchEvent(e);
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// don't reset the cursor if we're also dragging it -- do we want this?
		// if (!MouseEvent.getModifiersExText(e.getModifiersEx()).contains("Button1"))
		panel.setCursor(Cursor.getDefaultCursor());
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// forward event to node panel
		panel.dispatchEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// forward event to node panel
		panel.dispatchEvent(e);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		ClassDiagram containingDiagram = panel.getParentDiagram();
		Point delta = new Point(e.getX() - previousMouseLoc.x, e.getY() - previousMouseLoc.y);
		containingDiagram.movePanel(panel, delta);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		previousMouseLoc = e.getPoint();
	}
}
