package it.uniroma1.di.simulejos;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public final class WallGenerator {
	private static boolean flood(boolean[][] map, int x, int y) {
		if ((y < map.length) && (x < map[y].length) && map[y][x]) {
			map[y][x] = false;
			flood(map, x, y - 1);
			flood(map, x - 1, y);
			flood(map, x + 1, y);
			flood(map, x, y + 1);
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] arguments) throws IOException {
		if (arguments.length != 2) {
			System.err.println("Missing argument.");
			return;
		}

		// binarize image
		BufferedImage image = ImageIO.read(new File(arguments[0]));
		final int width = image.getWidth();
		final int height = image.getHeight();
		final BufferedImage map = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_BINARY);
		map.getGraphics().drawImage(image, 0, 0, null);
		image = null;

		// convert to boolean matrix
		final int[] mapData = map.getRGB(0, 0, width, height, null, 0, width);
		final boolean[][] mapFlags = new boolean[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final Color color = new Color(mapData[y * width + x]);
				mapFlags[y][x] = ((double) color.getRed() / 255
						+ (double) color.getGreen() / 255 + (double) color
						.getBlue() / 255) * 2 / 3 > 1;
			}
		}

		// detect connected components
		final boolean[][] tempMap = mapFlags.clone();
		for (int y = 0; y < height; y++) {
			tempMap[y] = tempMap[y].clone();
		}
		final List<Point> connectedComponents = new ArrayList<Point>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (flood(mapFlags, x, y)) {
					connectedComponents.add(new Point(x, y));
				}
			}
		}

		// TODO
	}
}
