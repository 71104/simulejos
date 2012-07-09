package lejos.nxt.comm;

/**
 * Connection object for an RS485/BitBus connection
 * This object models a single BitBus connection. It works closely with
 * the BitBus controller object to perform packet based I/O. It provides
 * buffer space, address bindings and flow control for connection. It also
 * handles higher level connection state (connection establishment, termination
 * etc).
 * @author andy
 */
public class RS485Connection extends NXTConnection
{
    // Additional states for this type
    static final int CS_CONNECTING1 = CS_EOF+1;
    static final int CS_CONNECTING2 = CS_EOF+2;
    static final int CS_CONNECTING3 = CS_EOF+3;

    int connNo;
	byte bbAddress;
    String remoteName;
    byte inSeq;
    byte outSeq;
    int sentCnt;
    boolean flowControl = false;
    int retryCnt;
    RS485.Controller controller;

	RS485Connection(RS485.Controller cont, int chan)
	{
        state = CS_IDLE;
        controller = cont;
        connNo = chan;
		is = null;
		os = null;
        bbAddress = RS485.BB_INVALID;
        bufSz = RS485.BUFSZ;
        maxPkt = 0xffff;
	}

	public String getName()
    {
        return remoteName;
    }

	synchronized void reset()
	{
		// Called by the low level implementation if things go wrong!
		state = CS_IDLE;
		inBuf = null;
		outBuf = null;
        bbAddress = RS485.BB_INVALID;
		notifyAll();
	}


	/**
	 * Bind the low level I/O handle to a connection object
	 * set things up ready to go.
	 */
	synchronized void bind(byte bbAddr, String remAddress, String remName)
	{
        // Allocate buffers if required. Note we use a double sized input
        // buffer to help flow control.
		if (inBuf == null )
			inBuf = new byte[2*RS485.BUFSZ];
		if (outBuf == null)
			outBuf = new byte[RS485.BUFSZ];
        setIOMode(PACKET);
		inCnt = 0;
		inOffset = 0;
		outCnt = 0;
		outOffset = 0;
        sentCnt = 0;
		pktLen = 0;
        inSeq = 0;
        outSeq = 0;
        bbAddress = bbAddr;
        address = remAddress;
        remoteName = remName;
        flowControl = false;
        retryCnt = 0;
	}


    /**
     * Send an EOF packet to the remote system.
     */
    synchronized void sendEOF()
    {
        // Nothing to do for RS485/BitBus. We rely on the underlying transport
        // for EOF
    }

    /**
     * Disconnect the device/channel
     */
    synchronized void disconnect()
    {
        if (state > CS_DISCONNECTING2)
            state = CS_DISCONNECTING2;
    }


	/**
	 * Low level output function. Take any data in the output buffer and write
	 * it to the device. Called by the network thread when this channel is
	 * active, to perform actual data I/O.
     * @return true if we sent data false otherwise.
	 */
	synchronized boolean send()
	{
        //RConsole.println("Send outcnt " + outCnt + " offset " + outOffset + " flow " + flowControl);
        // Do we have anything to send, and are we allowed to send it?
		if (outOffset >= outCnt || flowControl) return false;

        //RConsole.println("Pending " + Bluetooth.btPending());
		// Transmit the data in the output buffer
		sentCnt = controller.sendData(bbAddress, inSeq, outSeq, outBuf, outOffset, outCnt - outOffset);
        //RConsole.println("Send outcnt " + outCnt + " offset " + outOffset + " written " + sentCnt);
        return sentCnt > 0;
    }

    /**
     * Acknowledge previously sent frames. Called by the netowrk thread when
     * an acknowledgement is received. Free up buffer space that is no longer
     * needed for retransmissions etc.
     * @param seq
     * @param flow Indicates that we should apply flow control on this connection.
     */
    synchronized void ack(byte seq, boolean flow)
    {
        //RConsole.println("ack seq " + seq + " expect " + (outSeq+1) + " flow " + flow);
        // Record flow control state.
        flowControl = flow;
		//1 RConsole.print("Send " + cnt + "\n");
        // Is this an ack for the last packet?
        if (seq == ((outSeq + 1) & RS485.BB_SEQMASK))
        {
            // Got a good ack for the last packet sent
            outOffset += sentCnt;
            sentCnt = 0;
            if (outOffset >= outCnt)
            {
                // Sent all we have... free up the space.
                //RConsole.print("Send complete\n");
                outOffset = 0;
                outCnt = 0;
                notifyAll();
                //Thread.yield();
            }
            else
            {
                //RConsole.print("send remaining " + (outCnt - outOffset) + "\n");
                // We have more data waiting to go, send it...
            }
            // Move the outSeq on
            outSeq = seq;
        }
        else if (seq == outSeq)
        {
            // All packets acked, nothing to do
        }
        else
            // Bad seq number disconnect.
            disconnect();
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
        // If nothing to do say all ok.
        if (outOffset >= outCnt) return 1;
        if (wait)
            try {wait();} catch(Exception e){}
        // Data will be written eventually so say all ok.
        return 1;
    }

	/**
	 * Low level input function. Called by the network thread to transfer
	 * input from the system into the input buffer.
	 */
	synchronized void recv(byte seq, byte[] data, int dataOffset, int dataLen)
	{
        //RConsole.println("recv seq" + seq + " expect " + inSeq + " len " + dataLen);
        // Check that the seq number is ok
        if (seq == inSeq)
        {
            // Do we have room for the data
            if (dataLen <= inBuf.length - inCnt)
            {
                while (dataLen > 0)
                {
                    if (inCnt == 0) inOffset = 0;
                    int offset = (inOffset + inCnt) % inBuf.length;
                    int len = (offset >= inOffset ? inBuf.length - offset : inOffset - offset);
                    if (len > dataLen) len = dataLen;
                    System.arraycopy(data, dataOffset, inBuf, offset, len);
                    //RConsole.print("inCnt " + inCnt + " inOffset " + inOffset + " offset " + offset + " len " + len + "\n");
                    inCnt += len;
                    dataOffset += len;
                    dataLen -= len;
                    //1 RConsole.print("recv " + inCnt + "\n");
                }
                // Move the sequence number on
                inSeq = (byte)((inSeq + 1) & RS485.BB_SEQMASK);
            }
        }
        else if (seq == ((inSeq-1) & RS485.BB_SEQMASK))
        {
            // Duplicate packet, ignore it.
        }
        else
            // Bad sequence number. Disconnect
            disconnect();
		if (inCnt > 0) notifyAll();
        //Thread.yield();
	}

    /**
     * Get any available data into the input buffer.
     * @param wait if true wait for data to be available.
     * @return -ve if error, 0 if not read, +ve if read
     */
    synchronized int fillBuffer(boolean wait)
    {
        if (inCnt > 0) return inCnt;
        if (wait)
            try{wait();}catch(Exception e){}
        return inCnt;
    }

    /**
     * Tell the lower levels that they can release any resources for this
     * connection.
     */
    void freeConnection()
    {
        // Connections in a connect state handle the underlying connection
        // explicitly and so we do not free them
        //RConsole.println("free connection state " + state + " CS_CONNECTING " + CS_CONNECTING1);
        if (state < CS_CONNECTING1)
            controller.freeConnection(this);
    }


}
