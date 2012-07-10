package it.uniroma1.di.simulejos.bridge;

import java.awt.Frame;
import java.io.PrintWriter;

public interface SimulatorInterface {
	String getRobotName();

	Frame getParentWindow();

	PrintWriter getLogWriter();

	static interface Motor {
		static enum Mode {
			FORWARD, BACKWARD, STOP, FLOAT
		};

		Mode getMode();

		void setMode(Mode mode);

		int getPower();

		void setPower(int power);

		void control(int power, Mode mode);

		int getCount();

		void resetCount();
	}

	Motor getA();

	Motor getB();

	Motor getC();

	static interface Sensor {
	}

	static interface ColorSensor extends Sensor {
		int getColor();
	}

	static interface LightSensor extends Sensor {
		int getLight();
	}

	static interface CompassSensor extends Sensor {
		double getAngle();

		void setZero();

		void resetZero();
	}

	Sensor getS1();

	Sensor getS2();

	Sensor getS3();

	Sensor getS4();

	void shutDown();
}
