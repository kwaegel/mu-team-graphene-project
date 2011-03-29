package unitTesting;

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
	public void testMethods()
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
	public void testAttributes()
	{

		ClassNode testNode = new ClassNode(); // Attribute 1 is automatically created on Node creation
		testNode.attachPanel(new NodePanel(new ClassDiagram(new UMLEditor(), new JScrollPane()), testNode));
		testNode.addAttribute("Attrib 2");
		testNode.addAttribute("Attrib 3");
		testNode.addAttribute("Attrib 4");
		testNode.addAttribute("Attrib 5");
		assertTrue("Error: incorrect number of Attributes in class", testNode.getNumAttributes() == 5);

		// remove 3 Attributes
		testNode.removeAttribute(4);
		testNode.removeAttribute(3);
		testNode.removeAttribute(2);
		assertTrue("Error: Attributes were not all removed", testNode.getNumAttributes() < 3);
		assertTrue("Error: more Attributes removed than should have been", testNode.getNumAttributes() > 1);

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
	public void testNumberedTextFieldNumberSetter()
	{
		NumberedTextField ntf = new NumberedTextField("contentsoffield", 3, FieldType.Method);
		ntf.setNumberIndex(4);
		assertTrue("Error: did not change number", ntf.getNumberIndex() == 4);
	}

}
