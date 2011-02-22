package umleditor;

import java.util.ArrayList;

public class ClassNode {
	
	private NodePanel nodePanel;
	private String className;
	private ArrayList<String> listofMembers;
	private ArrayList<String> listofFields;
	private ArrayList<Relationship> relationships;
	
	//Constructor:
	//	Sets a reference to its Node Panel
	//	className set to Name
	//	Instantiates list of members, fields, and relationships
	public ClassNode(NodePanel node)
	{
		
		nodePanel = node;
		className = "Name";
		listofMembers = new ArrayList<String>();
		listofFields = new ArrayList<String>();
		relationships  = new ArrayList<Relationship>();
	}
	
	//Returns the name of the Class Node
	public String getName()
	{
		return className;
	}
	
	//Sets the name of the Class Node
	public void setName(String name)
	{
		className = name;
	}
	
	//Add String member to listofMembers
	public void addMember(String member)
	{
		listofMembers.add(member);
	}
	
	//Returns the name of the Member at the index in listofMembers
	public String getMember(int index)
	{
		return listofMembers.get(index);
	}
	
	//Sets the member at index in listofMembers with parameter String member
	public void setMember(int index, String member)
	{
		listofMembers.set(index, member);
	}
	
	//Removes the member in listofMembers at the index
	public void removeMember(int index)
	{
		listofMembers.remove(index);
	}
	
	//Adds String field to listofFields
	public void addField(String field)
	{
		listofFields.add(field);
	}
	
	//Returns the String at the index in listofFields
	public String getField(int index)
	{
		return listofFields.get(index);
	}
	
	//Sets the field in listofFields at the index with parameter String field
	public void setField(int index, String field)
	{
		listofFields.set(index, field);
	}
	
	//Removes the field in listofFields at the index
	public void removeField(int index)
	{
		listofFields.remove(index);
	}
	
	//Removes the Relationship from the relationships ArrayList
	//Method is called when the other class removes the relationship
	public void removeRelationship(Relationship relationship)
	{
		relationships.remove(relationship);
	}
	
	public NodePanel getNodePanel(){
		return nodePanel;
		
	}
	
	
}
