package lejos.nxt.comm;

import lejos.nxt.*;
import java.io.*;
import lejos.util.Delay;


/**
 * 
 * Provides a USB connection
 * Supports both packetized, raw and stream based communication.
 * Blocking and non-blocking I/O.
 *
 * Notes
 * When using the low level read/write functions no buffering is provided. This
 * means that for read operations te entire packet must be read or data will
 * be lost. A USB packet has a max size of 64 bytes. The Stream based functions
 * take care of this automatically.
 * 
 * When operating in RAW mode low level USB packets may be read and written.
 * however this mode has no concept of EOF or start of a connection.
 * When using PACKET mode each packet has a single byte header added to it. This
 * is used to provide a simple start of connection/EOF model.
 */
public class USBConnection extends NXTConnection 
{
    static final int HDRSZ = 2;
    // The following var controls how much data we will attempt to buffer for a read.
    // In all modes we ensure that we can always issue a read with at least HW_BUFSZ
    // bytes in the buffer to ensure no data is lost. When operating in LCP mode the
    // protocol relies on the hardware packet boudaries so we ensure that we never
    // attempt to read more than a single packet.
    private int readThreshold = 0;
    
    
 	public USBConnection(int mode)
	{
		state = CS_CONNECTED;
        bufSz = USB.BUFSZ;
        // Only one packet per buffer
        maxPkt = 0xffff;
        // Allow more input buffer space for faster overlapped I/O
        inBuf = new byte[USB.BUFSZ];
        outBuf = new byte[USB.BUFSZ];
        is = null;
        os = null;
        setIOMode(mode);
    }

	/**
	 * Low level output function. Take any data in the output buffer and write
	 * it to the device. Called by the USB thread when this channel is
	 * active, to perform actual data I/O.
     * @return the event to wait for
	 */
	synchronized int send()
	{
		//RConsole.print("send\n");
		if (outOffset >= outCnt) return USB.USB_NEWDATA;
		// Transmit the data in the output buffer
		int cnt = USB.usbWrite(outBuf, outOffset, outCnt - outOffset);
		//1 RConsole.print("Send " + cnt + "\n");
		outOffset += cnt;
		if (outOffset >= outCnt)
		{
			//RConsole.print("Send complete\n");
			outOffset = 0;
			outCnt = 0;
			notifyAll();
            return USB.USB_NEWDATA;
        }
        return USB.USB_WRITEABLE;
	}


    /**
     * Write all of the current output buffer to the device.
     * NOTE: To ensure correct operation of packet mode, this function should
     * only return 1 if all of the data will eventually be written. It should
     * avoid writing part of the data.
     * @param wait if true wait until the output has been written
     * @return -ve if error, 0 if not written, +ve if written
     */
    synchronized int flushBuffer(boolean wait)
    {
        if (outOffset >= outCnt) return 1;
        USB.notifyEvent(USB.USB_NEWDATA);
        if (wait)
            try {wait();} catch(Exception e){}
        // All of the data will be sent eventually, so say all gone.
        return 1;
    }

    /**
	 * Low level input function. Called by the USB thread to transfer
	 * input from the system into the input buffer.
     * @return the event to wait for
	 */
	synchronized int recv()
	{
		//1 RConsole.print("recv\n");
		// Read data into the input buffer
        while (inCnt < readThreshold)
		{
			if (inCnt == 0) inOffset = 0;
			int offset = (inOffset + inCnt) % inBuf.length;
			int len = (offset >= inOffset ? inBuf.length - offset : inOffset - offset);
			//RConsole.print("inCnt " + inCnt + " inOffset " + inOffset + " offset " + offset + " len " + len + "\n");
            if (len < USB.HW_BUFSZ)
            {
                // Not enough space to read a full packet, so wait until there
                // is more space available...
                notifyAll();
                return USB.USB_NEWSPACE;
            }
            int cnt = USB.usbRead(inBuf, offset, len);
            //if (cnt < len && cnt < 100) RConsole.println("rd " + cnt + " sp " + len);
			if (cnt <= 0) break;
			inCnt += cnt;
			//1 RConsole.print("recv " + inCnt + "\n");
		}
		if (inCnt > 0) notifyAll();
        // Decide what event is needed to be able to progress
        return (inCnt >= readThreshold ? USB.USB_NEWSPACE : USB.USB_READABLE);
	}


    /**
     * Get any available data into the input buffer.
     * @param wait if true wait for data to be available.
     * @return -ve if error, 0 if not read, +ve if read
     */
    synchronized int fillBuffer(boolean wait)
    {
        if (inCnt < readThreshold) USB.notifyEvent(USB.USB_NEWSPACE);
        if (inCnt > 0) return inCnt;
        if (wait)
            try{wait();}catch(Exception e){}
        return inCnt;
    }
         
    /**
     * Disconnect the device/channel
     */
    @Override
    void disconnect()
    {
        USB.notifyEvent(USB.USB_DISCONNECT);
    }

   /**
     * Tell the lower levels that they can release any resources for this
     * connection.
     */
    @Override
    void freeConnection()
    {
        USB.freeConnection();
    }


    /**
     * Set the IO mode to be used for this connection. 
     * USB has a 1 byte header, and does not use packet mode for LCP data.
     * @param mode
     */
    @Override
    public void setIOMode(int mode)
    {
        // Only packet modes uses a header for USB
        if (mode == PACKET)
        {
            readThreshold = inBuf.length - USB.HW_BUFSZ;
            setHeader(HDRSZ);
        }
        else
        {
            setHeader(0);
            // preserve the hw packet boundaries
            readThreshold = 1;
        }
    }

}

