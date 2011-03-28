package umleditor;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import umleditor.Relationship.RelationshipType;

public class ClassDiagram implements KeyListener
{
	// Lists of objects in the diagram
	private List<ClassNode> listOfNodes;
	private List<Relationship> m_relationships;

	// Listeners for mouse events on the diagram
	private RelationshipDragListener m_relationshipDragController;

	private ISelectable currentlySelectedObject;

	private ClassNode selectedNode;
	private UMLEditor parentEditor;
	private JPanel view;

	public ClassDiagram(UMLEditor parent)
	{
		parentEditor = parent;

		view = new JPanel();
		// view.addMouseListener(this);
		view.setFocusable(true);
		view.requestFocus();
		view.addKeyListener(this);
		view.setLayout(new MigLayout("", "", ""));

		// Add the view to the scroll pane.
		JScrollPane scrollPane = parent.getScrollPane();
		scrollPane.setViewportView(view);

		// Create lists.
		listOfNodes = new LinkedList<ClassNode>();
		m_relationships = new LinkedList<Relationship>();

		// Create listeners on the view.
		m_relationshipDragController = new RelationshipDragListener(this, m_relationships);
		view.addMouseListener(m_relationshipDragController);
		view.addMouseMotionListener(m_relationshipDragController);
	}

	/**
	 * Create a new node and initializes it.
	 */
	private void createNode(Point addLocation)
	{
		ClassNode newClassNode = new ClassNode();
		initNode(addLocation, newClassNode);
	}

	/**
	 * Adds new node to the list of nodes. Also add it's {@link NodePanel} to the view.
	 * 
	 * @param addLocation
	 *            - location to add node
	 * @param newClassNode
	 *            - node to add
	 */
	private void initNode(Point addLocation, ClassNode newClassNode)
	{
		NodePanel newNodePanel = new NodePanel(this, newClassNode);

		listOfNodes.add(newClassNode);

		String positionSpecs = "pos " + addLocation.x + " " + addLocation.y;
		view.add(newNodePanel, positionSpecs);
		view.revalidate();
	}

	private void unselectCurrentObject()
	{
		if (currentlySelectedObject instanceof ClassNode)
		{
			// This is a temporary workaround. Eventually switch entirely over to using ISelectable.
			selectedNode.getNodePanel().makeUnselected();
			selectedNode = null;
		}
		else if (currentlySelectedObject instanceof Relationship)
		{
			((Relationship) currentlySelectedObject).setSelected(false, null);
		}

		currentlySelectedObject = null;

		parentEditor.setDeleteButtonState(false);
	}

	/**
	 * Notify the classDiagram of object selection. Sending null indicates that no object was selected.
	 * 
	 * @param selected
	 */
	public void setSelectedObject(ISelectable selected)
	{
		if (selected != currentlySelectedObject)
		{
			unselectCurrentObject();
		}
		currentlySelectedObject = selected;

		if (selected instanceof ClassNode)
		{
			selectedNode = (ClassNode) selected;
		}

		// Turn the delete button on if something non-null was selected.
		parentEditor.setDeleteButtonState(selected != null ? true : false);

		parentEditor.disableAddNewClassMode();
	}

	public void deleteSelectedObject()
	{
		if (currentlySelectedObject instanceof ClassNode)
		{
			NodePanel panelToRemove = selectedNode.getNodePanel();
			removeRelationships(selectedNode.getRelationships());
			view.remove(panelToRemove);

			// need this call so deleting nodes not at edges of screen works properly
			view.repaint();

			// need this call so deleting nodes at edges of screen works properly
			view.revalidate();

			listOfNodes.remove(selectedNode);
			selectedNode = null;
		}
		else if (currentlySelectedObject instanceof Relationship)
		{
			Relationship r = (Relationship) currentlySelectedObject;
			r.removeFromLinkedNodes();
			m_relationships.remove(r);
			view.remove(r);
		}

		currentlySelectedObject = null;

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
		// Do not add relationships between identical classes.
		if (firstNode != secondNode)
		{
			RelationshipType[] possibleValues = RelationshipType.values();

			int selection = JOptionPane.showOptionDialog(parentEditor, "Choose a type of relationship",
					"Relationship Chooser", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
					possibleValues, RelationshipType.Aggregation);

			if (selection >= 0)
			{
				RelationshipType selectedType = possibleValues[selection];

				Relationship rel = new Relationship(firstNode, secondNode, selectedType);

				firstNode.addRelationship(rel);
				secondNode.addRelationship(rel);

				// Add the relationship to the model list
				m_relationships.add(rel);

				// Add the relationship to the view.
				// Using the "external" constraint prevents MigLayout from changing the bounds of the relationship.
				view.add(rel, "external");

				rel.repaint();
			}
		}
	}

	private void removeRelationships(Collection<Relationship> relationshipList)
	{
		m_relationships.removeAll(relationshipList);
		for (Relationship r : relationshipList)
		{
			view.remove(r);
		}
	}

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
			this.unselectCurrentObject();
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_DELETE && currentlySelectedObject != null)
		{
			this.deleteSelectedObject();
		}
		else if (arg0.getKeyCode() == KeyEvent.VK_N)
		{
			Point mouseLocation = arg0.getComponent().getMousePosition();
			if (mouseLocation != null)
			{
				this.createNode(mouseLocation);
			}
		}
		else if (arg0.getKeyCode() == KeyEvent.VK_C && arg0.isControlDown() && selectedNode != null)
		{
			parentEditor.setCopyNode(new ClassNode(selectedNode));
		}
		else if (arg0.getKeyCode() == KeyEvent.VK_V && arg0.isControlDown())
		{
			ClassNode copy = parentEditor.getCopyNode();
			Point mouseLocation = arg0.getComponent().getMousePosition();
			if (copy != null && mouseLocation != null)
			{
				ClassNode nodeCopy = new ClassNode(copy);
				initNode(mouseLocation, nodeCopy);
			}
		}
		else if (arg0.getKeyCode() == KeyEvent.VK_X && arg0.isControlDown() && selectedNode != null)
		{
			parentEditor.setCopyNode(new ClassNode(selectedNode));
			this.deleteSelectedObject();
		}
		else if (arg0.getKeyCode() == KeyEvent.VK_E && selectedNode != null)
		{
			selectedNode.getNodePanel().displayEditPanel();
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
		this.setSelectedObject(nodePanelToMove.getClassNode());
		nodePanelToMove.makeSelected();

		view.remove(nodePanelToMove);

		int newPosX = Math.max(nodePanelToMove.getX() + movePoint.x, 0);
		int newPosY = Math.max(nodePanelToMove.getY() + movePoint.y, 0);
		String newPositionSpecs = "pos " + newPosX + " " + newPosY;
		view.add(nodePanelToMove, newPositionSpecs);

		// call to revalidate makes node redraw.
		view.revalidate();

		// call to repaint makes relationships redraw
		view.repaint();
	}

}
