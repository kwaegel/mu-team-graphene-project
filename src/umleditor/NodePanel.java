package umleditor;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class NodePanel extends JPanel implements MouseListener{
	
	ClassNode classNode;
	ClassDiagram parentDiagram;
	
	public NodePanel(ClassDiagram parent)
	{
		parentDiagram = parent;
		this.addMouseListener(this);
	}
	
	public void setNode(ClassNode node)
	{
		classNode = node;
	}
	
	public void makeUnselected(){
		this.setBackground(Color.white);
	}
	
	public void makeSelected(){
		this.setBackground(Color.pink);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.makeSelected();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
