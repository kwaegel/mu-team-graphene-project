package umleditor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JComponent;

public class GlassDrawingPane extends JComponent
{
	private static final long serialVersionUID = -1184484160896729337L;

	private Path2D.Float m_dragPath;

	public GlassDrawingPane()
	{

	}

	public void setDrawPath(Path2D.Float path)
	{
		m_dragPath = path;
		this.setVisible(true);
		repaint(m_dragPath.getBounds());
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		if (m_dragPath != null)
		{
			g2d.draw(m_dragPath);
		}
	}

}
