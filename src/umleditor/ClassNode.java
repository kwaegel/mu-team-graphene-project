package umleditor;

import java.util.ArrayList;

public class ClassNode {
	
	private static int nodesCreated = 0;

	private NodePanel nodePanel;
	private String className;
	private ArrayList<String> listOfAttributes;
	private ArrayList<String> listOfMethods;
	private ArrayList<Relationship> relationships;

	/**
	 * Constructor with a default class name.
	 * 
	 * @param diagram
	 *            - the diagram this node belongs to.
	 * @param creationPoint
	 *            - the point to create the node at.
	 */
	public ClassNode() {
		className = "NewClass" + ++nodesCreated;
		listOfAttributes = new ArrayList<String>();
		listOfAttributes.add("attribute 1");
		listOfMethods = new ArrayList<String>();
		listOfMethods.add("method 1");
		relationships = new ArrayList<Relationship>();
	}

	public void attachPanel(NodePanel panel) {
		nodePanel = panel;
	}

	// Returns the name of the Class Node
	public String getName() {
		return className;
	}

	// Sets the name of the Class Node
	public void setName(String name) {
		className = name;
		nodePanel.createDisplay();
		nodePanel.getParent().validate();
	}

	// Add String attribute to listofAttributes
	public void addAttribute(String attribute) {
		listOfAttributes.add(attribute);
		nodePanel.createDisplay();
		nodePanel.getParent().validate();
	}

	// Returns the name of the Attribute at the index in listofAttributes
	public String getAttribute(int index) {
		return listOfAttributes.get(index);
	}

	// Sets the attribute at index in listofAttributes with parameter String attribute
	public void setAttribute(int index, String attribute) {
		listOfAttributes.set(index, attribute);
		nodePanel.createDisplay();
		nodePanel.getParent().validate();
	}

	// Removes the attribute in listofAttributes at the index
	public void removeAttribute(int index) {
		listOfAttributes.remove(index);
		nodePanel.createDisplay();
		nodePanel.getParent().validate();
	}

	public int getNumAttributes() {
		return (listOfAttributes.size());
	}

	// Adds String method to listofMethods
	public void addMethod(String method) {
		listOfMethods.add(method);
		nodePanel.createDisplay();
		nodePanel.getParent().validate();
	}

	// Returns the String at the index in listofMethods
	public String getMethod(int index) {
		return listOfMethods.get(index);
	}

	// Sets the method in listofMethods at the index with parameter String method
	public void setMethod(int index, String method) {
		listOfMethods.set(index, method);
		nodePanel.createDisplay();
		nodePanel.getParent().validate();
	}

	// Removes the method in listofMethods at the index
	public void removeMethod(int index) {
		listOfMethods.remove(index);
		nodePanel.createDisplay();
		nodePanel.getParent().validate();
	}

	public int getNumMethods() {
		return (listOfMethods.size());
	}

	// Removes the Relationship from the relationships ArrayList
	// Method is called when the other class removes the relationship
	public void removeRelationship(Relationship relationship) {
		relationships.remove(relationship);
	}

	// Returns this ClassNode's NodePanel
	public NodePanel getNodePanel() {
		return nodePanel;

	}

}
