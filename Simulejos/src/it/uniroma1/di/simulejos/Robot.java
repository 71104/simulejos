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
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.opengl.GL2GL3;
import static javax.media.opengl.GL2GL3.*;
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
	private volatile Matrix3 heading = Matrix3.IDENTITY;

	private transient volatile Frame parentWindow;
	private transient volatile PrintWriter logWriter;
	private transient volatile Invocable invocable;
	private transient volatile Thread thread;

	private transient volatile GL2GL3 gl;
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

	private transient final Motor motorA = new Motor();
	private transient final Motor motorB = new Motor();
	private transient final Motor motorC = new Motor();

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

	private final Simulator simulator = new Simulator();

	public final class RobotInterface {
		public void moveBy(double dx, double dy, double dz) {
			position = position.plus(heading.by(new Vector3(dx, dy, dz)));
		}

		public void rotateBy(double x, double y, double z, double da) {
			heading = Matrix3.createRotationMatrix(x, y, z, da).by(heading);
		}
	}

	private final RobotInterface robotInterface = new RobotInterface();

	void setParentWindow(Frame parentWindow) {
		this.parentWindow = parentWindow;
	}

	void setLogWriter(PrintWriter logWriter) {
		this.logWriter = logWriter;
	}

	public void play() throws ScriptException {
		final ScriptEngine scriptEngine = new ScriptEngineManager()
				.getEngineByMimeType("text/javascript");
		scriptEngine.put("robot", robotInterface);
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
				final VirtualClassLoader classLoader = new VirtualClassLoader(
						new URL[] { Robot.class.getResource("Framework.jar"),
								url });
				try {
					classLoader
							.loadClass(Bridge.class.getName())
							.getMethod("initialize", String.class,
									SimulatorInterface.class)
							.invoke(null, "NXT" + index, simulator);
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
		invocable.invokeFunction("tick", motorA.tick(), motorB.tick(),
				motorC.tick());
	}

	void draw(GL2GL3 gl, Program program) {
		if (gl != this.gl) {
			elements = new Elements(gl, modelData.indices);
			elements.add(4, modelData.vertices);
		}
		program.uniform("Position", position);
		program.uniform("Heading", heading);
		elements.bindAndDraw(GL_TRIANGLES);
	}
}
