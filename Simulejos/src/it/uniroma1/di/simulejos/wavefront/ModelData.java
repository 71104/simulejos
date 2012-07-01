package it.uniroma1.di.simulejos.wavefront;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ModelData implements Serializable {
	private static final long serialVersionUID = 3141711160723266117L;

	public final int count;
	public final FloatBuffer vertices;
	public final ShortBuffer indices;
	public final FloatBuffer colors;

	protected ModelData(float[] vertices, short[] indices, float[] colors) {
		this.count = indices.length;
		this.vertices = FloatBuffer.wrap(vertices).asReadOnlyBuffer();
		this.indices = ShortBuffer.wrap(indices);
		this.colors = FloatBuffer.wrap(colors).asReadOnlyBuffer();
	}
}
