package javax.microedition.sensor;

/**
 * Basic implementation of JSR256 RangeCondition class
 * 
 * @author Lawrie Griffiths
 */
public final class RangeCondition extends OperatorTest implements Condition {
	private double lowerLimit, upperLimit;
	private String lowerOp, upperOp;
	
	public RangeCondition(double lowerLimit, 
			              String lowerOp, 
			              double upperLimit,
			              String upperOp) {
		this.lowerLimit = lowerLimit;
		this.lowerOp = lowerOp;
		this.upperLimit = upperLimit;
		this.upperOp = upperOp;
		
	}
	public boolean isMet(double doubleValue) {
		return test(doubleValue, lowerLimit, lowerOp) && test(doubleValue, upperLimit, upperOp);
	}

	public boolean isMet(Object value) {
		return false;
	}
	
	public final double getLowerLimit() {
		return lowerLimit;
	}
	
	public final String getLowerOp() {
		return lowerOp;
	}
	
	public final double getUpperLimit() {
		return upperLimit;
	}
	
	public final String getUpperOp() {
		return upperOp;
	}
	
	public String toString() {
		return " " + lowerOp + " " + lowerLimit + " && " +  upperOp + " " + upperLimit; 
	}
}
