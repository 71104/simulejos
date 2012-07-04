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

import javax.media.opengl.DebugGL2GL3;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.TraceGL2GL3;
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

	private transient volatile GL2GL3 gl;
	private transient volatile Program robotProgram;

	private static volatile boolean debugMode;

	public static void setDebugMode(boolean debug) {
		Simulation.debugMode = debug;
	}

	private static GL2GL3 getGL(GLAutoDrawable drawable) {
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		if (debugMode) {
			return new TraceGL2GL3(gl, System.out);
		} else {
			return gl;
		}
	}

	private transient volatile GLAutoDrawable canvas;
	private transient final GLEventListener glEventListener = new GLEventListener() {
		@Override
		public void init(GLAutoDrawable drawable) {
			gl = getGL(drawable);
			gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			floor.setGL(gl);
			robotProgram = new Program(gl, Robot.class, "robot",
					new String[] { "in_Vertex" });
			for (Robot robot : robots) {
				robot.setGL(gl);
			}
		}

		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width,
				int height) {
			// TODO Auto-generated method stub
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
		robot.setGL(gl);
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
			thread.resume();
			for (Robot robot : robots) {
				robot.resume();
			}
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

				private void wait(int period) {
					synchronized (blocker) {
						long time = System.currentTimeMillis();
						do {
							try {
								blocker.wait(period);
							} catch (InterruptedException e) {
							}
						} while (System.currentTimeMillis() < time + period);
					}
				}

				@Override
				public void run() {
					while (true) {
						wait(100); // FIXME deve essere configurabile
						for (Robot robot : robots) {
							try {
								robot.tick();
							} catch (Exception e) {
								e.printStackTrace();
								Simulation.this.stop();
							}
						}
					}
				}
			};
			thread.start();
			for (Robot robot : robots) {
				robot.play();
			}
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
