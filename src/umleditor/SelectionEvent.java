package umleditor;

import javax.swing.event.ChangeEvent;

/**
 * Used with EventPublisher. Informs the Event Publisher if an object has been selected.
 */
public class SelectionEvent extends ChangeEvent
{
	private static final long serialVersionUID = -5342354909362780034L;

	private transient boolean m_singleSelectionRequested;

	/**
	 * construct a new selector event. The source object will be the only one selected.
	 * 
	 * @param source
	 *            - the object being selected.
	 */
	public SelectionEvent(ISelectable source)
	{
		this(source, true);
	}

	/**
	 * @param source
	 *            - the object being selected.
	 * @param singleSelection
	 *            - true to request that this object be the only one selected.
	 */
	public SelectionEvent(ISelectable source, boolean singleSelection)
	{
		super(source);
		m_singleSelectionRequested = singleSelection;
	}

	/**
	 * Returns the selected ISelectable object(s)
	 */
	@Override
	public ISelectable getSource()
	{
		return (ISelectable) this.source;
	}

	/**
	 * @return true if only one object is selected.
	 */
	public boolean isSingleSelectionRequested()
	{
		return m_singleSelectionRequested;
	}

	/**
	 * Returns a string stating the selected ISelectable object(s)
	 */
	@Override
	public String toString()
	{
		return ("Selected:" + source);
	}

}
