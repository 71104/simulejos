package lejos.robotics.kinematics;

/**
 * A generic interface for robot hands that can grip and release objects.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface RobotHand {
	
	/**
	 * Grip an object
	 */
	public void grip();
	
	/**
	 * Release an object
	 */
	public void release();

}
