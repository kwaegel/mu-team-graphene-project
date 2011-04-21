package umleditor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

/**
 * A collection of geometry and math helper functions.
 */
public final class MathHelper
{

	/**
	 * Return the point on the edge of a rectangle that is closest to the click point.
	 * 
	 * @param clickPoint
	 *            - the click point
	 * @param rect
	 *            - the {@link java.awt.Rectangle rectangle} to check.
	 * @return - the point on the rectangle nearest to the click point.
	 */
	public static Point getClosestEdgePoint(Point clickPoint, Rectangle rect)
	{
		Point nearestPoint = new Point(clickPoint);

		int maxX = rect.x + rect.width;
		int maxY = rect.y + rect.height;

		// Snap one of the coordinates to the side for points inside the rectangle
		if (rect.contains(clickPoint))
		{
			float centerDeltaX = (float) rect.getCenterX() - clickPoint.x;
			float centerDeltaY = (float) rect.getCenterY() - clickPoint.y;

			if (Math.abs(centerDeltaX) > Math.abs(centerDeltaY))
			{
				// Snap left if the delta is positive, else right.
				nearestPoint.x = (centerDeltaX > 0) ? rect.x : maxX;
			}
			else
			{
				// Snap up if the delta is positive, else down.
				nearestPoint.y = (centerDeltaY > 0) ? rect.y : maxY;
			}
		}
		else
		{
			// Check x coordinate for points outside the rectangle
			if (clickPoint.x < rect.x)
			{
				nearestPoint.x = rect.x;
			}
			else if (clickPoint.x > maxX)
			{
				nearestPoint.x = maxX;
			}

			// Check y coordinate for points outside the rectangle
			if (clickPoint.y < rect.y)
			{
				nearestPoint.y = rect.y;
			}
			else if (clickPoint.y > maxY)
			{
				nearestPoint.y = maxY;
			}
		}

		return nearestPoint;
	}

	/**
	 * Check if a path intersects a given rectangle. Used for checking click points.
	 * 
	 * @param lineItr
	 *            - a {@link PathIterator} to check for intersection with.
	 * @param clickArea
	 *            - the {@link Rectangle2D rectangle} to check intersection with.
	 * @return - true if the click area intersects the path.
	 */
	public static boolean intersectsPath(PathIterator lineItr, Rectangle2D clickArea)
	{
		boolean intersectsLine = false;
		/*
		 * Need to check line intersections by hand, as the intersects() method only checks shape containment, not line
		 * intersection. TODO: Find a more elegant way to do this.
		 */
		float[] nextPoints = new float[6];
		float[] lastPoints = new float[6];
		while (!lineItr.isDone() && !intersectsLine)
		{
			int segmentType = lineItr.currentSegment(nextPoints);
			if (segmentType == PathIterator.SEG_LINETO)
			{
				intersectsLine = clickArea.intersectsLine(lastPoints[0], lastPoints[1], nextPoints[0], nextPoints[1]);
			}
			System.arraycopy(nextPoints, 0, lastPoints, 0, 6);
			lineItr.next();
		}

		return intersectsLine;
	}
}
