package umleditor;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import umleditor.Relationship.RelationshipType;

import com.thoughtworks.xstream.XStream;

/**
 * A ClassDiagram contains all information associated with a UML diagram in the {@link UMLEditor}. It maintains the
 * models for Relationships and Classes in the diagram. It is responsible for creating nodes and connecting them with
 * relationships. It contains a reference to the view in which the classes and relationships are displayed. It keeps
 * track of the currently selected object in the diagram and deletes it when appropriate. It manages cutting and copying
 * classes from, and pasting classes to itself. It knows what file it was saved to, and keeps track of whether or not it
 * was saved or changed since it was saved.
 */
public class ClassDiagram implements KeyListener, FocusListener, Printable
{
	// Lists of objects in the diagram
	private List<ClassNode> listOfNodes;
	private List<RelationshipModel> listOfRelationships;

	// Listeners for mouse events on the diagram
	private transient RelationshipDragListener m_relationshipDragController;

	// transient fields that will not be encoded when diagram is saved
	private transient ISelectable currentlySelectedObject;

	private transient UMLEditor parentEditor;
	private transient JLayeredPane view;

	private transient File fileSavedTo;
	private transient boolean changedSinceSaved;

	/**
	 * Constructs a new ClassDiagram with the given parent, whose view is inside the given scroll pane.
	 * 
	 * @param parent
	 *            - UML Editor of which this ClassDIagram is part
	 * @param scrollPane
	 */
	public ClassDiagram(UMLEditor parent, JScrollPane scrollPane)
	{
		parentEditor = parent;

		setUpView(scrollPane);

		changedSinceSaved = true;

		// Create listeners on the view.
		m_relationshipDragController = new RelationshipDragListener(this);

		listOfNodes = new LinkedList<ClassNode>();
		listOfRelationships = new LinkedList<RelationshipModel>();
	}

	/**
	 * Completes setup of ClassDiagram after it has been initialized from a file.
	 * 
	 * @param parent
	 *            - {@link UMLEditor} to which this ClassDiagram belongs
	 * @param scrollPane
	 *            - scroll pane to which it's view should be attached
	 * @param fileLoadedFrom
	 *            - file this diagram was loaded from
	 */
	public void initAfterLoadFromFile(UMLEditor parent, JScrollPane scrollPane, File fileLoadedFrom)
	{
		parentEditor = parent;

		setUpView(scrollPane);

		fileSavedTo = fileLoadedFrom;
		changedSinceSaved = false;

		m_relationshipDragController = new RelationshipDragListener(this);

		// Create views for models.
		for (ClassNode node : listOfNodes)
		{
			Point addPoint = node.getLocation();
			initNodePanel(addPoint, node);
		}
		for (RelationshipModel rm : listOfRelationships)
		{
			Relationship r = new Relationship(rm);
			view.add(r, "external");
			r.addMouseListener(m_relationshipDragController);
			r.addMouseMotionListener(m_relationshipDragController);
		}
	}

	/**
	 * Creates and sets state of this ClassDiagram's view, and attached it to the given scroll pane.
	 * 
	 * @param scrollPane
	 */
	private void setUpView(JScrollPane scrollPane)
	{
		view = new JLayeredPane();
		view.setFocusable(true);
		view.addKeyListener(this);
		view.setLayout(new MigLayout("", "", ""));
		view.addFocusListener(this);
		view.addMouseListener(new MouseClickListener());

		// Add the view to the scroll pane.
		scrollPane.setViewportView(view);
	}

	/**
	 * Ensures the view gets focus when a new ClassDiagram has been created. Called whenever a new ClassDiagram is
	 * created in the UML Editor.
	 */
	public void requestFocusOnView()
	{
		view.requestFocus();
	}

	/**
	 * Creates and initializes a new {@link ClassNode} with NodePanel
	 * 
	 * @param addLocation
	 *            - place new class will be added
	 */
	private void createNode(Point addLocation)
	{
		ClassNode newClassNode = new ClassNode();
		initNodePanel(addLocation, newClassNode);
		attachNodeToDiagram(newClassNode);
	}

	/**
	 * Initializes the new node's {@link NodePanel} and adds it to the view.
	 * 
	 * @param addLocation
	 *            - location to add node
	 * @param newClassNode
	 *            - node to add
	 */
	private void initNodePanel(Point addLocation, ClassNode newClassNode)
	{
		NodePanel newNodePanel = new NodePanel(this, newClassNode);
		newNodePanel.attachToView(view);
		newNodePanel.resetBounds(addLocation);
	}

	/**
	 * Adds the class node to this diagram. Called whenever a new node is constructed.
	 * 
	 * @param newClassNode
	 */
	private void attachNodeToDiagram(ClassNode newClassNode)
	{
		listOfNodes.add(newClassNode);
		this.setSelectedObject(newClassNode);
		this.markAsChanged();
	}

	/**
	 * Deselect the currently selected object and disable the delete button.
	 */
	private void unselectCurrentObject()
	{
		if (currentlySelectedObject != null)
		{
			currentlySelectedObject.setSelected(false);
			currentlySelectedObject = null;
			parentEditor.setDeleteButtonState(false);
		}
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
			selected.setSelected(true);
		}
		currentlySelectedObject = selected;

		// Turn the delete button on if something non-null was selected.
		parentEditor.setDeleteButtonState(selected != null ? true : false);

		parentEditor.disableAddNewClassMode();
	}

	/**
	 * Deletes the selected object. The selected object can either be a {@link ClassNode} or Relationship.
	 */
	public void deleteSelectedObject()
	{
		if (currentlySelectedObject instanceof ClassNode)
		{
			ClassNode node = (ClassNode) currentlySelectedObject;
			removeRelationships(node.getRelationships());

			// Remove the view part
			NodePanel panelToRemove = node.getNodePanel();
			view.remove(panelToRemove);

			// need this call so deleting nodes not at edges of screen works properly
			view.repaint();

			// need this call so deleting nodes at edges of screen works properly
			view.revalidate();

			listOfNodes.remove(node);
		}
		else if (currentlySelectedObject instanceof Relationship)
		{
			Relationship r = (Relationship) currentlySelectedObject;
			if (r.isControlPointSelected())
			{
				r.removeSelectedControlPoint();
			}
			else
			{
				// Remove the entire relationship.
				r.removeFromLinkedNodes();
				listOfRelationships.remove(r.getModel());
				view.remove(r);
			}
			view.repaint(r.getBounds());
		}

		currentlySelectedObject = null;

		parentEditor.setDeleteButtonState(false);
		this.markAsChanged();
	}

	/**
	 * Workaround for bug with improper MouseReleased event handling in Swing.
	 * 
	 * @param evt
	 *            - the mouse event
	 * @return the component under this event
	 */
	public Component getComponentUnder(MouseEvent evt)
	{
		Point p = ((Component) evt.getSource()).getLocation();
		evt.translatePoint((int) p.getX(), (int) p.getY());
		return view.getComponentAt(evt.getX(), evt.getY());
	}

	/**
	 * Adds the relationship to the ClassDiagram's currently selected object, if that object is a {@link ClassNode}
	 * 
	 * @param secondNode
	 *            - the node that relationship will end on.
	 */
	public void addRelationship(ClassNode secondNode)
	{
		if (currentlySelectedObject instanceof ClassNode)
		{
			addRelationship((ClassNode) currentlySelectedObject, secondNode);
		}
	}

	/**
	 * Adds a relationship between the two nodes. Private internal method in ClassDiagram.
	 * 
	 * @param firstNode
	 *            - the starting node
	 * @param secondNode
	 *            - the ending node
	 */
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
				listOfRelationships.add(rel.getModel());

				// Add the relationship to the view.
				// Using the "external" constraint prevents MigLayout from changing the bounds of the relationship.
				view.add(rel, "external");
				rel.addMouseListener(m_relationshipDragController);
				rel.addMouseMotionListener(m_relationshipDragController);

				rel.repaint();
			}

			this.markAsChanged();
		}
	}

	/**
	 * Removes a list of relationships from the diagram. Called from a {@link ClassNode} whenever it gets deleted.
	 * 
	 * @param relationshipList
	 */
	public void removeRelationships(List<RelationshipModel> relationships)
	{
		// Clone list to prevent concurrent modification of the same list
		List<RelationshipModel> relationshipList = new ArrayList<RelationshipModel>(relationships);

		listOfRelationships.removeAll(relationshipList);
		for (RelationshipModel rm : relationshipList)
		{
			// Remove the model
			rm.removeFromLinkedNodes();

			// Remove the view.
			Relationship r = rm.getRelationship();
			r.removeMouseListener(m_relationshipDragController);
			view.remove(r);

		}
	}

	/**
	 * Saves this {@link ClassDiagram} to a file. If chooseNewFile is <code>true</code>, or no file name has been
	 * associated with this diagram, will display a JFileChoser to get user to select a file.
	 * 
	 * @param chooseNewFile
	 *            - whether or not to find a new file before saving.
	 */
	public void saveToFile(boolean chooseNewFile)
	{
		if (fileSavedTo == null || chooseNewFile)
		{
			JFileChooser fileSaveChooser = new JFileChooser();
			fileSaveChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileSaveChooser.setMultiSelectionEnabled(false);
			fileSaveChooser.setAcceptAllFileFilterUsed(false);
			fileSaveChooser.setFileFilter(new FileExtensionFilter());
			int userChoice = fileSaveChooser.showSaveDialog(parentEditor);
			if (userChoice == JFileChooser.APPROVE_OPTION)
			{
				fileSavedTo = fileSaveChooser.getSelectedFile();
				// add appropriate extension, if there the path does not have one already
				String absoluteFilePath = fileSavedTo.getAbsolutePath();
				String acceptedExtension = FileExtensionFilter.ACCEPTED_FILE_EXTENSION;
				if (!absoluteFilePath.endsWith(acceptedExtension))
				{
					absoluteFilePath += acceptedExtension;
					fileSavedTo = new File(absoluteFilePath);
				}
			}
		}
		if (fileSavedTo != null && (changedSinceSaved || chooseNewFile))
		{
			FileWriter fileOutStream;
			BufferedWriter buffOutStream;
			try
			{
				XStream xmlStream = FileUtils.getXmlReaderWriter();

				fileOutStream = new FileWriter(fileSavedTo);
				buffOutStream = new BufferedWriter(fileOutStream);

				xmlStream.toXML(this, buffOutStream);

				buffOutStream.close();
			}
			catch (IOException e)
			{

			}

			changedSinceSaved = false;
			this.setTabTitle(fileSavedTo.getName());
		}
	}

	/**
	 * Record that this Diagram has been changed since it was last saved. Useful for knowing if need to save. Marks the
	 * title of the diagram in the tabbed pane to indicate to the user that it has been modified.
	 */
	public void markAsChanged()
	{
		if (fileSavedTo != null && !changedSinceSaved)
		{
			changedSinceSaved = true;
			this.setTabTitle(fileSavedTo.getName() + "*");
		}
	}

	/**
	 * Returns whether or not the diagram has unsaved changes. Empty, unmodified diagrams do not have unsaved changes.
	 * 
	 * @return - <code>true</code> if the diagram has been modified and is not saved, otherwise <code>false</code>
	 */
	public boolean hasUnsavedChanges()
	{
		boolean diagramBlank = (fileSavedTo == null && listOfNodes.isEmpty());
		return (!diagramBlank && changedSinceSaved);
	}

	/**
	 * Sets the title on the tab that contains this diagram. Counts on the fact that it will be called only when this
	 * diagram is the currently displayed diagram.
	 * 
	 * @param title
	 *            - new title for the tab.
	 */
	private void setTabTitle(String title)
	{
		JTabbedPane containingTabbedPane = (JTabbedPane) (view.getParent().getParent().getParent());
		TabTitleComponent tabComponent = (TabTitleComponent) containingTabbedPane
				.getTabComponentAt(containingTabbedPane.getSelectedIndex());
		tabComponent.setTitle(title);
	}

	/**
	 * @return the name of this class diagram, or "Untitled document" if the diagarm has not been saved yet.
	 */
	public String getName()
	{
		if (fileSavedTo != null)
		{
			return fileSavedTo.getName();
		}
		else
		{
			return "Untitled document";
		}
	}

	/**
	 * Save a copy of the currently selected node (if there is one) to the parent UMLEditor. Since the copy constructor
	 * is used, only the name, attributes and methods will be copied.
	 */
	public void copyNode()
	{
		if (currentlySelectedObject instanceof ClassNode)
		{
			parentEditor.setCopyNode((ClassNode) currentlySelectedObject);
		}
	}

	/**
	 * Copies currently selected node and then deletes it.
	 */
	public void cutNode()
	{
		if (currentlySelectedObject instanceof ClassNode)
		{
			parentEditor.setCopyNode((ClassNode) currentlySelectedObject);
			this.deleteSelectedObject();
		}
	}

	/**
	 * Gets the most recently copied node from the UMLEditor and adds it to this class diagram. If the user is
	 * positioning the mouse in the diagram where they want the class added, attach it there. If they are accessing the
	 * paste option from a menu, put the pasted class in the (more or less) center of the screen. To get exactly the
	 * center would have added complications that were not justified for something the user will most likely want to
	 * move anyway. Newly pasted nodes will be selected.
	 */
	public void pasteNode()
	{
		ClassNode copy = parentEditor.getCopyNode();
		if (copy != null)
		{
			Point pastePosition;
			if (view.hasFocus())
			{
				pastePosition = view.getMousePosition();
			}
			else
			{
				pastePosition = new Point((parentEditor.getWidth() - 100) / 2, (parentEditor.getHeight() - 140) / 2);
			}
			ClassNode nodeCopy = new ClassNode(copy);
			initNodePanel(pastePosition, nodeCopy);
			attachNodeToDiagram(nodeCopy);
		}
	}

	/**
	 * Handle diagram-specific key events
	 */
	@Override
	public void keyPressed(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.VK_DELETE && currentlySelectedObject != null)
		{
			this.deleteSelectedObject();
		}
		else if (event.getKeyCode() == KeyEvent.VK_N && !event.isControlDown())
		{
			parentEditor.toggleAddNewClassMode();
		}
		else if (event.getKeyCode() == KeyEvent.VK_E && currentlySelectedObject instanceof ClassNode)
		{
			ClassNode node = (ClassNode) currentlySelectedObject;
			node.getNodePanel().displayEditPanel();
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

	/**
	 * Moves a NodePanel in the UML Diagram. Called when the NodeDragListener gets a dragged event for a node panel.
	 * 
	 * @param nodePanelToMove
	 *            - panel whose position in the diagram should be changed
	 * @param movePoint
	 *            - place to move the panel's upper left-hand corner to
	 */
	public void movePanel(NodePanel nodePanelToMove, Point movePoint)
	{
		this.setSelectedObject(nodePanelToMove.getClassNode());

		int newPosX = Math.max(nodePanelToMove.getX() + movePoint.x, 0);
		int newPosY = Math.max(nodePanelToMove.getY() + movePoint.y, 0);
		nodePanelToMove.resetBounds(new Point(newPosX, newPosY));

		// call to revalidate makes node redraw.
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
		if (currentlySelectedObject != null)
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

	/**
	 * This class listens for clicks to an empty part of the class diagram and creates a new {@link ClassNode} if the
	 * new node button is enabled.
	 */
	private class MouseClickListener extends MouseAdapter
	{

		@Override
		public void mouseReleased(MouseEvent arg0)
		{
			// mouse clicked in the view, not on any node
			// check to see if adding a class is enabled
			if (parentEditor.isAddNewClassModeEnabled())
			{
				// add new class mode enabled, so add a new class
				createNode(arg0.getPoint());
				if (arg0.isShiftDown())
				{
					parentEditor.enableAddNewClassMode();
				}
			}
			else
			{
				unselectCurrentObject();
			}
		}
	}

	@Override
	public int print(Graphics arg0, PageFormat arg1, int arg2) throws PrinterException
	{
		Graphics2D g2d = (Graphics2D) arg0;
		g2d.translate(arg1.getImageableX(), arg1.getImageableY());

		int numPagesWide = view.getWidth() / (int) arg1.getImageableWidth();
		if (arg2 > numPagesWide)
			return NO_SUCH_PAGE;
		view.printAll(arg0);

		return PAGE_EXISTS;
	}
}
