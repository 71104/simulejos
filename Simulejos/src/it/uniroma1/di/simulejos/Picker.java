package it.uniroma1.di.simulejos;

import java.nio.FloatBuffer;

import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import static javax.media.opengl.GL2GL3.*;

public final class Picker {
	private final Camera camera;
	private final Iterable<Robot> robots;
	private volatile GLAutoDrawable buffer;

	private volatile PickRequest request;

	public abstract class PickRequest {
		public final int x;
		public final int y;

		public PickRequest(int x, int y) {
			this.x = x;
			this.y = y;
			Picker.this.request = this;
			final GLAutoDrawable buffer = Picker.this.buffer;
			if (buffer != null) {
				buffer.display();
			}
		}

		public abstract void handle(int passThrough, Vector3 position);
	}

	Picker(Camera camera, Iterable<Robot> robots) {
		this.camera = camera;
		this.robots = robots;
	}

	private void resetBuffer(GLAutoDrawable source) {
		if (buffer != null) {
			buffer.destroy();
		}
		buffer = GLDrawableFactory.getFactory(GLProfile.getDefault())
				.createOffscreenAutoDrawable(null,
						new GLCapabilities(GLProfile.get(GLProfile.GL2GL3)),
						null, source.getWidth(), source.getHeight(),
						source.getContext());
		buffer.addGLEventListener(new GLEventListener() {
			private volatile Program program;

			@Override
			public void init(GLAutoDrawable drawable) {
				final GL2GL3 gl = drawable.getGL().getGL2GL3();
				gl.glEnable(GL_DEPTH_TEST);
				gl.glClearDepth(0);
				gl.glDepthFunc(GL_GREATER);
				gl.glEnable(GL_CULL_FACE);
				program = new Program(gl, Picker.class, "robot_picker",
						new String[] { "in_Vertex" });
				program.use();
			}

			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y,
					int width, int height) {
				final GL2GL3 gl = drawable.getGL().getGL2GL3();
				if (height > width) {
					gl.glViewport((width - height) / 2, 0, height, height);
				} else {
					gl.glViewport(0, (height - width) / 2, width, width);
				}
			}

			@Override
			public void display(GLAutoDrawable drawable) {
				PickRequest request = Picker.this.request;
				if (request != null) {
					final GL2GL3 gl = drawable.getGL().getGL2GL3();
					gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
					camera.uniform(gl, program);
					for (Robot robot : robots) {
						robot.share(gl);
						robot.drawForPicker(gl, program);
					}
					gl.glFinish();
					final float[] values = new float[4];
					gl.glReadPixels(request.x, request.y, 1, 1, GL_RGBA,
							GL_FLOAT, FloatBuffer.wrap(values));
					request.handle(Math.round(values[3] * 1024) - 1,
							new Vector3(values[0], values[1], values[2]));
				}
			}

			@Override
			public void dispose(GLAutoDrawable drawable) {
				program.delete();
			}
		});
	}

	final class CanvasHandler implements GLEventListener {
		@Override
		public void init(GLAutoDrawable drawable) {
			resetBuffer(drawable);
		}

		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width,
				int height) {
			resetBuffer(drawable);
		}

		@Override
		public void display(GLAutoDrawable drawable) {
		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
			final GLAutoDrawable buffer = Picker.this.buffer;
			Picker.this.buffer = null;
			buffer.destroy();
		}
	}
}
