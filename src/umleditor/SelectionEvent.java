package umleditor;

import javax.swing.event.ChangeEvent;

public class SelectionEvent extends ChangeEvent
{
	private static final long serialVersionUID = -5342354909362780034L;

	public SelectionEvent(ISelectable source)
	{
		super(source);
	}

	@Override
	public ISelectable getSource()
	{
		return (ISelectable) this.source;
	}

}
