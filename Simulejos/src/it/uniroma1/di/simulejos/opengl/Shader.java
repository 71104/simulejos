package it.uniroma1.di.simulejos.opengl;

import it.uniroma1.di.simulejos.util.FullReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.media.opengl.GL2GL3;
import static javax.media.opengl.GL2GL3.*;

public class Shader extends GLObject {
	public enum Type {
		VERTEX {
			@Override
			int getGLType() {
				return GL_VERTEX_SHADER;
			}
		},
		FRAGMENT {
			@Override
			int getGLType() {
				return GL_FRAGMENT_SHADER;
			}
		};
		abstract int getGLType();
	};

	private static final ConcurrentMap<Integer, Shader> SHADERS = new ConcurrentHashMap<Integer, Shader>();

	public Shader(GL2GL3 gl, Type type) {
		super(gl, gl.glCreateShader(type.getGLType()));
	}

	public static class CompileException extends RuntimeException {
		private static final long serialVersionUID = 7160429065453776008L;

		public final String infoLog;

		CompileException(String infoLog) {
			super(infoLog);
			this.infoLog = infoLog;
		}
	}

	public Shader(GL2GL3 gl, Type type, String source) {
		super(gl, gl.glCreateShader(type.getGLType()));
		source(source);
		compile();
		if (!isCompiled()) {
			throw new CompileException(getInfoLog());
		}
	}

	public Shader(GL2GL3 gl, Type type, Reader source) throws IOException {
		super(gl, gl.glCreateShader(type.getGLType()));
		source(source);
		compile();
		if (!isCompiled()) {
			throw new CompileException(getInfoLog());
		}
	}

	public Shader(GL2GL3 gl, Type type, Class<?> c, String name)
			throws IOException {
		this(gl, type, new InputStreamReader(c.getResourceAsStream(name)));
	}

	private Shader(GL2GL3 gl, int id) {
		super(gl, id);
	}

	static Shader getById(GL2GL3 gl, int id) {
		return SHADERS.putIfAbsent(id, new Shader(gl, id));
	}

	private int get(GL2GL3 gl, int name) {
		final int[] result = { 1234 };
		gl.glGetShaderiv(id, name, result, 0);
		return result[0];
	}

	public void source(GL2GL3 gl, String source) {
		final String[] sources = { source };
		gl.glShaderSource(id, 1, sources, null, 0);
	}

	public void source(String source) {
		final String[] sources = { source };
		gl.glShaderSource(id, 1, sources, null, 0);
	}

	public void source(GL2GL3 gl, Reader source) throws IOException {
		final FullReader reader = new FullReader(source);
		try {
			source(gl, reader.readAll());
		} finally {
			reader.close();
		}
	}

	public void source(Reader source) throws IOException {
		final FullReader reader = new FullReader(source);
		try {
			source(reader.readAll());
		} finally {
			reader.close();
		}
	}

	public String getSource(GL2GL3 gl) {
		final int length = get(gl, GL_SHADER_SOURCE_LENGTH);
		final byte[] source = new byte[length];
		gl.glGetShaderSource(id, length, null, 0, source, 0);
		return new String(source);
	}

	public String getSource() {
		final int length = get(gl, GL_SHADER_SOURCE_LENGTH);
		final byte[] source = new byte[length];
		gl.glGetShaderSource(id, length, null, 0, source, 0);
		return new String(source);
	}

	public void compile(GL2GL3 gl) {
		gl.glCompileShader(id);
	}

	public void compile() {
		gl.glCompileShader(id);
	}

	public boolean isCompiled(GL2GL3 gl) {
		return get(gl, GL_COMPILE_STATUS) != GL_FALSE;
	}

	public boolean isCompiled() {
		return get(gl, GL_COMPILE_STATUS) != GL_FALSE;
	}

	public String getInfoLog(GL2GL3 gl) {
		final int length = get(gl, GL_INFO_LOG_LENGTH);
		final byte[] log = new byte[length];
		gl.glGetShaderInfoLog(id, length, null, 0, log, 0);
		return new String(log);
	}

	public String getInfoLog() {
		final int length = get(gl, GL_INFO_LOG_LENGTH);
		final byte[] log = new byte[length];
		gl.glGetShaderInfoLog(id, length, null, 0, log, 0);
		return new String(log);
	}

	public void delete() {
		gl.glDeleteShader(id);
	}

	public boolean isDeleted(GL2GL3 gl) {
		return get(gl, GL_DELETE_STATUS) != GL_FALSE;
	}

	public boolean isDeleted() {
		return get(gl, GL_DELETE_STATUS) != GL_FALSE;
	}
}
