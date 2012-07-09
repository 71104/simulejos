package lejos.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.NXTConnection;

/** 
 * Defines the [minimum] required functionality for a data logger implementation.
 * 
 * @author Kirk P. Thompson
 */
public interface Logger {
    // Starts realtime logging. Must be called before any writeLog() methods. Resets startCachingLog() state
    /** 
     * Start a realtime logging session using passed data streams.
     * The <code>setColumns()</code>
     * method must be called after this method is called and before the first
     * <code>writeLog()</code> method is called. 
     * <p>
     * The use of this method is mutually exclusive with <code>startCachingLog()</code> and will reset internal state 
     * to realtime mode.
     * @param out A valid <code>DataOutputStream</code>
     * @param in A valid <code>DataInputStream</code>
     * @throws IOException if the data streams are not valid
     * @see #stopLogging
     * @see #startRealtimeLog(NXTConnection)
     * @see #setColumns
     */
    void startRealtimeLog(DataOutputStream out, DataInputStream in) throws IOException; // streams must be valid (not null)
     
     /** 
      * Start a realtime logging session using passed <code>NXTConnection</code> to retrieve the data streams. The
      * connection must already be established.
      * The <code>setColumns()</code>
      * method must be called after this method is called and before the first
      * <code>writeLog()</code> method is called. 
      * <p>
      * The use of this method is mutually exclusive with <code>startCachingLog()</code> and will reset internal state 
      * to realtime mode.
      * @param connection A connected <code>NXTConnection</code> instance
      * @throws IOException if the data streams are not valid
      * @see #stopLogging
      * @see #startRealtimeLog(DataOutputStream, DataInputStream)
      * @see #setColumns
      */
    void startRealtimeLog(NXTConnection connection) throws IOException; // isConnected()=true and streams must be valid (not null)
    
    // flush any buffered bytes. throws unchecked IllegalStateException if startRealtimeLog() has not been called
    //void stopRealtimeLog();
    
    // Sets caching (deferred) logging. Default mode at instantiation and will be called on first writeLog() method if not 
    // explicitly called. Resets startRealtimeLog() state.
    // Init for logging to cache for deferred transmit using sendCache()
    // Resets startRealtimeLog() state
     /** 
      * Sets caching (deferred) logging. This is the default mode at instantiation. 
      * The <code>setColumns()</code>
      * method must be called after this method is called and before the first
      * <code>writeLog()</code> method is called. 
      * <p>
      * The use of this method is mutually exclusive with the <code>startRealtimeLog()</code> methods and will reset internal state 
      * to caching mode.
      * @see #stopLogging
      * @see #sendCache(NXTConnection)
      * @see #startRealtimeLog(NXTConnection)
      */
    void startCachingLog(); // default
    // Sends log cache. Valid only for caching (deferred) logging using startCachingLog().
     /** Sends the log cache. Valid only for caching (deferred) logging using startCachingLog(). 
      * @param out A valid <code>DataOutputStream</code>
      * @param in A valid <code>DataInputStream</code>
      * @throws IOException if the data streams are not valid
      * @throws IllegalStateException if <code>startCachingLog()</code> has not been called
      */
    void sendCache(DataOutputStream out, DataInputStream in) throws IOException; // only if loggingMode=cached
     
     /** Sends the log cache using passed <code>NXTConnection</code> to retrieve the data streams. The
      * connection must already be established.  Valid only for caching (deferred) logging using <code>startCachingLog()</code>.
      * @param connection A connected <code>NXTConnection</code> instance
      * @throws IOException if the data streams are not valid
      * @throws IllegalStateException if <code>startCachingLog()</code> has not been called
      */
    void sendCache(NXTConnection connection) throws IOException; // only if loggingMode=cached
     
    // sets the header names, datatypes, count, chartable attribute, range axis ID (for multiple axis charting)
    // This is mandatory and implies a new log structure when called
    // throws IllegalArgumentException if bad datatype val
     /** 
      * Set the data set header information for the data log and chart series. The first column in the data log 
      * is always a system timestamp
      * (element 0) so <u>your</u> first <code>writeLog()</code> item would be column 1, element 2 is column 2, so on and so forth. 
      * The items per log row
      * must match the number of headers you define in this method. 
      * <p>
      * This method must be called after the <code>startCachingLog()</code>
      * or either of the <code>startRealtimeLog()</code> methods is called or an <code>IllegalStateException</code> will be 
      * thrown in the <code>writeLog()</code> methods.
      * <p>
      * The number and datatype of <code>writeLog()</code> calls per log row must match the number of columns and the datatypes
      * you define here. You must
      * end each log row with <code>finishLine()</code> or an <code>IllegalStateException</code> will be thrown on the
      * next <code>writeLog()</code> call. If using the NXT ChartingLogger tool, the chart
      * will only reflect the new data sent after this call since the series are redefined.
      * <p>
      * In realtime mode, if headers are set during logging with the <code>writeLog()</code> methods, the log will reflect 
      * the changes from that point on. In cached mode, if headers are set during logging with the <code>writeLog()</code> 
      * methods, an <code>UnsupportedOperationException</code> is thrown.
      * <P>
      * If length of the passed array is 
      * zero or if length > 255, the method does nothing and returns immediately. 
      * 
      * @param columnDefs The array of <code>LogColumn</code> instances to use for the data log column definitions
      * @see LogColumn
      * @see #finishLine
      * @see #startCachingLog
      * @see #startRealtimeLog(NXTConnection)
      * @throws UnsupportedOperationException if <code>setColumns</code> is called more than once in cached mode.
      */
    void setColumns(LogColumn[] columnDefs) throws IllegalArgumentException; 
    
    // Log a comment. Displayed as event marker on domain axis of chart and after the current line in the log. 
    // Ignored in cache mode.
     /** 
      * Log a text comment to the data log.  
      * Ignored in cache mode.
      * Only one comment per line. (i.e. before <code>finishLine()</code> is called)
     * @param comment The comment
     */
    void writeComment(String comment);
    
    // All of these throw unchecked IllegalStateException if datatypes don't match what was set in setColumns(), 
    // column counts don't match what was set in setColumns(), or startxxxLog() has not been called
     /** 
     * Write a <code>boolean</code> value as an <code>int</code> 1 (<code>true</code>) or 0 (<code>false</code>) to the log. 
     * In realtime logging mode, if an <code>IOException</code> occurs, the connection
     * and data streams are silently closed down and no exception is thrown from this method.
     * 
     * @param datapoint The <code>boolean</code> value to log.
     * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
     * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
     * <code>finishLine()</code> was not called after last column logged), or the column
     * definitions have not been set with <code>setColumns()</code>.
     * @throws OutOfMemoryError if in cache mode and memory is exhausted.
     * @see #setColumns
     * @see #finishLine
     */
    void writeLog(boolean datapoint); 
    
    /** 
    * Write a <code>byte</code> value to the log. 
    * In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>byte</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @throws OutOfMemoryError if in cache mode and memory is exhausted.
    * @see #setColumns
    * @see #finishLine
    */
    void writeLog(byte datapoint);
    
    /** 
    * Write a <code>short</code> value to the log. 
    * In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>short</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @throws OutOfMemoryError if in cache mode and memory is exhausted.
    * @see #setColumns
    * @see #finishLine
    */
    void writeLog(short datapoint);
    
    /** 
      * Write an <code>int</code> to the log. In realtime logging mode, if an <code>IOException</code> occurs, the connection
      * and data streams are silently closed down and no exception is thrown from this method.
      * 
      * @param datapoint The <code>int</code> value to log.
      * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
      * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
      * <code>finishLine()</code> was not called after last column logged), or the column
      * definitions have not been set with <code>setColumns()</code>.
      * @see #setColumns
      * @see #finishLine
      */
    void writeLog(int datapoint);
    
    /** 
    * Write an <code>long</code> to the log. In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>long</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @see #setColumns
    * @see #finishLine
    */
    void writeLog(long datapoint);
    
    /** 
    * Write an <code>float</code> to the log. In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>float</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @see #setColumns
    * @see #finishLine
    */
    void writeLog(float datapoint);
    
    /** 
    * Write an <code>double</code> to the log. In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>double</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @see #setColumns
    * @see #finishLine
    */
    void writeLog(double datapoint);
    
    // called to end each new line of log data. Logged values count per row must match rowcount/datatype set in setColumns() or
    // IllegalStateException is thrown. 
     /** 
      * Finish the row and start a new one. 
      * <p>
      * The Column count is set by calling
      * <code>setColumns()</code> and you must ensure that you call the appropriate <code>writeLog()</code> method the same number of 
      * times as that column count before this method is called.
      * 
      * @throws IllegalStateException if all the columns defined with <code>setColumns()</code> per row have not been logged. 
      * @see #setColumns
      */
    void finishLine() throws IllegalStateException; 
    
    // once closed, dos/dis cannot be reused. startRealtimeLog() must be called again.
     /** 
      * Stop the logging session and close down the connection and data streams. After this method is called, you must call
      * one of the logging mode start methods to begin a new logging session.
      * @see #startRealtimeLog(NXTConnection)
      * @see #startCachingLog
      */
    void stopLogging(); 
    
}
