package umleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class ClassDiagram implements MouseListener
{

	LinkedList<ClassNode> listofNodes;
	ClassNode selectedNode;
	UMLEditor parentEditor;
	JPanel view;

	public ClassDiagram(UMLEditor parent)
	{
		parentEditor = parent;

		view = new JPanel();
		view.setLayout(null);
		view.addMouseListener(this);
		view.setBorder(BorderFactory.createLineBorder(Color.red, 2));
		parentEditor.add(view, BorderLayout.CENTER);
	}

	public JPanel getViewPanel()
	{
		return view;
	}

	/**
	 * Create a new node and add it to the list of nodes. Also add it's {@link NodePanel} to the view.
	 * 
	 * @return the newly created node.
	 */
	private ClassNode createNode(Point creationPoint)
	{
		ClassNode newNode = new ClassNode(this, creationPoint);
		view.revalidate(); // Check for any new components.
		view.repaint(); // Repaint the diagram.

		return newNode;
	}

	private void unselectNode()
	{
		if (selectedNode != null)
		{
			selectedNode.getNodePanel().makeUnselected();
			selectedNode = null;
		}
	}

	public void setSelectedNode(ClassNode node)
	{
		selectedNode = node;
		parentEditor.setDeleteButtonState(true);
	}

	public void deleteSelectedNode()
	{
		NodePanel panelToRemove = selectedNode.getNodePanel();
		view.remove(panelToRemove);
		listofNodes.remove(selectedNode);
		selectedNode = null;
		parentEditor.setDeleteButtonState(false);
	}

	public void addRelationship(NodePanel secondNode)
	{
		if (selectedNode != null)
		{
			// add relationship
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		System.out.println("Mouse clicked at " + e.getPoint());

		// mouse clicked in the view, not on any node
		// check to see if adding a class is enabled
		if (parentEditor.isAddNewClassModeEnabled())
		{
			// add new class mode enabled, so add a new class
			createNode(e.getPoint());

			if (!e.isShiftDown())
			{
				parentEditor.disableAddNewClassMode();
			}
		}
		else
		{
			this.unselectNode();
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
	}

}
