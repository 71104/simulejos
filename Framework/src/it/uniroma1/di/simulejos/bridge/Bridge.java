package it.uniroma1.di.simulejos.bridge;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

import lejos.nxt.LCDOutputStream;

public final class Bridge {
	private static volatile SimulatorInterface simulator;
	private static volatile PrintWriter log;
	private static volatile BrickInterface brick;

	private static boolean checkRedirectIOPermission() {
		final SecurityManager securityManager = System.getSecurityManager();
		if (securityManager != null) {
			try {
				securityManager.checkPermission(new RuntimePermission("setIO"));
				return true;
			} catch (SecurityException e) {
				return false;
			}
		} else {
			return true;
		}
	}

	private static final boolean redirectIO = checkRedirectIOPermission();

	public static void initialize(String robotName,
			final SimulatorInterface simulator) {
		Bridge.simulator = simulator;
		log = new PrintWriter(new Writer() {
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
		brick = new NXTWindow(simulator.getParentWindow(), robotName);

		if (redirectIO) {
			System.setOut(new PrintStream(new LCDOutputStream()));
			System.setErr(new PrintStream(new OutputStream() {
				@Override
				public void flush() {
					log.flush();
				}

				@Override
				public void write(byte[] b, int off, int len) {
					final byte[] bytes = new byte[len];
					System.arraycopy(b, off, bytes, 0, len);
					log.write(new String(bytes).toCharArray());
				}

				@Override
				public void write(byte[] b) {
					log.write(new String(b).toCharArray());
				}

				@Override
				public void write(int b) {
					final byte[] bytes = new byte[] { (byte) b };
					log.write(new String(bytes).toCharArray());
				}
			}));
		}
	}

	public static void cleanup() {
		log.close();
		brick.dispose();
	}

	public static SimulatorInterface getSimulator() {
		return simulator;
	}

	public static PrintWriter getLog() {
		return log;
	}

	public static BrickInterface getBrick() {
		return brick;
	}

	private Bridge() {
	}
}
