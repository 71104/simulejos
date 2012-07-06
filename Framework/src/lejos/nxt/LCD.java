package lejos.nxt;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;

import it.uniroma1.di.simulejos.bridge.Bridge;

public class LCD {
	public static final int SCREEN_WIDTH = 100;
	public static final int SCREEN_HEIGHT = 64;
	public static final int NOOF_CHARS = 128;
	public static final int FONT_WIDTH = 5;
	public static final int FONT_HEIGHT = 8;
	public static final int CELL_WIDTH = FONT_WIDTH + 1;
	public static final int CELL_HEIGHT = FONT_HEIGHT;
	public static final int DISPLAY_CHAR_WIDTH = SCREEN_WIDTH / CELL_WIDTH;
	public static final int DISPLAY_CHAR_DEPTH = SCREEN_HEIGHT / CELL_HEIGHT;
	public static final int DEFAULT_REFRESH_PERIOD = 250;

	public static final int ROP_CLEAR = 0x00000000;
	public static final int ROP_AND = 0xff000000;
	public static final int ROP_ANDREVERSE = 0xff00ff00;
	public static final int ROP_COPY = 0x0000ff00;
	public static final int ROP_ANDINVERTED = 0xffff0000;
	public static final int ROP_NOOP = 0x00ff0000;
	public static final int ROP_XOR = 0x00ffff00;
	public static final int ROP_OR = 0xffffff00;
	public static final int ROP_NOR = 0xffffffff;
	public static final int ROP_EQUIV = 0x00ffffff;
	public static final int ROP_INVERT = 0x00ff00ff;
	public static final int ROP_ORREVERSE = 0xffff00ff;
	public static final int ROP_COPYINVERTED = 0x0000ffff;
	public static final int ROP_ORINVERTED = 0xff00ffff;
	public static final int ROP_NAND = 0xff0000ff;
	public static final int ROP_SET = 0x000000ff;

	public static boolean getPixel(byte[] buffer, int w, int x, int y) {
		return ((buffer[(y / 8) * w + x] >> (y % 8)) & 1) != 0;
	}

	public static void setPixel(byte[] buffer, int w, int x, int y, boolean set) {
		if (set) {
			buffer[(y / 8) * w + x] = (byte) (buffer[(y / 8) * w + x] | (byte) (1 << (y % 8)));
		} else {
			buffer[(y / 8) * w + x] = (byte) (buffer[(y / 8) * w + x] & ~(byte) (1 << (y % 8)));
		}
	}

	private static final byte[] font = new byte[FONT_WIDTH * FONT_HEIGHT * 16];
	static {
		final BufferedImage fontImage;
		try {
			fontImage = ImageIO.read(LCD.class.getResource("font.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for (int y = 0; y < FONT_HEIGHT; y++) {
			for (int x = 0; x < FONT_WIDTH * 128; x++) {
				setPixel(font, FONT_WIDTH * 128, x, y,
						((fontImage.getRGB(x, y) & 0xFFFFFF) != 0));
			}
		}
	}

	private static final byte[] buffer = new byte[SCREEN_HEIGHT * SCREEN_WIDTH
			/ 8];
	private static int autoRefreshPeriod = DEFAULT_REFRESH_PERIOD;

	/**
	 * This isn't in Lejos, it's Simulejos-specific. Simulejos programs cannot
	 * print on regular {@link System#out} because Java system classes are not
	 * virtualized by the simulator, thus all the robots share the same instance
	 * of {@link System#out}, which is the standard output stream of the
	 * simulator itself. Lejos classes instead ARE virtualized, so you can use
	 * this replacement when printing something on standard output and actually
	 * meaning to print it on the robot's LCD.
	 */
	public static final PrintStream SYSOUT = new PrintStream(
			new LCDOutputStream());

	private static void doRefresh() {
		final boolean[] data = new boolean[SCREEN_WIDTH * SCREEN_HEIGHT];
		for (int y = 0; y < SCREEN_HEIGHT; y++) {
			for (int x = 0; x < SCREEN_WIDTH; x++) {
				data[y * SCREEN_WIDTH + x] = getPixel(buffer, SCREEN_WIDTH, x,
						y);
			}
		}
		Bridge.BRICK.updateDisplay(data);
	}

	public static void asyncRefresh() {
		new Thread("display-refresh") {
			@Override
			public void run() {
				doRefresh();
			}
		}.start();
	}

	public static void asyncRefreshWait() {
		doRefresh();
	}

	public static void bitBlt(byte[] src, int sw, int sh, int sx, int sy,
			byte dst[], int dw, int dh, int dx, int dy, int w, int h, int rop) {
		switch (rop) {
		case ROP_CLEAR:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(buffer, SCREEN_WIDTH, dx + x, dy + y, false);
				}
			}
			break;
		case ROP_AND:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(
							buffer,
							SCREEN_WIDTH,
							dx + x,
							dy + y,
							getPixel(src, sw, sx + x, sy + y)
									&& getPixel(dst, dw, dx + x, dy + y));
				}
			}
			break;
		case ROP_COPY:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(buffer, SCREEN_WIDTH, dx + x, dy + y,
							getPixel(src, sw, sx + x, sy + y));
				}
			}
			break;
		case ROP_ANDINVERTED:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(
							buffer,
							SCREEN_WIDTH,
							dx + x,
							dy + y,
							!(getPixel(src, sw, sx + x, sy + y) && getPixel(
									dst, dw, dx + x, dy + y)));
				}
			}
			break;
		case ROP_NOOP:
			break;
		case ROP_XOR:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(
							buffer,
							SCREEN_WIDTH,
							dx + x,
							dy + y,
							getPixel(src, sw, sx + x, sy + y)
									^ getPixel(dst, dw, dx + x, dy + y));
				}
			}
			break;
		case ROP_OR:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(
							buffer,
							SCREEN_WIDTH,
							dx + x,
							dy + y,
							getPixel(src, sw, sx + x, sy + y)
									|| getPixel(dst, dw, dx + x, dy + y));
				}
			}
			break;
		case ROP_NOR:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(
							buffer,
							SCREEN_WIDTH,
							dx + x,
							dy + y,
							!(getPixel(src, sw, sx + x, sy + y) || getPixel(
									dst, dw, dx + x, dy + y)));
				}
			}
			break;
		case ROP_INVERT:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(buffer, SCREEN_WIDTH, dx + x, dy + y,
							!getPixel(dst, dw, dx + x, dy + y));
				}
			}
			break;
		case ROP_COPYINVERTED:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(buffer, SCREEN_WIDTH, dx + x, dy + y,
							!getPixel(src, sw, sx + x, sy + y));
				}
			}
			break;
		case ROP_ORINVERTED:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(
							buffer,
							SCREEN_WIDTH,
							dx + x,
							dy + y,
							!(getPixel(src, sw, sx + x, sy + y) || getPixel(
									dst, dw, dx + x, dy + y)));
				}
			}
			break;
		case ROP_NAND:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(
							buffer,
							SCREEN_WIDTH,
							dx + x,
							dy + y,
							!(getPixel(src, sw, sx + x, sy + y) && getPixel(
									dst, dw, dx + x, dy + y)));
				}
			}
			break;
		case ROP_SET:
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					setPixel(buffer, SCREEN_WIDTH, dx + x, dy + y, true);
				}
			}
			break;
		default:
			throw new IllegalArgumentException("invalid ROP");
		}
	}

	public static void bitBlt(byte[] src, int sw, int sh, int sx, int sy,
			int dx, int dy, int w, int h, int rop) {
		bitBlt(src, sw, sh, sx, sy, buffer, SCREEN_WIDTH, SCREEN_HEIGHT, dx,
				dy, w, h, rop);
		if (autoRefreshPeriod != 0) {
			doRefresh();
		}
	}

	public static void clear() {
		bitBlt(null, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, 0, 0, SCREEN_WIDTH,
				SCREEN_HEIGHT, ROP_CLEAR);
	}

	public static void clear(int y) {
		bitBlt(null, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, 0, y * CELL_HEIGHT,
				SCREEN_WIDTH, CELL_HEIGHT, ROP_CLEAR);
	}

	public static void clear(int x, int y, int n) {
		bitBlt(null, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, x * CELL_WIDTH, y
				* CELL_HEIGHT, n * CELL_WIDTH, CELL_HEIGHT, ROP_CLEAR);
	}

	public static void clearDisplay() {
		bitBlt(null, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, 0, 0, SCREEN_WIDTH,
				SCREEN_HEIGHT, ROP_CLEAR);
	}

	public static void drawChar(char c, int x, int y) {
		bitBlt(font, FONT_WIDTH * 128, FONT_HEIGHT, FONT_WIDTH * c, 0, x
				* CELL_WIDTH, y * CELL_HEIGHT, FONT_WIDTH, FONT_HEIGHT,
				ROP_COPY);
	}

	public static void drawInt(int i, int x, int y) {
		drawString(Integer.toString(i), x, y);
	}

	public static void drawInt(int i, int places, int x, int y) {
		String string = Integer.toString(i);
		while (string.length() < places) {
			string = " " + string;
		}
		drawString(string, x, y);
	}

	public static void drawString(String str, int x, int y) {
		char[] characters = str.toCharArray();
		for (int i = 0; i < characters.length; i++) {
			drawChar(characters[i], x + i, y);
		}
	}

	public static void drawString(String str, int x, int y, boolean inverted) {
		if (inverted) {
			char[] characters = str.toCharArray();
			bitBlt(null, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, x * CELL_WIDTH, y
					* CELL_HEIGHT, characters.length * CELL_WIDTH, CELL_HEIGHT,
					ROP_SET);
			for (int i = 0; i < characters.length; i++) {
				bitBlt(font, FONT_WIDTH * 128, FONT_HEIGHT, FONT_WIDTH
						* characters[i], 0, (x + i) * CELL_WIDTH, y
						* CELL_HEIGHT, FONT_WIDTH, FONT_HEIGHT,
						ROP_COPYINVERTED);
			}
		} else {
			drawString(str, x, y);
		}
	}

	public static byte[] getDisplay() {
		return buffer;
	}

	public static int getPixel(int x, int y) {
		if (x < 0 || x > SCREEN_WIDTH || y < 0 || y > SCREEN_HEIGHT) {
			return 0;
		}
		int bit = (y & 0x7);
		int index = (y / 8) * SCREEN_WIDTH + x;
		return ((buffer[index] >> bit) & 1);
	}

	public static int getRefreshCompleteTime() {
		return (int) System.currentTimeMillis();
	}

	public static byte[] getSystemFont() {
		return font;
	}

	public static void refresh() {
		asyncRefresh();
		if (autoRefreshPeriod == 0) {
			asyncRefreshWait();
		}
	}

	public static void scroll() {
		bitBlt(buffer, SCREEN_WIDTH, SCREEN_HEIGHT, 0, CELL_HEIGHT, 0, 0,
				SCREEN_WIDTH, SCREEN_HEIGHT - CELL_HEIGHT, ROP_COPY);
		bitBlt(null, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, 0, SCREEN_HEIGHT
				- CELL_HEIGHT, SCREEN_WIDTH, CELL_HEIGHT, ROP_CLEAR);
	}

	public static void setAutoRefresh(boolean on) {
		setAutoRefreshPeriod((on ? DEFAULT_REFRESH_PERIOD : 0));
	}

	public static int setAutoRefreshPeriod(int period) {
		int previous = autoRefreshPeriod;
		autoRefreshPeriod = period;
		return previous;
	}

	public static void setContrast(int contrast) {
	}

	public static void setPixel(int x, int y, int color) {
		bitBlt(buffer, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, x, y, 1, 1,
				(color == 1 ? ROP_SET : ROP_CLEAR));
	}
}
