package lejos.nxt.debug;
import lejos.nxt.VM;

/**
 * This class provides the primary interface to the debug capabilities of leJOS.
 * it provides an event based mechanism that allows Java applications to wait for
 * debug events from the VM.
 * @author andy
 */
public class DebugInterface
{
    private static final DebugInterface monitor = new DebugInterface(true);
    public static final int DBG_NONE = 0;
    public static final int DBG_EXCEPTION = 1;
    public static final int DBG_USER_INTERRUPT = 2;
    public static final int DBG_PROGRAM_EXIT = 3;
    public static final int DBG_EVENT_DISABLE = 0;
    public static final int DBG_EVENT_ENABLE = 1;
    public static final int DBG_EVENT_IGNORE = 2;
    // This is reflected in the kernel structure  
    public int typ;                // type of debug event
    public Throwable exception;
    public Thread thread;
    public int method;
    public int pc;

    // Additional fields no used by the kernel
    public Thread[] systemThreads;

    /**
     * Private constructor. Sets up the event interface in the kernel.
     */
    private DebugInterface(boolean dummy)
    {
        setDebug();
        typ = 0;
    }

    public static DebugInterface get()
    {
        return monitor;
    }

    /**
     * Clear the reported event.
     */
    public void clearEvent()
    {
        typ = 0;
    }

    /**
     * Wait for a debug event from the kernel
     *
     * @param millis wait for at most millis milliseconds. 0 = forever.
     * @return The new debug event
     */
    public final int waitEvent(int millis)
    {
        synchronized (monitor)
        {
            // Allow exit if the program finishes normally.
            Thread.currentThread().setDaemon(true);
            if (monitor.typ == DBG_NONE)
            {
                // Wait for an event, allow the wait to be interrupted.
                try {
                    monitor.wait(millis);
                } catch (InterruptedException e){}
            }
            // make sure we do not exit, if we are the only thread running.
            Thread.currentThread().setDaemon(false);
            return monitor.typ;
        }
    }

    /**
     * Take a snapshot of the current set of threads. These threads will be
     * resumed by a call to resumeSystemThreads. These threads are typically
     * required to perform I/O etc, and may be needed to allow the debug code
     * to communicate with a host system.
     */
    public void recordSystemThreads()
    {
        int cnt = 0;
        // First count them...
        for(VM.VMThread t : VM.getVM().getVMThreads())
            cnt++;
        // now take a snapshot.
        systemThreads = new Thread[cnt];
        cnt = 0;
        for(VM.VMThread t : VM.getVM().getVMThreads())
            systemThreads[cnt++] = t.getJavaThread();
        if (cnt != systemThreads.length) throw new AssertionError("bad thread count");
    }

    /**
     * Start a program to be monitored, running in a new thread.
     * @param progId The program number
     * @return the status
     */
    public final int startProgram(final int progId)
    {
        recordSystemThreads();
        // Start the real program in a new thread.
        Thread prog = new Thread()
        {

            @Override
            public void run()
            {
                 VM.executeProgram(progId);
                 // This point will never be reached
            }
        };
        // Make sure we keep running when we start the program
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        // Enable stricter run time type checking
        VM.setVMOptions(VM.getVMOptions() | VM.VM_TYPECHECKS);
        prog.start();
        return 1;
    }

    /**
     * Allow the monitored program to continue running.
     */
    public void resumeProgram()
    {
        clearEvent();
        VM.resumeThread(null);
    }

    /**
     * Resume essential system threads.
     */
    public void resumeSystemThreads()
    {
        for(Thread t : systemThreads)
            VM.resumeThread(t);
    }
    /**
     * Allow events to be enabled/disabled/ignored. Disabled events will
     * return to the default behaviour. Enabled events will be reported via
     * this interface. Ignored events will be discarded.
     * @param event
     * @param option
     * @return previous state of this event.
     */
    public int setEventOptions(int event, int option)
    {
        return eventOptions(event, option);
    }


    /**
     * Initialise the debug interface
     */
    private native final void setDebug();
    
    /**
     * Allow events to be enabled/disabled/ignored. Disabled events will
     * return to the default behaviour. Enabled events will be reported via
     * this interface. Ignored events will be discarded.
     * @param event
     * @param option
     * @return previous state of this event.
     */
    private native static final int eventOptions(int event, int option);
    


}
