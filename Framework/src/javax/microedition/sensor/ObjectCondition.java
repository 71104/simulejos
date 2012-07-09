package javax.microedition.sensor;

/**
 * Basic implementation of the JSR256 ObjectCondition class
 * 
 * @author Lawrie Griffiths
 */
public class ObjectCondition implements Condition {
	private Object limit;
	
	public ObjectCondition(Object limit) {
		this.limit = limit;	
	}
	
	public boolean isMet(double doubleValue) {
		return false;
	}

	public boolean isMet(Object value) {
		return value.equals(limit);
	}
	
	public final Object getLimit() {
		return limit;
	}
	
	public String toString() {
		return ".equals(" + limit + ")";
	}
}
