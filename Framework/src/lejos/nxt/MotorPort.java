package lejos.nxt;

/**
 * 
 * Abstraction for a NXT output port.
 *
 */
public class MotorPort implements TachoMotorPort {
	private int _id;
	private int _pwmMode = PWM_FLOAT; // default to float mode
	
	private MotorPort(int id)
	{
		_id = id;
	}
	
    /**
     * The number of ports available.
     */
    public static final int NUMBER_OF_PORTS = 3;
    
	/**
	 * MotorPort A.
	 */
	public static final MotorPort A = new MotorPort (0);
	
	/**
	 * MotorPort B.
	 */
	public static final MotorPort B = new MotorPort (1);
	
	/**
	 * MotorPort C.
	 */
	public static final MotorPort C = new MotorPort (2);
	
    /**
     * Return the MotorPort with the given Id.
     * @param id the Id, between 0 and {@link #NUMBER_OF_PORTS}-1.
     * @return the MotorPort object
     */
    public static MotorPort getInstance(int id)
    {
    	switch (id)
    	{
    		case 0:
    			return MotorPort.A;
    		case 1:
    			return MotorPort.B;
    		case 2:
    			return MotorPort.C;
    		default:
    			throw new IllegalArgumentException("no such motor port");
    	}
    }

	/**
	 * Low-level method to control a motor. 
	 * 
	 * @param power power from 0-100
	 * @param mode defined in <code>BasicMotorPort</code>. 1=forward, 2=backward, 3=stop, 4=float.
     * @see BasicMotorPort#FORWARD
     * @see BasicMotorPort#BACKWARD
     * @see BasicMotorPort#FLOAT
     * @see BasicMotorPort#STOP
	 */
	public void controlMotor(int power, int mode)
	{
		// Convert lejos power and mode to NXT power and mode
		controlMotorById(_id, 
				         (mode >= 3 ? 0 : (mode == 2 ? -power: power)) ,
				         (mode == 3 ? 1 : (mode == 4 ? 0 : _pwmMode)));
	}

	/**
	 * Low-level method to control a motor.
	 * 
	 * @param power power from -100 to =100
	 * @param mode 0=float, 1=brake
	 */
	private static synchronized native void controlMotorById(int id, int power, int mode);

	/**
	 * returns tachometer count
	 */
	public  int getTachoCount()
	{
		return getTachoCountById(_id);
	}

	private static native int getTachoCountById(int aMotor);
	
    /**
	 *resets the tachometer count to 0;
	 */ 
	public void resetTachoCount()
	{
		resetTachoCountById(_id);
	}
	
	public void setPWMMode(int mode)
	{
		_pwmMode = mode;
	}
	
	public int getId()
	{
		return this._id;
	}
	  
	private static synchronized native void resetTachoCountById(int aMotor);
}
