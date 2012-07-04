package it.uniroma1.di.spikes;

import it.uniroma1.di.simulejos.util.FullReader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.DebugGL2GL3;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import static javax.media.opengl.GL2GL3.*;

public class JOGLTest extends JFrame {
	private static final long serialVersionUID = -6571863228334725329L;

	private static String getShaderSource(String name) {
		try {
			return new FullReader(new InputStreamReader(
					JOGLTest.class.getResourceAsStream(name))).readAll();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private JOGLTest() {
		super("JOGL Test");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		final GLJPanel canvas = new GLJPanel();
		canvas.setPreferredSize(new Dimension(800, 600));
		canvas.addGLEventListener(new GLEventListener() {
			@Override
			public void init(GLAutoDrawable drawable) {
				final GL2GL3 gl = new DebugGL2GL3(drawable.getGL().getGL2GL3());

				gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

				final int[] buffers = new int[2];
				gl.glGenBuffers(2, buffers, 0);

				final int vertexShader = gl.glCreateShader(GL_VERTEX_SHADER);
				gl.glShaderSource(vertexShader, 1,
						new String[] { getShaderSource("shader.vert") }, null,
						0);
				gl.glCompileShader(vertexShader);

				final int[] compiled = new int[1];
				gl.glGetShaderiv(vertexShader, GL_COMPILE_STATUS, compiled, 0);
				if (compiled[0] == 0) {
					throw new RuntimeException("vertex shader did not compile");
				}

				final int fragmentShader = gl
						.glCreateShader(GL_FRAGMENT_SHADER);
				gl.glShaderSource(fragmentShader, 1,
						new String[] { getShaderSource("shader.frag") }, null,
						0);
				gl.glCompileShader(fragmentShader);

				gl.glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, compiled, 0);
				if (compiled[0] == 0) {
					throw new RuntimeException(
							"fragment shader did not compile");
				}

				final int program = gl.glCreateProgram();
				gl.glAttachShader(program, vertexShader);
				gl.glAttachShader(program, fragmentShader);
				gl.glBindAttribLocation(program, 0, "in_Vertex");
				gl.glLinkProgram(program);

				final int[] linked = new int[1];
				gl.glGetProgramiv(program, GL_LINK_STATUS, linked, 0);
				if (linked[0] == 0) {
					throw new RuntimeException("program did not link");
				}

				gl.glUseProgram(program);

				gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);

				final float[] vertices = new float[] { -1, 1, 2, 1, -1, -1, 2,
						1, 1, -1, 2, 1 };
				gl.glBufferData(GL_ARRAY_BUFFER, vertices.length * 4,
						FloatBuffer.wrap(vertices), GL_STATIC_DRAW);
				gl.glEnableVertexAttribArray(0);
				gl.glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);

				final short[] indices = new short[] { 0, 1, 2 };
				gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
				gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length * 2,
						ShortBuffer.wrap(indices), GL_STATIC_DRAW);
			}

			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y,
					int width, int height) {
			}

			@Override
			public void display(GLAutoDrawable drawable) {
				final GL2GL3 gl = new DebugGL2GL3(drawable.getGL().getGL2GL3());
				gl.glClear(GL_COLOR_BUFFER_BIT);
				gl.glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_SHORT, 0);
				gl.glFlush();
			}

			@Override
			public void dispose(GLAutoDrawable drawable) {
			}
		});
		add(canvas, BorderLayout.CENTER);
		setResizable(true);
		pack();
		setVisible(true);
	}

	public static void main(String[] arguments) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JOGLTest();
			}
		});
	}
}
