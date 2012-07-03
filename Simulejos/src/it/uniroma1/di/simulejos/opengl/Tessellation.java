package it.uniroma1.di.simulejos.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

public class Tessellation {
	public static interface Callback {
		void index(short index);
	}

	private final Callback callback;
	private final GLUtessellator tessellator = GLU.gluNewTess();

	private static interface Handler {
		void index(short index);
	}

	private final class TriangleHandler implements Handler {
		@Override
		public void index(short index) {
			callback.index(index);
		}
	}

	private final TriangleHandler triangleHandler = new TriangleHandler();

	private final class TriangleStripHandler implements Handler {
		private boolean first;
		private boolean second;
		private short index1;
		private short index2;

		@Override
		public void index(short index) {
			if (!first) {
				index1 = index;
				first = true;
			} else if (first && !second) {
				index2 = index;
				second = true;
			} else {
				callback.index(index1);
				callback.index(index2);
				callback.index(index);
				index1 = index2;
				index2 = index;
			}
		}
	}

	private final TriangleStripHandler triangleStripHandler = new TriangleStripHandler();

	private final class TriangleFanHandler implements Handler {
		private boolean first;
		private boolean second;
		private short firstIndex;
		private short lastIndex;

		@Override
		public void index(short index) {
			if (!first) {
				firstIndex = index;
				first = true;
			} else if (first && !second) {
				lastIndex = index;
				second = true;
			} else {
				callback.index(firstIndex);
				callback.index(lastIndex);
				callback.index(index);
			}
		}
	}

	private final TriangleFanHandler triangleFanHandler = new TriangleFanHandler();

	{
		final GLUtessellatorCallback callback = new GLUtessellatorCallbackAdapter() {
			private volatile Handler handler;

			@Override
			public void begin(int type) {
				switch (type) {
				case GL.GL_TRIANGLES:
					handler = triangleHandler;
					break;
				case GL.GL_TRIANGLE_STRIP:
					handler = triangleStripHandler;
					break;
				case GL.GL_TRIANGLE_FAN:
					handler = triangleFanHandler;
					break;
				}
			}

			@Override
			public void vertex(Object index) {
				handler.index((short) index);
			}
		};
		GLU.gluTessCallback(tessellator, GLU.GLU_TESS_BEGIN, callback);
		GLU.gluTessCallback(tessellator, GLU.GLU_TESS_VERTEX, callback);
	}

	public Tessellation(float[] vertices, short[] indices, Callback callback) {
		this.callback = callback;
		GLU.gluBeginPolygon(tessellator);
		for (short index : indices) {
			GLU.gluTessVertex(tessellator, new double[] { vertices[index * 3],
					vertices[index * 3 + 1], vertices[index * 3 + 2] }, 0,
					index);
		}
		GLU.gluEndPolygon(tessellator);
	}

	@Override
	protected void finalize() {
		GLU.gluDeleteTess(tessellator);
	}
}
