package umleditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import umleditor.Relationship.RelationshipType;

public class ClassDiagram implements MouseListener
{
	private LinkedList<ClassNode> listofNodes;
	private ClassNode selectedNode;
	private UMLEditor parentEditor;
	private JPanel view;
	private LinkedList<Relationship> m_relationships;

	public ClassDiagram(UMLEditor parent)
	{
		m_relationships = new LinkedList<Relationship>();

		parentEditor = parent;

		view = new JPanel();
		view.addMouseListener(this);
		view.setLayout(new MigLayout());
		parentEditor.add(view, BorderLayout.CENTER);

		listofNodes = new LinkedList<ClassNode>();
	}

	/**
	 * Create a new node and add it to the list of nodes. Also add it's
	 * {@link NodePanel} to the view.
	 * 
	 * @return the newly created node.
	 */
	private void createNode(Point addLocation)
	{
		ClassNode newClassNode = new ClassNode();
		NodePanel newNodePanel = new NodePanel(this, newClassNode);

		listofNodes.add(newClassNode);

		String positionSpecs = "pos " + addLocation.x + " " + addLocation.y;
		view.add(newNodePanel, positionSpecs);
		view.validate();
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
		view.remove(panelToRemove);
		view.paintImmediately(panelToRemove.getBounds());
		listofNodes.remove(selectedNode);
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
		RelationshipType[] possibleValues = RelationshipType.values();

		int selection = JOptionPane.showOptionDialog(parentEditor,
				"Choose a type of relationship", "Relationship Chooser",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				possibleValues, RelationshipType.Aggeration);

		RelationshipType selectedType = possibleValues[selection];

		System.out.println("Selection was " + selectedType);

		Relationship rel = new Relationship(firstNode, secondNode,
				Relationship.RelationshipType.Aggeration);

		firstNode.addRelationship(rel);
		secondNode.addRelationship(rel);
		m_relationships.add(rel);

		rel.draw(view.getGraphics());
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
	}

}
