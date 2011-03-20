package umleditor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

public class RelationshipDragController extends MouseAdapter
{
	private LinkedList<Relationship> m_relationships;

	public RelationshipDragController(LinkedList<Relationship> relationships)
	{
		m_relationships = relationships;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

}
