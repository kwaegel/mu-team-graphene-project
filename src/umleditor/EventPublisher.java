package umleditor;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Notifies all current listeners of a change or selection if it is relevant to the listener
 */
public class EventPublisher extends EventListenerList
{
	private static final long serialVersionUID = 8368345460586618761L;

	/**
	 * Default constructor.
	 */
	public EventPublisher()
	{
	}

	/**
	 * Notifies any interested listeners that an object has been changed.
	 */
	public void fireChangeEvent(Object changedObject)
	{
		ChangeEvent changeEvent = new ChangeEvent(changedObject);

		// Guaranteed to return a non-null array
		Object[] listeners = getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ChangeListener.class)
			{
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	/**
	 * Notify any interested listeners that a diagram object has been selected.
	 * 
	 * @param selectedObject
	 */
	public void fireSelectedEvent(ISelectable selectedObject)
	{
		SelectionEvent selectionEvent = new SelectionEvent(selectedObject);

		// Guaranteed to return a non-null array
		Object[] listeners = getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == SelectionListener.class)
			{
				((SelectionListener) listeners[i + 1]).objectSelected(selectionEvent);
			}
		}
	}
}
