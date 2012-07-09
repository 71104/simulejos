package it.uniroma1.di.simulejos.bridge;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public final class Bridge {
	public static SimulatorInterface SIMULATOR;
	public static PrintWriter LOG;
	public static BrickInterface BRICK;

	public static void initialize(String robotName,
			final SimulatorInterface simulator) {
		SIMULATOR = simulator;
		LOG = new PrintWriter(new Writer() {
			private volatile boolean closed;
			private final Writer writer = simulator.getLogWriter();

			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				if (!closed) {
					writer.write(cbuf, off, len);
				}
			}

			@Override
			public void flush() throws IOException {
				if (!closed) {
					writer.flush();
				}
			}

			@Override
			public void close() {
				closed = true;
			}
		});
		BRICK = new NXTWindow(simulator.getParentWindow(), robotName);
	}

	public static void cleanup() {
		LOG.close();
		BRICK.dispose();
	}

	private Bridge() {
	}
}
