package lejos.nxt.debug;

import lejos.nxt.*;

/**
 * Simple debug monitor that can be run alongside and nxj program. This class
 * catches un-handled exceptions and user interrupts (accept + escape key), it
 * displays information about the event (stack trace etc.). The user is then
 * able to either perform a soft reset (Escape), a hard reset (Escape + Accept),
 * or continue running the program (any other key). All output is directed via
 * System.err.
 * @author andy
 */
public class DebugMonitor
{
    protected DebugInterface monitor;
    /**
     * Display information about the uncaught exception on System.err
     * @param info the current VM event
     */
    protected void displayException(DebugInterface info)
    {
        VM vm = VM.getVM();
        int [] trace = VM.getThrowableStackTrace(info.exception);
        System.err.println("Exception: " + vm.getVMClass(info.exception).getClassNo());
        String msg = info.exception.getMessage();
        int cnt = 0;
        if (msg != null && msg.length() > 0)
        {
            System.err.println(msg);
            cnt++;
        }
        if (trace != null)
        {
            for(int sf : trace)
            {
                System.err.println(" at: " + (sf >> 16) + "(" + (sf & 0xffff) + ")");
                if (cnt++ > 6) break;
            }

        }
        else
            // No trace available probably we have run out of memory, so just
            // display the basics
            System.err.println(" at: " + info.method + "(" + info.pc + ")");

        // Mark thread as daemon to avoid system hang
        info.thread.setDaemon(true);
    }

    static String[] states = {"N", "D", "I", "R", "E", "W", "S"};

    /**
     * Dump information about all of the active threads to System.err. The
     * threads are dumped in reverse order (low priority first), so that if
     * there are more then eight threads the most important 8 will still be
     * on the LCD display!
     * @param info
     */
    protected void displayThreads(DebugInterface info)
    {
        VM vm = VM.getVM();
        VM.VMThreads threads = vm.getVMThreads();
        for(VM.VMThread thread : threads)
        {

            String out = "";
            out += thread.threadId;
            out += (thread.getJavaThread() == info.thread ? "*" : states[thread.state & 0x7f]);
            int cnt = 0;
            VM.VMStackFrames stack = thread.getStackFrames();
            for(VM.VMStackFrame frame : stack)
            {
                out += " " + frame.getVMMethod().getMethodNumber();
                if (++cnt >= 3) break;
            }
            System.err.println(out);
        }
    }

    protected void exit()
    {
        System.exit(1);
    }


    protected void processEvent(int event)
    {
        LCD.clear();
        switch (event)
        {
            case DebugInterface.DBG_EXCEPTION:
                displayException(monitor);
                break;
            case DebugInterface.DBG_USER_INTERRUPT:
                displayThreads(monitor);
                break;
            case DebugInterface.DBG_PROGRAM_EXIT:
                System.err.println("Program exit");
                exit();
                break;
        }
        LCD.refresh();
        Sound.playTone(73, 150);
        Sound.pause(300);
        Sound.playTone(62, 500);
        // Wait for any buttons to be released
        while (Button.readButtons() != 0)
            Thread.yield();
        // Enable user interrupts again
        monitor.setEventOptions(DebugInterface.DBG_USER_INTERRUPT, DebugInterface.DBG_EVENT_ENABLE);
        // and wait to see what the user wants to do
        int pressed = Button.waitForAnyPress();
        // If escape do soft-reboot
        if ((Button.ESCAPE.getId() & pressed) != 0)
            exit();
        // Otherwise try and continue gulp!
        LCD.clear();
        // Clear the event and continue
        monitor.resumeProgram();
    }


    protected void monitorEvents()
    {
        // Setup the monitoring thread.
        monitor = DebugInterface.get();
        monitor.setEventOptions(DebugInterface.DBG_EXCEPTION, DebugInterface.DBG_EVENT_ENABLE);
        monitor.setEventOptions(DebugInterface.DBG_USER_INTERRUPT, DebugInterface.DBG_EVENT_ENABLE);
        monitor.setEventOptions(DebugInterface.DBG_PROGRAM_EXIT, DebugInterface.DBG_EVENT_ENABLE);
        // Start the real program in a new thread.
        monitor.startProgram(1);
        while (true)
        {
            // Wait for a debug event
            int event = monitor.waitEvent(0);
            monitor.resumeSystemThreads();
            processEvent(event);
        }
    }

    public static void main(String[] args) throws Exception
    {
        // Simply create and run the monitor.
        new DebugMonitor().monitorEvents();
    }
}

