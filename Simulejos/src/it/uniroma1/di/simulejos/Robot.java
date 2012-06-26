package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.bridge.Bridge;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;

import java.awt.Frame;
import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

final class Robot implements Serializable {
	private static final long serialVersionUID = 1961674308786529328L;

	private static volatile int nextIndex = 1;

	private final int index;
	private final File classPath;
	private final String mainClassName;
	private final String script;

	private transient volatile Frame parentWindow;
	private transient volatile PrintWriter logWriter;
	private transient volatile Thread thread;

	Robot(File classPath, String mainClassName, String script,
			Frame parentWindow, Writer logWriter) {
		this.index = nextIndex++;
		this.classPath = classPath;
		this.mainClassName = mainClassName;
		this.script = script;
		this.parentWindow = parentWindow;
		this.logWriter = new PrintWriter(new PartialWriter("NXT" + index,
				logWriter));
	}

	private class Simulator implements SimulatorInterface {
		@Override
		public String getRobotName() {
			return "NXT" + index;
		}

		@Override
		public Frame getParentWindow() {
			return parentWindow;
		}

		@Override
		public PrintWriter getLogWriter() {
			return logWriter;
		}

		@Override
		public Motor getA() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Motor getB() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Motor getC() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Sensor getS1() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Sensor getS2() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Sensor getS3() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Sensor getS4() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	void setParentWindow(Frame parentWindow) {
		this.parentWindow = parentWindow;
	}

	void setLogWriter(PrintWriter logWriter) {
		this.logWriter = logWriter;
	}

	void play() {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				final URL url;
				try {
					url = classPath.toURI().toURL();
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
				final URL[] urls = { Robot.class.getResource("Framework.jar"),
						url };
				final VirtualClassLoader classLoader = new VirtualClassLoader(
						urls);
				try {
					classLoader.loadClass(Bridge.class.getName())
							.getMethod("initialize", SimulatorInterface.class)
							.invoke(null, new Simulator());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				final Class<?> mainClass;
				try {
					mainClass = classLoader.loadClass(mainClassName);
				} catch (ClassNotFoundException e) {
					// TODO
					e.printStackTrace();
					return;
				}
				try {
					final String[] arguments = {};
					mainClass.getMethod("main", String[].class).invoke(null,
							(Object[]) arguments);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, "NXT" + index);
		thread.start();
	}

	@SuppressWarnings("deprecation")
	void suspend() {
		thread.suspend();
	}

	@SuppressWarnings("deprecation")
	void resume() {
		thread.resume();
	}

	@SuppressWarnings("deprecation")
	void stop() {
		thread.stop();
	}
}