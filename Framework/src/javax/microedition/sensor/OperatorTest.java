package javax.microedition.sensor;

/**
 * Base class for RangeCondition and LimitCondition that implements a test 
 * on double values using the supplied operator.
 * 
 * @author Lawrie Griffiths
 *
 */
public class OperatorTest {
	
	protected boolean test(double value1, double value2, String op) {
		if (op.equals(Condition.OP_EQUALS)) return value1 == value2;
		if (op.equals(Condition.OP_GREATER_THAN)) return value1 > value2;
		if (op.equals(Condition.OP_GREATER_THAN_OR_EQUALS)) return value1 >= value2;
		if (op.equals(Condition.OP_LESS_THAN)) return value1 < value2;
		if (op.equals(Condition.OP_LESS_THAN_OR_EQUALS)) return value1 <= value2;
		return false;
	}
}
