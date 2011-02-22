package umleditor;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
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
		view.addMouseListener(this);
		parentEditor.add(view, BorderLayout.CENTER);
	}
	
	private void createNode()
	{
		NodePanel newNodePanel = new NodePanel();
		view.add(newNodePanel); // set up mig layout on view, then get appropriate parameters
		
		// ClassNode newClassNode = new ClassNode(newNodePanel);
		// add newClassNode to list of nodes
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// mouse clicked in the view, not on any node
		// check to see if adding a class is enabled
		if(parentEditor.isAddNewClassModeEnabled())
		{
			// add new class mode enabled, so add a new class
			System.out.println("adding new node...");
			createNode();
			if(!arg0.isShiftDown())
				parentEditor.disableAddNewClassMode();
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
