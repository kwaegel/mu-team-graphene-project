package umleditor;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

/**
 * Handles dragging class nodes within the diagram. Sets the cursor appropriately when the mouse enters or exits the
 * drag area on the node. Ensures that other behaviors work the same when performed on the drag area as on the rest of
 * the node by forwarding events to the NodePanel as appropriate.
 */
public class NodeDragListener extends MouseInputAdapter
{
	private NodePanel panel;
	private Point previousMouseLoc;

	/**
	 * Creates a new NodeDragListener that handles dragging for the specified NodePanel
	 * 
	 * @param parentPanel
	 */
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
		// set the cursor to indicate the node can be dragged
		panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// don't reset the cursor if we're also dragging it -- do we want this?
		// if (!MouseEvent.getModifiersExText(e.getModifiersEx()).contains("Button1"))
		// reset the cursor when the mouse exits the drag area.
		panel.setCursor(Cursor.getDefaultCursor());
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// forward event to node panel
		if (panel.isSelected())
		{
			// ensure that dragging an already selected nodes works as expected
			// click count of 20 is indicator to node panel handling event that it came
			// from the drag label
			// want to 'reverse' behavior - if not ctrl down - remain selected when drag
			// if ctrl down - deselect
			int modifierBindings = (e.isControlDown()) ? 0 : MouseEvent.CTRL_DOWN_MASK;
			MouseEvent e2 = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(),
					modifierBindings, e.getX(), e.getY(), 20, e.isPopupTrigger());
			panel.dispatchEvent(e2);
		}
		else
		{
			panel.dispatchEvent(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		/*
		 * This prevents bug where releasing a nodePanel after dragging creates a relationship with any node in the
		 * upper left-hand corner, if one happens to be there. this bug occurred because the getComponentUnder method in
		 * class diagram (itself a workaround for a swing bug) depends on the location of the released-event's source,
		 * but source was different if released event came from drag label (here) or the NodePanel itself. The following
		 * line ensures the panel is always the source while preserving all other info about the event
		 */
		MouseEvent e2 = new MouseEvent(panel, e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(),
				e.getClickCount(), e.isPopupTrigger());
		// forward event to node panel
		panel.dispatchEvent(e2);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		// tell the node's parent diagram to move the node to the new location
		ClassDiagram containingDiagram = panel.getParentDiagram();
		Point delta = new Point(e.getX() - previousMouseLoc.x, e.getY() - previousMouseLoc.y);
		containingDiagram.setSelectedObject(panel.getClassNode(), false);
		containingDiagram.moveSelectedNodes(delta);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// update previous location (so when begin to drag,
		// will know where mouse was when started)
		previousMouseLoc = e.getPoint();
	}
}
