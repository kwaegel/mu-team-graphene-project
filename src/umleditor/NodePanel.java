package umleditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class NodePanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 912113941232687505L;

	ClassNode associatedNode;
	ClassDiagram parentDiagram;

	public NodePanel(ClassDiagram parent, ClassNode node) {
		super();

		parentDiagram = parent;
		associatedNode = node;
		associatedNode.attachPanel(this);

		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setLayout(new MigLayout("wrap 1", "", ""));
		this.createDisplay();
		this.addMouseListener(this);
	}

	public void makeUnselected() {
		this.setBackground(Color.white);
	}

	public void makeSelected() {
		this.setBackground(Color.pink);
	}

	// recreated display from values in classNode
	private void createDisplay() {
		// clear everything in the class diagram
		this.removeAll();

		// add class name
		String className = associatedNode.getName();
		JLabel titleLable = new JLabel(className);
		this.add(titleLable, "align center");

		// add separator
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setPreferredSize(new Dimension(80, 1));
		this.add(separator);

		// add fields
		for (int i = 0; i < associatedNode.getNumFields(); ++i) {
			String fieldName = associatedNode.getField(i);
			JLabel fieldLabel = new JLabel(fieldName);
			this.add(fieldLabel);
		}

		// add separator
		JSeparator separator2 = new JSeparator(SwingConstants.HORIZONTAL);
		separator2.setPreferredSize(new Dimension(80, 1));
		this.add(separator2);

		// add members
		for (int i = 0; i < associatedNode.getNumMembers(); ++i) {
			String memberName = associatedNode.getMember(i);
			JLabel memberLabel = new JLabel(memberName);
			this.add(memberLabel);
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int clickCount = e.getClickCount();
		if (clickCount > 1) {
			// open edit dialog
		} else {
			parentDiagram.setSelectedNode(associatedNode);
			this.makeSelected();
		}
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
		parentDiagram.addRelationship(this);

	}

}
