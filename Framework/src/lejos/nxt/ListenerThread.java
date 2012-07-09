package lejos.nxt;

/**
 * Utility class for dispatching events to button,  and sensor port listeners.
 * Re-worked to use new event mechanism by Andy
 * @author Paul Andrews, Andy
 */
class ListenerThread extends Thread
{

    static ListenerThread singleton = null;
    private NXTEvent[] events = new NXTEvent[0];
    private ListenerCaller[] callers = new ListenerCaller[0];

    /**
     * Return the singleton that provides access to the listener system.
     * @return The listener object
     */
    static synchronized ListenerThread get()
    {
        if (singleton == null)
        {
            singleton = new ListenerThread();
            singleton.setDaemon(true);
            singleton.setPriority(Thread.MAX_PRIORITY);
        }
        return singleton;
    }

    /**
     * Add a new listener.
     * @param eventType The event type used for this object
     * @param filter The initial event filter
     * @param update How often to check for a change
     * @param lc The listener caller to call when things change
     */
    synchronized void addListener(int eventType, int filter, int update, ListenerCaller lc)
    {
        NXTEvent[] newEvents = new NXTEvent[events.length+1];
        System.arraycopy(events, 0, newEvents, 0, events.length);
        newEvents[events.length] = NXTEvent.allocate(eventType, filter|NXTEvent.TIMEOUT, update);
        events = newEvents;
        ListenerCaller[] newCallers = new ListenerCaller[callers.length+1];
        System.arraycopy(callers, 0, newCallers, 0, callers.length);
        newCallers[callers.length] = lc;
        callers = newCallers;
        // Either start the process, or restart it with the new event
        if (events.length == 1)
            singleton.start();
        else
            events[0].notifyEvent(NXTEvent.TIMEOUT);
    }


    @Override
    public void run()
    {
        try {
            for (;;)
            {
                setPriority(Thread.MAX_PRIORITY);
                if (NXTEvent.waitEvent(events, NXTEvent.WAIT_FOREVER))
                {
                    // Run events at normal priority so they can use Thread.yield()
                    setPriority(Thread.NORM_PRIORITY);
                    for(int i = 0; i < callers.length; i++)
                    {
                        // Check to see if this event was triggered, if so call the
                        // associated listener
                        if (events[i].getEventData() > 0)
                        {
                            int ret = callers[i].callListeners();
                            // Set the new filter data
                            events[i].setFilter(ret|NXTEvent.TIMEOUT);
                        }
                    }
                }
            }
        }
        catch(InterruptedException e)
        {
            // Must have been told to abort. So exit
            return;
        }
    }
}
