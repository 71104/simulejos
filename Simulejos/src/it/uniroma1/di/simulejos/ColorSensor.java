package it.uniroma1.di.simulejos;

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
	private volatile Program floorProgram;
	private volatile Program robotProgram;

	private volatile FloodLight floodLight = FloodLight.FULL;

	public ColorSensor(Robot robot, Vector3 position, Matrix3 heading) {
		robot.super(1, 1);
		this.position = position;
		this.heading = heading;
	}

	@Override
	public int getColor() {
		// TODO Auto-generated method stub
		return 0;
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
		floorProgram.uniform("SensorPosition", position);
		floorProgram.uniform("SensorHeading", heading);
		uniform(floorProgram);
		// TODO
		robotProgram.use();
		robotProgram.uniform("SensorPosition", position);
		robotProgram.uniform("SensorHeading", heading);
		uniform(robotProgram);
		// TODO
	}
}
