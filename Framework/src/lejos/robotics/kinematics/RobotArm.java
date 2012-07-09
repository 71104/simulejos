package lejos.robotics.kinematics;

/**
 * A generic interface for robot arms that can move to points in 3D space. Makes no assumption 
 * about the design of the arm.
 * 
 * @author Lawrie Griffiths
 */
public interface RobotArm {
	
	/**
	 * Move the end of the robot arm (the hand, or hook, etc.) to a specified point in 3D space.
	 * Throws PointUnreachableException for any point that cannot be reached due to being out of 
	 * range, or because the robot body is in the way, or because of the design of the arm.
	 */
	public void gotoPoint(double x, double y, double z) throws PointUnreachableException;
}
