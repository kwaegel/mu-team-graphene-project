package umleditor;

import javax.swing.JPopupMenu;

/**
 * Defines a object than can be edited with a context menu.
 */
public interface IEditable
{
	/**
	 * @return - the default pop-up menu for this object.
	 */
	public JPopupMenu getPopupMenu();
}
