package it.uniroma1.di.simulejos;

import java.nio.IntBuffer;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;

import it.uniroma1.di.simulejos.Robot.GPUSensor;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

import static javax.media.opengl.GL2GL3.*;

final class ColorSensor extends GPUSensor implements
		SimulatorInterface.ColorSensor {
	private final Vector3 position;
	private final Matrix3 heading;
	private final Matrix3 inverseHeading;
	private volatile Program floorProgram;
	private volatile Program robotProgram;

	private volatile FloodLight floodLight = FloodLight.FULL;
	private final int[] value = new int[1];

	public ColorSensor(Robot robot, Vector3 position, Matrix3 heading) {
		robot.super(1, 1);
		this.position = position;
		this.heading = heading;
		this.inverseHeading = heading.invert();
	}

	@Override
	public int getColor() {
		return value[0];
	}

	@Override
	public FloodLight getFloodLight() {
		return floodLight;
	}

	@Override
	public void setFloodLight(FloodLight light) {
		if (light != null) {
			this.floodLight = light;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		floorProgram = new Program(gl, ColorSensor.class, "floor_color",
				new String[] { "in_Vertex" });
		robotProgram = new Program(gl, ColorSensor.class, "robot_color",
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
		gl.glFinish();
		gl.glReadPixels(0, 0, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE,
				IntBuffer.wrap(value));
		super.display(drawable);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		floorProgram.delete();
		robotProgram.delete();
		super.dispose(drawable);
	}
}
