package lejos.nxt;

/**
 * This class allows communication of event data between the leJOS firmware and
 * the leJOS low level classes. It can be used to detect I/O completion, Port
 * values changing, button presses etc. To use create a class having the required
 * device type and filter (this may identify a particular port, or I/O operation).
 * Then call the waitEvent function to wait for events from the firmware. This
 * call will block until either the firmware signals an event or the timeout
 * occurs. Upon completion the eventData field will contain information about
 * the event(s) that have been reported. Events themselves are normally reset
 * by calling the associated function to read/write the associated device. If an
 * event is not cleared, it will be reported again in subsequent calls to eventWait.
 *
 *
 * <p><b>NOTE:</b> This is a low level system interface and should probably not be used
 * directly by user code.</p>
 *
 * @author andy
 */
public class NXTEvent {
    // Note the following fields are all shared with the the firmware
    // and should not be modified without care!
    private volatile int state;
    private NXTEvent sync;
    private int updatePeriod;
    private int updatePeriodCnt;
    private int type;
    private int filter;
    private volatile int eventData;
    private volatile int userEvents;

    /** Event type for no hardware events */
    public final static int NONE = 0;
    /** Event type for the Bluetooth device */
    public final static int BLUETOOTH = 1;
    /** Event type for the USB device */
    public final static int USB = 2;
    /** Event type for the RS485 device */
    public final static int RS485 = 3;
    /** Event type for the Analogue ports */
    public final static int ANALOG_PORTS = 4;
    /** Event type for the i2c ports */
    public final static int I2C_PORTS = 5;
    /** Event type for the NXT Buttons */
    public final static int BUTTONS = 6;
    /** Event type for system events */
    public final static int SYSTEM = 7;

    // Internal state flags
    private final static int WAITING = 1;
    private final static int SET = 2;

    public final static int TIMEOUT = 1 << 31;
    /** These bits are reserved in the eventData field to indicate that a user
     * event has occurred. User events are created by the  notifyEvent method.
     */
    public final static int USER1 = 1 << 29;
    public final static int USER2 = 1 << 28;
    public final static int USER3 = 1 << 27;
    public final static int USER4 = 1 << 26;

    /**
     * Value used to make a timeout be forever.
     */
    public final static long WAIT_FOREVER = 0x7fffffffffffffffL;


    /**
     * Register this event with the system.
     * Events must be registered before they are waited on.
     * @return >= 0 if the event has been registered < 0 if not.
     */
    public native int registerEvent();

    /**
     * Unregister this event. After calling this function the event should not
     * be waited on.
     * @return >= 0 if the event was unregistered < 0 if not
     */
    public native int unregisterEvent();

    /**
     * Set and clear events flags atomically
     * @param set events to be set
     * @param clear events to be cleared
     * @return the current events
     */
    private native int changeEvent(int set, int clear);

    /**
     * Wait for an event to occur or for the specified timeout. If a timeout occurs
     * then the TIMEOUT event bit will be set in the result. This bit is the
     * sign bit so timeouts can be detected by testing for a -ve result.
     * @param timeout the timeout in ms. Note a value of <= 0 will return immeadiately.
     * @return the event flags
     */
    public synchronized int waitEvent(long timeout) throws InterruptedException
    {
        if (timeout <= 0L) return TIMEOUT;
        sync = this;
        updatePeriodCnt = 0;
        eventData = userEvents & filter;
        state = WAITING;
        try
        {
            // If we already have a user event don't wait
            if (eventData != 0)
                state |= SET;
            else
            {
                wait(timeout);
                if ((state & SET) == 0)
                    eventData |= TIMEOUT;
            }
        }
        finally
        {
            changeEvent(0, eventData);
            state = 0;
        }
        return eventData;
    }


    /**
     * Wait for an event to occur using the specified filter
     * or for the specified timeout.
     * @param newFilter The type specific filter for this wait.
     * @param timeout the timeout in ms. Note a value of <= 0 will return immediately.
     * @return the event flags or 0 if the event timed out
     */
    public synchronized int waitEvent(int newFilter, long timeout) throws InterruptedException
    {
        int old = this.filter;
        int ret;
        filter = newFilter;
        try {
            ret = waitEvent(timeout);
        }
        finally
        {
            this.filter = old;
        }
        return ret;
    }

    /**
     * Wait for multiple events.
     * @param events an array of events to wait on.
     * @param timeout the wait timeout. Note a value of <= 0 will return immediately.
     * @return true if an event occurred, false otherwise.
     */
    public static boolean waitEvent(NXTEvent[] events, long timeout) throws InterruptedException
    {
        // We always use the first event as the one to synchronize on.
        NXTEvent sync = events[0];
        synchronized(sync)
        {
            // Make all of the events share the same notifier
            for(int i = 1; i < events.length; i++)
            {
                events[i].eventData = 0;
                events[i].sync = sync;
                events[i].updatePeriodCnt = 0;
            }
            boolean ret;
            try {
                ret = (sync.waitEvent(timeout) & TIMEOUT) == 0;
            }
            finally
            {
                for(int i = 1; i < events.length; i++)
                {
                    events[i].sync = events[i];
                    events[i].changeEvent(0, events[i].eventData);
                }
            }
            return ret;
        }
    }

    /**
     * This call can be used to raise a user event. User events will wake a thread
     * from an eventWait call. 
     * @param event
     */
    public void notifyEvent(int event)
    {
        changeEvent(event, 0);
    }

    /**
     * Clear an event. 
     * @param event The events to be cleared.
     */
    public void clearEvent(int event)
    {
        changeEvent(0, event);
    }

    public int getEventData()
    {
        return eventData;
    }
    /**
     * Set the filter to be applied to this event.
     * @param filter The new filter value.
     */
    public synchronized void setFilter(int filter)
    {
        this.filter = filter;
    }

    /**
     * Return the current filter settings.
     * @return the filter
     */
    public synchronized int getFilter()
    {
        return filter;
    }

    private static NXTEvent cache;
    /**
     * Create a new event ready for use.
     * @param type The event type.
     * @param filter The event specific filter.
     * @param update The update period used when checking the event.
     * @return The new event object.
     */
    public static synchronized NXTEvent allocate(int type, int filter, int update)
    {
        NXTEvent event = cache;
        if (event == null)
            event = new NXTEvent();
        else
            cache = null;

        event.type = type;
        event.filter = filter;
        event.updatePeriod = update;
        event.registerEvent();
        event.userEvents = 0;
        return event;
    }

    /**
     * Release an event.
     */
    public void free()
    {
        unregisterEvent();
        synchronized(NXTEvent.class)
        {
            cache = this;
        }
    }
}
