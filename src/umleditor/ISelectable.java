package umleditor;

/**
 * Defines a UML diagram object that can be selected.
 * 
 */
public interface ISelectable
{
	/**
	 * Notify an object that it has been selected or not.
	 * 
	 * @param selected
	 */
	public abstract void setSelected(boolean selected);
};
