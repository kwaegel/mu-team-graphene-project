package umleditor;

import java.util.ArrayList;

public class ClassNode {
	
	NodePanel nodePanel;
	String className;
	ArrayList<String> listofAttributes;
	ArrayList<String> listofOperations;
	ArrayList<Relationship> relationships;
	
	public ClassNode()
	{
		
	}
	
	public String getName()
	{
		return className;
	}
	
	public void setName(String name)
	{
		className = name;
	}

}
