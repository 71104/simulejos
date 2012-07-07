package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.opengl.Program;
import it.uniroma1.di.simulejos.wavefront.ParseException;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.media.opengl.DebugGL2GL3;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.script.ScriptException;
import static javax.media.opengl.GL2GL3.*;

public final class Simulation implements Serializable {
	private static final long serialVersionUID = -290517947218502549L;

	private final Camera camera = new Camera();
	private final Floor floor = new Floor();
	private final List<Robot> robots = new LinkedList<>();
	private transient volatile boolean dirty;

	private transient volatile Thread thread;
	private transient volatile Frame parentWindow;
	private transient volatile PrintWriter logWriter;

	private transient volatile Program robotProgram;

	private static volatile boolean debugMode;

	public static void setDebugMode(boolean debug) {
		Simulation.debugMode = debug;
	}

	private static GL2GL3 getGL(GLAutoDrawable drawable) {
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		if (debugMode) {
			return new DebugGL2GL3(gl);
		} else {
			return gl;
		}
	}

	private transient volatile GLAutoDrawable canvas;
	private transient final GLEventListener glEventListener = new GLEventListener() {
		@Override
		public void init(GLAutoDrawable drawable) {
			final GL2GL3 gl = getGL(drawable);
			gl.glEnable(GL_DEPTH_TEST);
			gl.glClearDepth(0);
			gl.glDepthFunc(GL_GREATER);
			gl.glEnable(GL_CULL_FACE);
			robotProgram = new Program(gl, Robot.class, "robot",
					new String[] { "in_Vertex" });
		}

		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width,
				int height) {
			final GL2GL3 gl = getGL(drawable);
			if (height > width) {
				gl.glViewport((width - height) / 2, 0, height, height);
			} else {
				gl.glViewport(0, (height - width) / 2, width, width);
			}
		}

		@Override
		public void display(GLAutoDrawable drawable) {
			final GL2GL3 gl = getGL(drawable);
			gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);
			floor.draw(gl, camera);
			robotProgram.use();
			camera.uniform(robotProgram);
			for (Robot robot : robots) {
				robot.draw(gl, robotProgram);
			}
			gl.glFlush();
		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
			// TODO Auto-generated method stub
		}
	};

	public Simulation(Frame parentWindow, Writer logWriter) {
		this.parentWindow = parentWindow;
		this.logWriter = new PrintWriter(new PartialWriter("Simulation",
				logWriter));
	}

	public boolean isDirty() {
		return dirty;
	}

	public void clearDirty() {
		dirty = false;
	}

	public void setCanvas(GLAutoDrawable canvas) {
		if (this.canvas != null) {
			this.canvas.removeGLEventListener(glEventListener);
		}
		this.canvas = canvas;
		if (canvas != null) {
			canvas.addGLEventListener(glEventListener);
		}
	}

	public void discard() {
		if (canvas != null) {
			canvas.removeGLEventListener(glEventListener);
			canvas = null;
		}
	}

	public void addRobot(File classPath, String mainClassName, String script,
			File modelFile, boolean swapYAndZ) throws IOException,
			ParseException, ScriptException {
		dirty = true;
		final Robot robot = new Robot(classPath, mainClassName, script,
				ModelData.parseWavefront(modelFile, swapYAndZ), parentWindow,
				logWriter);
		robot.setParentWindow(parentWindow);
		robot.setLogWriter(logWriter);
		robots.add(robot);
	}

	interface State {
		State play() throws ScriptException;

		State suspend();

		State stop();
	};

	private final State runningState = new State() {
		@Override
		public State play() {
			return this;
		}

		@Override
		public State suspend() {
			thread.suspend();
			for (Robot robot : robots) {
				robot.suspend();
			}
			logWriter.println("suspended");
			return suspendedState;
		}

		@Override
		public State stop() {
			thread.stop();
			for (Robot robot : robots) {
				robot.stop();
			}
			logWriter.println("stopped");
			return stoppedState;
		}
	};

	private final State suspendedState = new State() {
		@Override
		public State play() {
			logWriter.println("resumed");
			for (Robot robot : robots) {
				robot.resume();
			}
			thread.resume();
			return runningState;
		}

		@Override
		public State suspend() {
			return this;
		}

		@Override
		public State stop() {
			thread.stop();
			for (Robot robot : robots) {
				robot.stop();
			}
			logWriter.println("stopped");
			return stoppedState;
		}
	};

	private final State stoppedState = new State() {
		@Override
		public State play() throws ScriptException {
			logWriter.println("started");
			thread = new Thread("ticker") {
				private final Object blocker = new Object();
				private volatile long lastTimestamp;

				private void waitTo(int period) {
					synchronized (blocker) {
						do {
							try {
								blocker.wait(period);
							} catch (InterruptedException e) {
							}
						} while (System.currentTimeMillis() < lastTimestamp
								+ period);
						lastTimestamp = System.currentTimeMillis();
					}
				}

				@Override
				public void run() {
					final int rate = Preferences.userNodeForPackage(
							Simulation.class).getInt("frameRate", 60);
					final int period = (int) Math.round(1000.0 / rate);
					lastTimestamp = System.currentTimeMillis();
					while (true) {
						waitTo(period);
						for (Robot robot : robots) {
							try {
								robot.tick();
							} catch (Exception e) {
								e.printStackTrace();
								Simulation.this.stop();
							}
						}
						canvas.display();
					}
				}
			};
			for (Robot robot : robots) {
				robot.play();
			}
			thread.start();
			return runningState;
		}

		@Override
		public State suspend() {
			return this;
		}

		@Override
		public State stop() {
			return this;
		}
	};

	private State state = stoppedState;

	public void play() throws ScriptException {
		state = state.play();
	}

	public void suspend() {
		state = state.suspend();
	}

	public void stop() {
		state = state.stop();
	}
}
