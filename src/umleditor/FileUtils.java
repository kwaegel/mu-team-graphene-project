package umleditor;

import com.thoughtworks.xstream.XStream;

/**
 * A collection of utility functions used during saving and loading of files.
 * 
 */
public class FileUtils
{

	public static XStream getXmlReaderWriter()
	{
		XStream xmlStream = new XStream();

		// Alias names for class diagram
		xmlStream.alias("diagram", ClassDiagram.class);
		xmlStream.aliasField("relationships", ClassDiagram.class, "m_relationships");

		// Alias names for class nodes
		xmlStream.alias("class", ClassNode.class);
		xmlStream.aliasField("name", ClassNode.class, "className");
		xmlStream.aliasField("attributes", ClassNode.class, "listOfAttributes");
		xmlStream.aliasField("methods", ClassNode.class, "listOfMethods");
		xmlStream.aliasField("relationships", ClassNode.class, "m_relationships");
		xmlStream.aliasField("location", ClassNode.class, "m_location");

		// Alias for relationship data
		xmlStream.alias("relationshipData", RelationshipModel.class);
		xmlStream.aliasField("type", RelationshipModel.class, "m_type");
		xmlStream.aliasField("pointList", RelationshipModel.class, "m_points");
		xmlStream.aliasField("type", RelationshipModel.class, "m_type");
		xmlStream.aliasField("firstClassNode", RelationshipModel.class, "m_firstNode");
		xmlStream.aliasField("secondClassNode", RelationshipModel.class, "m_secondNode");
		xmlStream.aliasField("firstNodeOffset", RelationshipModel.class, "m_firstNodeOffset");
		xmlStream.aliasField("secondNodeOffset", RelationshipModel.class, "m_secondNodeOffset");

		xmlStream.alias("point", java.awt.Point.class);

		// Alias field names

		return xmlStream;
	}

}
