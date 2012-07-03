package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.bridge.Bridge;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Elements;
import it.uniroma1.di.simulejos.opengl.Program;

import java.awt.Frame;
import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.opengl.GL2GL3;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public final class Robot implements Serializable {
	private static final long serialVersionUID = 1961674308786529328L;

	private static volatile int nextIndex = 1;

	public static String getNextName() {
		return "NXT" + nextIndex;
	}

	private final int index;
	private final File classPath;
	private final String mainClassName;
	private final String script;
	private final ModelData modelData;
	private volatile Vector3 position = Vector3.NULL;
	private volatile Vector3 heading = Vector3.NULL;

	private transient volatile Frame parentWindow;
	private transient volatile PrintWriter logWriter;
	private transient volatile Invocable invocable;
	private transient volatile Thread thread;

	private transient volatile Elements elements;

	Robot(File classPath, String mainClassName, String script,
			ModelData modelData, Frame parentWindow, Writer logWriter)
			throws ScriptException {
		this.index = nextIndex++;
		this.classPath = classPath;
		this.mainClassName = mainClassName;
		this.script = script;
		this.modelData = modelData;
		this.parentWindow = parentWindow;
		this.logWriter = new PrintWriter(new PartialWriter("NXT" + index,
				logWriter));
	}

	private final class Motor implements SimulatorInterface.Motor {
		private static final int RPM = 160;

		private final String name;

		private volatile Mode mode = Mode.FLOAT;
		private volatile int power;
		private volatile double count;
		private volatile double offset;
		private volatile long lastUpdateTimestamp = System.currentTimeMillis();

		private volatile double lastSample;

		public Motor(String name) {
			this.name = name;
		}

		@Override
		public Mode getMode() {
			return mode;
		}

		@Override
		public void setMode(Mode mode) {
			control(this.power, mode);
		}

		@Override
		public int getPower() {
			return power;
		}

		@Override
		public void setPower(int power) {
			control(power, this.mode);
		}

		@Override
		public void control(int power, Mode mode) {
			final long timestamp = System.currentTimeMillis();
			if (mode == Mode.FORWARD) {
				count += (timestamp - lastUpdateTimestamp)
						* (power * RPM / 100.0) / 60000.0;
			} else if (mode == Mode.BACKWARD) {
				count -= (timestamp - lastUpdateTimestamp)
						* (power * RPM / 100.0) / 60000.0;
			}
			lastUpdateTimestamp = timestamp;
			this.power = power;
			this.mode = mode;
		}

		@Override
		public int getCount() {
			return (int) Math.round((count + offset) * 360);
		}

		@Override
		public void resetCount() {
			offset = -count;
		}

		public void tick() throws NoSuchMethodException, ScriptException {
			invocable.invokeFunction("motor" + name, count - lastSample);
			lastSample = count;
		}
	}

	private transient final Motor motorA = new Motor("A");
	private transient final Motor motorB = new Motor("B");
	private transient final Motor motorC = new Motor("C");

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
			return motorA;
		}

		@Override
		public Motor getB() {
			return motorB;
		}

		@Override
		public Motor getC() {
			return motorC;
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

	void setGL(GL2GL3 gl) {
		if (gl != null) {
			gl.getContext().makeCurrent();
			elements = new Elements(gl, modelData.indices);
			elements.add(4, modelData.vertices);
		}
	}

	public void play() throws ScriptException {
		final ScriptEngine scriptEngine = new ScriptEngineManager()
				.getEngineByMimeType("text/javascript");
		scriptEngine.eval(script);
		invocable = (Invocable) scriptEngine;
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
	public void suspend() {
		thread.suspend();
	}

	@SuppressWarnings("deprecation")
	public void resume() {
		thread.resume();
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		thread.stop();
	}

	void tick() throws NoSuchMethodException, ScriptException {
		motorA.tick();
		motorB.tick();
		motorC.tick();
	}

	void draw(GL2GL3 gl, Program program) {
		program.uniform("Position", position);
		program.uniform("Heading", heading);
		elements.bindAndDraw(GL2GL3.GL_TRIANGLES);
	}
}
