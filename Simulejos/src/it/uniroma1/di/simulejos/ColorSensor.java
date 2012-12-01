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

final class ColorSensor extends GPUSensor implements
		SimulatorInterface.ColorSensor {
	private final Vector3 position;
	private final Matrix3 inverseHeading;
	private volatile Program floorProgram;
	private volatile Program robotProgram;

	private volatile int value;
	private volatile FloodLight floodLight;
	private volatile Vector3 color;

	public ColorSensor(Robot robot, Vector3 position, Matrix3 heading) {
		robot.super(1, 1);
		this.position = position;
		this.inverseHeading = heading.invert();
	}

	@Override
	public FloodLight getFloodLight() {
		return floodLight;
	}

	@Override
	public void setFloodLight(FloodLight light) {
		this.floodLight = light;
		final int color = floodLight.getColor();
		this.color = new Vector3((double) ((color >> 16) & 0xFF) / 0xFF,
				(double) ((color >> 8) & 0xFF) / 0xFF,
				(double) (color & 0xFF) / 0xFF);
	}

	@Override
	public int getColor() {
		return value;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		floorProgram = new Program(gl, getClass(), "floor_color",
				new String[] { "in_Vertex" });
		floor.share(gl);
		robotProgram = new Program(gl, getClass(), "robot_color",
				new String[] { "in_Vertex" });
		gl.glClearColor(0, 0, 0, 1);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glClearDepth(0);
		gl.glDepthFunc(GL_GREATER);
		gl.glEnable(GL_CULL_FACE);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		floorProgram.use();
		floorProgram.uniform("Color", color);
		floorProgram.uniform("SensorPosition", position);
		floorProgram.uniform("InverseSensorHeading", inverseHeading);
		uniform(floorProgram);
		floor.drawForSensor(gl, floorProgram);
		robotProgram.use();
		robotProgram.uniform("Color", color);
		robotProgram.uniform("SensorPosition", position);
		robotProgram.uniform("InverseSensorHeading", inverseHeading);
		uniform(robotProgram);
		for (Robot robot : robots) {
			robot.drawForSensor(gl, robotProgram);
		}
		gl.glFinish();
		final float[] values = new float[1];
		gl.glReadPixels(0, 0, 1, 1, GL_LUMINANCE, GL_FLOAT,
				FloatBuffer.wrap(values));
		value = 1023 - Math.round(values[0] * 1023);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		floorProgram.delete();
		robotProgram.delete();
	}
}
