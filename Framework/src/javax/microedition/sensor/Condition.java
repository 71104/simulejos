package javax.microedition.sensor;

/**
 * Standard JSR256 Condition interface
 * 
 * @author Lawrie Griffiths
 */
public interface Condition {
	public static final String OP_EQUALS = "eq";
	public static final String OP_GREATER_THAN = "gt"; 
	public static final String OP_GREATER_THAN_OR_EQUALS = "ge";
	public static final String OP_LESS_THAN = "lt";
	public static final String OP_LESS_THAN_OR_EQUALS = "le";
	
	public boolean isMet(double doubleValue);
	
	public boolean isMet(Object value);
}
