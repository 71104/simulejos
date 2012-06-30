package it.uniroma1.di.simulejos.wavefront;

import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.wavefront.model.BoundingBox;
import it.uniroma1.di.simulejos.wavefront.model.Corner;
import it.uniroma1.di.simulejos.wavefront.model.Material;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import static javax.media.opengl.GL.*;

public final class ModelReader {
	private final GL gl;
	private final WavefrontParser parser;

	private final List<Vector3> vertices = new ArrayList<Vector3>();
	private final List<Vector3> textureVertices = new ArrayList<Vector3>();
	private final List<Vector3> normals = new ArrayList<Vector3>();

	private final Map<String, Material> materials = new HashMap<String, Material>();
	private Material currentMaterial;

	private BoundingBox boundingBox;

	private abstract class ModelCommandHandler extends
			WavefrontParser.CommandHandler {
		protected ModelCommandHandler() {
			parser.super();
		}
	}

	private abstract class VectorHandler extends ModelCommandHandler {
		public final Vector3 readVector() throws IOException, ParseException {
			float x = readFloat();
			float z = readFloat();
			float y = readFloat();
			return new Vector3(x, y, z);
		}
	}

	private final class VertexHandler extends VectorHandler {
		@Override
		public void handle() throws IOException, ParseException {
			vertices.add(readVector());
		}
	}

	private final class TextureVertexHandler extends ModelCommandHandler {
		@Override
		public void handle() throws IOException, ParseException {
			float x = readFloat();
			float z = eol() ? 0.0f : readFloat();
			float y = eol() ? 0.0f : readFloat();
			textureVertices.add(new Vector3(x, y, z));
		}
	}

	private final class NormalHandler extends VectorHandler {
		@Override
		public void handle() throws IOException, ParseException {
			normals.add(readVector().invert());
		}
	}

	private final class FaceHandler extends ModelCommandHandler {
		private List<Corner> corners = new LinkedList<Corner>();

		private Corner readCorner() throws IOException, ParseException {
			Vector3 vertex = readVertex();
			if (!slash()) {
				return new Corner(vertex, null, null);
			}

			Vector3 texture = null;
			if (!slash()) {
				texture = readTextureVertex();
				if (!slash()) {
					return new Corner(vertex, texture, null);
				}
			}

			Vector3 normal = readNormal();
			return new Corner(vertex, texture, normal);
		}

		private void readAndProcessCorner() throws IOException, ParseException {
			Corner corner = readCorner();
			corners.add(corner);
			if (boundingBox != null) {
				boundingBox.test(corner.vertex);
			} else {
				boundingBox = new BoundingBox(corner.vertex);
			}
		}

		@Override
		public void handle() throws IOException, ParseException {
			corners.clear();
			readAndProcessCorner();
			readAndProcessCorner();
			readAndProcessCorner();
			while (!eol()) {
				readAndProcessCorner();
			}
			// TODO qui avveniva la compilazione della lista
		}
	}

	private final class MaterialLibraryHandler extends ModelCommandHandler {
		public void readMaterials(String fileName) throws IOException {
			MaterialReader materialReader;
			try {
				materialReader = new MaterialReader(
						getReferencedFile(fileName), gl);
			} catch (FileNotFoundException e) {
				throwParseException();
				return;
			}

			materialReader.loadMaterials(gl);

			for (Material material : materialReader.getMaterials()) {
				materials.put(material.name, material);
			}
		}

		@Override
		public void handle() throws IOException, ParseException {
			readMaterials(parser.readString());
			while (!eol()) {
				readMaterials(parser.readString());
			}
		}
	}

	private final class MaterialHandler extends ModelCommandHandler {
		@Override
		public void handle() throws IOException, ParseException {
			String materialName = parser.readString();
			if (!materials.containsKey(materialName)) {
				throwParseException();
			}

			if (currentMaterial != null) {
				currentMaterial.reset(gl);
			}
			currentMaterial = materials.get(materialName);
			currentMaterial.compile(gl);
		}
	}

	public ModelReader(File modelFile, GL gl) throws FileNotFoundException {
		HashMap<String, WavefrontParser.CommandHandler> handlers = new HashMap<String, WavefrontParser.CommandHandler>();
		parser = new WavefrontParser(modelFile, handlers);
		handlers.put("v", new VertexHandler());
		handlers.put("vt", new TextureVertexHandler());
		handlers.put("vn", new NormalHandler());
		handlers.put("f", new FaceHandler());
		handlers.put("mtllib", new MaterialLibraryHandler());
		handlers.put("usemtl", new MaterialHandler());
		this.gl = gl;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	private final Vector3 readVertex() throws IOException, ParseException {
		return vertices.get(parser.readIndex(vertices));
	}

	private final Vector3 readTextureVertex() throws IOException,
			ParseException {
		return textureVertices.get(parser.readIndex(textureVertices));
	}

	private final Vector3 readNormal() throws IOException, ParseException {
		return normals.get(parser.readIndex(normals));
	}

	public void loadModel() throws IOException, ParseException {
		parser.parse();

		if (currentMaterial != null) {
			currentMaterial.reset(gl);
			currentMaterial = null;
		}
	}
}
