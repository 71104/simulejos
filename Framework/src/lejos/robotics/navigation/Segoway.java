package lejos.robotics.navigation;

import lejos.nxt.Sound; // TODO: Visual count-down only, no sound? Or some sort of sound interface and container for Sound class (can't implement interface on static methods)?
import lejos.robotics.EncoderMotor;
import lejos.robotics.Gyroscope;

/**
 * <p>This class balances a two-wheeled Segway-like robot. It works with almost any construction 
 * (tall or short) such as the <a href="http://www.laurensvalk.com/nxt-2_0-only/anyway">Anyway</a> or
 * the <a href="http://www.hitechnic.com/blog/gyro-sensor/htway/">HTWay</a>. Wheel diameter is the most
 * important construction variable, which is specified in the constructor.</p> 
 * 
 * <p>To start the robot balancing:
 * <li>1. Run the program. You will be prompted to lay it down.
 * <li>2. Lay it down (orientation doesn't matter). When it detects it is not moving it will automatically calibrate the gyro sensor.
 * <li>3. When the beeping begins, stand it up so it is vertically balanced.
 * <li>4. When the beeping stops, let go and it will begin balancing on its own.</p>
 * 
 * <p>Alternately you can lean the robot against a wall and run the program. After the gyro 
 * calibration, the robot backs up against the wall until it falls forward. When it detects the
 * forward fall, it start the balance loop.</p>
 * 
 * <p>NOTE: In order to make the robot move and navigate, use the SegowayPilot class.</p> 
 * 
 * <p><i>This code is based on the <a href="http://www.hitechnic.com/blog/gyro-sensor/htway/">HTWay</a> by HiTechnic.</i></p>
 * 
 * @author BB
 *
 */
public class Segoway extends Thread { // TODO: Thread should be a private inner class.

	// Motors and gyro:
	//private GyroSensor gyro; 
	private Gyroscope gyro;
	protected EncoderMotor left_motor;
	protected EncoderMotor right_motor;
	
	//=====================================================================
	// Balancing constants
	//
	// These are the constants used to maintain balance.
	//=====================================================================
	
	/** 
	 * Loop wait time.  WAIT_TIME is the time in ms passed to the Wait command.
	 * NOTE: Balance control loop only takes 1.128 MS in leJOS NXJ. 
	 */
	private static final int WAIT_TIME = 7; // originally 8
	
	// These are the main four balance constants, only the gyro
	// constants are relative to the wheel size.  KPOS and KSPEED
	// are self-relative to the wheel size.
	private static final double KGYROANGLE = 7.5;
	private static final double KGYROSPEED = 1.15;
	private static final double KPOS = 0.07;
	private static final double KSPEED = 0.1;

	/**
	 * This constant aids in drive control. When the robot starts moving because of user control,
	 * this constant helps get the robot leaning in the right direction.  Similarly, it helps 
	 * bring robot to a stop when stopping.
	 */
	private static final double KDRIVE = -0.02;

	/**
	 * Power differential used for steering based on difference of target steering and actual motor difference.
	 */
	private static final double KSTEER = 0.25;

	/**
	 * Gyro offset control
	 * The gyro sensor will drift with time.  This constant is used in a simple long term averaging
	 * to adjust for this drift. Every time through the loop, the current gyro sensor value is
	 * averaged into the gyro offset weighted according to this constant.
	 */
	private static final double EMAOFFSET = 0.0005;

	/** 
	 * If robot power is saturated (over +/- 100) for over this time limit then 
	 * robot must have fallen.  In milliseconds.
	 */
	private static final double TIME_FALL_LIMIT = 500; // originally 1000

	//---------------------------------------------------------------------

	/**
	 * This constant is in degrees/second for maximum speed.  Note that position 
	 * and speed are measured as the sum of the two motors, in other words, 600 
	 * would actually be 300 degrees/second for each motor.
	 */
	private static final double CONTROL_SPEED  = 600.0;

	//=====================================================================
	// Global variables
	//=====================================================================
	
	// These two xxControlDrive variables are used to control the movement of the robot. Both
	// are in degrees/second:	
	/**
	 * motorControlDrive is the target speed for the sum of the two motors
	 * in degrees per second.
	 */
	private double motorControlDrive = 0.0;
	
	/**
	 * motorControlSteer is the target change in difference for two motors
	 * in degrees per second.
	 */
	private double motorControlSteer = 0.0;

	/**
	 * This global contains the target motor differential, essentially, which 
	 * way the robot should be pointing.  This value is updated every time through 
	 * the balance loop based on motorControlSteer.
	 */
	private double motorDiffTarget = 0.0;

	/**
	 * Time that robot first starts to balance.  Used to calculate tInterval.
	 */
	private long tCalcStart;

	/**
	 * tInterval is the time, in seconds, for each iteration of the balance loop.
	 */
	private double tInterval;

	/**
	 * ratioWheel stores the relative wheel size compared to a standard NXT 1.0 wheel.
	 * RCX 2.0 wheel has ratio of 0.7 while large RCX wheel is 1.4.
	 */
	private double ratioWheel;

	// Gyro globals
	private double gOffset;
	private double gAngleGlobal = 0;

	// Motor globals
	private double motorPos = 0;
	private long mrcSum = 0, mrcSumPrev;
	private long motorDiff;
	private long mrcDeltaP3 = 0;
	private long mrcDeltaP2 = 0;
	private long mrcDeltaP1 = 0;

	/**
	 * Creates an instance of the Segoway, prompts the user to lay Segoway flat for gyro calibration,
	 * then begins self-balancing thread. Wheel diameter is used in balancing equations.
	 *  
	 *  <li>NXT 1.0 wheels = 5.6 cm
	 *  <li>NXT 2.0 wheels = 4.32 cm
	 *  <li>RCX "motorcycle" wheels = 8.16 cm
	 * 
	 * @param left The left motor. An unregulated motor.
	 * @param right The right motor. An unregulated motor.
	 * @param gyro A HiTechnic gyro sensor
	 * @param wheelDiameter diameter of wheel, preferably use cm (printed on side of LEGO tires in mm)
	 */
	//public Segoway(EncoderMotor left, EncoderMotor right, GyroSensor gyro, double wheelDiameter) {
	public Segoway(EncoderMotor left, EncoderMotor right, Gyroscope gyro, double wheelDiameter) {
		this.left_motor = left;
		this.right_motor = right;
		// Optional code to accept BasicMotor: this.right_motor = (NXTMotor)right;
		this.gyro = gyro;
		this.ratioWheel = wheelDiameter/5.6; // Original algorithm was tuned for 5.6 cm NXT 1.0 wheels.
		
		// Took out 50 ms delay here.
		
		// Get the initial gyro offset
		getGyroOffset();

		// Play warning beep sequence before balance starts
		startBeeps();
		
		// Start balance thread
		this.setDaemon(true);
		this.start();		
	}

	/**
	 * This function returns a suitable initial gyro offset.  It takes
	 * 100 gyro samples over a time of 1/2 second and averages them to
	 * get the offset.  It also check the max and min during that time
	 * and if the difference is larger than one it rejects the data and
	 * gets another set of samples.
	 */
	private void getGyroOffset() {
		
		System.out.println("NXJ Segoway");
		System.out.println();
		System.out.println("Lay robot down");
		System.out.println("to calibrate");
		System.out.println("the gyro");
		System.out.println();
		
		//left_motor.flt(); // TODO: This didn't seem to make a bit of difference with GyroSensor calibration.
		//right_motor.flt();
		
		gyro.recalibrateOffset();
	}

	/**
	 * Warn user the Segoway is about to start balancing. 
	 */
	private void startBeeps() {
		
		System.out.println("Balance in");

		// Play warning beep sequence to indicate balance about to start
		for (int c=5; c>0;c--) {
			System.out.print(c + " ");
			Sound.playTone(440,100);
			try { Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		System.out.println("GO");
		System.out.println();
	}

	/**
	 * Get the data from the gyro. 
	 * Fills the pass by reference gyroSpeed and gyroAngle based on updated information from the Gyro Sensor.
	 * Maintains an automatically adjusted gyro offset as well as the integrated gyro angle.
	 * 
	 */
	private void updateGyroData() {
		// TODO: The GyroSensor class actually rebaselines for drift ever 5 seconds. This not needed? Or is this method better?
		// Some of this fine tuning may actually interfere with fine-tuning happening in the hardcoded dIMU and GyroScope code.
		float gyroRaw;

		gyroRaw = gyro.getAngularVelocity();
		gOffset = EMAOFFSET * gyroRaw + (1-EMAOFFSET) * gOffset;
		gyroSpeed = gyroRaw - gOffset; // Angular velocity (degrees/sec)

		gAngleGlobal += gyroSpeed*tInterval;
		gyroAngle = gAngleGlobal; // Absolute angle (degrees)
	}

	/**
	 * Keeps track of wheel position with both motors.
	 */
	private void updateMotorData() {
		long mrcLeft, mrcRight, mrcDelta;

		// Keep track of motor position and speed
		mrcLeft = left_motor.getTachoCount();
		mrcRight = right_motor.getTachoCount();

		// Maintain previous mrcSum so that delta can be calculated and get
		// new mrcSum and Diff values
		mrcSumPrev = mrcSum;
		mrcSum = mrcLeft + mrcRight;
		motorDiff = mrcLeft - mrcRight;

		// mrcDetla is the change int sum of the motor encoders, update
		// motorPos based on this detla
		mrcDelta = mrcSum - mrcSumPrev;
		motorPos += mrcDelta;

		// motorSpeed is based on the average of the last four delta's.
		motorSpeed = (mrcDelta+mrcDeltaP1+mrcDeltaP2+mrcDeltaP3)/(4*tInterval);

		// Shift the latest mrcDelta into the previous three saved delta values
		mrcDeltaP3 = mrcDeltaP2;
		mrcDeltaP2 = mrcDeltaP1;
		mrcDeltaP1 = mrcDelta;
	}

	/** 
	 * Global variables used to control the amount of power to apply to each wheel.
	 * Updated by the steerControl() method.
	 */
	private int powerLeft, powerRight; // originally local variables

	/**
	 * This function determines the left and right motor power that should
	 * be used based on the balance power and the steering control.
	 */
	private void steerControl(int power) {
		int powerSteer;

		// Update the target motor difference based on the user steering
		// control value.
		motorDiffTarget += motorControlSteer * tInterval;

		// Determine the proportionate power differential to be used based
		// on the difference between the target motor difference and the
		// actual motor difference.
		powerSteer = (int)(KSTEER * (motorDiffTarget - motorDiff));

		// Apply the power steering value with the main power value to
		// get the left and right power values.
		powerLeft = power + powerSteer;
		powerRight = power - powerSteer;

		// Limit the power to motor power range -100 to 100
		if (powerLeft > 100)   powerLeft = 100;
		if (powerLeft < -100)  powerLeft = -100;

		// Limit the power to motor power range -100 to 100
		if (powerRight > 100)  powerRight = 100;
		if (powerRight < -100) powerRight = -100;
	}

	/**
	 * Calculate the interval time from one iteration of the loop to the next.
	 * Note that first time through, cLoop is 0, and has not gone through
	 * the body of the loop yet.  Use it to save the start time.
	 * After the first iteration, take the average time and convert it to
	 * seconds for use as interval time.
	 */
	private void calcInterval(long cLoop) {
		if (cLoop == 0) {
			// First time through, set an initial tInterval time and
			// record start time
			tInterval = 0.0055;
			tCalcStart = System.currentTimeMillis();
		} else {
			// Take average of number of times through the loop and
			// use for interval time.
			tInterval = (System.currentTimeMillis() - tCalcStart)/(cLoop*1000.0);
		}
	}

	private double gyroSpeed, gyroAngle; // originally local variables
	private double motorSpeed; // originally local variable

	//---------------------------------------------------------------------
	// 
	// This is the main balance thread for the robot.
	//
	// Robot is assumed to start leaning on a wall.  The first thing it
	// does is take multiple samples of the gyro sensor to establish and
	// initial gyro offset.
	//
	// After an initial gyro offset is established, the robot backs up
	// against the wall until it falls forward, when it detects the
	// forward fall, it start the balance loop.
	//
	// The main state variables are:
	// gyroAngle  This is the angle of the robot, it is the results of
	//            integrating on the gyro value.
	//            Units: degrees
	// gyroSpeed  The value from the Gyro Sensor after offset subtracted
	//            Units: degrees/second
	// motorPos   This is the motor position used for balancing.
	//            Note that this variable has two sources of input:
	//             Change in motor position based on the sum of
	//             MotorRotationCount of the two motors,
	//            and,
	//             forced movement based on user driving the robot.
	//            Units: degrees (sum of the two motors)
	// motorSpeed This is the speed of the wheels of the robot based on the
	//            motor encoders.
	//            Units: degrees/second (sum of the two motors)
	//
	// From these state variables, the power to the motors is determined
	// by this linear equation:
	//     power = KGYROSPEED * gyro +
	//             KGYROANGLE * gyroAngle +
	//             KPOS       * motorPos +
	//             KSPEED     * motorSpeed;
	//
	public void run() {

		int power;
		long tMotorPosOK;
		long cLoop = 0;
				
		System.out.println("Balancing");
		System.out.println();
		
		tMotorPosOK = System.currentTimeMillis();

		// Reset the motors to make sure we start at a zero position
		left_motor.resetTachoCount();
		right_motor.resetTachoCount();

		// NOTE: This balance control loop only takes 1.128 MS to execute each loop in leJOS NXJ.
		while(true) {
			calcInterval(cLoop++);

			updateGyroData();

			updateMotorData();

			// Apply the drive control value to the motor position to get robot to move.
			motorPos -= motorControlDrive * tInterval;

			// This is the main balancing equation
			power = (int)((KGYROSPEED * gyroSpeed +               // Deg/Sec from Gyro sensor
					KGYROANGLE * gyroAngle) / ratioWheel + // Deg from integral of gyro
					KPOS       * motorPos +                 // From MotorRotaionCount of both motors
					KDRIVE     * motorControlDrive +        // To improve start/stop performance
					KSPEED     * motorSpeed);                // Motor speed in Deg/Sec

			if (Math.abs(power) < 100)
				tMotorPosOK = System.currentTimeMillis();

			steerControl(power); // Movement control. Not used for balancing.

			// Apply the power values to the motors
			// NOTE: It would be easier/faster to use MotorPort.controlMotorById(), but it needs to be public.
			left_motor.setPower(Math.abs(powerLeft));
			right_motor.setPower(Math.abs(powerRight));

			if(powerLeft > 0) left_motor.forward(); 
			else left_motor.backward();

			if(powerRight > 0) right_motor.forward(); 
			else right_motor.backward();

			// Check if robot has fallen by detecting that motorPos is being limited
			// for an extended amount of time.
			if ((System.currentTimeMillis() - tMotorPosOK) > TIME_FALL_LIMIT) break;
			
			try {Thread.sleep(WAIT_TIME);} catch (InterruptedException e) {}
		} // end of while() loop
		
		left_motor.flt();
		right_motor.flt();

		Sound.beepSequenceUp();
		System.out.println("Oops... I fell");
		System.out.println("tInt ms:");
		System.out.println((int)tInterval*1000);
	} // END OF BALANCING THREAD CODE

	/**
	 * This method allows the robot to move forward/backward and make in-spot rotations as
	 * well as arcs by varying the power to each wheel. This method does not actually 
	 * apply direct power to the wheels. Control is filtered through to each wheel, allowing the robot to 
	 * drive forward/backward and make turns. Higher values are faster. Negative values cause the wheel
	 * to rotate backwards. Values between -200 and 200 are good. If values are too high it can make the
	 * robot balance unstable.
	 * 
	 * @param left_wheel The relative control power to the left wheel. -200 to 200 are good numbers.
	 * @param right_wheel The relative control power to the right wheel. -200 to 200 are good numbers.
	 */
	
	public void wheelDriver(int left_wheel, int right_wheel) {
		// Set control Drive and Steer.  Both these values are in motor degree/second
		motorControlDrive = (left_wheel + right_wheel) * CONTROL_SPEED / 200.0;
		motorControlSteer = (left_wheel - right_wheel) * CONTROL_SPEED / 200.0;
	}		
}