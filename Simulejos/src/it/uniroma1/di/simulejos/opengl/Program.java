package it.uniroma1.di.simulejos.opengl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2GL3;

public class Program extends GLObject {
	private final Map<String, Integer> uniformLocationCache = Collections
			.synchronizedMap(new HashMap<String, Integer>());

	public Program(GL2GL3 gl) {
		super(gl, gl.glCreateProgram());
	}

	public static class LinkException extends RuntimeException {
		private static final long serialVersionUID = -7104743047447892268L;

		public final String infoLog;

		LinkException(String infoLog) {
			super(infoLog);
			this.infoLog = infoLog;
		}
	}

	public Program(GL2GL3 gl, VertexShader vertexShader,
			FragmentShader fragmentShader, String[] variableNames) {
		super(gl, gl.glCreateProgram());
		attachShader(vertexShader);
		attachShader(fragmentShader);
		for (int i = 0; i < variableNames.length; i++) {
			gl.glBindAttribLocation(id, i, variableNames[i]);
		}
		link();
		if (!isLinked()) {
			throw new LinkException(getInfoLog());
		}
	}

	private static VertexShader createVertexShader(GL2GL3 gl, Class<?> c,
			String name) {
		try {
			return new VertexShader(gl, c, name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static FragmentShader createFragmentShader(GL2GL3 gl, Class<?> c,
			String name) {
		try {
			return new FragmentShader(gl, c, name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Program(GL2GL3 gl, Class<?> c, String name, String[] variableNames) {
		this(gl, createVertexShader(gl, c, name), createFragmentShader(gl, c,
				name), variableNames);
	}

	private int get(int name) {
		final int[] result = { 1234 };
		gl.glGetProgramiv(id, name, result, 0);
		return result[0];
	}

	public void use() {
		gl.glUseProgram(id);
	}

	public void attachShader(Shader shader) {
		gl.glAttachShader(id, shader.id);
	}

	public void detachShader(Shader shader) {
		gl.glDetachShader(id, shader.id);
	}

	public Shader[] getAttachedShaders() {
		final int count = get(GL2GL3.GL_ATTACHED_SHADERS);
		final int[] ids = new int[count];
		gl.glGetAttachedShaders(id, count, null, 0, ids, 0);
		final Shader[] shaders = new Shader[count];
		for (int i = 0; i < ids.length; i++) {
			shaders[i] = Shader.getById(gl, ids[i]);
		}
		return shaders;
	}

	public void link() {
		uniformLocationCache.clear();
		gl.glLinkProgram(id);
	}

	public boolean isLinked() {
		return get(GL2GL3.GL_LINK_STATUS) != GL2GL3.GL_FALSE;
	}

	public String getInfoLog() {
		final int length = get(GL2GL3.GL_INFO_LOG_LENGTH);
		final byte[] log = new byte[length];
		gl.glGetProgramInfoLog(id, length, null, 0, log, 0);
		return new String(log);
	}

	public boolean validate() {
		gl.glValidateProgram(id);
		return get(GL2GL3.GL_VALIDATE_STATUS) != GL2GL3.GL_FALSE;
	}

	public int getUniformLocation(String name) {
		if (uniformLocationCache.containsKey(name)) {
			return uniformLocationCache.get(name);
		} else {
			final int location = gl.glGetUniformLocation(id, name);
			uniformLocationCache.put(name, location);
			return location;
		}
	}

	// TODO metodi uniform

	public void getUniformfv(int location, float[] data) {
		gl.glGetUniformfv(id, location, data, 0);
	}

	public void getUniformiv(int location, int[] data) {
		gl.glGetUniformiv(id, location, data, 0);
	}

	public float getUniformf(int location) {
		final float[] data = new float[1];
		gl.glGetUniformfv(id, location, data, 0);
		return data[0];
	}

	public int getUniformi(int location) {
		final int[] data = new int[1];
		gl.glGetUniformiv(id, location, data, 0);
		return data[0];
	}

	public void getUniformfv(String name, float[] data) {
		gl.glGetUniformfv(id, getUniformLocation(name), data, 0);
	}

	public void getUniformiv(String name, int[] data) {
		gl.glGetUniformiv(id, getUniformLocation(name), data, 0);
	}

	public float getUniformf(String name) {
		final float[] data = new float[1];
		gl.glGetUniformfv(id, getUniformLocation(name), data, 0);
		return data[0];
	}

	public int getUniformi(String name) {
		final int[] data = new int[1];
		gl.glGetUniformiv(id, getUniformLocation(name), data, 0);
		return data[0];
	}

	public void delete() {
		gl.glDeleteProgram(id);
	}

	public boolean isDeleted() {
		return get(GL2GL3.GL_DELETE_STATUS) != GL2GL3.GL_FALSE;
	}
}
