package umleditor;

import javax.swing.event.ChangeEvent;

/**
 * Used with EventPublisher. Informs the Event Publisher if an object has been 
 * selected.
 */
public class SelectionEvent extends ChangeEvent
{
	private static final long serialVersionUID = -5342354909362780034L;

	private transient boolean m_singleSelectionRequested;

	public SelectionEvent(ISelectable source)
	{
		this(source, true);
	}

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
	 * Returns true if only one object is selected
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
