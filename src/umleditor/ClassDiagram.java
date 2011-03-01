package umleditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import umleditor.Relationship.RelationshipType;

public class ClassDiagram implements MouseListener
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
		view.setLayout(new MigLayout("", "", ""));

		// Add the view to a scroll pane.
		JScrollPane scrollPane = parent.getScrollPane();
		scrollPane.setViewportView(view);

		// Add the scroll pane to the frame
		parentEditor.add(scrollPane, BorderLayout.CENTER);

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
	}

	public void deleteSelectedNode()
	{
		NodePanel panelToRemove = selectedNode.getNodePanel();
		view.removeRelationships(selectedNode.getRelationships());
		view.remove(panelToRemove);
		view.repaint();
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

	public DiagramPanel getView()
	{
		return (view);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// mouse clicked in the view, not on any node
		// check to see if adding a class is enabled
		if (parentEditor.isAddNewClassModeEnabled())
		{
			// add new class mode enabled, so add a new class
			this.createNode(e.getPoint());
			if (!e.isShiftDown())
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
	public void mouseEntered(MouseEvent arg0)
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
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

}
