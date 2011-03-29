package unitTesting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import javax.swing.JScrollPane;

import org.junit.Test;

import umleditor.ClassDiagram;
import umleditor.ClassNode;
import umleditor.NodePanel;
import umleditor.NumberedTextField;
import umleditor.NumberedTextField.FieldType;
import umleditor.UMLEditor;

public class JUnitTests /*extends UMLEditor*/
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

	/*
	 * create a ClassDiagram,ClassNode, and NodePanel. Then delete new node
	 */
	@Test
	public void testDelete()
	{
		ClassDiagram testDiagram = new ClassDiagram(new UMLEditor(), new JScrollPane());
		ClassNode testNode = new ClassNode();
		testNode.attachPanel(new NodePanel(testDiagram, testNode));

		testDiagram.setSelectedObject(testNode);
		testDiagram.deleteSelectedObject();
		
		//assertNotNull(testDiagram.getView());
		//assertTrue("Error: panel not removed when node deleted", testDiagram.getView().getComponentCount() == 0);
	}

	/*
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
	
	/*
	 * create several Attributes, add them, remove them
	 */
	@Test
	public void testAttributeAddRemove()
	{
		String attribute1 = "Attribute 2";
		String attribute2 = "Attribute 3";
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
		
		//TODO: put setters in here
	}
	
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
	
	@Test
	public void testNaming()
	{
		ClassNode testNode = new ClassNode();
		testNode.attachPanel(new NodePanel(new ClassDiagram(new UMLEditor(), new JScrollPane()), testNode));
		testNode.setName("ThisIsTheTestNode");
		String name = testNode.getName();
		assertTrue("Error: Class name was not set, or get does not work", name.equals("ThisIsTheTestNode"));
	}
	
	@Test
	public void testNumberedTextFieldConstructor()
	{
		NumberedTextField ntf = new NumberedTextField("contentsoffield", 3, FieldType.Method);
		assertTrue("Error: does not contain initial text", ntf.getText().equals("contentsoffield"));
		assertTrue("Error: does not have correct number", ntf.getNumberIndex() == 3);
		assertTrue("Error: does not have correct type", ntf.getType() == FieldType.Method);
	}
	
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

}
