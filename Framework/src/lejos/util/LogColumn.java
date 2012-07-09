package lejos.util;

/**
 * Use to define the header names, datatypes, count, chartable attribute, and range axis ID (for multiple axis charts).
 * 
 * @see Logger#setColumns
 * @see NXTDataLogger
 * @author Kirk P. Thompson
 */
public class LogColumn {
    public static final int    DT_BOOLEAN = 0;
    public static final int    DT_BYTE    = 1;
    public static final int    DT_SHORT   = 2;
    public static final int    DT_INTEGER = 3;        
    public static final int    DT_LONG    = 4;
    public static final int    DT_FLOAT   = 5;
    public static final int    DT_DOUBLE  = 6;
    
    private String name;
    private int datatype=DT_FLOAT; //default is float
    private boolean chartable=true; // true = display on chart
    private int rangeAxisID = 1; // one-based ID of range axis for multi-axis charting. limit to 4 axes
    private int byteCount=4;

    /** <code>RangeAxisID</code> defaults to 1,
     * <code>chartable</code> defaults to <code>true</code>, and datatype defaults to <code>DT_FLOAT</code>.
     * @param name name the label/name of the column/series. 
     */
    public LogColumn(String name) {
        this.name = name;
    }

    /** Throws unchecked IllegalArgumentException if bad datatype value. <code>RangeAxisID</code> defaults to 1 and
     * <code>chartable</code> defaults to <code>true</code>.
     * @param name The label/name of the column/series
     * @param datatype The datatype of of the column/series
     * @see #DT_BOOLEAN
     * @see #DT_BYTE
     * @see #DT_SHORT
     * @see #DT_INTEGER
     * @see #DT_LONG
     * @see #DT_FLOAT
     * @see #DT_DOUBLE
     */
    public LogColumn(String name, int datatype) {
        this(name);
        setDatatype(datatype);
    }
    /** Throws unchecked IllegalArgumentException if bad datatype value. <code>RangeAxisID</code> defaults to 1.
     * @param name The label/name of the column/series
     * @param datatype The datatype of of the column/series
     * @param chartable <code>true</code> to chart the data, <code>false</code> to only log it.
     * @see #LogColumn(String, int, int)
     */
    public LogColumn(String name, int datatype, boolean chartable) {
        this(name, datatype);
        this.chartable=chartable;
    }
    
    /** Throws unchecked IllegalArgumentException if bad datatype value or <code>rangeAxisID</code> is not in within 1-4.
     * <code>chartable</code> defaults to <code>true</code>.
     * @param name The label/name of the column/series
     * @param datatype The datatype of of the column/series
     * @param rangeAxisID Range axis ID 1-4
     * @see #LogColumn(String, int, boolean)
     */
    public LogColumn(String name, int datatype, int rangeAxisID) {
        this(name, datatype, true);
        if (rangeAxisID<1 || rangeAxisID>4) throw new IllegalArgumentException("Invalid rangeAxisID " + rangeAxisID);
        this.rangeAxisID=rangeAxisID;
    }
    
    /** Set the datatype for this column/series. Throws unchecked IllegalArgumentException if bad datatype value
     * @param datatype The datatype. Use one of the constant values list below in "See also".
     * @see Logger
     * @see #DT_BOOLEAN
     * @see #DT_BYTE
     * @see #DT_SHORT
     * @see #DT_INTEGER
     * @see #DT_LONG
     * @see #DT_FLOAT
     * @see #DT_DOUBLE
     * @throws IllegalArgumentException if bad datatype value
     */
    private void setDatatype(int datatype) {
        // validate datatypes
        switch (datatype) { 
            case DT_BOOLEAN :
            case DT_BYTE    :
            case DT_SHORT   :
            case DT_INTEGER : 
            case DT_FLOAT   :
                this.byteCount=4;
                break;
            case DT_LONG    :
            case DT_DOUBLE  :
                this.byteCount=8;
                break;
            default:
                throw new IllegalArgumentException("Invalid datatype " + datatype);
        }
        this.datatype = datatype;
    }

    /**
     * @return the datatype size in bytes
     */
    public int getSize() {
        return this.byteCount;
    }

    /**
     * @return The datatype value set in the constructor
     */
    public int getDatatype() {
        return this.datatype;
    }

    /**
     * @return The chartable flag value set in the constructor
     */
    public boolean isChartable() {
        return this.chartable;
    }
    /**
    
     * @return The range Axis ID value set in the constructor
     */
    public int getRangeAxisID() {
        return this.rangeAxisID;
    }
    
    /**
     * @return The name value set in the constructor
     */
    public String getName() {
        return name;
    }
}
