package lejos.nxt;

import it.uniroma1.di.simulejos.bridge.SimulatorInterface;

public interface BasicSensorPort extends SensorConstants {
	<SensorType extends SimulatorInterface.Sensor> SensorType getSensor(
			Class<SensorType> sensorType);

	int getMode();

	int getType();

	void setMode(int mode);

	void setType(int type);

	void setTypeAndMode(int type, int mode);
}
