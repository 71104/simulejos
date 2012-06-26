package javax.microedition.io;

import java.io.IOException;

public interface StreamConnectionNotifier extends Connection {
        /*
         * Returns a StreamConnection that represents a server side socket
         * connection. Returns: A socket to communicate with a client. Throws:
         * IOException - If an I/O error occurs.
         */
        public StreamConnection acceptAndOpen() throws IOException;
}
