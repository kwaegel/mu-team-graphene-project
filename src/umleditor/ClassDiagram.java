package umleditor;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import umleditor.Relationship.RelationshipType;

public class ClassDiagram implements MouseListener, KeyListener, FocusListener
{
	private LinkedList<ClassNode> listOfNodes;

	private ClassNode selectedNode;
	private UMLEditor parentEditor;
	private DiagramPanel view;

	private File fileSavedTo;
	private boolean changedSinceSaved;

	public ClassDiagram(UMLEditor parent, JScrollPane scrollPane)
	{
		parentEditor = parent;

		view = new DiagramPanel();
		view.addMouseListener(this);
		view.setFocusable(true);
		view.addKeyListener(this);
		view.setLayout(new MigLayout("", "", ""));
		view.addFocusListener(this);

		// Add the view to the scroll pane.
		scrollPane.setViewportView(view);

		changedSinceSaved = true;

		listOfNodes = new LinkedList<ClassNode>();
	}

	public void requestFocusOnView()
	{
		view.requestFocus();
	}

	/**
	 * Create a new node and initializes it.
	 */
	private void createNode(Point addLocation)
	{
		ClassNode newClassNode = new ClassNode();
		initNode(addLocation, newClassNode);
		this.markAsChanged();
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
		this.markAsChanged();
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

			this.markAsChanged();
		}
	}

	public void saveToFile(boolean chooseNewFile)
	{
		if (fileSavedTo == null || chooseNewFile)
		{
			JFileChooser fileSaveChooser = new JFileChooser();
			int userChoice = fileSaveChooser.showSaveDialog(parentEditor);
			if (userChoice == JFileChooser.APPROVE_OPTION)
			{
				fileSavedTo = fileSaveChooser.getSelectedFile();
			}
		}
		if(changedSinceSaved)
		{
			// code to save to file goes here
			changedSinceSaved = false;
			this.setTabTitle(fileSavedTo.getName());
		}
	}

	public void markAsChanged()
	{
		if (fileSavedTo != null)
		{
			changedSinceSaved = true;
			this.setTabTitle(fileSavedTo.getName() + "*");
		}
	}

	private void setTabTitle(String title)
	{
		JTabbedPane containingTabbedPane = (JTabbedPane) (view.getParent().getParent().getParent());
		containingTabbedPane.setTitleAt(containingTabbedPane.getSelectedIndex(), title);
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
			this.deleteSelectedNode();
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
		
		this.markAsChanged();
	}

	/**
	 * When a diagram becomes visible in the UML editor, ensure that the editor's delete button appropriately reflects
	 * the current diagram.
	 * 
	 * @param e
	 */
	@Override
	public void focusGained(FocusEvent e)
	{
		if (selectedNode != null)
		{
			parentEditor.setDeleteButtonState(true);
		}
		else
		{
			parentEditor.setDeleteButtonState(false);
		}
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		// do nothing
	}

}
