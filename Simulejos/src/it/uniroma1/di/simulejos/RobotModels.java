package it.uniroma1.di.simulejos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2GL3;

import it.uniroma1.di.simulejos.opengl.Arrays;
import it.uniroma1.di.simulejos.opengl.Program;
import it.uniroma1.di.simulejos.wavefront.ModelData;

class RobotModels {
	private final Program program;

	public class Model {
		private final Arrays arrays;

		private Model(GL2GL3 gl, ModelData data) {
			arrays = new Arrays(gl);
			// TODO add arrays
		}

		public void draw(GL2GL3 gl) {
			arrays.bind();
			// TODO draw indices
		}
	}

	private final List<Model> models = Collections
			.synchronizedList(new LinkedList<Model>());

	public RobotModels(GL2GL3 gl) {
		program = new Program(gl, getClass(), "robot", new String[] {
				"in_Vertex", "in_Color" });
	}

	public void draw(GL2GL3 gl) {
		program.use();
		for (Model model : models) {
			model.draw(gl);
		}
	}
}
