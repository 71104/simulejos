package it.uniroma1.di.simulejos.wavefront;

public interface WavefrontCommandHandler {
	void vertex(double x, double y, double z, double w);

	void normal(double x, double y, double z);

	public static class Corner {
		public final int vertexIndex;
		public final int normalIndex;
		public final int textureIndex;

		Corner(int vertexIndex, int normalIndex, int textureIndex) {
			this.vertexIndex = vertexIndex;
			this.normalIndex = normalIndex;
			this.textureIndex = textureIndex;
		}
	}

	void face(Corner[] corners);
}
