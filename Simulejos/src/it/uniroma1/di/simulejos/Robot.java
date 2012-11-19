package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.BoundingBox;
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

import javax.imageio.ImageIO;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

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
	private volatile Vector3 position;
	private volatile Matrix3 heading;
	private volatile Matrix3 inverseHeading;

	private transient volatile Frame parentWindow;
	private transient volatile PrintWriter logWriter;
	private transient volatile Invocable invocable;
	private transient volatile boolean initializing;
	private transient volatile boolean running;
	private transient volatile boolean suspended;
	private transient volatile ThreadGroup threads;

	private transient volatile Elements elements;

	private transient final Floor floor;
	private transient final Iterable<Robot> robots;

	static {
		/* XXX workaround a bug in ImageIO when used with Java Web Start */
		ImageIO.setUseCache(false);
	}

	Robot(File classPath, String mainClassName, String script,
			ModelData modelData, Floor floor, Iterable<Robot> robots)
			throws ScriptException {
		this.index = nextIndex++;
		this.classPath = classPath;
		this.mainClassName = mainClassName;
		this.script = script;
		this.modelData = modelData;
		this.floor = floor;
		this.robots = robots;

		this.position = new Vector3(-modelData.boundingBox.center.x, -1
				- modelData.boundingBox.min.y, -modelData.boundingBox.center.z);

		final double maxSpan = modelData.boundingBox.getMaxSpan();
		this.heading = Matrix3.createScaling(2 / maxSpan, 2 / maxSpan,
				2 / maxSpan);
		this.inverseHeading = Matrix3.createScaling(maxSpan / 2, maxSpan / 2,
				maxSpan / 2);

		this.robotInterface = new RobotInterface();
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
		protected final Floor floor = Robot.this.floor;
		protected final Iterable<Robot> robots = Robot.this.robots;

		protected final Vector3 head(Vector3 v) {
			return heading.by(v);
		}

		protected final Vector3 transform(Vector3 v) {
			return position.plus(heading.by(v));
		}

		protected final void uniform(Program program) {
			program.uniform("RobotPosition", position);
			program.uniform("InverseRobotHeading", inverseHeading);
		}
	}

	abstract class GPUSensor extends Sensor implements GLEventListener {
		private final int width;
		private final int height;
		private volatile GLAutoDrawable buffer;

		protected GPUSensor(int bufferWidth, int bufferHeight) {
			this.width = bufferWidth;
			this.height = bufferHeight;
			gpuSensors.add(this);
		}

		public final void assertBuffer(GL2GL3 gl) {
			if (buffer == null) {
				resetBuffer(gl);
			}
		}

		private final void resetBuffer(GL2GL3 gl) {
			if (buffer != null) {
				buffer.destroy();
			}
			buffer = GLDrawableFactory
					.getFactory(GLProfile.getDefault())
					.createOffscreenAutoDrawable(
							null,
							new GLCapabilities(GLProfile.get(GLProfile.GL2GL3)),
							null, width, height, gl.getContext());
			buffer.addGLEventListener(this);
		}

		public final void tick() {
			if (buffer != null) {
				buffer.display();
			}
		}

		@Override
		public void init(GLAutoDrawable drawable) {
		}

		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width,
				int height) {
		}

		@Override
		public void display(GLAutoDrawable drawable) {
		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
		}
	}

	public final class RobotInterface {
		public final BoundingBox boundingBox = modelData.boundingBox;

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

			public void touchSensor(Vector3 position, Matrix3 heading,
					float size) {
				initializeSensor(new TouchSensor(Robot.this, position, heading,
						size));
			}

			public void colorSensor(Vector3 position, Matrix3 heading) {
				initializeSensor(new ColorSensor(Robot.this, position, heading));
			}

			public void lightSensor(Vector3 position, Matrix3 heading) {
				initializeSensor(new LightSensor(Robot.this, position, heading));
			}

			public void compassSensor(Matrix3 heading) {
				initializeSensor(new CompassSensor(Robot.this, heading));
			}

			public void ultrasonicSensor(Vector3 position, Matrix3 heading) {
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

	private transient final RobotInterface robotInterface;
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
		if (running) {
			return;
		}
		initializing = true;
		final ScriptEngine scriptEngine = new ScriptEngineManager()
				.getEngineByMimeType("text/javascript");
		scriptEngine.put("robot", robotInterface);
		scriptEngine.eval(script);
		invocable = (Invocable) scriptEngine;
		threads = new ThreadGroup("NXT" + index);
		initializing = false;
		running = true;
		suspended = false;
		new Thread(threads, new Runnable() {
			@Override
			public void run() {
				final URL url;
				try {
					url = classPath.toURI().toURL();
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
				final LejosClassLoader classLoader = new LejosClassLoader(
						new URL[] { url });
				final Class<?> bridge;
				try {
					bridge = classLoader
							.loadClass("it.uniroma1.di.simulejos.bridge.Bridge");
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
					suspended = false;
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
		if (running) {
			threads.suspend();
			running = false;
			suspended = true;
			motorA.timer.suspend();
			motorB.timer.suspend();
			motorC.timer.suspend();
		}
	}

	@SuppressWarnings("deprecation")
	void resume() {
		if (running && suspended) {
			motorA.timer.resume();
			motorB.timer.resume();
			motorC.timer.resume();
			running = true;
			suspended = false;
			threads.resume();
		}
	}

	@SuppressWarnings("deprecation")
	void stop() {
		if (running) {
			threads.stop();
			running = false;
			suspended = false;
			motorA.setMode(Motor.Mode.FLOAT);
			motorB.setMode(Motor.Mode.FLOAT);
			motorC.setMode(Motor.Mode.FLOAT);
		}
	}

	private Vector3 transform(Vector3 v) {
		return heading.by(v).plus(position);
	}

	private boolean vertexCollides(Robot robot, double x, double y, double z) {
		return modelData.boundingBox.contains(inverseHeading.by(robot
				.transform(new Vector3(x, y, z)).minus(position)));
	}

	boolean collidesWith(Robot robot) {
		final BoundingBox box = modelData.boundingBox;
		return robot.vertexCollides(this, box.min.x, box.min.y, box.min.z)
				|| robot.vertexCollides(this, box.min.x, box.min.y, box.max.z)
				|| robot.vertexCollides(this, box.min.x, box.max.y, box.min.z)
				|| robot.vertexCollides(this, box.min.x, box.max.y, box.max.z)
				|| robot.vertexCollides(this, box.max.x, box.min.y, box.min.z)
				|| robot.vertexCollides(this, box.max.x, box.min.y, box.max.z)
				|| robot.vertexCollides(this, box.max.x, box.max.y, box.min.z)
				|| robot.vertexCollides(this, box.max.x, box.max.y, box.max.z);
	}

	void tick() throws NoSuchMethodException, ScriptException {
		if (running && !suspended) {
			invocable.invokeFunction("tick", motorA.sample(), motorB.sample(),
					motorC.sample());
			for (GPUSensor sensor : gpuSensors) {
				sensor.tick();
			}
		}
	}

	void init(GL2GL3 gl) {
		elements = new Elements(gl, modelData.indices);
		elements.add(4, modelData.vertices);
		for (GPUSensor sensor : gpuSensors) {
			sensor.resetBuffer(gl);
		}
	}

	void draw(GL2GL3 gl, Program program) {
		if (elements == null) {
			init(gl);
		}
		for (GPUSensor sensor : gpuSensors) {
			sensor.assertBuffer(gl);
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

	void drawForSensor(GL2GL3 gl, Program program) {
		program.uniform("TargetRobotPosition", position);
		program.uniform("TargetRobotHeading", heading);
		elements.bindAndDraw(GL_TRIANGLES);
	}
}
