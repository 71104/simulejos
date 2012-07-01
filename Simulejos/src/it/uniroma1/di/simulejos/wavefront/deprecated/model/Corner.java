package it.uniroma1.di.simulejos.wavefront.deprecated.model;

import it.uniroma1.di.simulejos.math.Vector3;

public final class Corner {
	public final Vector3 vertex;
	public final Vector3 texture;
	public final Vector3 normal;

	public Corner(Vector3 vertex, Vector3 texture, Vector3 normal) {
		this.vertex = vertex;
		this.texture = texture;
		this.normal = normal;
	}
}
