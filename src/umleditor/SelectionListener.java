/**
 * 
 */
package umleditor;

import java.util.EventListener;

/**
 * Defines an object that can listen for selection events.
 * 
 */
public interface SelectionListener extends EventListener
{

	/**
	 * Invoked when an object is selected.
	 * 
	 * @param e
	 *            - the selection event.
	 */
	public void objectSelected(SelectionEvent e);
}
