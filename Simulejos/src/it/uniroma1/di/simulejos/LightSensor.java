package it.uniroma1.di.simulejos;

import static javax.media.opengl.GL2GL3.*;

import java.nio.FloatBuffer;

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
	private final Matrix3 inverseHeading;
	private volatile Program floorProgram;
	private volatile Program robotProgram;

	private final float[] value = new float[1];
	private volatile boolean floodLight = true;

	public LightSensor(Robot robot, Vector3 position, Matrix3 heading) {
		robot.super(1, 1);
		this.position = position;
		this.inverseHeading = heading.invert();
	}

	@Override
	public int getLight() {
		return 1023 - Math.round(value[0] * 1023);
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
		floorProgram = new Program(gl, getClass(), "floor_light",
				new String[] { "in_Vertex" });
		floor.share(gl);
		robotProgram = new Program(gl, getClass(), "robot_light",
				new String[] { "in_Vertex" });
		gl.glClearColor(0, 0, 0, 1);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LESS);
		gl.glClearDepth(1);
		// gl.glEnable(GL_CULL_FACE);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		floorProgram.use();
		floorProgram.uniform("SensorPosition", position);
		floorProgram.uniform("InverseSensorHeading", inverseHeading);
		uniform(floorProgram);
		floor.drawForSensor(gl, floorProgram);
		// robotProgram.use();
		// robotProgram.uniform("SensorPosition", position);
		// robotProgram.uniform("InverseSensorHeading", heading);
		// uniform(robotProgram);
		// for (Robot robot : robots) {
		// robot.share(gl);
		// robot.drawForSensor(gl, robotProgram);
		// }
		gl.glFinish();
		if (floodLight) {
			gl.glReadPixels(0, 0, 1, 1, GL_RED, GL_FLOAT,
					FloatBuffer.wrap(value));
		} else {
			gl.glReadPixels(0, 0, 1, 1, GL_LUMINANCE, GL_FLOAT,
					FloatBuffer.wrap(value));
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		floorProgram.delete();
		robotProgram.delete();
	}
}
