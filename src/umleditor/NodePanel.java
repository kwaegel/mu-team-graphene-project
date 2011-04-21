package umleditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

/**
 * This is the visible representation of a class that appears in the UML Editor. Contains no information about the class
 * itself, but has a link to the {@link ClassNode} that does. Displays the information in this {@link ClassNode} in the
 * appropriate format. Handles interactions between the user and the class.
 * 
 */
public class NodePanel extends JPanel implements IEditable
{
	/**
	 * Generated id, recommended for all GUI components
	 */
	private static final long serialVersionUID = 912113941232687505L;

	/**
	 * Height of top label, which displays the class name and also serves as a drag area for the NodePanel.
	 */
	private static int LABEL_AND_DRAG_Y_BOUND = 30;
	/**
	 * Height of a default (method or attribute) field. Used to ensure empty fields (which may be present during
	 * editing), or classes that have no attributes and/or methods, display correctly. This is the height that a JLabel
	 * with some title in the default font will have automatically.
	 */
	private static int DEFAULT_FIELD_HEIGHT = 16;

	/**
	 * {@link ClassNode} associated with this NodePanel. The NodePanel will display this {@link ClassNode}'s name,
	 * methods and attributes.
	 */
	private ClassNode associatedNode;

	/**
	 * ClassDiagram of which this NodePanel's associated node is part, and to whose view this NodePanel is attached.
	 */
	private ClassDiagram parentDiagram;

	/**
	 * Maintains whether or not this NodePanel has been selected.
	 */
	private boolean isSelected;

	/**
	 * Constructs a new NodePanel which displays the given {@link ClassNode} which is part of the given ClassDiagram.
	 * 
	 * @param parent
	 * @param node
	 */
	public NodePanel(ClassDiagram parent, ClassNode node)
	{
		super();

		parentDiagram = parent;
		associatedNode = node;
		associatedNode.attachPanel(this);

		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setLayout(new MigLayout("wrap 1, fill", "0[fill]0", ""));
		isSelected = false;
		this.setAppropriateColor();

		this.createDisplay();

		NodeSelectionListener nsl = new NodeSelectionListener();
		this.addMouseListener(nsl);
		this.addMouseMotionListener(nsl);
	}

	/**
	 * Returns the {@link ClassNode} associated with this NodePanel.
	 * 
	 * @return - this panel's class node
	 */
	public ClassNode getClassNode()
	{
		return associatedNode;
	}

	/**
	 * Returns this panel's parent diagram. Used in classes such as the {@link NodeDragListener} and {@link EditPanel}
	 * which maintain a reference to the node panel and may occasionally need to interact with the panel's parent
	 * diagram.
	 * 
	 * @return - the {@link ClassDiagram} to which this node panel belongs
	 */
	public ClassDiagram getParentDiagram()
	{
		return parentDiagram;
	}

	/**
	 * Constructs an EditPanel so user can edit this panel's associated {@link ClassNode}.
	 */
	public void displayEditPanel()
	{
		EditPanel editPanel = new EditPanel(this.associatedNode, this.parentDiagram);
		editPanel.setVisible(true);
	}

	/**
	 * Recreates display from values in classNode
	 */
	public void createDisplay()
	{
		// clear everything in the class diagram
		this.removeAll();

		// set up title label / dragging label
		String className = associatedNode.getName();
		JLabel titleDragLabel = new JLabel(className, JLabel.CENTER);
		Dimension labelSize = titleDragLabel.getMinimumSize();
		titleDragLabel.setMinimumSize(new Dimension(labelSize.width + 30, LABEL_AND_DRAG_Y_BOUND));
		NodeDragListener ndl = new NodeDragListener(this);
		titleDragLabel.addMouseMotionListener(ndl);
		titleDragLabel.addMouseListener(ndl);
		this.add(titleDragLabel, "dock north");

		// add separator
		addSeparator();

		// add attributes
		for (int i = 0; i < associatedNode.getNumAttributes(); ++i)
		{
			String attributeName = associatedNode.getAttribute(i);
			JLabel attributeLabel = new JLabel(attributeName);
			// ensure that blank fields don't make node panel smaller
			// will never have a blank attribute except while editing,
			// but while editing, don't want strange effects where making
			// a field empty causes node length to shrink
			if (attributeName.isEmpty())
			{
				attributeLabel.setMinimumSize(new Dimension(0, 16));
			}
			this.add(attributeLabel, "gapx 3 3");
		}

		// add space if there are no attributes so displays properly
		if (associatedNode.getNumAttributes() == 0)
		{
			addSpacer();
		}

		// add separator
		addSeparator();

		// add methods
		for (int i = 0; i < associatedNode.getNumMethods(); ++i)
		{
			String methodName = associatedNode.getMethod(i);
			JLabel methodLabel = new JLabel(methodName);
			// ensure that blank fields don't make node panel smaller
			// will never have a blank attribute except while editing,
			// but while editing, don't want strange effects where making
			// a field empty causes node lenght to shrink
			if (methodName.isEmpty())
			{
				methodLabel.setMinimumSize(new Dimension(0, DEFAULT_FIELD_HEIGHT));
			}
			this.add(methodLabel, "gapx 3 3");
		}

		// add space if there are no attributes so displays properly
		if (associatedNode.getNumMethods() == 0)
		{
			addSpacer();
		}

		// add an empty JLabel, workaround for a bug in MigLayout
		// where using direction docking causes the last component
		// in the container to not have the normal gap after it
		// adding this empty JLabel causes last method to have
		// appropriate spacing
		this.add(new JLabel());
	}

	/**
	 * Adds a separator to the {@link ClassNode}
	 */
	private void addSeparator()
	{
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setForeground(Color.black);
		this.add(separator);
	}

	/**
	 * Adds a spacer the height of an empty field
	 */
	private void addSpacer()
	{
		JLabel spacer = new JLabel();
		spacer.setMinimumSize(new Dimension(0, DEFAULT_FIELD_HEIGHT));
		this.add(spacer);
	}

	/**
	 * Moves the NodePanel to position (if position is not null) and ensures its size reflects the contents of it's
	 * {@link ClassNode}. Updates the {@link ClassNode}'s location field.
	 * 
	 * @param position
	 *            - new location for the panel, or <code>null</code>, if the panel is not being moved.
	 */
	public void resetBounds(Point position)
	{
		int x = (position == null) ? this.getX() : position.x;
		int y = (position == null) ? this.getY() : position.y;
		int width = this.getPreferredSize().width;
		int height = this.getPreferredSize().height;
		this.setBounds(x, y, width, height);
		associatedNode.saveLocation(new Point(x, y));
	}

	/**
	 * Attaches this node panel to the given view. Node panel is added at the default layer. When the {@link ClassNode}
	 * associated with this node panel is selected (which happens automatically if class is added by user instead of
	 * being constructed from a file), the node panel will be elevated to the Drag_Layer.
	 * 
	 * @param view
	 *            - View to attach to so will be displayed.
	 */
	public void attachToView(JLayeredPane view)
	{
		view.add(this, "external", JLayeredPane.DEFAULT_LAYER);
	}

	/**
	 * Tells this node panel that it is selected, so it can change it's appearance and layer appropriately
	 */
	public void makeSelected()
	{
		isSelected = true;
		this.setAppropriateColor();
		JLayeredPane view = (JLayeredPane) this.getParent();
		view.setLayer(this, JLayeredPane.DRAG_LAYER);
	}

	/**
	 * Tells this node panel that has been deselected, so it can change it's appearance and layer appropriately
	 */
	public void makeUnselected()
	{
		isSelected = false;
		this.setAppropriateColor();
		JLayeredPane view = (JLayeredPane) this.getParent();
		view.setLayer(this, JLayeredPane.DEFAULT_LAYER);
	}

	/**
	 * Returns whether or not this panel is selected
	 * 
	 * @return <code>true</code> if is selected, <code>false</code> if not.
	 */
	public boolean isSelected()
	{
		return (isSelected);
	}

	public void setAppropriateColor()
	{
		this.setBackground(isSelected ? Color.pink : Color.white);
	}

	@Override
	public JPopupMenu getPopupMenu()
	{
		JPopupMenu menu = new JPopupMenu();
		menu.add(new AbstractAction("Cut")
		{
			private static final long serialVersionUID = -5298062676250211620L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				parentDiagram.cutNode();
			}
		});
		menu.add(new AbstractAction("Copy")
		{
			private static final long serialVersionUID = 5944567330239631558L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				parentDiagram.copyNode();
			}
		});
		menu.add(new AbstractAction("Edit")
		{
			private static final long serialVersionUID = 4527734957996948144L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				displayEditPanel();
			}
		});

		return menu;
	}

	/**
	 * Class to handle {@link ClassNode} selection and adding relationships.
	 */
	private class NodeSelectionListener extends MouseAdapter
	{
		private final Color m_nodeHoverColor = new Color(150, 250, 130);
		private Point m_initialDragPoint;

		private NodePanel m_lastHoveredNode;

		/**
		 * One click selects this panel's node Two clicks opens the edit panel.
		 */
		@Override
		public void mouseClicked(MouseEvent e)
		{
			int clickCount = e.getClickCount();
			if (clickCount > 1)
			{
				displayEditPanel();
			}
			else if (!e.isControlDown())
			{
				parentDiagram.setSelectedObject(associatedNode, true);
			}
		}

		/**
		 * Selects this panel's node
		 */
		@Override
		public void mousePressed(MouseEvent e)
		{
			boolean deselectOthers = e.isControlDown() ? false : true;
			parentDiagram.setSelectedObject(associatedNode, deselectOthers);

			// Set initial point when drawing drag lines.
			m_initialDragPoint = e.getPoint();
		}

		/**
		 * Tells the parent diagram to create a relationship between this node and whichever one was previously
		 * selected.
		 */
		@Override
		public void mouseReleased(MouseEvent e)
		{
			// Clear any drawn lines
			parentDiagram.clearDragLine();

			// necessary because of error in swing.
			Component comp = parentDiagram.getComponentUnder(e);

			if (comp instanceof NodePanel)
			{
				NodePanel panel = ((NodePanel) comp);
				ClassNode targetNode = panel.getClassNode();
				if (!e.isPopupTrigger())
				{
					parentDiagram.addRelationship(targetNode);
					panel.setAppropriateColor();
				}
			}
		}

		/**
		 * When dragging to create a relationship, draws teh line behind the mouse, and colors nodes that are hovered
		 * over.
		 */
		@Override
		public void mouseDragged(MouseEvent e)
		{
			// drag line drawing code
			Point dragPoint = e.getPoint();
			NodePanel startNode = NodePanel.this;
			parentDiagram.drawDragLine(m_initialDragPoint, dragPoint, startNode);

			// node hover coloring code
			Component targetComponent = parentDiagram.getComponentUnder(e);
			NodePanel newHoveredNode = null;
			if (targetComponent instanceof NodePanel)
			{
				newHoveredNode = (NodePanel) targetComponent;
			}

			if (m_lastHoveredNode != null)
			{
				m_lastHoveredNode.setAppropriateColor();
			}

			if (newHoveredNode != null && newHoveredNode != NodePanel.this)
			{
				newHoveredNode.setBackground(m_nodeHoverColor);
			}
			m_lastHoveredNode = newHoveredNode;
		}
	}

}
