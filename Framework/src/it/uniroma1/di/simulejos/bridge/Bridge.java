package it.uniroma1.di.simulejos.bridge;

import java.io.PrintWriter;

public final class Bridge {
	public static SimulatorInterface SIMULATOR;
	public static PrintWriter LOG;
	public static BrickInterface BRICK;

	public static void initialize(String robotName, SimulatorInterface simulator) {
		SIMULATOR = simulator;
		LOG = simulator.getLogWriter();
		BRICK = new NXTWindow(simulator.getParentWindow(), robotName);
	}

	private Bridge() {
	}
}
