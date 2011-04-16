package umleditor;

import javax.swing.event.ChangeEvent;

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

	@Override
	public ISelectable getSource()
	{
		return (ISelectable) this.source;
	}

	public boolean isSingleSelectionRequested()
	{
		return m_singleSelectionRequested;
	}

}
