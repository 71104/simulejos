package it.uniroma1.di.simulejos;

import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_CULL_FACE;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LESS;
import static javax.media.opengl.GL.GL_RGB;
import static javax.media.opengl.GL2GL3.GL_UNSIGNED_INT_8_8_8_8;

import java.nio.IntBuffer;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;

import it.uniroma1.di.simulejos.Robot.GPUSensor;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

final class LightSensor extends GPUSensor implements
		SimulatorInterface.LightSensor {
	private final Vector3 position;
	private final Matrix3 heading;
	private final Matrix3 inverseHeading;
	private volatile Program floorProgram;
	private volatile Program robotProgram;

	private final int[] value = new int[1];
	private volatile boolean floodLight = true;

	public LightSensor(Robot robot, Vector3 position, Matrix3 heading) {
		robot.super(1, 1);
		this.position = position;
		this.heading = heading;
		this.inverseHeading = heading.invert();
	}

	@Override
	public int getLight() {
		return value[0];
	}

	@Override
	public void setFloodLight(boolean on) {
		this.floodLight = on;
	}

	@Override
	public boolean isFloodLightOn() {
		return floodLight;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		floorProgram = new Program(gl, ColorSensor.class, "floor_light",
				new String[] { "in_Vertex" });
		robotProgram = new Program(gl, ColorSensor.class, "robot_light",
				new String[] { "in_Vertex" });
		gl.glClearColor(0, 0, 0, 0);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LESS);
		gl.glClearDepth(1);
		gl.glEnable(GL_CULL_FACE);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		floorProgram.use();
		floorProgram.uniform("Color", Vector3.RED);
		floorProgram.uniform("SensorPosition", position);
		floorProgram.uniform("InverseSensorHeading", inverseHeading);
		uniform(floorProgram);
		floor.drawForSensor(gl, floorProgram);
		robotProgram.use();
		robotProgram.uniform("Color", Vector3.RED);
		robotProgram.uniform("SensorPosition", position);
		robotProgram.uniform("InverseSensorHeading", heading);
		uniform(robotProgram);
		for (Robot robot : robots) {
			robot.drawForSensor(gl, robotProgram);
		}
		gl.glReadPixels(0, 0, 1, 1, GL_RGB, GL_UNSIGNED_INT_8_8_8_8,
				IntBuffer.wrap(value));
	}
}
