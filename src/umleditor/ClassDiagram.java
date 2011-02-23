package umleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class ClassDiagram implements MouseListener {

	LinkedList<ClassNode> listofNodes;
	ClassNode selectedNode;
	UMLEditor parentEditor;
	JPanel view;
	
	public ClassDiagram(UMLEditor parent)
	{
		parentEditor = parent;
		
		view = new JPanel();
		view.setLayout(new FlowLayout());
		view.addMouseListener(this);
		view.setBorder(BorderFactory.createLineBorder(Color.red, 2));
		parentEditor.add(view, BorderLayout.CENTER);
	}
	
	public JPanel getViewPanel()
	{
		return view;
	}
	
	private ClassNode createNode()
	{
		ClassNode newNode = new ClassNode(this);
		view.add(newNode.getNodePanel());
		return newNode;
	}
	
	private void unselectNode()
	{
		if(selectedNode != null)
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
	public void mouseClicked(MouseEvent arg0) {
		// mouse clicked in the view, not on any node
		// check to see if adding a class is enabled
		if(parentEditor.isAddNewClassModeEnabled())
		{
			// add new class mode enabled, so add a new class
			System.out.print("adding new node: ");
			ClassNode newNode = createNode();
			System.out.println("\""+newNode.getName()+"\"");
			
			if(!arg0.isShiftDown())
				parentEditor.disableAddNewClassMode();
		}
		else
		{
			this.unselectNode();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) { }

	@Override
	public void mouseExited(MouseEvent arg0) { }

	@Override
	public void mousePressed(MouseEvent arg0) {	}

	@Override
	public void mouseReleased(MouseEvent arg0) { }

}
