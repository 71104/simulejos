package lejos.nxt.comm;
import lejos.util.Delay;

/**
 * Low-level RS485/BitBus Network implementation
 * This class provides simple low level access to the underlying RS485 hardware
 * implemented on port 4 of the Lego NXT.
 *
 * It also supports a higher level network connection based on this hardware, in
 * the form of a BitBus network node implementation. For full details see the
 * BitBus specification at:
 * http://www.bitbus.org/
 * Basically the network provides a simple master/slave implementation using
 * SDLC packet framing with CRC-16-CITT error checking. In this implementation
 * the on the wire format has been modified to use the Lego defined baud rate
 * of 921600 bps and uses byte stuffing rather than bit stuffing (since there is
 * no hardware support). The protocol has been integrated with the standard
 * leJOS connection object and offers up to 7 connections. The BitBus broadcast
 * mechanism is used to provide a higher level mapping between NXT address (we
 * use the Bluetooth address of the nxt), or name and the underlying BitBus
 * address (a single byte).
 *
 * The original implementation used Java for frame handling. However this was
 * found to add considerable overhead to the process. To improve performance
 * (by a factor of approximately 10), the lowest level routines have been
 * re-implemented in C and moved into the firmware as native methods.
 * 
 * @author Andy Shaw
 *
 */
public class RS485 extends NXTCommDevice {
    public static final int BUFSZ = 128;
    public static final int MAX_CONNECTIONS = 8;

    static final int CRC_CITT = 0x1021;
    static final int CRC_INIT = 0xffff;
    static final byte BB_EXTRA = 6;
    static final byte BB_FLAG = 0x7e;
    static final byte BB_ESCAPE = 0x7d;
    static final byte BB_XOR = 0x20;
    static final byte BB_SEQMASK = 0x7;
    static final byte BB_SNRM = (byte)0x93;
    static final byte BB_DISC = 0x53;
    static final byte BB_UA = 0x73;
    static final byte BB_FRMR = (byte) 0x97;
    static final byte BB_IFRAME = 0x10;
    static final byte BB_IMASK = 0x11;
    static final byte BB_ACK = 0x11;
    static final byte BB_ACKMASK = 0x1b;
    static final byte BB_ACKRR = BB_ACK;
    static final byte BB_ACKNR = BB_ACK|0x4;
    static final byte BB_BROADCAST = 0;
    static final byte BB_INVALID = (byte)0xff;
    static final int BB_ASEQSHIFT = 5;
    static final int BB_SSEQSHIFT = 1;
    static final int BB_DATAOFFSET = 2;
    static final int BB_CSUMSZ = 2;

    static final int FRAME_TIMEOUT = 10;
    static final int REPLY_RETRY = 5;
    static final int REQUEST_RETRY = 5;
    static final int RECV_RETRY = 1000;

    static final int DS_DISABLED = 0;
    static final int DS_SLAVE = 1;
    static final int DS_MASTER = 2;

    static final int ST_FLAG = 0;
    static final int ST_ESCAPE = 1;
    static final int ST_DATA = 2;
    
    static final int OVERRUN = -1;

    static final Controller controller = new Controller();
    static volatile RS485Connection listeningCon = null;

    /**
     * prevent construction of this class
     */
    private RS485()
	{
        // do nothing
	}


    /**
     * BitBus network interface control class.
     * Controls access to the underlying RS485 port and operates it as part of a
     * BitBus network. Schedules and performs packet I/O operations via the port.
     * Allocates network connections and addresses.
     * Will operate in either BitBus master or slave mode.
     *
     */
    static class Controller extends Thread
    {
        private char[] CRCTable;
        private byte[] frame;
        private int frameLen;
        private final RS485Connection connections[] = new RS485Connection[MAX_CONNECTIONS];
        private final RS485Connection slaveConnections[] = new RS485Connection[MAX_CONNECTIONS];
        private int devMode = DS_DISABLED;
        private byte[] netAddress = new byte[ADDRESS_LEN];
        private byte[] netName = new byte[NAME_LEN];
        private int connectionCnt = 0;
        private int slaveCnt = 0;
        private int retryCnt = 0;

        Controller()
        {
            // Allocate space for frame, allow for max byte stuffing
            frame = new byte[(BUFSZ+BB_EXTRA)*2];
            initCRCTable256(CRC_CITT);
            devMode = DS_DISABLED;
            connectionCnt = 0;
            slaveCnt = 0;
            retryCnt = 0;        
            setDaemon(true);
            start();
        }
        
        /**
         * Set the local address for this node.
         */
        private void setAddress()
        {
            //RConsole.println("Load address " + NXTCommDevice.getAddress());
            netAddress = stringToAddress(NXTCommDevice.getAddress());
            //RConsole.println("name " + NXTCommDevice.getName());
            netName = stringToName(NXTCommDevice.getName());
        }
        
        
        /**
         * Set the operating mode for this node. This may require reseting exisiting
         * connections.
         * @param newMode
         */
        private void setMode(int newMode)
        {
            synchronized(connections)
            {
                //RConsole.println("setMode " + newMode);
                if (devMode == newMode) return;
                //RConsole.println("change mode from " + devMode);
                // stop things while we sort stuff out
                devMode = DS_DISABLED;

                // Now set things up for the new mode
                if (newMode == DS_DISABLED)
                    RS485.hsDisable();
                else
                {
                    // Make sure our address is correct and up to date.
                    setAddress();
                    // enable using default speed and buffering
                    RS485.hsEnable(0, 0);
                    if (newMode == DS_MASTER)
                    {
                        // Reset the network
                        for(int i = 0; i < REPLY_RETRY; i++)
                        {
                            Delay.msDelay(FRAME_TIMEOUT);
                            sendControl(BB_BROADCAST, BB_DISC, (byte)0);
                        }
                        // Give things time to settle
                        Delay.msDelay(FRAME_TIMEOUT*REPLY_RETRY*REQUEST_RETRY);
                    }
                    // drop any old frames
                    while(recvFrame() > 0)
                        Thread.yield();
                }
                devMode = newMode;
                //RConsole.println("Mode set to " + devMode);
            }
        }

        /**
         * Rebuild the mapping from address to connection for slave connections.
         * This table allows fast lookup of slave address to the corresponding
         * connection object.
         * Maintain a count of the number of active slave connections for this device.
         * NOTE: as a special case the entry for channel 0 (the broadcast address),
         * is associated with the first listening connection, thus allowing it to
         * respond to address assignment requests
         */
        private void updateSlaveConnections()
        {
            synchronized(connections)
            {
                slaveCnt = 0;
                if (devMode < DS_SLAVE) return;
                // First remove all current associations
                for(int i = 0; i < slaveConnections.length; i++)
                    slaveConnections[i] = null;
                // Now build up the map
                for(int i = 0; i < connections.length; i++)
                {
                    if (connections[i] != null)
                    {
                        if (connections[i].bbAddress == BB_INVALID)
                        {
                            if (slaveConnections[BB_BROADCAST] == null)
                                slaveConnections[BB_BROADCAST] = connections[i];
                        }
                        else
                        {
                            slaveConnections[connections[i].bbAddress] = connections[i];
                            // We have at least one active slave
                            slaveCnt++;
                        }
                    }
                }
                if (slaveCnt == 0) retryCnt = 0;
            }
        }


        /**
         * Disconnect all active connections.
         */
        void disconnectAll()
        {
            synchronized(connections)
            {
                for(int i = 0; i < MAX_CONNECTIONS; i++)
                    if (connections[i] != null)
                        connections[i].disconnected();
            }
        }

        /**
         * Allocate a new connection channel, and return a connection object for
         * it. The channel number is used as the BitBus address for this
         * connection. If this is the first active connection
         * set the mode of the device to be either master or slave.
         * @param mode
         * @return The new connection object or null if none are available
         */
        RS485Connection newConnection(int mode)
        {
            // Find a free slot, note that we never use address 0, for master side
            // addreses. This is the broadcast address.
            // Make sure we are in the correct mode
            if (devMode != mode && devMode != DS_DISABLED) return null;
            synchronized(connections)
            {
                if (connectionCnt == 0) setMode(mode);
                for(int i = (mode == DS_SLAVE ? 0 : 1); i < connections.length; i++)
                    if (connections[i] == null)
                    {
                        connections[i] = new RS485Connection(this, i);
                        connectionCnt++;
                        updateSlaveConnections();
                        return connections[i];
                    }
            }
            return null;
        }

        /**
         * Free a connection and make the channel available for re-use. If this is
         * the last connection in use return the state of the device to disabled
         * @param con the connection being released.
         */
        void freeConnection(RS485Connection con)
        {
            if (con == null) return;
            int i = con.connNo;
            if (i < 0 || i >= connections.length) return;
            synchronized(connections)
            {
                if (connections[i] == null) return;
                //RConsole.println("freeConnection " + i + " state " + connections[i].state);
                // Unbind things
                connections[i].connNo = -1;
                connections[i] = null;
                updateSlaveConnections();
                if (--connectionCnt <= 0)
                {
                    setMode(DS_DISABLED);
                }
            }
        }

       
        /**
         * Create the CRC lookup table based upon the supplied polynomial
         * definition. For more details see:
         * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
         * http://wiki.wxwidgets.org/Development:_Small_Table_CRC
         * @param crc_poly
         */
        private void initCRCTable256(int crc_poly)
        {
            int i, val, result;
            CRCTable = new char[256];
            for (val = 0; val < 256; val++)
            {
                result = val << 8;

                for (i = 0; i < 8; i++)
                    if ((result & 0x8000) != 0)
                        result = (result << 1) ^ crc_poly;
                    else
                        result <<= 1;

                CRCTable[val] = (char)(result & 0xffff);
            }
        }


        /**
         * Send a frame. We use the firmware to assemble the actual bytes,
         * performing byte stuffing and calculating the checksum value. We
         * supply a pre-computed CRC table to aid in this process.
         * @param address The BitBus address for this packet
         * @param control The BitBus control field
         * @param data The data to be sent
         * @param offset Offset of the start of the data
         * @param len Length of the data.
         * @return number of bytes sent
         */
        private int sendFrame(byte address, byte control, byte [] data, int offset, int len )
        {
            int cnt;
            while ((cnt = hsSend(address, control, data, offset, len, CRCTable)) == 0)
                Thread.yield();
            return cnt;
        }

        /**
         * Send an IFrame to the specified address. Send a BitBud IFrame (one
         * containing data).
         * @param address The BitBus address
         * @param ack The seq number being acknowledged by this frame
         * @param seq The seq number for this frame.
         * @param data
         * @param offset
         * @param len
         * @return number of data bytes sent
         */
        public int sendData(byte address, byte ack, byte seq, byte [] data, int offset, int len)
        {
            //RConsole.println("sendData address " + address + " len " + len + " seq " + seq + " ack " + ack);

            if (len > BUFSZ) len = BUFSZ;
            byte control = BB_IFRAME;
            control |= (byte)(ack << BB_ASEQSHIFT);
            control |= (byte)(seq << BB_SSEQSHIFT);
            if (sendFrame(address, control, data, offset, len) > 0)
                return len;
            else
                return 0;
        }

        /**
         * Send a control frame. Send a BitBus control frame (one with no data).
         * @param address The BitBus address
         * @param control BitBus control field
         * @param ack The seq number being acknowledged by this frame.
         * @return number of bytes sent
         */
        private int sendControl(byte address, byte control, byte ack)
        {
            //RConsole.println("sendControl address " + address + " control " + control + " ack " + ack + " time " + (int)System.currentTimeMillis());
            return sendFrame(address, (byte)(control | (ack << BB_ASEQSHIFT)), null, 0, 0);
        }


        /**
         * Recv a frame from the hardware into the frame buffer. Handle byte
         * stuffing and check the CRC. Timeout if no frame is available or only
         * a partial frame is read.
         * @return < 0 no data 0 in frame > 0 the length of the frame
         */
        private int recvFrame()
        {
            int endTime = (int) System.currentTimeMillis() + FRAME_TIMEOUT;
            // Wait for a a packet to start or timeout
            int ret;
            while((ret = hsRecv(frame, frame.length, CRCTable, 1)) < 0 && (int)System.currentTimeMillis() < endTime)
                Thread.yield();
            frameLen = ret;
            // did we get a complete frame?
            if (ret > 0) return ret;
            // Have we timed out?
            if (ret < 0) return -1;
            // No so we have a partial frame give it time to complete
            endTime = (int) System.currentTimeMillis() + FRAME_TIMEOUT;
            while((ret = hsRecv(frame, frame.length, CRCTable, 0)) == 0 && (int)System.currentTimeMillis() < endTime)
                Thread.yield();
            frameLen = ret;
            if (ret > 0) return ret;
            // Frame timeout
            return -1;
        }


        /**
         * Assign an address. Attempt to dynamically assign an address to a slave device.
         * We do this by broadcasting the address and the name of the slave along with the
         * assigned address. Slave must be in disconnected state to respond to this.
         * @param addr
         * @param remAddress
         * @param remName
         * @return number of bytes sent
         */
        int assignAddress(byte addr, byte[] remAddress, byte[] remName)
        {
            byte [] data = new byte[1+2*ADDRESS_LEN+2*NAME_LEN];
            int offset = 1;
            //First byte is the address to be assigned
            data[0] = addr;
            // Now copy in the address bytes
            if (remAddress != null && remAddress.length == ADDRESS_LEN)
                System.arraycopy(remAddress, 0, data, offset, ADDRESS_LEN);
            offset += ADDRESS_LEN;
            // and the name
            if (remName != null && remName.length <= NAME_LEN)
                System.arraycopy(remName, 0, data, offset, remName.length);
            offset += NAME_LEN;

            // Copy in our netAddress and netName
            System.arraycopy(netAddress, 0, data, offset, ADDRESS_LEN);
            offset += ADDRESS_LEN;

            System.arraycopy(netName, 0, data, offset, NAME_LEN);
            // Now broadcast it
            return sendData(BB_BROADCAST, (byte)0, (byte)0, data, 0, data.length);
        }

        /**
         * Compare two byte arrays, return true if they contain the same values.
         * @param b1
         * @param off1
         * @param b2
         * @param off2
         * @param len
         * @return
         */
        boolean match(byte [] b1, int off1, byte[] b2, int off2, int len)
        {
            for(int i = 0; i < len; i++)
            {
                //RConsole.println("Match " + b1[off1+i] + " with " + b2[off2+i]);
                if (b1[off1 + i] != b2[off2 + i]) return false;
            }
            return true;
        }

        /**
         * Check to see if we are being assigned a new dynamic netAddress. If
         * we are make a note of the new address so that we will respond to
         * requests directed to it.
         * @param con Connection object to be bound to the new address
         */
        void checkAddress(RS485Connection con)
        {
            // First check to see if we have a match on the netAddress.
            int offset = BB_DATAOFFSET + 1;
            // if either the netAddress or the netName matches we accept the assignment
            if (match(frame, offset, netAddress, 0, netAddress.length) || match(frame, offset + ADDRESS_LEN, netName, 0, netName.length))
            {
                //RConsole.println("Got netAddress match " + frame[BB_DATAOFFSET]);
                offset += ADDRESS_LEN + NAME_LEN;
                // Get the remote netAddress and netName
                byte[] remAddr = new byte[ADDRESS_LEN];
                byte[] remName = new byte[NAME_LEN];
                System.arraycopy(frame, offset, remAddr, 0, remAddr.length);
                offset += ADDRESS_LEN;
                System.arraycopy(frame, offset, remName, 0, remName.length);
                con.bind(frame[BB_DATAOFFSET], addressToString(remAddr), nameToString(remName));
                con.state = RS485Connection.CS_CONNECTING2;
                updateSlaveConnections();
            }

        }

        /**
         * Poll the specified slave device.
         * @param con
         */
        boolean pollSlave(RS485Connection con)
        {
            // Gain access to the shared data
            synchronized(con)
            {
                int now = (int)System.currentTimeMillis();
                //RConsole.println("Poll slave state " + con.state + " time " + now);
                // Now we need to work out exactly what to do
                switch (con.state)
                {
                    case RS485Connection.CS_IDLE:
                    case RS485Connection.CS_DISCONNECTED:
                        // nothing to do in these states.
                        return false;

                    case RS485Connection.CS_DISCONNECTING2:
                        // Need to close down this connection
                        sendControl(con.bbAddress, BB_DISC, (byte)0);
                        break;
                    case RS485Connection.CS_CONNECTING1:
                        // Need to do netAddress assignment. We do this via broadcast
                        assignAddress(con.bbAddress, stringToAddress(con.address), stringToName(con.remoteName));
                        sendControl(con.bbAddress, BB_DISC, (byte)0);
                        break;
                    case RS485Connection.CS_CONNECTING2:
                        // Need to initialise the device
                        sendControl(con.bbAddress, BB_SNRM, (byte)0);
                        break;
                    default:
                        // If we have data, send it.
                        if (con.send()) break;
                        // Do we have space for more data?
                        if (con.inBuf.length - con.inCnt >= BUFSZ)
                            sendControl(con.bbAddress, BB_ACKRR, con.inSeq);
                        else
                            sendControl(con.bbAddress, BB_ACKNR, con.inSeq);
                        break;
                }
            }
            return true;
        }

        /**
         * Handle a response from a slave device.
         * @param con The connection to the slave
         */
        void processResponse(RS485Connection con)
        {
            byte addr = frame[0];
            byte control = frame[1];
            // Access shared data
            //RConsole.println("processResponse addr" + addr + " control " + control + " time " + (int)System.currentTimeMillis());
            synchronized(con)
            {
                con.retryCnt = 0;
                // Make sure it is addressed to us
                if (addr == con.bbAddress)
                {
                    if (control == BB_FRMR)
                    {
                        // Slave has disconnected
                        con.disconnect();
                        return;
                    }
                    switch(con.state)
                    {
                        case RS485Connection.CS_IDLE:
                        case RS485Connection.CS_DISCONNECTED:
                            // Should never happen drop the packet.
                            break;
                        case RS485Connection.CS_DISCONNECTING2:
                            // Has the slave accepted the disconnect?
                            if (control == BB_UA)
                                con.disconnected();
                            break;
                        case RS485Connection.CS_CONNECTING1:
                            if (control == BB_UA)
                            {
                                //RConsole.println("Got conn ack");
                                con.state = RS485Connection.CS_CONNECTING2;
                            }
                            break;
                        case RS485Connection.CS_CONNECTING2:
                            // Has the slave accepted the new connection?
                            if (control == BB_UA)
                            {
                                //RConsole.println("Got conn ack");
                                con.state = RS485Connection.CS_CONNECTING3;
                                con.notifyAll();
                            }
                            break;
                        default:
                            // If this is a data packet try and accept the data
                            if ((control & BB_IMASK) == BB_IFRAME)
                            {
                                con.recv((byte)((control >>> BB_SSEQSHIFT) & BB_SEQMASK), frame, BB_DATAOFFSET, frameLen - (BB_DATAOFFSET + BB_CSUMSZ));
                                con.ack((byte)((control >>> BB_ASEQSHIFT) & BB_SEQMASK), false);
                            }
                            else if ((control & BB_ACKMASK) == BB_ACK)
                                con.ack((byte)((control >>> BB_ASEQSHIFT) & BB_SEQMASK), (control & BB_ACKNR) == BB_ACKNR);
                            else
                                // Unexpected packet
                                con.disconnect();
                            break;
                    }
                }
                else if (addr == BB_BROADCAST)
                {
                    // Broadcast packet. Decide if we should deal with it
                    if (control == BB_DISC)
                        // Another master has come on line we give up control
                        disconnectAll();
                }
            }
        }

        /**
         * Handle a request from the master. We respond to messages directed to
         * any one of our open connections or to broadcast requests.
         */
        void processRequest()
        {
            RS485Connection con;
            byte addr = frame[0];
            byte control = frame[1];
            //RConsole.println("addr " + addr + " control " + control);
            if (addr >= slaveConnections.length) return;
            con = slaveConnections[addr];
            if (addr == BB_BROADCAST)
            {
                // Broadcast packet. Decide if we should deal with it
                if (control == BB_DISC)
                    // A new master has come on line we reset
                    disconnectAll();
                else if ((control & BB_IMASK) == BB_IFRAME)
                {
                    // Got a broadcast data packet must be trying to do netAddress
                    // assignment. Check to see if we are interested.
                    if (con != null)
                    {
                        //RConsole.println("state " + con.state);
                        synchronized(con)
                        {
                            if (con.state == RS485Connection.CS_CONNECTING1)
                                checkAddress(con);
                        }
                    }
                }
                return;
            }
            // Do we have an active connection for this address?
            if (con == null) return;
            // Access shared data
            synchronized(con)
            {
                //RConsole.println("processRequest addr " + addr + " control " + control + " time " + (int)System.currentTimeMillis()+ " state " + con.state);
                con.retryCnt = 0;
                if (control == BB_DISC)
                {
                    sendControl(addr, BB_UA, (byte)0);
                    // We have been told to disconnect or we are being connected
                    // to (in which case we ignore this command).
                    if (con.state != RS485Connection.CS_CONNECTING2)
                        con.disconnected();
                    return;
                }
                switch(con.state)
                {
                    case RS485Connection.CS_IDLE:
                    case RS485Connection.CS_DISCONNECTED:
                        // Should nevr happen abort, by falling through
                    case RS485Connection.CS_DISCONNECTING2:
                        // Tell the master our connection has been closed.
                        sendControl(addr, BB_FRMR, (byte)0);
                        break;
                    case RS485Connection.CS_CONNECTING2:
                        // Has the connection been opened
                        if (control == BB_SNRM)
                        {
                            // Yes so move to next state.
                            con.state = RS485Connection.CS_CONNECTING3;
                            con.notifyAll();
                            sendControl(addr, BB_UA, (byte)0);
                        }
                        break;
                    default:
                        // If this is a data packet try and accept the data
                        if ((control & BB_IMASK) == BB_IFRAME)
                        {
                            con.recv((byte)((control >>> BB_SSEQSHIFT) & BB_SEQMASK), frame, BB_DATAOFFSET, frameLen - (BB_DATAOFFSET + BB_CSUMSZ));
                            con.ack((byte)((control >>> BB_ASEQSHIFT) & BB_SEQMASK), false);
                        }
                        else if ((control & BB_ACKMASK) == BB_ACK)
                            con.ack((byte)((control >>> BB_ASEQSHIFT) & BB_SEQMASK), (control & BB_ACKNR) == BB_ACKNR);
                        else
                            // Unexpected packet
                            con.disconnect();
                        // If we are still connected
                        if (con.state >= RS485Connection.CS_DISCONNECTING)
                        {
                            // try and send any data we may have
                            if (!con.send())
                            {
                                // Otherwise send the appropriate ack
                                if (con.inBuf.length - con.inCnt >= BUFSZ)
                                    sendControl(con.bbAddress, BB_ACKRR, con.inSeq);
                                else
                                    sendControl(con.bbAddress, BB_ACKNR, con.inSeq);
                            }
                        }
                        break;
                }

            }
        }

        
        /**
         * Low level communications thread processing. Used to actually
         * send and receive data between devices.
         */
        @Override
        public void run()
        {
            while(true)
            {
                switch(devMode)
                {
                    case DS_DISABLED:
                        Delay.msDelay(1);
                        break;
                    case DS_MASTER:
                        // We need to poll each of the connected devices
                        for (int i = 1; i < connections.length; i++)
                        {
                            RS485Connection con = connections[i];
                            if (con != null)
                            {
                                if (pollSlave(con))
                                {
                                    int cnt;
                                    for(cnt = 0; cnt < REPLY_RETRY; cnt++)
                                        if (recvFrame() >= 0) break;
                                    // Have we been waiting too long?
                                    if (cnt >= REPLY_RETRY)
                                    {
                                        //RConsole.println("Timeout");
                                        if (++con.retryCnt > REQUEST_RETRY)
                                        {
                                            //RConsole.println("Request timeout chan " + i);
                                            con.disconnected();
                                        }
                                    }
                                    else
                                        processResponse(con);
                                }
                            }
                        }
                        break;
                    case DS_SLAVE:
                    {
                        if (recvFrame() >= 0)
                            processRequest();
                        else if (slaveCnt > 0 && ++retryCnt > RECV_RETRY)
                        {
                            //RConsole.println("Retry count exceeded");
                            disconnectAll();
                        }

                        break;
                    }
                }
                Thread.yield();
            }
        }
    }


    /**
     * Connect to a remote device either by name or by address.
     * @param target The address/name of the remote device to connect to
     * @param mode I/O mode to use for this connection
     * @return null if failed to connect, or a NXTConnection object
     */
    public static RS485Connection connect(String target, int mode)
    {
        // Allocate a chan for the new connection.
        RS485Connection con;
        if (target == null) return null;
        con = controller.newConnection(DS_MASTER);
        if (con == null) return null;

        synchronized(con)
        {
            int chan = con.connNo;
            //RConsole.println("Got connection " + chan);
            // Make sure we have not been disconnected.
            if (con.state == RS485Connection.CS_DISCONNECTED) return null;
            if (isAddress(target))
                con.bind((byte)chan, target, null);
            else
                con.bind((byte) chan, null, target);
            // Now try to do the connect
            //RConsole.println("Try to connect");
            // Do Address assignmant and try to connect
            con.state = RS485Connection.CS_CONNECTING1;
            try{con.wait();}catch(Exception e){}
            if (con.state == RS485Connection.CS_CONNECTING3)
            {
                // We found a slave with the correct Name/Address
                con.state = RS485Connection.CS_CONNECTED;
                con.setIOMode(mode);
                //RConsole.println("Connected");
                return con;
            }
            // failed to connect
            con.disconnected();
            controller.freeConnection(con);

        }
        //RConsole.println("Connection failed");
        return null;
    }

    /**
     * Connect to a remote device by name/address
     * @param target The name/address of the remote device
     * @return null if failed to connect, or a NXTConnection object
     */
    public static RS485Connection connect(String target)
    {
        return connect(target, 0);
    }

    /**
     * Wait for a connection from another nxt
     * @param timeout How long to wait for the connect 0 means wait forever
     * @param mode The I/O mode to use for this connection
     * @return null if failed to connect, or a NXTConnection object
     */
    public static RS485Connection waitForConnection(int timeout, int mode)
    {
        RS485Connection con;

        synchronized(controller)
        {
            // We only allow a single listening connection
            if (listeningCon != null) return null;
            con = controller.newConnection(DS_SLAVE);
            if (con == null) return null;
            listeningCon = con;
        }
        if (timeout == 0) timeout = 0x7ffffff;
        //RConsole.println("Wait for connect on chan " + con.connNo);
        // Keep trying allowing for resets
        for(;;)
        {
            synchronized(con)
            {
                con.reset();
                con.state = RS485Connection.CS_CONNECTING1;
                do{
                    try{
                        con.wait(timeout < 1000 ? timeout : 1000);}
                    catch(InterruptedException e){break;}
                    timeout -= 1000;
                } while (timeout > 0 && con.state >= RS485Connection.CS_CONNECTING1 &&
                        con.state < RS485Connection.CS_CONNECTING3);
                //RConsole.println("After wait state " + con.state);
                if (con.state == RS485Connection.CS_CONNECTING3)
                {
                    // Now connected
                    //RConsole.println("Now connected...");
                    con.state = RS485Connection.CS_CONNECTED;
                    con.setIOMode(mode);
                    listeningCon = null;
                    return con;
                }
                else if (con.state > RS485Connection.CS_DISCONNECTED)
                {
                    // Failed to connect free things up
                    //RConsole.println("Connection failed");
                    con.disconnect();
                    controller.freeConnection(con);
                    listeningCon = null;
                    return null;
                }
                // Must have been a network reset/timeout try again
            }
        }
    }

    /**
     * Cancel a long running command issued on another thread.
     * NOTE: Currently only the WaitForConnection calls can be cancelled.
     * @return true if the command was cancelled, false otherwise.
     */
    public static boolean cancelConnect()
    {
        RS485Connection con = listeningCon;
        if (con == null) return false;
        synchronized(con)
        {
            // Check again now we have things locked.
            if (listeningCon != con) return false;
            con.disconnect();
            con.notifyAll();
        }
        return true;
    }

    /**
     * Firmware routine to send a BitBus packet.
     * Create a SDLC/BitBus packet including framing and byte stuffing operations
     * Calculate and include an CITT 16 CRC checksum, using the supplied
     * pre-calculated lookup table.
     * @param address BitBus address
     * @param control BitBus control field
     * @param data Data to send
     * @param offset offset of start of data
     * @param len Length of data must be <= the firmware BUFSZ (currently 128 bytes)
     * @param crc Lookup table for crc calculation.
     * @return >= 0 number of bytes sent, < 0 error
     */
    static native int hsSend(byte address, byte control, byte[]data, int offset, int len, char[]crc);

    /**
     * Firmware routine to read a BitBus packet.
     * Read a valid BitBus packet from the network. This routine does not block
     * and may need to be called multiple times to assemble a complete packet.
     * Any packets that have a bad crc are discarded.
     * @param data Location to place the packet
     * @param len Max length of the packet (must be at least 4 bytes in len)
     * @param crc Lookup table for crc calculations.
     * @param reset If != 0 the packet state is reset.
     * @return < 0 no data == 0 Partial packet > 0 length of valid packet.
     */
    static native int hsRecv(byte[]data, int len, char[]crc, int reset);

    /**
     * Enable the RS485 hardware port.
     * @param baudRate the baud rate to use in bps, a value of 0 uses the Lego standard rate
     * @param bufferSize the I/O buffer size in bytes, a value of 0 uses values suitable for BitBus
     */
    public static native void hsEnable(int baudRate, int bufferSize);

    /**
     * Disable the RS485 hardware port.
     */
    public static native void hsDisable();

    /**
     * Low level read from the RS485 port
     * @param buf
     * @param offset
     * @param len
     * @return the number of bytes read, if a buffer overrun occurs OVERRUN (-1) is returned.
     */
    public static native int hsRead(byte [] buf, int offset, int len);

    /**
     * Low level write to the RS485 hardware port.
     * @param buf
     * @param offset
     * @param len
     * @return the number of bytes written
     */
    public static native int hsWrite(byte[] buf, int offset, int len);
    
    /**
     * Class to provide polymorphic access to the connection methods.
     * Gets returned as a singleton by getConnector and can be used to create
     * connections.
     */
    static class Connector extends NXTCommConnector
    {
        /**
         * Open a connection to the specified name/address using the given I/O mode
         * @param target The name or address of the device/host to connect to.
         * @param mode The I/O mode to use for this connection
         * @return A NXTConnection object for the new connection or null if error.
         */
        @Override
        public NXTConnection connect(String target, int mode)
        {
            return RS485.connect(target, mode);
        }

        /**
         * Wait for an incoming connection, or for the request to timeout.
         * @param timeout Time in msDelay to wait for the connection to be made
         * @param mode I/O mode to be used for the accepted connection.
         * @return A NXTConnection object for the new connection or null if error.
         */
        @Override
        public NXTConnection waitForConnection(int timeout, int mode)
        {
            return RS485.waitForConnection(timeout, mode);
        }

        /**
         * Cancel a connection attempt.
         * @return true if the connection attempt has been aborted.
         */
        @Override
        public boolean cancel()
        {
            return RS485.cancelConnect();
        }

    }
    
    static NXTCommConnector connector = null;

    /**
     * Provides access to the singleton connection object.
     * This object can be used to create new connections.
     * @return the connector object
     */
    public static NXTCommConnector getConnector()
    {
        if (connector == null)
            connector = new Connector();
        return connector;
    }

}
