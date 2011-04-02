package umleditor;

import com.thoughtworks.xstream.XStream;

/**
 * A collection of utility functions used during saving and loading of files.
 * 
 */
public class FileUtils
{

	/**
	 * 
	 * @return an {@link XStream} object to be used of reading and writing.
	 */
	public static XStream getXmlReaderWriter()
	{
		XStream xmlStream = new XStream();

		// Alias names for class diagram
		xmlStream.alias("diagram", ClassDiagram.class);
		xmlStream.aliasField("relationships", ClassDiagram.class, "m_relationships");

		// Alias names for class nodes
		xmlStream.alias("class", ClassNode.class);
		xmlStream.aliasField("name", ClassNode.class, "className");
		xmlStream.useAttributeFor(ClassNode.class, "className");
		xmlStream.aliasField("attributes", ClassNode.class, "listOfAttributes");
		xmlStream.aliasField("methods", ClassNode.class, "listOfMethods");
		xmlStream.aliasField("relationships", ClassNode.class, "m_relationships");
		xmlStream.aliasField("location", ClassNode.class, "m_location");

		// Alias for relationship data
		xmlStream.alias("relationship", RelationshipModel.class);
		xmlStream.aliasField("type", RelationshipModel.class, "m_type");
		xmlStream.useAttributeFor(RelationshipModel.class, "m_type");
		xmlStream.aliasField("points", RelationshipModel.class, "m_points");
		xmlStream.aliasField("firstClassNode", RelationshipModel.class, "m_firstNode");
		xmlStream.aliasField("secondClassNode", RelationshipModel.class, "m_secondNode");
		xmlStream.aliasField("firstNodeOffset", RelationshipModel.class, "m_firstNodeOffset");
		xmlStream.aliasField("secondNodeOffset", RelationshipModel.class, "m_secondNodeOffset");

		xmlStream.alias("point", java.awt.Point.class);
		xmlStream.useAttributeFor(java.awt.Point.class, "x");
		xmlStream.useAttributeFor(java.awt.Point.class, "y");

		// Alias field names

		return xmlStream;
	}

}
