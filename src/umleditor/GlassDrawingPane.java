package umleditor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;

import javax.swing.JComponent;

/**
 * A line that appears showing how the draw path of a relationship. When the user is dragging their mouse from the first
 * class to the second, the line will draw from where they initially pressed down the mouse button to the current
 * position of the mouse. The line will redraw itself whenever the mouse changes position. This line disappears after
 * the user releases the mouse button.
 */
public class GlassDrawingPane extends JComponent
{
	private static final long serialVersionUID = -1184484160896729337L;

	private Path2D.Float m_dragPath;

	/**
	 * The area to use for drawing. This allows the glass pane to be restricted to a specific area of the window.
	 */
	Rectangle m_drawableArea;

	/**
	 * Default constructor.
	 */
	public GlassDrawingPane()
	{
		m_drawableArea = getBounds();
	}

	/**
	 * Set the area of the window to use for drawing.
	 * 
	 * @param area
	 *            - the area to use for drawing.
	 */
	public void setDrawableArea(Rectangle area)
	{
		m_drawableArea = area;
	}

	/**
	 * Sets the dimensions of the line to be drawn.
	 * 
	 * @param path
	 *            - the path to draw on the screen.
	 */
	public void setDrawPath(Path2D.Float path)
	{
		m_dragPath = path;
		this.setVisible(true);
		Rectangle repaintArea = new Rectangle();
		Rectangle.intersect(m_dragPath.getBounds(), m_drawableArea, repaintArea);
		repaint(repaintArea);
	}

	/**
	 * Draws the line based on m_dragPath if it isn't null.
	 */
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
