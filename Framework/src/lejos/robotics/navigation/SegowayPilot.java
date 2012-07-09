package lejos.robotics.navigation;

import java.util.ArrayList;
import lejos.robotics.EncoderMotor;
import lejos.robotics.Gyroscope;

/* DEVELOPER NOTES:
 * TODO: Currently no option to build Segoway with opposite motor directions. I assume you can just flip the gyro to
 * the opposite side of the robot (right to left) and it will balance. Technically AnyWay and HTWay motors 
 * must be backwards because forward motion makes the tachometers go negative. Should allow users to switch direction
 * of motors.
 * TODO: It might be possible to reduce this code greatly. The STOP code in the inner class MoveControlRegulator 
 * that keeps wheels hovering over one spot might be able to also seek new tacho targets on its own. The speed it 
 * pursues the goal should be related to the distance it is away from the targets.
 * TODO: The whole movement scheme works by assuming a move will be allowed to complete, or it will be interrupted
 * with a call to stop(). If a user calls a different move method like arc() while travel() is occurring, it won't 
 * work properly.
 * TODO: Strategies for improving rotate:
 * 1. Try to keep motors moving in sync by monitoring the differences in rotation between them and upping the juice
 * to the lagging motor. This will prevent one motor from getting ahead of the other. Altar values in controlDriver().
 * 2. Simple: Stop one motor when it gets to destination. Wait for other to get there. (Seems unstable.) This solution 
 * won't matter if 1. is implemented properly.
 * 
 */

/**
 * <p>Allow standard moves with a Segoway robot. Currently the robot has a 5 second delay between moves to allow
 * it some time to rebalance and straighten out uneven tacho rotations. This can be changed with setMoveDelay().</p>
 *  
 * <p>This code will work with any Segway-style robot, but tall robots will have problems balancing when the robot
 * is moving. To counteract this, use larger wheels and/or slow down the speed using setTravelSpeed(). Make sure the 
 * battery is <b>fully charged</b>. The robot is more stable on carpet than hardwood at higher speeds.</p> 
 *  
 * <p>The default speed is 50, which can be changed with setTravelSpeed().</p>  
 * 
 * @see lejos.robotics.navigation.Segoway
 * @author BB
 *
 */
public class SegowayPilot extends Segoway implements ArcRotateMoveController {
	
	private int move_delay = 5000; // amount to delay between moves
	
	private double wheelDiameter; // used for calculating travel distances
	private double trackWidth; // used for calculating differential rotation angle of whole robot

	private long left_start_tacho; // tachometer reading at start of move. Used by calcXxxNotify() methods.
	private long right_start_tacho; // tachometer reading at start of move. Used by calcXxxNotify() methods.

	/**
	 * MoveListeners to notify when a move is started or stopped.
	 */
	private ArrayList<MoveListener> listeners= new ArrayList<MoveListener>();

	/**
	 * Creates an instance of SegowayPilot.
	 * 
	 * @param left The left motor. 
	 * @param right The right motor. 
	 * @param gyro A generic gyroscope
	 * @param wheelDiameter The diameter of the wheel. For convenience, use the WHEEL_SIZE_XXX constants.
	 * @param trackWidth Distance between the center of the right tire and left tire. Use the same units as wheelDiameter.
	 */
	public SegowayPilot(EncoderMotor left, EncoderMotor right, Gyroscope gyro, double wheelDiameter, double trackWidth) {
		super (left, right, gyro, wheelDiameter);

		// Initialize move target, which keeps the robot stationary when it starts. Ideally these 
		// would be recorded before the robot starts balance algorithm but won't allow it. Shouldn't make a big difference. 
		this.left_tacho_target = left.getTachoCount();
		this.right_tacho_target = right.getTachoCount();

		this.trackWidth = trackWidth;
		this.wheelDiameter = wheelDiameter;

		// Start move control thread
		new MoveControlRegulator().start();
		
		// Sit still for a few seconds at start to establish good balance:
		try {Thread.sleep(move_delay);} catch (InterruptedException e) {}
	}

	/**
	 * Calculates the turn rate corresponding to the turn radius; <br>
	 * use as the parameter for steer() negative argument means center of turn
	 * is on right, so angle of turn is negative
	 * @param radius
	 * @return turnRate to be used in steer()
	 */
	private float turnRate(final float radius) {
		int direction;
		float radiusToUse;
		if (radius < 0) {
			direction = -1;
			radiusToUse = -radius;
		} else {
			direction = 1;
			radiusToUse = radius;
		}
		double ratio = (2 * radiusToUse - trackWidth) / (2 * radiusToUse + trackWidth);
		return (float)(direction * 100 * (1 - ratio));
	}

	private void steerPrep(double turnRate, double angle) {

		// Record starting tachos before rotation begins:
		left_start_tacho = left_tacho_target;
		right_start_tacho = right_tacho_target;

		float steerRatio;
		double rate = turnRate; // TODO: Not sure why this occurs. Try it with leaving as turnRate.
		if (rate < -200) rate = -200;
		if (rate > 200) rate = 200;

		int side = (int) Math.signum(turnRate);
		steerRatio = (float)(1 - rate / 100.0); // calculates ratio for inside wheel
		int rotAngle = (int) (angle * trackWidth * 2 / (wheelDiameter * (1 - steerRatio)));

		if (turnRate < 0) { // -ve radius, right wheel is inside of turn
			left_tacho_target -= (side * rotAngle);
			right_tacho_target -= (int) (side * rotAngle * steerRatio);

		} else { // +ve radius, right wheel is outside of turn
			left_tacho_target -= (int) (side * rotAngle * steerRatio);
			right_tacho_target -= (side * rotAngle);
		}

		if(angle > 0) { // move backwards if -ve angle
			move_mode = ARC_F; // forward
			wheelDriver(-Math.round(SPEED * steerRatio), -SPEED);
		} else {
			move_mode = ARC_B; // backward
			wheelDriver(Math.round(SPEED * steerRatio), SPEED);
		}
	}

	/**
	 * Moves the robot along a curved path for a specified angle of rotation. This method is similar to the
	 * {@link #arc(double radius, double angle, boolean immediateReturn)} method except it uses the <code> turnRate()</code>
	 * parameter to determine the curvature of the path and therefore has the ability to drive straight.
	 * This makes it useful for line following applications. This method has the ability to return immediately
	 * by using the <code>immediateReturn</code> parameter set to <b>true</b>.
	 *
	 * <p>
	 * The <code>turnRate</code> specifies the sharpness of the turn. Use values between -200 and +200.<br>
	 * For details about how this parameter works, see {@link lejos.robotics.navigation.DifferentialPilot#steer(double, double)}
	 * <p>
	 * The robot will stop when its heading has changed by the amount of the  <code>angle</code> parameter.<br>
	 * If <code>angle</code> is positive, the robot will move in the direction that increases its heading (it turns left).<br>
	 * If <code>angle</code> is negative, the robot will move in the direction that decreases its heading (turns right).<br>
	 * If <code>angle</code> is zero, the robot will not move and the method returns immediately.<br>
	 * For more details about this parameter, see {@link lejos.robotics.navigation.DifferentialPilot#steer(double, double)}
	 * <p>
	 * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
	 *
	 * @param turnRate If positive, the left side of the robot is on the inside of the turn. If negative,
	 * the left side is on the outside.
	 * @param angle The angle through which the robot will rotate. If negative, robot traces the turning circle backwards.
	 * @param immediateReturn If immediateReturn is true then the method returns immediately.
	 */
	public void steer(double turnRate, double angle, boolean immediateReturn) {
		this.arc_target_angle = (float)angle; // required for calcArcNotify() method

		if (angle == 0) return;

		if (turnRate == 0) {
			forward();
			return;
		}

		steerPrep((float)turnRate, (float)angle); // This method runs wheelDriver() which starts it moving

		// TODO: Assumes user won't go straight from travel to say rotate and assumes stop lasts at least MONITOR_INTERVAL.
		if(!immediateReturn) while(move_mode != STOP);
		try {Thread.sleep(move_delay);} catch (InterruptedException e) {}

	}

	private int angle_parity = 1;

	public void arc(double radius, double angle, boolean immediateReturn) {
		for(MoveListener ml:listeners) 
			ml.moveStarted(new Move(true, (float)angle, (float)radius), this); // TODO: Hasn't really been tested

		if (radius == Double.POSITIVE_INFINITY || radius == Double.NEGATIVE_INFINITY) {
			forward();
			return;
		}

		// For use in calcArcNotify() method
		if(radius < 0) angle_parity = -1;
		else angle_parity = 1;

		steer(turnRate((float)radius), angle, immediateReturn);// type and move started called by steer()
	}

	public void forward() {
		travel(Double.POSITIVE_INFINITY, true);
	}

	public void backward() {
		travel(Double.NEGATIVE_INFINITY, true);
	}

	// TODO: Possible setDelay() method to delay between each movement to recover. Assume will use some default
	// value unless it performs extraordinarily stable between movement.
	
	/**
	 * Set the delay between movements which allows the Segoway to recover balance. Default value is 
	 * five seconds (5000 millis).
	 */
	public void setMoveDelay(int millis) {
		move_delay = millis;
	}
	
	private long arc_target_tacho_avg; // Global for the target, used by calcXxxNotify()
	private double arc_target_angle; // Global for the target, used by calcArcNotify()

	public void _stop() {
		// Get the average tacho distance it was supposed to travel (for arc calculation) 
		arc_target_tacho_avg = left_start_tacho - left_tacho_target;
		arc_target_tacho_avg += right_start_tacho - right_tacho_target;
		arc_target_tacho_avg /= 2;

		// Get the current tachometer readings for each wheel
		this.left_tacho_target = left_motor.getTachoCount();
		this.right_tacho_target = right_motor.getTachoCount();

		int previous_move_mode = move_mode;

		// Change mode value to STOP so robot hovers in place without wandering
		move_mode = STOP;

		wheelDriver(0,0); // Causes the robot to stop moving

		// Choose the proper method to calculate the movement and notify MoveListeners
		if(previous_move_mode == ROTATE_L||previous_move_mode == ROTATE_R)
			calcRotateNotify();
		if(previous_move_mode == ARC_F||previous_move_mode == ARC_B)
			calcArcNotify();
		else
			calcTravelNotify();
		//try {Thread.sleep(move_delay);} catch (InterruptedException e) {}
	}

	/**
	 * This method calculates the total distance traveled in straight lines.
	 */
	private void calcTravelNotify() {
		// Calculate distance moved from old values
		long tacho_total = this.left_start_tacho - this.left_tacho_target;
		tacho_total += this.right_start_tacho - this.right_tacho_target;
		tacho_total /= 2;
		double circ = Math.PI * wheelDiameter;
		double dist = (circ * tacho_total) / 360;

		// Notify MoveListeners
		for(MoveListener ml:listeners) 
			ml.moveStopped(new Move((float)dist, 0, false), this);
	}

	/**
	 * This method calculates the distance and angle change of an arc.
	 */
	private void calcArcNotify() {
		// TODO: NOTE: If user tries to do an infinite arc distance then this is not going
		// to work because it calculates the final angle based on the original target angle.
		
		// Calculate distance moved--average of both motors
		long left_tacho_total = this.left_start_tacho - this.left_tacho_target;
		long right_tacho_total = this.right_start_tacho - this.right_tacho_target;
		long tacho_total = (left_tacho_total + right_tacho_total) / 2;

		double circ = Math.PI * wheelDiameter;
		double dist = (circ * tacho_total) / 360;
		
		// Divide dist by target distance. 1.0 is to convert to double value
		double angle = (1.0 * tacho_total) / arc_target_tacho_avg;

		// Multiply by angle it was trying for to get actual angle
		angle *= arc_target_angle;

		// TODO: Strange, it seems like left motor has LESS tacho count than right when making a -ve radius turn!
		// It works, but internally something seems messed up. Probably traces back to how arc calculations are made.
		
		// If radius was negative (right arc), reverse the sign on the angle it turned:
		// TODO: Due to this angle_parity kludge this method won't properly calculate 
		// if user goes straight to the steer() method.
		angle *= angle_parity; 

		// Notify MoveListeners
		for(MoveListener ml:listeners) 
			ml.moveStopped(new Move((float)dist, (float)angle, false), this);
	}

	/**
	 * Calculates the total angle made after a rotation completes.
	 */
	private void calcRotateNotify() {
		// Calculate distance moved from old values
		long tacho_total = this.left_tacho_target - this.left_start_tacho;
		tacho_total += this.right_start_tacho - this.right_tacho_target;
		tacho_total /= 2;
		double dist = (tacho_total * wheelDiameter * Math.PI)/ 360;
		double angle = (360 * dist)/(trackWidth * Math.PI);

		// Notify MoveListeners
		for(MoveListener ml:listeners) 
			ml.moveStopped(new Move(0, (float)angle, false), this);
	}

	public void travel(double distance, boolean immediateReturn) {
		// Use left_tacho_target as starting point:
		left_start_tacho = left_tacho_target;
		right_start_tacho = right_tacho_target;

		// Calculate degrees to rotate the wheels
		double circ = Math.PI * wheelDiameter;
		long degree_rotations = (long)((distance / circ) * 360);

		// Notice that forward movement results in NEGATIVE tacho movement. Motors are backwards.
		this.left_tacho_target -= degree_rotations;
		this.right_tacho_target -= degree_rotations;

		// In case forward() or backward() calls filtered through to here, want indefinite movement:
		if(distance == Double.POSITIVE_INFINITY) {
			this.left_tacho_target = Integer.MIN_VALUE; // Technically want NEGATIVE_INFINITY here.
			this.right_tacho_target = Integer.MIN_VALUE;
		} else if(distance == Double.NEGATIVE_INFINITY) {
			this.left_tacho_target = Integer.MAX_VALUE; // Technically want POSITIVE_INFINITY here.
			this.right_tacho_target = Integer.MAX_VALUE; // Shouldn't cause a problem in practical use = 809 km.
		}
		
		// Set steer() values to begin movement
		if(distance > 0) {
			move_mode = FORWARD_T;
			wheelDriver(-SPEED, -SPEED); // forward
		} else {
			move_mode = BACKWARD_T;
			wheelDriver(SPEED, SPEED); // backward
		}

		// Notify MoveListeners that a new move has begun.
		for(MoveListener ml:listeners) 
			ml.moveStarted(new Move(Move.MoveType.TRAVEL, (float)distance, 0, true), this);

		// The MoveControlRegulator will switch to stop mode when the targets are reached. Wait here if immediateReturn = false.
		if(!immediateReturn) while(move_mode != STOP);
		
		try {Thread.sleep(move_delay);} catch (InterruptedException e) {}
	}

	public void rotate(double degrees, boolean immediateReturn) {
		// Calculate how much to rotate the wheels (tachometer target)
		double circleCirc = trackWidth * Math.PI; // Circumference of circle traced out by robot wheels 
		double circleDist = (degrees/360) * circleCirc;

		double wheelCirc = this.wheelDiameter * Math.PI;
		long degree_rotations = Math.round((circleDist/wheelCirc) * 360); // Degrees to rotate wheels 

		// Record starting tachos before rotation begins:
		left_start_tacho = left_tacho_target;
		right_start_tacho = right_tacho_target;

		// Assign rotation targets for each motor:
		left_tacho_target += degree_rotations;
		right_tacho_target -= degree_rotations;

		if(degrees > 0) {
			move_mode = ROTATE_L;
			wheelDriver(SPEED, -SPEED); // rotate counter clockwise
		} else {
			move_mode = ROTATE_R;
			wheelDriver(-SPEED, SPEED); // rotate clockwise
		}

		// Notify all MoveListeners
		for(MoveListener ml:listeners) 
			ml.moveStarted(new Move(Move.MoveType.ROTATE, 0, (float)degrees, true), this);

		// Wait here until move completes if immediateReturn = false:
		if(!immediateReturn) while(move_mode != STOP);
		
		try {Thread.sleep(move_delay);} catch (InterruptedException e) {}
	}

	public double getMaxTravelSpeed() {
		return 200; // TODO: Find some other theoretical top speed
	}

	public double getMovementIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getTravelSpeed() {
		return SPEED;
	}

	/**
	 * Currently this method isn't properly implemented with the proper units. Speed is just
	 * an arbitrary number up to about 200. At higher speed values it can be unstable. Currently it
	 * uses a default of 80 which is near the unstable speed.
	 * 
	 * Will need to make this method use units/second. 
	 * @param speed The speed to travel.
	 */
	public void setTravelSpeed(double speed) {
		// TODO Use actual units per second
		SPEED = (int)speed;
	}

	public boolean isMoving() {
		if(move_mode == STOP) return false;
		else return true;
	}

	public void travel(double distance) {
		travel(distance, false);
	}

	public void addMoveListener(MoveListener m) {
		listeners.add(m);
	}

	public Move getMovement() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getAngleIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getRotateMaxSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getRotateSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void rotate(double angle) {
		rotate(angle, false);
	}

	public void setRotateSpeed(double arg0) {
		// TODO Auto-generated method stub
	}

	public void arc(double radius, double angle) {
		arc(radius, angle, false);
	}

	public void arcBackward(double radius) {
		arc(radius, Double.NEGATIVE_INFINITY, true);
	}

	public void arcForward(double radius) {
		arc(radius, Double.POSITIVE_INFINITY, true);
	}

	private double minRadius = 0; 

	public double getMinRadius() {
		return minRadius;
	}

	public void setMinRadius(double radius) {
		this.minRadius = radius;
	}

	public void travelArc(double radius, double distance) {
		travelArc(radius, distance, false);
	}

	public void travelArc(double radius, double distance, boolean immediateReturn) {
		if (radius == Double.POSITIVE_INFINITY || radius == Double.NEGATIVE_INFINITY) {
			travel(distance, immediateReturn);
			return;
		}

		if (radius == 0) {
			throw new IllegalArgumentException("Zero arc radius");
		}
		float angle = (float)((distance * 180) / (Math.PI * radius));
		arc(radius, angle, immediateReturn);
	}

	// Used by MoveControlRegulator to keep robot from wandering or move to location.
	private long left_tacho_target;
	private long right_tacho_target;

	// TODO: PROPERLY Integrate this into method for setting speed (units/sec). Make lower case.
	public int SPEED = 50;

	public static final int STOP = 0;
	public static final int FORWARD_T = 1;
	public static final int BACKWARD_T = 2;
	public static final int ROTATE_L = 3;
	public static final int ROTATE_R = 4;
	public static final int ARC_F = 5;
	public static final int ARC_B = 6;

	private int move_mode = STOP; // start robot holding current position

	/**
	 * This thread runs in parallel to the balance thread.  This thread monitors
	 * the current move mode (STOP, FORWARD, etc...) and seeks to carry out the move.
	 */
	private class MoveControlRegulator extends Thread {

		/**
		 * Period of time (in ms) between checking the control situation
		 */
		private static final int MONITOR_INTERVAL = 7;

		/**
		 * Maximum impulse (kind of like speed) when correcting position in stop() mode.
		 */
		private static final int MAX_CORRECTION = 5;

		public MoveControlRegulator(){
			this.setDaemon(true);
		}

		public void run() {
			
			while(true) { // TODO: Could use crash monitoring code to flip conditional to false when it keels over

				switch(move_mode) {
				case STOP:

					// TODO: This might work for every move if MAX_CORRECTION replaced with speed.
					// TODO: Didn't try optimizing MAX_CORRECTION value. Get rid of /2? Make it /4?
					long leftDiff = (left_tacho_target - left_motor.getTachoCount())/2; 
					long rightDiff = (right_tacho_target - right_motor.getTachoCount())/2;
					if(leftDiff > MAX_CORRECTION) leftDiff = MAX_CORRECTION;
					if(rightDiff > MAX_CORRECTION) rightDiff = MAX_CORRECTION;

					wheelDriver((int)leftDiff, (int)rightDiff);

					break;
				case ARC_F:
				case FORWARD_T:
					// Monitor until it gets to target.
					long left_diff = left_motor.getTachoCount() - left_tacho_target;
					long right_diff = right_motor.getTachoCount() - right_tacho_target;
					if(left_diff < 0 && right_diff < 0) {
						// Get the average tacho distance it was supposed to travel (for arc calculation) 
						arc_target_tacho_avg = left_start_tacho - left_tacho_target;
						arc_target_tacho_avg += right_start_tacho - right_tacho_target;
						arc_target_tacho_avg /= 2;

						int prev_move_mode = move_mode;
						wheelDriver(0,0);
						move_mode = STOP;
						if(prev_move_mode == FORWARD_T)
							calcTravelNotify();
						else if(prev_move_mode == ARC_F)
							calcArcNotify();
					}
					break;
				case ARC_B:
				case BACKWARD_T:
					// Monitor until it gets to target.
					left_diff = left_motor.getTachoCount() - left_tacho_target;
					right_diff = right_motor.getTachoCount() - right_tacho_target;
					if(left_diff > 0 && right_diff > 0) {
						// Get the average tacho distance it was supposed to travel (for arc calculation) 
						arc_target_tacho_avg = left_start_tacho - left_tacho_target;
						arc_target_tacho_avg +=  right_start_tacho - right_tacho_target;
						arc_target_tacho_avg /= 2;

						int prev_move_mode = move_mode;
						wheelDriver(0,0);
						move_mode = STOP;

						if(prev_move_mode == BACKWARD_T)
							calcTravelNotify();
						else if(prev_move_mode == ARC_B)
							calcArcNotify();
					}
					break;
				// TODO: Possible strategy for better rotations--use some sort of equation that watches the tachos for
				// each motor and can determine when they have made it rotate xx degrees, even if one ahead of the other.
				// Also, the 7ms delay between balance loop probably means this can miss the target by a bit.
				case ROTATE_L:
					// Monitor until it gets to target.
					left_diff = left_motor.getTachoCount() - left_tacho_target;
					right_diff = right_motor.getTachoCount() - right_tacho_target;

					if(left_diff > 0 && right_diff < 0) {
						wheelDriver(0,0);
						move_mode = STOP;
						Thread.yield();
						calcRotateNotify();
					}
					break;
				case ROTATE_R:
					// Monitor until it gets to target.
					left_diff = left_motor.getTachoCount() - left_tacho_target;
					right_diff = right_motor.getTachoCount() - right_tacho_target;
					
					if(left_diff < 0 && right_diff > 0) {
						wheelDriver(0,0);
						move_mode = STOP;
						Thread.yield();
						calcRotateNotify();
					}
					break;
				}

				try {Thread.sleep(MONITOR_INTERVAL);} catch (InterruptedException e) {}
			}
		}
	}	
}