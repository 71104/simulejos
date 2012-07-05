package it.uniroma1.di.simulejos.math;

import java.io.Serializable;

public class BoundingBox implements Cloneable, Serializable {
	private static final long serialVersionUID = 8761446190649778946L;

	public final Vector3 min;
	public final Vector3 max;
	public final Vector3 size;
	public final Vector3 center;

	public BoundingBox(float[] vertices) {
		if (vertices.length < 3) {
			throw new IllegalArgumentException();
		}
		Vector3 min = new Vector3(vertices[0] / vertices[3], vertices[1]
				/ vertices[3], vertices[2] / vertices[3]);
		Vector3 max = min;
		for (int i = 4; i < vertices.length / 4; i++) {
			min = min.floor(new Vector3(vertices[i * 4] / vertices[i * 4 + 3],
					vertices[i * 4 + 1] / vertices[i * 4 + 3],
					vertices[i * 4 + 2] / vertices[i * 4 + 3]));
			max = max.ceil(new Vector3(vertices[i * 4] / vertices[i * 4 + 3],
					vertices[i * 4 + 1] / vertices[i * 4 + 3],
					vertices[i * 4 + 2] / vertices[i * 4 + 3]));
		}
		this.min = min;
		this.max = max;
		this.size = max.minus(min);
		this.center = min.plus(max).by(0.5);
	}
}
