package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.BoundingBox;
import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Elements;
import it.uniroma1.di.simulejos.opengl.Program;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;

import static javax.media.opengl.GL2GL3.*;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

public final class Robot {
	private static volatile int nextIndex = 1;

	public static String getNextName() {
		return "NXT" + nextIndex;
	}

	public final int index;

	private final Frame parentWindow;
	private final GLJPanel canvas;
	private final PrintWriter logWriter;

	public final File classPath;
	public final String mainClassName;
	public final String script;
	public final ModelData modelData;
	public final BoundingBox boundingBox;
	public volatile boolean hilited;
	private volatile Vector3 position;
	private volatile Matrix3 heading;
	private volatile Matrix3 inverseHeading;
	private volatile Elements elements;

	private final Motor motorA;
	private final Motor motorB;
	private final Motor motorC;
	private volatile Invocable invocable;
	private volatile boolean initializing;
	private volatile boolean running;
	private volatile boolean suspended;
	private volatile ThreadGroup threads;

	private final Floor floor;
	private final Iterable<Robot> robots;

	static {
		/* XXX workaround a bug in ImageIO when used with Java Web Start */
		ImageIO.setUseCache(false);
	}

	Robot(Frame parentWindow, GLJPanel canvas, Writer logWriter, Clock clock,
			File classPath, String mainClassName, String script,
			ModelData modelData, Floor floor, Iterable<Robot> robots)
			throws ScriptException {
		this.index = nextIndex++;

		this.parentWindow = parentWindow;
		this.canvas = canvas;
		this.logWriter = new PrintWriter(new PartialWriter("NXT" + index,
				logWriter));

		this.motorA = new Motor(clock);
		this.motorB = new Motor(clock);
		this.motorC = new Motor(clock);

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

		this.boundingBox = new BoundingBox(
				modelData.boundingBox.min.by(2 / maxSpan),
				modelData.boundingBox.max.by(2 / maxSpan));

		this.robotInterface = new RobotInterface();
	}

	abstract class Sensor implements SimulatorInterface.Sensor {
		protected final Floor floor = Robot.this.floor;
		protected final Iterable<Robot> robots = Robot.this.robots;

		protected final Vector3 head(Vector3 v) {
			return heading.by(v);
		}

		protected final Vector3 transform(Vector3 v) {
			return position.plus(heading.by(v));
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

		private final void destroy() {
			buffer.destroy();
			buffer = null;
		}

		protected final void uniform(Program program) {
			program.uniform("RobotPosition", position);
			program.uniform("InverseRobotHeading", inverseHeading);
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

	/**
	 * Exposes APIs to robot scripts.
	 * 
	 * @author Alberto La Rocca
	 */
	public final class RobotInterface {
		/**
		 * Describes the bounding box of this robot.
		 */
		public final BoundingBox boundingBox = Robot.this.modelData.boundingBox;

		private RobotInterface() {
		}

		/**
		 * Provides access to a sensor port. This class cannot be instantiated
		 * by scripts; instead, a script must use the {@link RobotInterface#S1},
		 * {@link RobotInterface#S2}, {@link RobotInterface#S3} and
		 * {@link RobotInterface#S4} singleton objects.
		 * 
		 * @author Alberto La Rocca
		 */
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

			/**
			 * Initializes a touch sensor on this port. This method may be
			 * invoked only in the script's global scope.
			 * 
			 * @param position
			 *            The sensor's position, relative to the center of the
			 *            robot's bounding box.
			 * @param heading
			 *            A 3x3 matrix that orients the sensor correctly
			 *            relative to the robot.
			 * @param size
			 *            A floating point parameter indicating the width and
			 *            height of the pushable square area of the sensor.
			 */
			public void touchSensor(Vector3 position, Matrix3 heading,
					float size) {
				initializeSensor(new TouchSensor(Robot.this, position, heading,
						size));
			}

			/**
			 * Initializes a color sensor on this port. This method may be
			 * invoked only in the script's global scope.
			 * 
			 * @param position
			 *            A {@link Vector3} object indicating the position of
			 *            the sensor relative to the center of the robot.
			 * @param heading
			 *            A {@link Matrix3} used to orient or otherwise
			 *            transform the sensor relative to the robot. Usually a
			 *            rotation matrix (see
			 *            {@link Matrix3#createRotation(double, double, double, double)}
			 *            ).
			 */
			public void colorSensor(Vector3 position, Matrix3 heading) {
				initializeSensor(new ColorSensor(Robot.this, position, heading));
			}

			/**
			 * Initializes a light sensor on this port. This method may be
			 * invoked only in the script's global scope.
			 * 
			 * @param position
			 *            A {@link Vector3} object indicating the position of
			 *            the sensor relative to the center of the robot.
			 * @param heading
			 *            A {@link Matrix3} used to orient or otherwise
			 *            transform the sensor relative to the robot. Usually a
			 *            rotation matrix (see
			 *            {@link Matrix3#createRotation(double, double, double, double)}
			 *            ).
			 */
			public void lightSensor(Vector3 position, Matrix3 heading) {
				initializeSensor(new LightSensor(Robot.this, position, heading));
			}

			/**
			 * Initializes a compass sensor on this port. This method may be
			 * invoked only in the script's global scope.
			 * 
			 * @param heading
			 *            A {@link Matrix3} used to orient or otherwise
			 *            transform the sensor relative to the robot. Usually a
			 *            rotation matrix (see
			 *            {@link Matrix3#createRotation(double, double, double, double)}
			 *            ).
			 */
			public void compassSensor(Matrix3 heading) {
				initializeSensor(new CompassSensor(Robot.this, heading));
			}

			/**
			 * Initializes a ultrasonic sensor on this port. This method may be
			 * invoked only in the script's global scope.
			 * 
			 * @param position
			 *            A {@link Vector3} object indicating the position of
			 *            the sensor relative to the center of the robot.
			 * @param heading
			 *            A {@link Matrix3} used to orient or otherwise
			 *            transform the sensor relative to the robot. Usually a
			 *            rotation matrix (see
			 *            {@link Matrix3#createRotation(double, double, double, double)}
			 *            ).
			 */
			public void ultrasonicSensor(Vector3 position, Matrix3 heading) {
				initializeSensor(new UltrasonicSensor(Robot.this, position,
						heading));
			}

			/**
			 * Initializes an accelerometer sensor on this port. This method may
			 * be invoked only in the script's global scope.
			 * 
			 * @param heading
			 *            A {@link Matrix3} used to orient or otherwise
			 *            transform the sensor relative to the robot. Usually a
			 *            rotation matrix (see
			 *            {@link Matrix3#createRotation(double, double, double, double)}
			 *            ).
			 */
			public void accelerometer(Matrix3 heading) {
				initializeSensor(new Accelerometer(heading));
			}

			/**
			 * Returns a {@link Sensor} object representing the sensor connected
			 * to this port, or <code>null</code> if no sensor is connected to
			 * this port.
			 * 
			 * @return A {@link Sensor} object representing the sensor connected
			 *         to this port or <code>null</code> if no sensor is
			 *         connected to this port.
			 */
			public SimulatorInterface.Sensor getSensor() {
				return sensor;
			}
		}

		/**
		 * A {@link SensorPort} object representing NXT sensor port 1.
		 */
		public final SensorPort S1 = new SensorPort();

		/**
		 * A {@link SensorPort} object representing NXT sensor port 2.
		 */
		public final SensorPort S2 = new SensorPort();

		/**
		 * A {@link SensorPort} object representing NXT sensor port 3.
		 */
		public final SensorPort S3 = new SensorPort();

		/**
		 * A {@link SensorPort} object representing NXT sensor port 4.
		 */
		public final SensorPort S4 = new SensorPort();

		/**
		 * Translates the robot in the three-dimensional space by the specified
		 * offsets along the X, Y and Z axes.
		 * 
		 * @param dx
		 *            The offset along the X axis.
		 * @param dy
		 *            The offset along the Y axis.
		 * @param dz
		 *            The offset along the Z axis.
		 */
		public void moveBy(double dx, double dy, double dz) {
			position = position.plus(heading.by(new Vector3(dx, dy, dz)));
		}

		/**
		 * Rotates the robot in the three-dimensional space about the specified
		 * axis and by the specified angle offset.
		 * 
		 * The rotation axis is specified by three X, Y and Z coordinates which
		 * are used internally to create a rotation matrix that transforms the
		 * robot. The specified (x, y, z) vector must be a unit-length vector.
		 * 
		 * @param x
		 *            The X component of a unit-length vector representing the
		 *            rotation axis.
		 * @param y
		 *            The Y component of a unit-length vector representing the
		 *            rotation axis.
		 * @param z
		 *            The Z component of a unit-length vector representing the
		 *            rotation axis.
		 * @param da
		 *            The angular offset.
		 */
		public void rotateBy(double x, double y, double z, double da) {
			heading = Matrix3.createRotation(x, y, z, da).by(heading);
			inverseHeading = heading.invert();
		}
	}

	private final RobotInterface robotInterface;
	private final List<GPUSensor> gpuSensors = new LinkedList<GPUSensor>();

	private class Simulator implements SimulatorInterface {
		private final List<Runnable> suspendHandlers = new Vector<Runnable>();
		private final List<Runnable> resumeHandlers = new Vector<Runnable>();

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
		public void onSuspend(Runnable runnable) {
			suspendHandlers.add(runnable);
		}

		@Override
		public void onResume(Runnable runnable) {
			resumeHandlers.add(runnable);
		}

		public void suspend() {
			for (Runnable runnable : suspendHandlers) {
				runnable.run();
			}
		}

		public void resume() {
			for (Runnable runnable : resumeHandlers) {
				runnable.run();
			}
		}

		public void reset() {
			suspendHandlers.clear();
			resumeHandlers.clear();
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
			reset();
		}
	}

	private final Simulator simulator = new Simulator();

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
					try {
						classLoader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					throw new RuntimeException(e);
				}
				try {
					bridge.getMethod("initialize", String.class,
							SimulatorInterface.class).invoke(null,
							"NXT" + index, simulator);
				} catch (IllegalAccessException | InvocationTargetException
						| NoSuchMethodException e) {
					try {
						classLoader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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
					try {
						classLoader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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
					try {
						classLoader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}, "NXT" + index).start();
	}

	@SuppressWarnings("deprecation")
	void suspend() {
		if (running) {
			simulator.suspend();
			threads.suspend();
			running = true;
			suspended = true;
		}
	}

	@SuppressWarnings("deprecation")
	void resume() {
		if (running && suspended) {
			running = true;
			suspended = false;
			threads.resume();
			simulator.resume();
		}
	}

	@SuppressWarnings("deprecation")
	void stop() {
		if (running) {
			simulator.reset();
			threads.stop();
			running = false;
			suspended = false;
			motorA.setMode(Motor.Mode.FLOAT);
			motorB.setMode(Motor.Mode.FLOAT);
			motorC.setMode(Motor.Mode.FLOAT);
			for (GPUSensor sensor : gpuSensors) {
				sensor.destroy();
			}
			gpuSensors.clear();
		}
	}

	private Vector3 transform(Vector3 v) {
		return heading.by(v).plus(position);
	}

	private boolean vertexCollides(Robot robot, double x, double y, double z) {
		return modelData.boundingBox.contains(inverseHeading.by(robot
				.transform(new Vector3(x, y, z)).minus(position)));
	}

	public boolean collidesWith(Robot robot) {
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

	public void moveTo(double x, double y, double z) {
		position = new Vector3(x, y, z);
		canvas.repaint();
	}

	public void moveTo(Vector3 v) {
		position = v;
		canvas.repaint();
	}

	public void moveBy(Vector3 d) {
		position = position.plus(d);
		canvas.repaint();
	}

	public void rotate(double a) {
		heading = Matrix3.createRotation(0, 1, 0, a).by(heading);
		inverseHeading = heading.invert();
		canvas.repaint();
	}

	void tick(long timestamp) throws NoSuchMethodException, ScriptException {
		if (running && !suspended) {
			invocable.invokeFunction("tick", motorA.sample(timestamp),
					motorB.sample(timestamp), motorC.sample(timestamp));
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
		if (hilited) {
			program.uniform3f("Color", 1, 0, 0);
		} else {
			program.uniform3f("Color", 1, 1, 1);
		}
		elements.draw(GL_TRIANGLES);
	}

	void drawForSensor(GL2GL3 gl, Program program) {
		if (elements != null) {
			program.uniform(gl, "TargetRobotPosition", position);
			program.uniform(gl, "TargetRobotHeading", heading);
			elements.share(gl);
			elements.bindAndDraw(gl, GL_TRIANGLES);
		}
	}

	void drawForPicker(GL2GL3 gl, Program program) {
		if (elements != null) {
			program.uniform(gl, "Position", position);
			program.uniform(gl, "Heading", heading);
			program.uniform1f(gl, "PassThrough", (float) index / 128);
			elements.share(gl);
			elements.bindAndDraw(gl, GL_TRIANGLES);
		}
	}
}
