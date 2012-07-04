package it.uniroma1.di.simulejos.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2GL3;

public class Buffer extends GLObject {
	private static int createBuffer(GL2GL3 gl) {
		final int[] result = new int[1];
		gl.glGenBuffers(1, result, 0);
		return result[0];
	}

	public static enum Target {
		ARRAY {
			@Override
			int getGLTarget() {
				return GL2GL3.GL_ARRAY_BUFFER;
			}
		},
		ELEMENT_ARRAY {
			@Override
			int getGLTarget() {
				return GL2GL3.GL_ELEMENT_ARRAY_BUFFER;
			}
		},
		PIXEL_PACK {
			@Override
			int getGLTarget() {
				return GL2GL3.GL_PIXEL_PACK_BUFFER;
			}
		},
		PIXEL_UNPACK {
			@Override
			int getGLTarget() {
				return GL2GL3.GL_PIXEL_UNPACK_BUFFER;
			}
		};
		abstract int getGLTarget();
	}

	public static enum Usage {
		STREAM_DRAW {
			@Override
			int getGLUsage() {
				return GL2GL3.GL_STREAM_DRAW;
			}
		},
		STREAM_READ {
			@Override
			int getGLUsage() {
				return GL2GL3.GL_STREAM_READ;
			}
		},
		STREAM_COPY {
			@Override
			int getGLUsage() {
				return GL2GL3.GL_STREAM_COPY;
			}
		},
		STATIC_DRAW {
			@Override
			int getGLUsage() {
				return GL2GL3.GL_STATIC_DRAW;
			}
		},
		STATIC_READ {
			@Override
			int getGLUsage() {
				return GL2GL3.GL_STATIC_READ;
			}
		},
		STATIC_COPY {
			@Override
			int getGLUsage() {
				return GL2GL3.GL_STATIC_COPY;
			}
		},
		DYNAMIC_DRAW {
			@Override
			int getGLUsage() {
				return GL2GL3.GL_DYNAMIC_DRAW;
			}
		},
		DYNAMIC_READ {
			@Override
			int getGLUsage() {
				return GL2GL3.GL_DYNAMIC_READ;
			}
		},
		DYNAMIC_COPY {
			@Override
			int getGLUsage() {
				return GL2GL3.GL_DYNAMIC_COPY;
			}
		};
		abstract int getGLUsage();
	}

	private final int target;
	private final int usage;

	public Buffer(GL2GL3 gl, Target target, Usage usage) {
		super(gl, createBuffer(gl));
		this.target = target.getGLTarget();
		this.usage = usage.getGLUsage();
		bind();
	}

	public void bind() {
		gl.glBindBuffer(target, id);
	}

	public void unbind() {
		gl.glBindBuffer(target, 0);
	}

	public void data(byte[] data) {
		gl.glBufferData(target, data.length, ByteBuffer.wrap(data), usage);
	}

	public void data(ByteBuffer data) {
		gl.glBufferData(target, data.limit(), data, usage);
	}

	public void data(short[] data) {
		gl.glBufferData(target, data.length * 2, ShortBuffer.wrap(data), usage);
	}

	public void data(ShortBuffer data) {
		gl.glBufferData(target, data.limit() * 2, data, usage);
	}

	public void data(int[] data) {
		gl.glBufferData(target, data.length * 4, IntBuffer.wrap(data), usage);
	}

	public void data(IntBuffer data) {
		gl.glBufferData(target, data.limit() * 4, data, usage);
	}

	public void data(long[] data) {
		gl.glBufferData(target, data.length * 8, LongBuffer.wrap(data), usage);
	}

	public void data(LongBuffer data) {
		gl.glBufferData(target, data.limit() * 8, data, usage);
	}

	public void data(float[] data) {
		gl.glBufferData(target, data.length * 4, FloatBuffer.wrap(data), usage);
	}

	public void data(FloatBuffer data) {
		gl.glBufferData(target, data.limit() * 4, data, usage);
	}

	public void data(double[] data) {
		gl.glBufferData(target, data.length * 8, DoubleBuffer.wrap(data), usage);
	}

	public void data(DoubleBuffer data) {
		gl.glBufferData(target, data.limit() * 8, data, usage);
	}

	public void subData(long offset, byte[] data) {
		gl.glBufferSubData(target, offset, data.length, ByteBuffer.wrap(data));
	}

	public void subData(long offset, ByteBuffer data) {
		gl.glBufferSubData(target, offset, data.limit(), data);
	}

	public void subData(long offset, short[] data) {
		gl.glBufferSubData(target, offset, data.length * 2,
				ShortBuffer.wrap(data));
	}

	public void subData(long offset, ShortBuffer data) {
		gl.glBufferSubData(target, offset, data.limit() * 2, data);
	}

	public void subData(long offset, int[] data) {
		gl.glBufferSubData(target, offset, data.length * 4,
				IntBuffer.wrap(data));
	}

	public void subData(long offset, IntBuffer data) {
		gl.glBufferSubData(target, offset, data.limit() * 4, data);
	}

	public void subData(long offset, long[] data) {
		gl.glBufferSubData(target, offset, data.length * 8,
				LongBuffer.wrap(data));
	}

	public void subData(long offset, LongBuffer data) {
		gl.glBufferSubData(target, offset, data.limit() * 8, data);
	}

	public void subData(long offset, float[] data) {
		gl.glBufferSubData(target, offset, data.length * 4,
				FloatBuffer.wrap(data));
	}

	public void subData(long offset, FloatBuffer data) {
		gl.glBufferSubData(target, offset, data.limit() * 4, data);
	}

	public void subData(long offset, double[] data) {
		gl.glBufferSubData(target, offset, data.length * 8,
				DoubleBuffer.wrap(data));
	}

	public void subData(long offset, DoubleBuffer data) {
		gl.glBufferSubData(target, offset, data.limit() * 8, data);
	}

	public java.nio.Buffer getSubData(long offset, int size) {
		final ByteBuffer buffer = ByteBuffer.allocate(size);
		gl.glGetBufferSubData(target, offset, size, buffer);
		return buffer;
	}

	public int getSize() {
		final int[] result = new int[1];
		gl.glGetBufferParameteriv(target, GL2GL3.GL_BUFFER_SIZE, result, 0);
		return result[0];
	}

	public void delete() {
		final int[] ids = { id };
		gl.glDeleteBuffers(1, ids, 0);
	}
}
