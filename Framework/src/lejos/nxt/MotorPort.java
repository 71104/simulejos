package lejos.nxt;

import it.uniroma1.di.simulejos.bridge.Bridge;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface.Motor.Mode;

/**
 * 
 * Abstraction for a NXT output port.
 * 
 */
public class MotorPort implements TachoMotorPort {
	private final int id;
	private final SimulatorInterface.Motor motor;

	private MotorPort(int id, SimulatorInterface.Motor motor) {
		this.id = id;
		this.motor = motor;
	}

	/**
	 * The number of ports available.
	 */
	public static final int NUMBER_OF_PORTS = 3;

	/**
	 * MotorPort A.
	 */
	public static final MotorPort A = new MotorPort(0, Bridge.getSimulator()
			.getA());

	/**
	 * MotorPort B.
	 */
	public static final MotorPort B = new MotorPort(1, Bridge.getSimulator()
			.getB());

	/**
	 * MotorPort C.
	 */
	public static final MotorPort C = new MotorPort(2, Bridge.getSimulator()
			.getC());

	/**
	 * Return the MotorPort with the given Id.
	 * 
	 * @param id
	 *            the Id, between 0 and {@link #NUMBER_OF_PORTS}-1.
	 * @return the MotorPort object
	 */
	public static MotorPort getInstance(int id) {
		switch (id) {
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

	private static final SimulatorInterface.Motor.Mode MODES[] = {
			Mode.FORWARD, Mode.BACKWARD, Mode.FLOAT, Mode.STOP };

	/**
	 * Low-level method to control a motor.
	 * 
	 * @param power
	 *            power from 0-100
	 * @param mode
	 *            defined in <code>BasicMotorPort</code>. 1=forward, 2=backward,
	 *            3=stop, 4=float.
	 * @see BasicMotorPort#FORWARD
	 * @see BasicMotorPort#BACKWARD
	 * @see BasicMotorPort#FLOAT
	 * @see BasicMotorPort#STOP
	 */
	public void controlMotor(int power, int mode) {
		motor.control(power, MODES[mode - 1]);
	}

	/**
	 * returns tachometer count
	 */
	public int getTachoCount() {
		return motor.getCount();
	}

	/**
	 * resets the tachometer count to 0;
	 */
	public void resetTachoCount() {
		motor.resetCount();
	}

	public int getId() {
		return id;
	}

	@Override
	public void setPWMMode(int mode) {
	}
}
