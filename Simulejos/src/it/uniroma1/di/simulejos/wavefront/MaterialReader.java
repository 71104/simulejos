package it.uniroma1.di.simulejos.wavefront;

import it.uniroma1.di.simulejos.wavefront.model.Color;
import it.uniroma1.di.simulejos.wavefront.model.Material;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

final class MaterialReader {
	private final GL gl;
	private final WavefrontParser parser;

	private final List<Material> materials = new LinkedList<Material>();
	private Material currentMaterial;

	private abstract class MaterialHandler extends
			WavefrontParser.CommandHandler {
		protected MaterialHandler() {
			parser.super();
		}

		protected final Material getCurrentMaterial() throws ParseException {
			if (currentMaterial == null) {
				throwParseException();
			}
			return currentMaterial;
		}
	}

	private final class NewMaterialHandler extends MaterialHandler {
		@Override
		public void handle() throws IOException, ParseException {
			String materialName = parser.readString();
			currentMaterial = new Material(materialName);
			materials.add(currentMaterial);
		}
	}

	private abstract class ColorHandler extends MaterialHandler {
		protected final Color readColor() throws IOException, ParseException {
			float r = readFloat();
			float g = readFloat();
			float b = readFloat();
			return new Color(r, g, b);
		}
	}

	private final class AmbientHandler extends ColorHandler {
		@Override
		public void handle() throws IOException, ParseException {
			getCurrentMaterial().ambient = readColor();
		}
	}

	private final class DiffuseHandler extends ColorHandler {
		@Override
		public void handle() throws IOException, ParseException {
			getCurrentMaterial().diffuse = readColor();
		}
	}

	private final class SpecularHandler extends ColorHandler {
		@Override
		public void handle() throws IOException, ParseException {
			getCurrentMaterial().specular = readColor();
		}
	}

	private final class DissolveHandler extends MaterialHandler {
		@Override
		public void handle() throws IOException, ParseException {
			getCurrentMaterial().setAlpha(readFloat());
		}
	}

	private final class TextureHandler extends MaterialHandler {
		@Override
		public void handle() throws IOException, ParseException {
			String fileName = parser.readString();
			try {
				getCurrentMaterial().setTexture(
						ImageIO.read(getReferencedFile(fileName)), gl);
			} catch (IllegalStateException e) {
				throwParseException();
			}
		}
	}

	public MaterialReader(File libraryFile, GL gl) throws FileNotFoundException {
		this.gl = gl;

		HashMap<String, WavefrontParser.CommandHandler> handlers = new HashMap<String, WavefrontParser.CommandHandler>();
		parser = new WavefrontParser(libraryFile, handlers);
		handlers.put("newmtl", new NewMaterialHandler());
		handlers.put("ka", new AmbientHandler());
		handlers.put("kd", new DiffuseHandler());
		handlers.put("ks", new SpecularHandler());
		handlers.put("d", new DissolveHandler());
		handlers.put("map_kd", new TextureHandler());
	}

	public void loadMaterials(GL gl) throws IOException, ParseException {
		parser.parse();
	}

	public Iterable<Material> getMaterials() {
		return materials;
	}
}
