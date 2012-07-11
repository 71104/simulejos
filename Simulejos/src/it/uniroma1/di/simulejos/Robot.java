package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.bridge.Bridge;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Elements;
import it.uniroma1.di.simulejos.opengl.Program;

import java.awt.Frame;
import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2GL3;

import static javax.media.opengl.GL2GL3.*;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

public final class Robot implements Serializable {
	private static final long serialVersionUID = 1961674308786529328L;

	private static volatile int nextIndex = 1;

	public static String getNextName() {
		return "NXT" + nextIndex;
	}

	public final int index;
	public final File classPath;
	public final String mainClassName;
	public final String script;
	public final ModelData modelData;
	private volatile Vector3 position = Vector3.NULL;
	private volatile Matrix3 heading = Matrix3.IDENTITY;
	private volatile Matrix3 inverseHeading = Matrix3.IDENTITY;

	private transient volatile Frame parentWindow;
	private transient volatile PrintWriter logWriter;
	private transient volatile Invocable invocable;
	private transient volatile boolean initializing;
	private transient volatile boolean running;
	private transient volatile ThreadGroup threads;

	private transient volatile GL2GL3 gl;
	private transient volatile Elements elements;

	Robot(File classPath, String mainClassName, String script,
			ModelData modelData) throws ScriptException {
		this.index = nextIndex++;
		this.classPath = classPath;
		this.mainClassName = mainClassName;
		this.script = script;
		this.modelData = modelData;
	}

	void setUI(Frame parentWindow, Writer logWriter) {
		this.parentWindow = parentWindow;
		this.logWriter = new PrintWriter(new PartialWriter("NXT" + index,
				logWriter));
	}

	private transient final Motor motorA = new Motor();
	private transient final Motor motorB = new Motor();
	private transient final Motor motorC = new Motor();

	abstract class Sensor implements SimulatorInterface.Sensor {
		protected Vector3 transform(Vector3 v) {
			return position.plus(heading.by(v));
		}
	}

	abstract class GPUSensor extends Sensor {
		protected GPUSensor(int bufferWidth, int bufferHeight) {
			// TODO
		}

		public void tick() {
			// TODO
		}
	}

	public final class RobotInterface {
		private RobotInterface() {
		}

		public final class SensorPort {
			private volatile SimulatorInterface.Sensor sensor;

			private void initializeSensor(SimulatorInterface.Sensor sensor) {
				if (initializing) {
					this.sensor = sensor;
				} else {
					throw new RuntimeException(
							"NXT"
									+ index
									+ "'s script tried to initialize a sensor port after the initialization stage");
				}
			}

			public void initializeTouchSensor(Vector3 position, Vector3 heading) {
				initializeSensor(new TouchSensor(Robot.this, position, heading));
			}

			public void initializeColorSensor(Vector3 position, Vector3 heading) {
				initializeSensor(new ColorSensor(Robot.this, position, heading));
			}

			public void initializeLightSensor(Vector3 position, Vector3 heading) {
				initializeSensor(new LightSensor(Robot.this, position, heading));
			}

			public void initializeCompassSensor(Matrix3 heading) {
				initializeSensor(new CompassSensor(Robot.this, heading));
			}

			public void initializeUltrasonicSensor(Vector3 position,
					Matrix3 heading) {
				initializeSensor(new UltrasonicSensor(Robot.this, position,
						heading));
			}

			public SimulatorInterface.Sensor getSensor() {
				return sensor;
			}
		}

		public final SensorPort S1 = new SensorPort();
		public final SensorPort S2 = new SensorPort();
		public final SensorPort S3 = new SensorPort();
		public final SensorPort S4 = new SensorPort();

		public void moveBy(double dx, double dy, double dz) {
			position = position
					.plus(inverseHeading.by(new Vector3(dx, dy, dz)));
		}

		public void rotateBy(double x, double y, double z, double da) {
			heading = Matrix3.createRotation(x, y, z, da).by(heading);
			inverseHeading = heading.invert();
		}
	}

	private transient final RobotInterface robotInterface = new RobotInterface();
	private transient final List<GPUSensor> gpuSensors = new LinkedList<GPUSensor>();

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
			return robotInterface.S1.getSensor();
		}

		@Override
		public Sensor getS2() {
			return robotInterface.S2.getSensor();
		}

		@Override
		public Sensor getS3() {
			return robotInterface.S3.getSensor();
		}

		@Override
		public Sensor getS4() {
			return robotInterface.S4.getSensor();
		}

		@Override
		public void shutDown() {
			stop();
		}
	}

	private transient final Simulator simulator = new Simulator();

	void play() throws ScriptException {
		initializing = true;
		final ScriptEngine scriptEngine = new ScriptEngineManager()
				.getEngineByMimeType("text/javascript");
		scriptEngine.put("robot", robotInterface);
		scriptEngine.eval(script);
		invocable = (Invocable) scriptEngine;
		threads = new ThreadGroup("NXT" + index);
		initializing = false;
		running = true;
		new Thread(threads, new Runnable() {
			@Override
			public void run() {
				final URL url;
				try {
					url = classPath.toURI().toURL();
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
				final VirtualClassLoader classLoader = new VirtualClassLoader(
						new URL[] { Robot.class.getResource("Framework.jar"),
								url });
				final Class<?> bridge;
				try {
					bridge = classLoader.loadClass(Bridge.class.getName());
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				try {
					bridge.getMethod("initialize", String.class,
							SimulatorInterface.class).invoke(null,
							"NXT" + index, simulator);
				} catch (IllegalAccessException | InvocationTargetException
						| NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
				final Class<?> mainClass;
				try {
					mainClass = classLoader.loadClass(mainClassName);
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(parentWindow,
							"Unable to find the specified main class "
									+ mainClassName, "Simulejos",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					mainClass.getMethod("main", String[].class).invoke(null,
							(Object) new String[] {});
					logWriter.println("terminated regularly");
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					running = false;
					try {
						bridge.getMethod("cleanup").invoke(null);
					} catch (IllegalAccessException | InvocationTargetException
							| NoSuchMethodException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}, "NXT" + index).start();
	}

	@SuppressWarnings("deprecation")
	void suspend() {
		threads.suspend();
		running = false;
		motorA.timer.suspend();
		motorB.timer.suspend();
		motorC.timer.suspend();
	}

	@SuppressWarnings("deprecation")
	void resume() {
		motorA.timer.resume();
		motorB.timer.resume();
		motorC.timer.resume();
		running = true;
		threads.resume();
	}

	@SuppressWarnings("deprecation")
	void stop() {
		threads.stop();
		running = true;
		motorA.setMode(Motor.Mode.FLOAT);
		motorB.setMode(Motor.Mode.FLOAT);
		motorC.setMode(Motor.Mode.FLOAT);
	}

	void tick() throws NoSuchMethodException, ScriptException {
		if (running) {
			invocable.invokeFunction("tick", motorA.sample(), motorB.sample(),
					motorC.sample());
			for (GPUSensor sensor : gpuSensors) {
				sensor.tick();
			}
		}
	}

	void draw(GL2GL3 gl, Program program) {
		if (gl != this.gl) {
			elements = new Elements(gl, modelData.indices);
			elements.add(4, modelData.vertices);
		}
		program.uniform("Position", position);
		program.uniform("Heading", heading);
		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		program.uniform3f("Color", 0, 0, 0);
		elements.bindAndDraw(GL_TRIANGLES);
		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		program.uniform3f("Color", 1, 1, 1);
		elements.draw(GL_TRIANGLES);
	}
}
