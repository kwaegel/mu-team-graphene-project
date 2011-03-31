package unitTesting;

import static org.junit.Assert.*;

import java.util.Collection;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.junit.Test;

import umleditor.ClassDiagram;
import umleditor.ClassNode;
import umleditor.NodePanel;
import umleditor.NumberedTextField;
import umleditor.NumberedTextField.FieldType;
import umleditor.Relationship;
import umleditor.Relationship.RelationshipType;
import umleditor.UMLEditor;


public class JUnitTests
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3943944413362019489L;

	/**
	 * Creates a testnode and tests the default settings (name, attributes, methods)
	 */
	@Test
	public void testConstructors()
	{
		ClassNode testNode = new ClassNode();
		assertTrue("Error: not constructed correctly, wrong number of attributes", testNode.getNumAttributes() == 1);
		assertTrue("Error: first attribute has wrong name", testNode.getAttribute(0).equals("attribute 1"));
		assertTrue("Error: not constructed correctly, wrong number of methods", testNode.getNumMethods() == 1);
		assertTrue("Error: first method has wrong name", testNode.getMethod(0).equals("method 1"));

	}

	/**
	 * create a ClassDiagram,ClassNode, and NodePanel. Then delete new node
	 */
	//TODO: Need to either remove or fix method
	@Test
	public void testDelete()
	{
		UMLEditor editor = new UMLEditor();
		ClassDiagram testDiagram = new ClassDiagram(editor, new JScrollPane());
		ClassNode testNode = new ClassNode();	
		NodePanel np = new NodePanel(testDiagram, testNode);
		np.attachToView(new JLayeredPane());
		testNode.attachPanel(np);

//		testDiagram.setSelectedObject(testNode);
//		testDiagram.deleteSelectedObject();
		
		//assertNotNull(testDiagram.getView());
		//assertTrue("Error: panel not removed when node deleted", testDiagram.getView().getComponentCount() == 0);
	}

	/**
	 * create several methods, add them, remove them
	 */
	@Test
	public void testMethodAddRemove()
	{
		String m1 = "this is the second method name";
		String m2 = "Method 3";
		String m3 = "Method 4";
		String m4 = "Method 5";
		ClassNode testNode = new ClassNode(); // Method 1 is automatically created on Node creation
		testNode.attachPanel(new NodePanel(new ClassDiagram(new UMLEditor(), new JScrollPane()), testNode));
		testNode.addMethod(m1);
		assertTrue("Error: incorrect number of methods in class", testNode.getNumMethods() == 2);
		assertTrue("Error: second method has wrong name", testNode.getMethod(1).equals(m1));
		testNode.addMethod(m2);
		assertTrue("Error: incorrect number of methods in class", testNode.getNumMethods() == 3);
		assertTrue("Error: third method has wrong name", testNode.getMethod(2).equals(m2));
		testNode.addMethod(m3);
		assertTrue("Error: incorrect number of methods in class", testNode.getNumMethods() == 4);
		assertTrue("Error: fourth method has wrong name", testNode.getMethod(3).equals(m3));
		testNode.addMethod(m4);
		assertTrue("Error: incorrect number of methods in class", testNode.getNumMethods() == 5);
		assertTrue("Error: fourth method has wrong name", testNode.getMethod(4).equals(m4));

		testNode.removeMethod(4);
		assertTrue("Error: incorrect number of methods in class", testNode.getNumMethods() == 4);
		assertTrue("Error: first method has wrong name", testNode.getMethod(0).equals("method 1"));
		assertTrue("Error: second method has wrong name", testNode.getMethod(1).equals(m1));
		assertTrue("Error: third method has wrong name", testNode.getMethod(2).equals(m2));
		
		testNode.removeMethod(2);
		assertTrue("Error: incorrect number of methods in class", testNode.getNumMethods() == 3);
		assertTrue("Error: first method has wrong name", testNode.getMethod(0).equals("method 1"));
		assertTrue("Error: second method has wrong name", testNode.getMethod(1).equals(m1));
		assertTrue("Error: third method has wrong name", testNode.getMethod(2).equals(m3));
		
		testNode.removeMethod(0);
		assertTrue("Error: Methods were not all removed", testNode.getNumMethods() < 3);
		assertTrue("Error: more Methods removed than should have been", testNode.getNumMethods() > 1);
		assertTrue("Error: first method has wrong name", testNode.getMethod(0).equals(m1));
		assertTrue("Error: second method has wrong name", testNode.getMethod(1).equals(m3));
	}
	
	/**
	 * create several Attributes, add them, remove them
	 */
	@Test
	public void testAttributeAddRemove()
	{
		String attribute1 = "Attribute 2";
		String attribute2 = "bla bla bla";
		String attribute3 = "Attribute 4";
		String attribute4 = "Attribute 5";
		ClassNode testNode = new ClassNode(); // Attribute 1 is automatically created on Node creation
		testNode.attachPanel(new NodePanel(new ClassDiagram(new UMLEditor(), new JScrollPane()), testNode));
		testNode.addAttribute(attribute1);
		assertTrue("Error: incorrect number of attributes in class", testNode.getNumAttributes() == 2);
		assertTrue("Error: second attribute has wrong name", testNode.getAttribute(1).equals(attribute1));
		testNode.addAttribute(attribute2);
		assertTrue("Error: incorrect number of attributes in class", testNode.getNumAttributes() == 3);
		assertTrue("Error: third attribute has wrong name", testNode.getAttribute(2).equals(attribute2));
		testNode.addAttribute(attribute3);
		assertTrue("Error: incorrect number of attributes in class", testNode.getNumAttributes() == 4);
		assertTrue("Error: fourth attribute has wrong name", testNode.getAttribute(3).equals(attribute3));
		testNode.addAttribute(attribute4);
		assertTrue("Error: incorrect number of attributes in class", testNode.getNumAttributes() == 5);
		assertTrue("Error: fourth attribute has wrong name", testNode.getAttribute(4).equals(attribute4));

		testNode.removeAttribute(4);
		assertTrue("Error: incorrect number of attributes in class", testNode.getNumAttributes() == 4);
		assertTrue("Error: first attribute has wrong name", testNode.getAttribute(0).equals("attribute 1"));
		assertTrue("Error: second attribute has wrong name", testNode.getAttribute(1).equals(attribute1));
		assertTrue("Error: third attribute has wrong name", testNode.getAttribute(2).equals(attribute2));
		
		testNode.removeAttribute(1);
		assertTrue("Error: incorrect number of attribute in class", testNode.getNumAttributes() == 3);
		assertTrue("Error: first attribute has wrong name", testNode.getAttribute(0).equals("attribute 1"));
		assertTrue("Error: second attribute has wrong name", testNode.getAttribute(1).equals(attribute2));
		assertTrue("Error: third attribute has wrong name", testNode.getAttribute(2).equals(attribute3));
		
		testNode.removeAttribute(0);
		assertTrue("Error: attributes were not all removed", testNode.getNumAttributes() < 3);
		assertTrue("Error: more attributes removed than should have been", testNode.getNumAttributes() > 1);
		assertTrue("Error: first attribute has wrong name", testNode.getAttribute(0).equals(attribute2));
		assertTrue("Error: second attributehas wrong name", testNode.getAttribute(1).equals(attribute3));
	}
	
	/**
	 * test setters
	 */
	@Test
	public void testSetters()
	{
		// test attribute setters
		String attrib1 = "initialattrib";
		ClassNode testNode = new ClassNode(); // Attribute 1 is automatically created on Node creation
		testNode.attachPanel(new NodePanel(new ClassDiagram(new UMLEditor(), new JScrollPane()), testNode));
		assertTrue("Error: wrong name attribute name to start with", testNode.getAttribute(0).equals("attribute 1"));
		testNode.setAttribute(0, attrib1);
		assertTrue("Error: name not set correctly", testNode.getAttribute(0).equals(attrib1));
		assertFalse("Error: name still same", testNode.getAttribute(0).equals("attribute 1"));
		assertTrue("Error: now wrong number of attributes", testNode.getNumAttributes() == 1);
		
		// test method setters
		String meth1 = "initialmeth";
		ClassNode testNode2 = new ClassNode(); // Attribute 1 is automatically created on Node creation
		testNode.attachPanel(new NodePanel(new ClassDiagram(new UMLEditor(), new JScrollPane()), testNode2));
		assertTrue("Error: wrong name method name to start with", testNode2.getMethod(0).equals("method 1"));
		testNode2.setMethod(0, meth1);
		assertTrue("Error: name not set correctly", testNode2.getMethod(0).equals(meth1));
		assertFalse("Error: name still same", testNode2.getMethod(0).equals("method 1"));
		assertTrue("Error: now wrong number of methods", testNode2.getNumMethods() == 1);
	}
	
	/**
	 * test naming
	 */
	@Test
	public void testNaming()
	{
		ClassNode testNode = new ClassNode();
		testNode.attachPanel(new NodePanel(new ClassDiagram(new UMLEditor(), new JScrollPane()), testNode));
		testNode.setName("ThisIsTheTestNode");
		String name = testNode.getName();
		assertTrue("Error: Class name was not set, or get does not work", name.equals("ThisIsTheTestNode"));
	}
	
	/**
	 * test Numbered Text Field Constructor
	 */
	@Test
	public void testNumberedTextFieldConstructor()
	{
		NumberedTextField ntf = new NumberedTextField("contentsoffield", 3, FieldType.Method);
		assertTrue("Error: does not contain initial text", ntf.getText().equals("contentsoffield"));
		assertTrue("Error: does not have correct number", ntf.getNumberIndex() == 3);
		assertTrue("Error: does not have correct type", ntf.getType() == FieldType.Method);
	}
	
	/**
	 * test copy constructor
	 */
	@Test
	public void testCopyConstructor()
	{
		// set up initial class node & test contructed properly 
		String attrib = "this is the name of the attribute";
		String meth = "this is the method name";
		String name = "class name!";
		ClassNode testNode = new ClassNode();
		testNode.attachPanel(new NodePanel(new ClassDiagram(new UMLEditor(), new JScrollPane()), testNode));
		testNode.setName(name);
		testNode.setAttribute(0, attrib);
		testNode.addMethod(meth);
		assertTrue("Error: wrong number of attributes", testNode.getNumAttributes() == 1);
		assertTrue("Error: first attribute has wrong name", testNode.getAttribute(0).equals(attrib));
		assertTrue("Error: not constructed correctly, wrong number of methods", testNode.getNumMethods() == 2);
		assertTrue("Error: first method has wrong name", testNode.getMethod(0).equals("method 1"));
		assertTrue("Error: second method has wrong name", testNode.getMethod(1).equals(meth));
		assertTrue("Error: wrong class name", testNode.getName().equals(name));
		
		// construct a copy
		ClassNode copyConstructedNode = new ClassNode(testNode);
		copyConstructedNode.attachPanel(new NodePanel(new ClassDiagram(new UMLEditor(), new JScrollPane()), testNode));
		assertTrue("Error: wrong number of attributes", copyConstructedNode.getNumAttributes() == 1);
		assertTrue("Error: first attribute has wrong name", copyConstructedNode.getAttribute(0).equals(attrib));
		assertTrue("Error: not constructed correctly, wrong number of methods", copyConstructedNode.getNumMethods() == 2);
		assertTrue("Error: first method has wrong name", copyConstructedNode.getMethod(0).equals("method 1"));
		assertTrue("Error: second method has wrong name", copyConstructedNode.getMethod(1).equals(meth));
		assertTrue("Error: wrong class name", copyConstructedNode.getName().equals(name));
		
		// modify copied, ensure original not modified
		copyConstructedNode.setMethod(1, "newMeth!!");
		assertTrue("Error: wrong number of attributes", testNode.getNumAttributes() == 1);
		assertTrue("Error: first attribute has wrong name", testNode.getAttribute(0).equals(attrib));
		assertTrue("Error: not constructed correctly, wrong number of methods", testNode.getNumMethods() == 2);
		assertTrue("Error: first method has wrong name", testNode.getMethod(0).equals("method 1"));
		assertTrue("Error: second method has wrong name", testNode.getMethod(1).equals(meth));
		assertTrue("Error: wrong class name", testNode.getName().equals(name));
		
		// modify original, ensure copy not modified
		testNode.setName("newname!!");
		assertTrue("Error: wrong number of attributes", copyConstructedNode.getNumAttributes() == 1);
		assertTrue("Error: first attribute has wrong name", copyConstructedNode.getAttribute(0).equals(attrib));
		assertTrue("Error: not constructed correctly, wrong number of methods", copyConstructedNode.getNumMethods() == 2);
		assertTrue("Error: first method has wrong name", copyConstructedNode.getMethod(0).equals("method 1"));
		assertTrue("Error: second method has wrong name", copyConstructedNode.getMethod(1).equals("newMeth!!"));
		assertTrue("Error: wrong class name", copyConstructedNode.getName().equals(name));	
	}
	
	/**
	 * Test adding, removing, and getting relationships
	 * Also test creating relationships and getting nodes
	 */
	//TODO:Fix testRelationship NullPointerException
	@Test
	public void testRelationships()
	{
		//Initialize 3 nodes with relationships
		//node1 has a relationship with node2
		//node2 has a relationship with node3
		ClassDiagram testDiagram = new ClassDiagram(new UMLEditor(), new JScrollPane());
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		ClassNode node3 = new ClassNode();
                NodePanel np1 = new NodePanel(testDiagram, node1);
		np1.attachToView(new JLayeredPane());
		node1.attachPanel(np1);
		NodePanel np2 = new NodePanel(testDiagram, node1);
		np2.attachToView(new JLayeredPane());
		node2.attachPanel(np2);
		NodePanel np3 = new NodePanel(testDiagram, node1);
		np3.attachToView(new JLayeredPane());
		node3.attachPanel(np3);
		RelationshipType[] possibleValues = RelationshipType.values();
		Relationship rel = new Relationship(node1, node2, possibleValues[1]);
		Relationship rel2 = new Relationship(node2, node3, possibleValues[0]);
		node1.addRelationship(rel);
		node2.addRelationship(rel);
		node2.addRelationship(rel2);
		node3.addRelationship(rel2);
		
		//Test relationship construction and getting nodes
		Collection<ClassNode> nodes = rel2.getClassNodes();
		assertFalse("Error: node1 should not be in this relationship", nodes.contains(node1));
		assertTrue("Error: nodes are not properly stored", nodes.contains(node2));
		assertTrue("Error: nodes are not properly stored", nodes.contains(node3));
		
		//Test relationships are added properly
		assertTrue("Error: wrong number of relationships in node 1", node1.getRelationships().size() == 1);
		assertTrue("Error: wrong number of relationships in node 2", node2.getRelationships().size() == 2);
		assertTrue("Error: wrong number of relationships in node 3", node3.getRelationships().size() == 1);
		assertTrue("Error: addRelationship error, relationship not properly stored", node2.getRelationships().get(0).equals(rel));
		
		//Delete node1
		testDiagram.setSelectedObject(node1);
		testDiagram.deleteSelectedObject();
		
		assertTrue("Error: relationship was not removed", node2.getRelationships().size() == 2);
		assertTrue("Error: relationship was removed from wrong node", node3.getRelationships().size() == 1);
		assertTrue("Error: relationships with node1 was not removed", node2.getRelationships().get(0).equals(rel2));
		
		testDiagram.setSelectedObject(rel2);
		testDiagram.deleteSelectedObject();
		
		assertTrue("Error: relationship 2 was not deleted from node2", node2.getRelationships().size() == 0);
		assertTrue("Error: relationship 2 was not deleted from node3", node3.getRelationships().size() == 0);
		
	}

	/**
	 * Testing node equality with propertiesEqual and setPropertiesTo methods
	 */
	@Test
	public void testNodeEquality() 
	{
		//Initialize two nodes
		ClassDiagram testDiagram = new ClassDiagram(new UMLEditor(), new JScrollPane());
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		node1.attachPanel(new NodePanel(testDiagram, node1));
		node2.attachPanel(new NodePanel(testDiagram, node2));
		
		assertFalse("Error: nodes should not be the same because of different names", node1.propertiesEqual(node2));
		
		//Setting nodes to have equal names
		node1.setName("hi");
		node2.setName("hi");
		
		assertTrue("Error: nodes should be the same", node1.propertiesEqual(node2));
		
		//Nodes different because of additional attribute
		node1.addAttribute("trib");
		
		assertFalse("Error: node1 should have another attribute", node1.propertiesEqual(node2));
		
		//Setting properties of node2 to node1
		node2.setPropertiesTo(node1);
		
		assertTrue("Error: nodes are not equal", node1.propertiesEqual(node2));
	}
}
