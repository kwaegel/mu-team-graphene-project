/**
 * 
 */
package umleditor;

import java.util.EventListener;

public interface SelectionListener extends EventListener
{

	/**
	 * Invoked when an object is selected.
	 */
	public void objectSelected(SelectionEvent e);
}
