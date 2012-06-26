package javax.microedition.sensor;

/**
 * Basic implementation of the JSR256 LimitCondition class
 * 
 * @author Lawrie Griffiths
 */
public class LimitCondition extends OperatorTest implements Condition {
	private double limit;
	private String operator;

	public LimitCondition(double limit, String operator) {
		this.limit = limit;
		this.operator = operator;
	}
	
	public boolean isMet(double doubleValue) {
		return test(doubleValue, limit, operator);
	}

	public boolean isMet(Object value) {
		return false;
	}
	
	public final double getLimit() {
		return limit;
	}
	
	public final String getOperator() {
		return operator;
	}
	
	public String toString() {
		return " " + operator + " " + limit;
	}
}
