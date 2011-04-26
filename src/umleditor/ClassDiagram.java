package umleditor;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
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

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
public class ClassDiagram implements KeyListener, FocusListener, Printable, ChangeListener, SelectionListener
{
	// Lists of objects in the diagram
	/**
	 * A list containing all the {@link ClassNode class nodes} contained in this diagram.
	 */
	private List<ClassNode> listOfNodes = new LinkedList<ClassNode>();

	/**
	 * A list containing all the relationship {@link RelationshipModel models} contained in this diagram.
	 */
	private List<RelationshipModel> listOfRelationships = new LinkedList<RelationshipModel>();

	// Publisher to handle change events
	private transient EventPublisher m_changePublisher = new EventPublisher();

	// transient fields that will not be encoded when diagram is saved
	private transient List<ISelectable> currentlySelectedObjects = new LinkedList<ISelectable>();

	private transient UMLEditor parentEditor;
	private transient JLayeredPane view;

	private transient File saveFile;
	private transient boolean hasUnsavedChanges;

	private transient DiagramBackgroundPopup m_diagramPopup;

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

		hasUnsavedChanges = true;

		listOfNodes = new LinkedList<ClassNode>();
		listOfRelationships = new LinkedList<RelationshipModel>();

		m_changePublisher.add(ChangeListener.class, this);
		m_changePublisher.add(SelectionListener.class, this);

		m_diagramPopup = new DiagramBackgroundPopup();
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

		saveFile = fileLoadedFrom;
		hasUnsavedChanges = false;

		// Create views for models.
		for (ClassNode node : listOfNodes)
		{
			Point addPoint = node.getLocation();
			initNodePanel(addPoint, node);
		}
		for (RelationshipModel rm : listOfRelationships)
		{
			Relationship r = new Relationship(rm);
			r.setEventPublisher(m_changePublisher);
			r.addMouseListener(new PopupListener());
			view.add(r, "external");
		}

		m_diagramPopup = new DiagramBackgroundPopup();
	}

	/**
	 * Used by xStream during deserialization.
	 * 
	 * @return
	 */
	private Object readResolve()
	{
		currentlySelectedObjects = new LinkedList<ISelectable>();
		m_changePublisher = new EventPublisher();
		m_changePublisher.add(ChangeListener.class, this);
		m_changePublisher.add(SelectionListener.class, this);
		return this;
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
		newNodePanel.addMouseListener(new PopupListener());
		newNodePanel.attachToView(view);
		newNodePanel.resetBounds(addLocation);
		view.revalidate();
	}

	/**
	 * Adds the class node to this diagram. Called whenever a new node is constructed.
	 * 
	 * @param newClassNode
	 */
	private void attachNodeToDiagram(ClassNode newClassNode)
	{
		listOfNodes.add(newClassNode);
		setSelectedObject(newClassNode, true);
		markAsChanged();
	}

	/**
	 * Deselect the currently selected objects and disable the delete button.
	 */
	private void unselectCurrentObjects(ISelectable leaveSelected)
	{
		int index = 0;
		while (currentlySelectedObjects.size() > index)
		{
			ISelectable currentlySelectedObject = currentlySelectedObjects.get(index);
			if (currentlySelectedObject != leaveSelected)
			{
				currentlySelectedObject.setSelected(false);
				currentlySelectedObjects.remove(currentlySelectedObject);
			}
			else
			{
				++index;
			}
		}

		if (currentlySelectedObjects.isEmpty())
		{
			parentEditor.reflectUnselectedState();
		}
	}

	/**
	 * 
	 * @param toDeselect
	 */
	public void unselectObject(ISelectable toDeselect)
	{
		if (currentlySelectedObjects.contains(toDeselect))
		{
			currentlySelectedObjects.remove(toDeselect);
			toDeselect.setSelected(false);
			if (currentlySelectedObjects.isEmpty())
			{
				parentEditor.reflectUnselectedState();
			}
		}
	}

	/**
	 * Notify the classDiagram of object selection. Sending null indicates that no object was selected.
	 * 
	 * @param selected
	 *            - item to select
	 * @param deselectOthers
	 *            - whether or not other items should be unselected when this one becomes selected. Will be true if this
	 *            method is being called because the user Ctrl-clicked on a class in the diagram.
	 */
	public void setSelectedObject(ISelectable selected, boolean deselectOthers)
	{
		if (deselectOthers || relationshipSelected())
		{
			unselectCurrentObjects(selected);
		}

		if (!currentlySelectedObjects.contains(selected))
		{
			currentlySelectedObjects.add(selected);
		}

		// Prevent infinite loop
		if (!(selected instanceof Relationship))
		{
			// Select objects that do not handle self selection.
			selected.setSelected(true);
		}

		// Turn the delete button on if something non-null was selected,
		// and turn on copy/cut mode it the selected item was a ClassNode
		if (selected != null)
		{
			parentEditor.enableDeleteButtonState();
			parentEditor.setCopyCutState(deselectOthers && selected instanceof ClassNode);
		}

		parentEditor.disableAddNewClassMode();
	}

	/**
	 * @return Whether or not a relationship is selected. If a relationship is selected, it will be the only element in
	 *         the list of currently selected objects.
	 */
	private boolean relationshipSelected()
	{
		return (!currentlySelectedObjects.isEmpty() && currentlySelectedObjects.get(0) instanceof Relationship);
	}

	/**
	 * Deletes the selected objects. The selected object can either be a {@link ClassNode} or Relationship.
	 */
	public void deleteSelectedObjects()
	{
		if (!currentlySelectedObjects.isEmpty())
		{
			ISelectable firstSelectedObject = currentlySelectedObjects.get(0);
			if (firstSelectedObject instanceof ClassNode)
			{
				// multiple nodes may be selected
				for (int i = 0; i < currentlySelectedObjects.size(); ++i)
				{
					ClassNode node = (ClassNode) currentlySelectedObjects.get(i);
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
				currentlySelectedObjects.clear();
				parentEditor.reflectUnselectedState();
			}
			else if (firstSelectedObject instanceof Relationship)
			{
				Relationship r = (Relationship) firstSelectedObject;
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
					// remove the selected object
					currentlySelectedObjects.clear();
					parentEditor.reflectUnselectedState();
					view.repaint(r.getBounds());
				}

			}

			markAsChanged();
		}
	}

	/**
	 * Workaround for bug with undesired MouseReleased event handling in Swing.
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
		if (currentlySelectedObjects.size() == 1)
		{
			ISelectable currentlySelectedObject = currentlySelectedObjects.get(0);
			if (currentlySelectedObject instanceof ClassNode)
			{
				addRelationship((ClassNode) currentlySelectedObject, secondNode);
			}
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
				rel.addMouseListener(new PopupListener());

				firstNode.addRelationship(rel);
				secondNode.addRelationship(rel);

				// Add the relationship to the model list
				listOfRelationships.add(rel.getModel());

				// Add the relationship to the view.
				// Using the "external" constraint prevents MigLayout from changing the bounds of the relationship.
				view.add(rel, "external");
				rel.setEventPublisher(m_changePublisher);

				rel.repaint();

				markAsChanged();
			}
		}
	}

	/**
	 * Removes a list of relationships from the diagram. Called from a {@link ClassNode} whenever it gets deleted.
	 * 
	 * @param relationships
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
			view.remove(r);

		}
	}

	/**
	 * Saves this {@link ClassDiagram} to a file. If chooseNewFile is <code>true</code>, or no file name has been
	 * associated with this diagram, will display a JFileChoser to get user to select a file.
	 * 
	 * @param chooseNewFile
	 *            - whether or not to find a new file before saving.
	 * @return <code>true</code> if the diagram was saved, <code>false</code> if the save was canceled.
	 */
	public boolean saveToFile(boolean chooseNewFile)
	{
		boolean saveCanceled = false;
		if (saveFile == null || chooseNewFile)
		{
			JFileChooser fileSaveChooser = new JFileChooser();
			fileSaveChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileSaveChooser.setMultiSelectionEnabled(false);
			fileSaveChooser.setAcceptAllFileFilterUsed(false);
			fileSaveChooser.setFileFilter(new FileExtensionFilter());
			int userChoice = fileSaveChooser.showSaveDialog(parentEditor);
			if (userChoice == JFileChooser.APPROVE_OPTION)
			{
				File userSelectedFile = fileSaveChooser.getSelectedFile();
				saveFile = FileUtils.attachAppropriateExtension(userSelectedFile);
			}
			else
			{
				saveCanceled = true;
			}
		}
		if (!saveCanceled && (hasUnsavedChanges || chooseNewFile))
		{
			FileWriter fileOutStream;
			BufferedWriter buffOutStream;
			try
			{
				XStream xmlStream = FileUtils.getXmlReaderWriter();

				fileOutStream = new FileWriter(saveFile);
				buffOutStream = new BufferedWriter(fileOutStream);

				xmlStream.toXML(this, buffOutStream);

				buffOutStream.close();
			}
			catch (IOException e)
			{

			}

			hasUnsavedChanges = false;
			this.setTabTitle(saveFile.getName());
		}
		return (!saveCanceled);
	}

	/**
	 * Record that this Diagram has been changed since it was last saved. Useful for knowing if need to save. Marks the
	 * title of the diagram in the tabbed pane to indicate to the user that it has been modified.
	 */
	public void markAsChanged()
	{
		if (saveFile != null && !hasUnsavedChanges)
		{
			hasUnsavedChanges = true;
			this.setTabTitle(saveFile.getName() + "*");
		}
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		markAsChanged();
	}

	/**
	 * Returns whether or not the diagram has unsaved changes. Empty, unmodified diagrams do not have unsaved changes.
	 * 
	 * @return - <code>true</code> if the diagram has been modified and is not saved, otherwise <code>false</code>
	 */
	public boolean hasUnsavedChanges()
	{
		boolean diagramBlank = (saveFile == null && listOfNodes.isEmpty());
		return (!diagramBlank && hasUnsavedChanges);
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
		int selectedIndex = containingTabbedPane.getSelectedIndex();
		TabTitleComponent tabComponent = (TabTitleComponent) containingTabbedPane.getTabComponentAt(selectedIndex);
		tabComponent.setTitle(title);
	}

	/**
	 * @return the name of this class diagram, or "Untitled document" if the diagarm has not been saved yet.
	 */
	public String getName()
	{
		if (saveFile != null)
		{
			return saveFile.getName();
		}
		else
		{
			return "Untitled Diagram";
		}
	}

	/**
	 * Determines whether or not this Diagram was saved to this file
	 * 
	 * @param file
	 *            - {@link File} this diagram might be saved to
	 * @return <code>true</code> if the diagram is saved in this file.
	 */
	public boolean isSavedInFile(File file)
	{
		return (file.equals(saveFile));
	}

	/**
	 * Save a copy of the currently selected node (if there is one) to the parent UMLEditor. Since the copy constructor
	 * is used, only the name, attributes and methods will be copied.
	 */
	public void copyNode()
	{
		if (canCopy())
		{
			parentEditor.setCopyNode((ClassNode) currentlySelectedObjects.get(0));
		}
	}

	/**
	 * Copies currently selected node and then deletes it.
	 */
	public void cutNode()
	{
		copyNode();
		if (canCopy())
		{
			this.deleteSelectedObjects();
		}
	}

	/**
	 * Enables the user to use the copy function
	 */
	private boolean canCopy()
	{
		return (currentlySelectedObjects.size() == 1 && currentlySelectedObjects.get(0) instanceof ClassNode);
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
		Point pastePosition;
		if (view.hasFocus())
		{
			pastePosition = view.getMousePosition();
		}
		else
		{
			pastePosition = new Point((parentEditor.getWidth() - 100) / 2, (parentEditor.getHeight() - 140) / 2);
		}
		pasteAtLoc(pastePosition);
	}

	/**
	 * Pastes a copy of the given node at the given location. Internal convenience method also useful for pasting from
	 * popup menu
	 * 
	 * @param pastePosition
	 *            - place to insert the node
	 */
	private void pasteAtLoc(Point pastePosition)
	{
		ClassNode copy = parentEditor.getCopyNode();
		if (copy != null && pastePosition != null)
		{
			ClassNode nodeCopy = new ClassNode(copy);
			initNodePanel(pastePosition, nodeCopy);
			attachNodeToDiagram(nodeCopy);
		}
	}

	/**
	 * Enables the user to use the paste function
	 */
	public void enablePastePopup()
	{
		m_diagramPopup.enablePasteOption();
	}

	/**
	 * Handle diagram-specific key events
	 */
	@Override
	public void keyPressed(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.VK_DELETE)
		{
			this.deleteSelectedObjects();
		}
		else if (event.getKeyCode() == KeyEvent.VK_N && !event.isControlDown())
		{
			parentEditor.toggleAddNewClassMode();
		}
		else if (event.getKeyCode() == KeyEvent.VK_E && currentlySelectedObjects.size() == 1
				&& currentlySelectedObjects.get(0) instanceof ClassNode)
		{
			ClassNode node = (ClassNode) currentlySelectedObjects.get(0);
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
	 * Moves NodePanel(s) in the UML Diagram. Called when the NodeDragListener gets a dragged event for a node panel.
	 * 
	 * @param movePoint
	 *            - place to move the panel's upper left-hand corner to
	 */
	public void moveSelectedNodes(Point movePoint)
	{
		// go through all nodes, and adjust amount to move by,
		// if it would take any of the nodes off of the screen
		for (int i = 0; i < currentlySelectedObjects.size(); ++i)
		{
			NodePanel nodePanelToMove = ((ClassNode) currentlySelectedObjects.get(i)).getNodePanel();
			if (nodePanelToMove.getX() + movePoint.x < 0)
			{
				movePoint.x = (0 - nodePanelToMove.getX());
			}
			if (nodePanelToMove.getY() + movePoint.y < 0)
			{
				movePoint.y = (0 - nodePanelToMove.getY());
			}
		}

		// go through all nodes again and move them by the desired amount
		for (int i = 0; i < currentlySelectedObjects.size(); ++i)
		{
			NodePanel nodePanelToMove = ((ClassNode) currentlySelectedObjects.get(i)).getNodePanel();
			int newPosX = nodePanelToMove.getX() + movePoint.x;
			int newPosY = nodePanelToMove.getY() + movePoint.y;
			nodePanelToMove.resetBounds(new Point(newPosX, newPosY));

			nodePanelToMove.revalidate();
		}


		view.repaint();
		markAsChanged();
	}

	/**
	 * When a diagram becomes visible in the UML editor, ensure that the editor's delete button and copy/cut menu
	 * appropriately reflects the current diagram.
	 * 
	 * @param e
	 */
	@Override
	public void focusGained(FocusEvent e)
	{
		if (currentlySelectedObjects != null && !currentlySelectedObjects.isEmpty())
		{
			parentEditor.enableDeleteButtonState();
			boolean enableCopyCut = (currentlySelectedObjects.get(0) instanceof ClassNode)
					&& (currentlySelectedObjects.size() == 1);
			parentEditor.setCopyCutState(enableCopyCut);
		}
		else
		{
			parentEditor.reflectUnselectedState();
		}
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		// do nothing
	}

	/**
	 * Prints the entire Uml Diagram on one page. If the diagram is larger than the printable portion of the page, it
	 * will be scaled down to fit on the page. Also prints the title of the diagram in the upper LH corner of the page.
	 */
	@Override
	public int print(Graphics arg0, PageFormat arg1, int arg2) throws PrinterException
	{
		if (arg2 > 0)
		{
			return NO_SUCH_PAGE;
		}

		Graphics2D g2d = (Graphics2D) arg0;

		g2d.translate(arg1.getImageableX(), arg1.getImageableY());

		// TODO: make text centered
		g2d.setFont(new Font("Serif", Font.PLAIN, 12));
		FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
		g2d.drawString(getName(), 1, metrics.getHeight());

		g2d.translate(0, metrics.getHeight());

		double widthScale = arg1.getImageableWidth() / view.getWidth();
		double heightScale = arg1.getImageableHeight() / view.getHeight();

		double uniformScale = Math.min(widthScale, heightScale);

		g2d.scale(uniformScale, uniformScale);

		view.printAll(arg0);

		return PAGE_EXISTS;
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
			if (arg0.isPopupTrigger())
			{
				m_diagramPopup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
			else
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
					unselectCurrentObjects(null);
				}
			}
		}
	}

	/**
	 * Creates a popup menu when the user right-clicks on the background of a class diagram
	 */
	private class DiagramBackgroundPopup extends JPopupMenu implements ActionListener
	{
		private static final long serialVersionUID = 8918402885332092962L;

		private Point originalClickLocation;

		private JMenuItem pasteOption;

		public DiagramBackgroundPopup()
		{
			super();
			// set up
			JMenuItem addClassOption = new JMenuItem("Add Class");
			addClassOption.addActionListener(this);
			addClassOption.setActionCommand("Add Class");
			this.add(addClassOption);

			pasteOption = new JMenuItem("Paste");
			pasteOption.addActionListener(this);
			pasteOption.setActionCommand("Paste");
			pasteOption.setEnabled(false);
			this.add(pasteOption);

			JMenuItem closeOption = new JMenuItem("Close");
			closeOption.addActionListener(this);
			closeOption.setActionCommand("Close");
			this.add(closeOption);
		}

		/**
		 * Once a {@link ClassNode} has been copied, ensure that popup menu will now always have the Paste option
		 * enabled.
		 */
		public void enablePasteOption()
		{
			pasteOption.setEnabled(true);
		}

		/**
		 * Show the popup and record where exactly the user clicked, so can add or paste nodes in the appropriate
		 * location.
		 */
		@Override
		public void show(Component invoker, int x, int y)
		{
			super.show(invoker, x, y);
			originalClickLocation = new Point(x, y);
		}

		/**
		 * Handle selection of items in the popup menu.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getActionCommand() == "Add Class")
			{
				createNode(originalClickLocation);
			}
			else if (e.getActionCommand() == "Paste")
			{
				pasteAtLoc(originalClickLocation);
			}
			else if (e.getActionCommand() == "Close")
			{
				parentEditor.closeCurrentTab();
				parentEditor.requestFocus();
			}
		}
	}

	@Override
	public void objectSelected(SelectionEvent e)
	{
		setSelectedObject(e.getSource(), e.isSingleSelectionRequested());
	}

	/**
	 * Drag a drag line on the screen.
	 * 
	 * @param startPoint
	 * @param endPoint
	 * @param startNode
	 */
	public void drawDragLine(Point startPoint, Point endPoint, JComponent startNode)
	{
		GlassDrawingPane gp = (GlassDrawingPane) parentEditor.getGlassPane();

		Point start = SwingUtilities.convertPoint(startNode, startPoint, gp);
		Point end = SwingUtilities.convertPoint(startNode, endPoint, gp);

		Path2D.Float dragPath = new Path2D.Float();
		dragPath.moveTo(start.x, start.y);
		dragPath.lineTo(end.x, end.y);

		gp.setDrawPath(dragPath);
		view.repaint();
	}

	/**
	 * Removes the drag line that was draw as the user dragged between nodes
	 */
	public void clearDragLine()
	{
		GlassDrawingPane gp = (GlassDrawingPane) parentEditor.getGlassPane();
		gp.setVisible(false);
	}

	/**
	 * Shows a {@link JPopupMenu pop-up menu} on mouse press/release if the system pop-up trigger is matched. Used for
	 * {@link IEditable}s: ClassNodes and Relationships. Does not handle the popup for the ClassDiagram background -
	 * that requires extra information and is handled by {@link DiagramBackgroundPopup}
	 */
	class PopupListener extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		/**
		 * If something editable has been right-clicked, show it's popup
		 * 
		 * @param e
		 */
		private void maybeShowPopup(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				IEditable editableSource = (IEditable) e.getSource();
				JPopupMenu menu = editableSource.getPopupMenu();
				addMenuOptions(menu);

				// e.getX() and e.getY() should just work here but don't - probably a bug in swing
				// getting the mouse position ensures popup menu will always appear where clicked
				Point mousePosition = e.getComponent().getMousePosition();
				menu.show(e.getComponent(), mousePosition.x, mousePosition.y);
			}
		}

		private void addMenuOptions(JPopupMenu menu)
		{
			menu.add(new JSeparator());
			menu.add(new DeleteAction("Delete"));
		}
	}

	private class DeleteAction extends AbstractAction
	{
		private static final long serialVersionUID = -7188176402011656556L;

		public DeleteAction(String name)
		{
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			deleteSelectedObjects();
		}

	}
}
