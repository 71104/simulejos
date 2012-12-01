package it.uniroma1.di.simulejos;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;

import it.uniroma1.di.simulejos.Robot.GPUSensor;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

import static javax.media.opengl.GL2GL3.*;

final class TouchSensor extends GPUSensor implements
		SimulatorInterface.TouchSensor {
	private final Vector3 position;
	private final Matrix3 inverseHeading;
	private final float size;

	private volatile Program floorProgram;
	private volatile Program robotProgram;

	private volatile boolean pressed;

	public TouchSensor(Robot robot, Vector3 position, Matrix3 heading,
			float size) {
		robot.super(1, 1);
		this.position = position;
		this.inverseHeading = heading.invert();
		this.size = size;
	}

	@Override
	public boolean isPressed() {
		return pressed;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		floorProgram = new Program(gl, getClass(), "floor_touch",
				new String[] { "in_Vertex" });
		robotProgram = new Program(gl, getClass(), "robot_touch",
				new String[] { "in_Vertex" });
		gl.glEnable(GL_DEPTH_TEST);
		gl.glClearDepth(1);
		gl.glDepthFunc(GL_LESS);
		gl.glEnable(GL_CULL_FACE);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		floorProgram.use();
		floorProgram.uniform1f("Size", size);
		floorProgram.uniform("SensorPosition", position);
		floorProgram.uniform("InverseSensorHeading", inverseHeading);
		uniform(floorProgram);
		floor.drawForSensor(gl, floorProgram);
		robotProgram.use();
		robotProgram.uniform1f("Size", size);
		robotProgram.uniform("SensorPosition", position);
		robotProgram.uniform("InverseSensorHeading", inverseHeading);
		uniform(robotProgram);
		for (Robot robot : robots) {
			robot.drawForSensor(gl, robotProgram);
		}
		gl.glFinish();
		final float[] values = new float[1];
		gl.glReadPixels(0, 0, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT,
				FloatBuffer.wrap(values));
		pressed = values[0] < 0.99;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		floorProgram.delete();
		robotProgram.delete();
	}
}
