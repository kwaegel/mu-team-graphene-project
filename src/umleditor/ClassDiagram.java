package umleditor;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ClassDiagram implements MouseListener {

	private LinkedList<ClassNode> listofNodes;
	private ClassNode selectedNode;
	private UMLEditor parentEditor;
	private JPanel view;

	public ClassDiagram(UMLEditor parent) {
		parentEditor = parent;

		view = new JPanel();
		view.addMouseListener(this);
		view.setLayout(new MigLayout());
		parentEditor.add(view, BorderLayout.CENTER);

		listofNodes = new LinkedList<ClassNode>();
	}
	
	/**
	 * Create a new node and add it to the list of nodes. Also add it's {@link NodePanel} to the view.
	 * 
	 * @return the newly created node.
	 */
	private void createNode(Point addLocation) {
		ClassNode newClassNode = new ClassNode();
		NodePanel newNodePanel = new NodePanel(this, newClassNode);

		listofNodes.add(newClassNode);

		String positionSpecs = "pos " + addLocation.x + " " + addLocation.y;
		view.add(newNodePanel, positionSpecs);
		view.validate();
	}

	private void unselectCurrentNode() {
		if (selectedNode != null) {
			selectedNode.getNodePanel().makeUnselected();
			selectedNode = null;
		}
		parentEditor.setDeleteButtonState(false);
	}

	public void setSelectedNode(ClassNode node) {
		unselectCurrentNode();
		selectedNode = node;
		parentEditor.setDeleteButtonState(true);
		parentEditor.disableAddNewClassMode();
	}

	public void deleteSelectedNode() {
		NodePanel panelToRemove = selectedNode.getNodePanel();
		view.remove(panelToRemove);
		view.paintImmediately(panelToRemove.getBounds());
		listofNodes.remove(selectedNode);
		selectedNode = null;
		parentEditor.setDeleteButtonState(false);
	}

	public void addRelationship(NodePanel secondNode) {
		if (selectedNode != null) {
			// add relationship
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// mouse clicked in the view, not on any node
		// check to see if adding a class is enabled
		if (parentEditor.isAddNewClassModeEnabled()) {
			// add new class mode enabled, so add a new class
			this.createNode(arg0.getPoint());
			if (!arg0.isShiftDown())
				parentEditor.disableAddNewClassMode();
		} else {
			this.unselectCurrentNode();
		}
	}
	
	public LinkedList<ClassNode> getNodes()
	{
		return listofNodes;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

}
