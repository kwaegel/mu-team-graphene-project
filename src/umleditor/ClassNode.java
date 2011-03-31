package umleditor;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;

/**
 * Model of a class in the UML Diagram.
 */
public class ClassNode implements ISelectable
{

	private static int nodesCreated = 0;

	private transient NodePanel nodePanel;
	private String className;
	private ArrayList<String> listOfAttributes;
	private ArrayList<String> listOfMethods;
	private ArrayList<Relationship> m_relationships;

	/**
	 * Constructor with a default class name.
	 * 
	 * @param diagram
	 *            - the diagram this node belongs to.
	 * @param creationPoint
	 *            - the point to create the node at.
	 */
	public ClassNode()
	{
		className = "NewClass" + (++nodesCreated);
		listOfAttributes = new ArrayList<String>();
		listOfAttributes.add("attribute 1");
		listOfMethods = new ArrayList<String>();
		listOfMethods.add("method 1");
		m_relationships = new ArrayList<Relationship>();
	}

	/**
	 * Constructs a copy of otherNode which has identical name, attributes, and methods NodePanel will initially be
	 * null. Relationships will not be copied.
	 * 
	 * @param otherNode
	 *            - node from which we get name, attributes and methods
	 */
	public ClassNode(ClassNode otherNode)
	{
		this.setPropertiesTo(otherNode);
		this.m_relationships = new ArrayList<Relationship>();
		this.nodePanel = null;
	}

	/**
	 * Attaches the given node panel to this node. Called in NodePanel's constructor.
	 */
	public void attachPanel(NodePanel panel)
	{
		nodePanel = panel;
	}

	/**
	 * Sets the core properties of the node (name, attributes, methods) to those of otherNode. Used in the EditPanel to
	 * easily revert the node's properties without modifying unrelated values such as nodePanel or relationships
	 * 
	 * @param otherNode
	 *            - node from which to get name, attributes and methods
	 */
	public void setPropertiesTo(ClassNode otherNode)
	{
		this.className = otherNode.className;
		this.listOfAttributes = new ArrayList<String>(otherNode.listOfAttributes);
		this.listOfMethods = new ArrayList<String>(otherNode.listOfMethods);
	}

	/**
	 * Determines if the properties of otherNode are identical to those of this node. Checks classNames, contents of
	 * attribute list and method list.
	 * 
	 * @param otherNode
	 *            - ClassNode to compare this one to.
	 * @return - <code>true</code> if properties are equal, <code>false</code> if they are not
	 */
	public boolean propertiesEqual(ClassNode otherNode)
	{
		return (this.className.equals(otherNode.className) && this.listOfAttributes.equals(otherNode.listOfAttributes) && this.listOfMethods
				.equals(otherNode.listOfMethods));
	}

	/** 
	 * Returns the name of the Class Node
	 * @return the class name
	 */
	public String getName()
	{
		return className;
	}

	/** 
	 * Sets the name of the Class Node
	 * @param name
	 * 			- String to replace the previous class name
	 */
	public void setName(String name)
	{
		className = name;
		updateNodePanel();
	}

	@Override
	public void setSelected(boolean selected)
	{
		if (selected)
		{
			nodePanel.setBackground(Color.pink);
			JLayeredPane view = (JLayeredPane) nodePanel.getParent();
			view.setLayer(nodePanel, JLayeredPane.DRAG_LAYER);
		}
		else
		{
			nodePanel.setBackground(Color.white);
			JLayeredPane view = (JLayeredPane) nodePanel.getParent();
			view.setLayer(nodePanel, JLayeredPane.DEFAULT_LAYER);
		}
	}

	/** 
	 * Add String attribute to listofAttributes
	 * @param attribute
	 * 			- String to be added to the list of attributes
	 */
	public void addAttribute(String attribute)
	{
		listOfAttributes.add(attribute);
		updateNodePanel();
	}

	/** 
	 * Returns the name of the Attribute at the index in listofAttributes
	 * @param index
	 * 			-int used as an index to find an attribute in the list of attributes
	 * @return the attribute at the index in the list of attributes
	 */
	public String getAttribute(int index)
	{
		return listOfAttributes.get(index);
	}

	/** 
	 * Sets the attribute at index in listofAttributes with parameter String
	 * @param index
	 * 			-int used to find the attribute to be replaced in the list of attributes
	 * @param attribute
	 * 			-String used to replace the attribute at the index in the list of attributes
	 */
	public void setAttribute(int index, String attribute)
	{
		listOfAttributes.set(index, attribute);
		updateNodePanel();
	}

	/** 
	 * Removes the attribute in listofAttributes at the index
	 * @param index
	 * 			-int used as an index to find the attribute to be removed from list of attributes
	 */
	public void removeAttribute(int index)
	{
		listOfAttributes.remove(index);
		updateNodePanel();
	}

	/**
	 * Get the number of attributes in this class node
	 * 
	 * @return - number of attributes
	 */
	public int getNumAttributes()
	{
		return (listOfAttributes.size());
	}

	/** 
	 * Adds String method to listofMethods
	 * @param method
	 * 			-String to be added to the list of methods
	 */
	public void addMethod(String method)
	{
		listOfMethods.add(method);
		updateNodePanel();
	}

	/**
	 * Returns the String at the index in listofMethods
	 * @param index
	 * 			-int used as an index to find the method in the list of methods
	 * @return the method at index in list of methods
	 */
	public String getMethod(int index)
	{
		return listOfMethods.get(index);
	}

	/**
	 * Sets the method in listofMethods at the index with parameter String method
	 * @param index
	 * 			-int used as an index to find the method in the list of methods
	 * @param method
	 * 			-String to replace the method at the index in list of methods
	 */
	public void setMethod(int index, String method)
	{
		listOfMethods.set(index, method);
		updateNodePanel();
	}

	/** 
	 * Removes the method in listofMethods at the index
	 * @param index
	 * 			-int used as an index to find the method in list of methods
	 */
	public void removeMethod(int index)
	{
		listOfMethods.remove(index);
		updateNodePanel();
	}

	/**
	 * Get the number of methods this class node has
	 * 
	 * @return - the number of methods
	 */
	public int getNumMethods()
	{
		return (listOfMethods.size());
	}

	/**
	 * Attached the given relationship to this node
	 * 
	 * @param relationship
	 *            - the relationship to attach
	 */
	public void addRelationship(Relationship relationship)
	{
		m_relationships.add(relationship);
	}

	/**
	 * Removes the Relationship from the relationships ArrayList
	 * Method is called when the other class removes the relationship
	 * @param relationship
	 * 			-Relationship to be removed from the ArrayList
	 */
	public void removeRelationship(Relationship relationship)
	{
		m_relationships.remove(relationship);
	}

	/**
	 * Get all of the relationships which end on or start from this node
	 * 
	 * @return - list of relationships
	 */
	public List<Relationship> getRelationships()
	{
		return m_relationships;
	}

	/**
	 * Returns the NodePanel associated with this class node
	 * 
	 * @return
	 */
	public NodePanel getNodePanel()
	{
		return nodePanel;
	}

	/**
	 * @return - the bounds of this node's panel
	 */
	public Rectangle getBounds()
	{
		return nodePanel.getBounds();
	}

	/**
	 * Ensures the NodePanel will reflect changes in the ClassNode Called whenever the class node changes.
	 */
	public void updateNodePanel()
	{
		nodePanel.createDisplay();
		nodePanel.resetBounds(null);
		nodePanel.revalidate();
	}
}
