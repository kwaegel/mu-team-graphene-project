package umleditor;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import umleditor.Relationship.RelationshipType;

public class ClassDiagram implements MouseListener, KeyListener
{
	private LinkedList<ClassNode> listOfNodes;

	private ClassNode selectedNode;
	private UMLEditor parentEditor;
	private DiagramPanel view;

	public ClassDiagram(UMLEditor parent)
	{
		parentEditor = parent;

		view = new DiagramPanel();
		view.addMouseListener(this);
		view.setFocusable(true);
		view.requestFocus();
		view.addKeyListener(this);
		view.setLayout(new MigLayout("", "", ""));

		// Add the view to the scroll pane.
		JScrollPane scrollPane = parent.getScrollPane();
		scrollPane.setViewportView(view);

		listOfNodes = new LinkedList<ClassNode>();
	}

	/**
	 * Create a new node and add it to the list of nodes. Also add it's {@link NodePanel} to the view.
	 * 
	 * @return the newly created node.
	 */
	private void createNode(Point addLocation)
	{
		ClassNode newClassNode = new ClassNode();
		NodePanel newNodePanel = new NodePanel(this, newClassNode);

		listOfNodes.add(newClassNode);

		String positionSpecs = "pos " + addLocation.x + " " + addLocation.y;
		view.add(newNodePanel, positionSpecs);
		view.revalidate();
	}

	private void unselectCurrentNode()
	{
		if (selectedNode != null)
		{
			selectedNode.getNodePanel().makeUnselected();
			selectedNode = null;
		}
		parentEditor.setDeleteButtonState(false);
	}

	public void setSelectedNode(ClassNode node)
	{
		unselectCurrentNode();
		selectedNode = node;
		parentEditor.setDeleteButtonState(true);
		parentEditor.disableAddNewClassMode();
		// node.makeSelected here?
	}

	public void deleteSelectedNode()
	{
		NodePanel panelToRemove = selectedNode.getNodePanel();
		view.removeRelationships(selectedNode.getRelationships());
		view.remove(panelToRemove);
		// need this call so deleting nodes not at edges of screen works properly
		view.repaint();
		// need this call so deleting nodes at edges of screen works properly
		view.revalidate();
		listOfNodes.remove(selectedNode);
		selectedNode = null;
		parentEditor.setDeleteButtonState(false);
	}

	public Component getComponentUnder(MouseEvent evt)
	{
		Point p = ((Component) evt.getSource()).getLocation();
		evt.translatePoint((int) p.getX(), (int) p.getY());
		return view.getComponentAt(evt.getX(), evt.getY());
	}

	public void addRelationship(ClassNode secondNode)
	{
		if (selectedNode != null)
		{
			addRelationship(selectedNode, secondNode);
		}
	}

	private void addRelationship(ClassNode firstNode, ClassNode secondNode)
	{
		// Do not add relationships between idential classes.
		if (firstNode == secondNode)
		{
			return;
		}

		RelationshipType[] possibleValues = RelationshipType.values();

		int selection = JOptionPane.showOptionDialog(parentEditor, "Choose a type of relationship",
				"Relationship Chooser", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, possibleValues,
				RelationshipType.Aggregation);

		if (selection >= 0)
		{
			RelationshipType selectedType = possibleValues[selection];

			Relationship rel = new Relationship(firstNode, secondNode, selectedType);

			firstNode.addRelationship(rel);
			secondNode.addRelationship(rel);
			view.addRelationship(rel);

			rel.draw(view.getGraphics());
		}
	}


	@Override
	public void mouseClicked(MouseEvent e)
	{
		// do nothing
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		// do nothing
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		// do nothing
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		// do nothing
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		// mouse clicked in the view, not on any node
		// check to see if adding a class is enabled
		if (parentEditor.isAddNewClassModeEnabled())
		{
			// add new class mode enabled, so add a new class
			this.createNode(arg0.getPoint());
			if (!arg0.isShiftDown())
			{
				parentEditor.disableAddNewClassMode();
			}
		}
		else
		{
			this.unselectCurrentNode();
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_DELETE && selectedNode != null)
		{
			this.deleteSelectedNode();
		}
		else if (arg0.getKeyCode() == KeyEvent.VK_N)
		{
			Point mouseLocation = arg0.getComponent().getMousePosition();
			if (mouseLocation != null)
				this.createNode(mouseLocation);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		// do nothing
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		// do nothing
	}

	public void movePanel(NodePanel nodePanelToMove, Point movePoint)
	{
		this.setSelectedNode(nodePanelToMove.getClassNode());
		nodePanelToMove.makeSelected();

		view.remove(nodePanelToMove);

		int newPosX = Math.max(nodePanelToMove.getX() + movePoint.x, 0);
		int newPosY = Math.max(nodePanelToMove.getY() + movePoint.y, 0);
		String newPositionSpecs = "pos " + newPosX + " " + newPosY;
		view.add(nodePanelToMove, newPositionSpecs);

		// call to revalidate makes node redraw
		view.revalidate();

		// call to repaint makes relationships redraw
		view.repaint();
	}

}
