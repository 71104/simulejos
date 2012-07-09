package lejos.nxt.comm;

import java.io.*;
import lejos.nxt.*;



/**
 * Provides a Bluetooth connection
 * Supports both packetized, raw and stream based communication.
 * Blocking and non-blocking I/O.
 * Notes:
 * Because of the limited buffer space and the way that several connections
 * have to share the interface to the Bluetooth device data may be lost. This
 * will happen if a switch into command mode is required when there is data
 * arriving from the remote connection that can not be placed into the input
 * buffer. Every attempt is made to avoid this but it can happen. Application
 * programs can help avoid this problem by:
 * 1) Using just a single Bluetooth connection
 * 2) Using Bluetooth commands while data transfers are in progress.
 * 3) Performing application level flow control to avoid more then 256 bytes
 *    of data being sent from the remote side at any one time.
 * 4) Reading any pending data as soon as possible.
 * If data is lost then calls to read and write will return -2 to indicate the
 * problem. If using packet mode then the input stream can be re-synchronized
 * by issuing a read to discard the partial packet which may be in the input
 * buffer.
 * 
 * When operating in RAW mode bytes are read/written as is. This mode is useful
 * for talking to none leJOS/Lego devices.
 * When operating in PACKET mode the standard Lego 2 byte header is added to
 * each packet (and is expected to be present on each incoming packet). Use this
 * mode when talking to other leJOS/Lego devices.
 */
public class BTConnection extends NXTConnection
{
	
	private static final int BTC_FLUSH_WAIT = 20;
	
	public static final int AM_DISABLE = 0;
	public static final int AM_ALWAYS = 1;
	public static final int AM_OUTPUT = 2;

	int chanNo;
	byte handle;
	int switchMode;
    volatile boolean active;

	public BTConnection(int chan)
	{
		state = CS_IDLE;
		chanNo = chan;
        bufSz = Bluetooth.BUFSZ;
        maxPkt = 0xffff;
        active = false;
		is = null;
		os = null;
	}
	
	synchronized void reset()
	{
		// Called by the low level implementation if things go wrong!
		state = CS_IDLE;
		inBuf = null;
		outBuf = null;
		notifyAll();
	}

	
	/**
	 * Bind the low level I/O handle to a connection object
	 * set things up ready to go.
	 */
	synchronized void bind(byte handle, String address, int mode)
	{
		if (inBuf == null )
			inBuf = new byte[Bluetooth.BUFSZ];
		if (outBuf == null)
			outBuf = new byte[Bluetooth.BUFSZ];
        setIOMode(mode);
		inCnt = 0;
		inOffset = 0;
		outCnt = 0;
		outOffset = 0;
		state = CS_CONNECTED;
		switchMode = AM_ALWAYS;
		this.handle = handle;
		pktLen = 0;
		this.address = address;
	}


    /**
     * Send an EOF packet to the remote system.
     */
    synchronized void sendEOF()
    {
        // Nothing to do for Bluetooth. We rely on the underlying transport
        // for EOF
    }

    /**
     * Disconnect the device/channel
     */
    void disconnect()
    {
        //LCD.drawString("disconnect", 0, 5);
        Bluetooth.closeConnection(handle);
    }
    
	
	/**
	 * Low level output function. Take any data in the output buffer and write
	 * it to the device. Called by the Bluetooth thread when this channel is
	 * active, to perform actual data I/O.
	 */
	synchronized int send()
	{
		//RConsole.print("send\n");
		if (outOffset >= outCnt) return Bluetooth.BT_NEWDATA;
        //RConsole.println("Pending " + Bluetooth.btPending());
		// Transmit the data in the output buffer
		int cnt = Bluetooth.btWrite(outBuf, outOffset, outCnt - outOffset);
		//1 RConsole.print("Send " + cnt + "\n");
		outOffset += cnt;
		if (outOffset >= outCnt)
		{
			//RConsole.print("Send complete\n");
			outOffset = 0;
			outCnt = 0;
			notifyAll();
            return Bluetooth.BT_NEWDATA;
        }
        return Bluetooth.BT_WRITEABLE;
	}

    /**
     * Write all of the current output buffer to the device.
     * NOTE: To ensure correct operation of packet mode, this function should
     * only return 1 if all of the data will eventually be written. It should
     * avoid writing part of the data. 
     * @param wait if true wait until the output has been written
     * @return -ve if error, 0 if not written, +ve if written/no data
     */
    synchronized int flushBuffer(boolean wait)
    {
        // No data to send so say all done.
        if (outOffset >= outCnt) return 1;
        if (active) Bluetooth.notifyEvent(Bluetooth.BT_NEWDATA);
        if (wait)
            try {wait();} catch(Exception e){}
        // All of the data should go eventually, so say everything is ok.
        return 1;
    }

	/**
	 * Low level input function. Called by the Bluetooth thread to transfer
	 * input from the system into the input buffer.
	 */
	synchronized int recv()
	{
		//1 RConsole.print("recv\n");
		// Read data into the input buffer
		while (inCnt < inBuf.length)
		{
			if (inCnt == 0) inOffset = 0;
			int offset = (inOffset + inCnt) % inBuf.length;
			int len = (offset >= inOffset ? inBuf.length - offset : inOffset - offset);
			//RConsole.print("inCnt " + inCnt + " inOffset " + inOffset + " offset " + offset + " len " + len + "\n");
			int cnt = Bluetooth.btRead(inBuf, offset, len);
            //if (cnt < len && cnt < 100) RConsole.println("rd " + cnt + " sp " + len);
			if (cnt <= 0) break;
			inCnt += cnt;
			//1 RConsole.print("recv " + inCnt + "\n");
		}
		if (inCnt > 0) notifyAll();
        // Decide what event is needed to be able to progress
        return (inCnt >= inBuf.length ? Bluetooth.BT_NEWSPACE : Bluetooth.BT_READABLE);
	}

    /**
     * Get any available data into the input buffer.
     * @param wait if true wait for data to be available.
     * @return -ve if error, 0 if not read, +ve if read
     */
    synchronized int fillBuffer(boolean wait)
    {
        if (inCnt > 0) return inCnt;
        if (active) Bluetooth.notifyEvent(Bluetooth.BT_NEWSPACE);
        if (wait)
            try{wait();}catch(Exception e){}   
        return inCnt;
    }

			
	/**
	 * Low level function called by the Bluetooth thread. It basically answers
	 * the question: Should I switch to this channel and perform I/O? The answer
	 * to this question can be controlled using the setActiveMode method.
	 * @return			true if the channel is interesting!
	 */
	synchronized boolean needsAttention()
	{
		//1 if (chanNo == 0) RConsole.print("na s" + state + " i " + inCnt + "\n");
		//RConsole.print("needs attention\n");
		// return true if we need to perform low level I/O on this channel
		if (state < CS_DISCONNECTING || switchMode == AM_DISABLE) return false;
		// If we have any output then need to send it
		if (outOffset < outCnt) return true;
		if (switchMode == AM_OUTPUT) return false;
		// If we do not have any input need to see if there is more waiting
		if (inCnt <= 0) return true;
		return false;
	}
	
	/**
	 * Set the channel switching mode. Allows control of when we will switch to
	 * this channel. By default we will switch to this channel to check for
	 * input. However if AM_OUTPUT is set we only switch if we have output
	 * waiting to be sent.
	 * @param	mode	The switch control mode.
	 */
	public void setActiveMode(int mode)
	{
		switchMode = mode;
        Bluetooth.notifyEvent(Bluetooth.BT_NEWCMD);
	}

	
	private boolean pendingInput()
	{
		return (Bluetooth.btPending() & Bluetooth.BT_READABLE) != 0;
	}

	/**
	 * Prepare the low level Bluetooth interface for a switch into command mode.
	 * To switch to command mode we need to be sure that there is no pending
	 * input for this channel. To do this we ready any data into the available
	 * input buffers. If all else fails we discard data. When we return the
	 * interface should be ready to be switched.
	 */
	synchronized void flushInput(boolean discard)
	{
		// Need to be sure that there is no input in the input buffer before
		// we switch mode. 
		if (state == CS_IDLE) return;
		//RConsole.println("Flush " + discard);
		// Try to empty the low level input buffer while giving the 
		// application chance to help by reading the data.
        int cnt = 0;
		int timeout = (int)System.currentTimeMillis() + BTC_FLUSH_WAIT;
		while (inCnt < inBuf.length && (pendingInput() || timeout > (int)System.currentTimeMillis()))
		{
            //RConsole.println("PI" + pendingInput() + " ic " + inCnt);
			// Read as much as we can
			recv();
			// Give the app chance to process it
			try{wait(1);}catch(Exception e){}
            // Connection may have been closed down, check for this and give up
            if (state == CS_IDLE) return;
            cnt++;
		}
        //RConsole.println("flush cnt " + cnt);
		if (!discard || !pendingInput()) return;
		//RConsole.print("Dropping packets\n");
		// If we still have input we are now in big trouble we will have
		// to discard data. Note even if we read all of the data we need
		// to linger a little to see if more arrives.
		timeout = (int)System.currentTimeMillis() + BTC_FLUSH_WAIT;
		while (pendingInput() || (timeout > (int)System.currentTimeMillis()))
		{
			while (read(null, inBuf.length, false) > 0)
				;
			recv();
		}
		// Mark the channel as having lost data
		if (state == CS_CONNECTED)
			state = CS_DATALOST;
	}
	

	/**
	 * Close the stream for this connection.
	 * This suspends the connection and switch the BC4 chip to command mode.
	 *
	 */
	public void closeStream() {
		// Nothing to do for Bluetooth
	}
	
	/**
	 * Open the stream for this connection.
	 * This resumes the connection and switches the BC4 chip to data mode.
	 *
	 */
	public void openStream() {
		// Nothing to do for Bluetooth
	}
	
	/**
	 * Get the signal strength of this connection.
	 * This necessitates closing and reopening the data stream.
	 *  
	 * @return a value from 0 to 255
	 */
	public int getSignalStrength() {
		int strength = Bluetooth.getSignalStrength(handle); 
		return strength;
	}
}