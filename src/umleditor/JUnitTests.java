package umleditor;

import org.junit.Test;
import static org.junit.Assert.*;

public class JUnitTests extends UMLEditor {
	
	/*
	 * Creates a testnode and tests the default settings (name, attributes, methods)
	 */
	
	@Test
	public void testConstructors() {
		ClassDiagram testDiagram = new ClassDiagram(this);
		ClassNode testNode = new ClassNode();
		
		//Testing manual naming
		//testNode.setName("TestName");
		//assertTrue("Error: Name is not correct", testNode.getName() == "TestName");
		
		assertTrue("Test", testNode.getNumAttributes() == 1);
		assertTrue("Test", testNode.getNumMethods() == 1);
		
		
		
	}

	/*
	 * create a ClassDiagram,ClassNode, and NodePanel. Then delete new node
	 */
	@Test
	public void testDelete() {
		ClassDiagram testDiagram = new ClassDiagram(this);
		ClassNode testNode = new ClassNode();
		NodePanel testNodePanel = new NodePanel(testDiagram,testNode);
		
		testDiagram.setSelectedNode(testNode);
		testDiagram.deleteSelectedNode();		
			
		
	}
	
	/*
	 * create several methods, add them, remove them, add again
	 */
	@Test
	public void testMethods() {
	
		ClassNode testNode = new ClassNode(); //Method 1 is automatically created on Node creation
		testNode.addMethod("Method 2");
		testNode.addMethod("Method 3");
		testNode.addMethod("Method 4");
		testNode.addMethod("Method 5");
		assertTrue("Error: Methods are not counted correct", testNode.getNumMethods() == 5);
		
		//remove 3 methods		
		testNode.removeMethod(4);
		testNode.removeMethod(3);
		testNode.removeMethod(2);
		assertTrue("Error: Methods are not all removed", testNode.getNumMethods() == 2);
		
		
	
	}

	
	

}
