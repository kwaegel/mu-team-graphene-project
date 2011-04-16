package umleditor;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class EventPublisher extends EventListenerList
{
	private static final long serialVersionUID = 8368345460586618761L;

	private ChangeEvent m_cachedChangeEvent = null;
	private transient SelectionEvent m_cachedSelectionEvent;

	public EventPublisher()
	{
	}

	public void fireChangeEvent(Object changedObject)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ChangeListener.class)
			{
				// Lazily create the event:
				if (m_cachedChangeEvent == null)
				{
					m_cachedChangeEvent = new ChangeEvent(changedObject);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(m_cachedChangeEvent);
			}
		}

		m_cachedChangeEvent = null;
	}

	/**
	 * Notify any interested listeners that a diagram object has been selected.
	 * 
	 * @param selectedObject
	 */
	public void fireSelectedEvent(ISelectable selectedObject)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == SelectionListener.class)
			{
				// Lazily create the event:
				if (m_cachedSelectionEvent == null)
				{
					m_cachedSelectionEvent = new SelectionEvent(selectedObject);
				}
				((SelectionListener) listeners[i + 1]).objectSelected(m_cachedSelectionEvent);
			}
		}

		m_cachedChangeEvent = null;
	}
}
