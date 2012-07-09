package javax.microedition.lcdui;

import lejos.nxt.LCD;

/**
 * Provides access to fonts for use with the display or images. The actual font
 * data is held as a series of glyphs with all inter character spacing removed.
 * The format of the bitmap is in standard leJOS format (so aligned for use on
 * NXT LCD display). There is one bit per pixel. The pixels are packed into bytes
 * with each byte spanning 8 scan lines. The least significant bit of each byte
 * is the pixel for the top most scan line, the most significant bit is the
 * 8th scan line. Values of 1 represent black. 0 white.
 * @author Andy
 */
public class Font
{

    final int width;
    final int height;
    final int glyphWidth;
    final int glyphCount;
    final int firstChar;
    final int base;
    final byte[] glyphs;
    public final static int SIZE_SMALL = 8;
    public final static int SIZE_MEDIUM = 0;
    public final static int SIZE_LARGE = 16;
    private static Font small;
    private static Font large;
    // The system font is held in flash. Gain access to the glyphs.
    private static Font systemFont = new Font(LCD.getSystemFont(), LCD.CELL_WIDTH, LCD.CELL_HEIGHT, LCD.CELL_HEIGHT - 1, LCD.FONT_WIDTH, 128, 0);

    /*
    static class SmallFont extends Font
    {
    SmallFont()
    {
    super(
    new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x17, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x1f, (byte) 0x0a, (byte) 0x1f, (byte) 0x12, (byte) 0x1f, (byte) 0x09, (byte) 0x19, (byte) 0x04, (byte) 0x13, (byte) 0x0a, (byte) 0x15, (byte) 0x1d, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x0e, (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x11, (byte) 0x0e, (byte) 0x15, (byte) 0x0e, (byte) 0x15, (byte) 0x04, (byte) 0x1f, (byte) 0x04, (byte) 0x10, (byte) 0x0c, (byte) 0x00, (byte) 0x04, (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x18, (byte) 0x04, (byte) 0x03, (byte) 0x1f, (byte) 0x11, (byte) 0x1f, (byte) 0x02, (byte) 0x1f, (byte) 0x00, (byte) 0x1d, (byte) 0x15, (byte) 0x17, (byte) 0x15, (byte) 0x15, (byte) 0x1f, (byte) 0x07, (byte) 0x04, (byte) 0x1f, (byte) 0x17, (byte) 0x15, (byte) 0x1d, (byte) 0x1f, (byte) 0x15, (byte) 0x1d, (byte) 0x01, (byte) 0x01, (byte) 0x1f, (byte) 0x1f, (byte) 0x15, (byte) 0x1f, (byte) 0x07, (byte) 0x05, (byte) 0x1f, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x10, (byte) 0x0a, (byte) 0x00, (byte) 0x04, (byte) 0x0a, (byte) 0x11, (byte) 0x0a, (byte) 0x0a, (byte) 0x0a, (byte) 0x11, (byte) 0x0a, (byte) 0x04, (byte) 0x01, (byte) 0x15, (byte) 0x02, (byte) 0x1e, (byte) 0x1b, (byte) 0x16, (byte) 0x1e, (byte) 0x09, (byte) 0x1e, (byte) 0x1f, (byte) 0x15, (byte) 0x0a, (byte) 0x0e, (byte) 0x11, (byte) 0x11, (byte) 0x1f, (byte) 0x11, (byte) 0x0e, (byte) 0x1f, (byte) 0x15, (byte) 0x15, (byte) 0x1f, (byte) 0x05, (byte) 0x05, (byte) 0x0e, (byte) 0x15, (byte) 0x1d, (byte) 0x1f, (byte) 0x04, (byte) 0x1f, (byte) 0x11, (byte) 0x1f, (byte) 0x11, (byte) 0x08, (byte) 0x11, (byte) 0x0f, (byte) 0x1f, (byte) 0x0a, (byte) 0x11, (byte) 0x1f, (byte) 0x10, (byte) 0x10, (byte) 0x1f, (byte) 0x02, (byte) 0x1f, (byte) 0x1f, (byte) 0x01, (byte) 0x1f, (byte) 0x0e, (byte) 0x11, (byte) 0x0e, (byte) 0x1f, (byte) 0x05, (byte) 0x07, (byte) 0x0f, (byte) 0x19, (byte) 0x17, (byte) 0x1f, (byte) 0x0d, (byte) 0x17, (byte) 0x12, (byte) 0x15, (byte) 0x09, (byte) 0x01, (byte) 0x1f, (byte) 0x01, (byte) 0x1f, (byte) 0x10, (byte) 0x1f, (byte) 0x0f, (byte) 0x10, (byte) 0x0f, (byte) 0x1f, (byte) 0x08, (byte) 0x1f, (byte) 0x1b, (byte) 0x04, (byte) 0x1b, (byte) 0x03, (byte) 0x1c, (byte) 0x03, (byte) 0x19, (byte) 0x15, (byte) 0x13, (byte) 0x1f, (byte) 0x11, (byte) 0x00, (byte) 0x03, (byte) 0x04, (byte) 0x18, (byte) 0x00, (byte) 0x11, (byte) 0x1f, (byte) 0x02, (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x04, (byte) 0x04, (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x1e, (byte) 0x09, (byte) 0x1e, (byte) 0x1f, (byte) 0x15, (byte) 0x0a, (byte) 0x0e, (byte) 0x11, (byte) 0x11, (byte) 0x1f, (byte) 0x11, (byte) 0x0e, (byte) 0x1f, (byte) 0x15, (byte) 0x15, (byte) 0x1f, (byte) 0x05, (byte) 0x05, (byte) 0x0e, (byte) 0x15, (byte) 0x1d, (byte) 0x1f, (byte) 0x04, (byte) 0x1f, (byte) 0x11, (byte) 0x1f, (byte) 0x11, (byte) 0x08, (byte) 0x11, (byte) 0x0f, (byte) 0x1f, (byte) 0x0a, (byte) 0x11, (byte) 0x1f, (byte) 0x10, (byte) 0x10, (byte) 0x1f, (byte) 0x02, (byte) 0x1f, (byte) 0x1f, (byte) 0x01, (byte) 0x1f, (byte) 0x0e, (byte) 0x11, (byte) 0x0e, (byte) 0x1f, (byte) 0x05, (byte) 0x07, (byte) 0x0f, (byte) 0x19, (byte) 0x17, (byte) 0x1f, (byte) 0x0d, (byte) 0x17, (byte) 0x12, (byte) 0x15, (byte) 0x09, (byte) 0x01, (byte) 0x1f, (byte) 0x01, (byte) 0x1f, (byte) 0x10, (byte) 0x1f, (byte) 0x0f, (byte) 0x10, (byte) 0x0f, (byte) 0x1f, (byte) 0x08, (byte) 0x1f, (byte) 0x1b, (byte) 0x04, (byte) 0x1b, (byte) 0x03, (byte) 0x1c, (byte) 0x03, (byte) 0x19, (byte) 0x15, (byte) 0x13, (byte) 0x04, (byte) 0x1f, (byte) 0x11, (byte) 0x00, (byte) 0x1f, (byte) 0x00, (byte) 0x11, (byte) 0x1f, (byte) 0x04, (byte) 0x02, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, },
    4, 5, 5, 3, 128, 0);
    }
    }*/
    // The folowing classes contain the glyph bitmaps for the additonal fonts.
    // They are wrapped inside of classes to allow the linker to eliminate the
    // array initialization if the font is not used.
    /**
     * Small system font (4x6)
     */
    static class SmallFont extends Font
    {

        SmallFont()
        {
            super(
                    new byte[]
                    {
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x17, (byte) 0x00, (byte) 0x03, (byte) 0x00,
                        (byte) 0x03, (byte) 0x1f, (byte) 0x0a, (byte) 0x1f,
                        (byte) 0x14, (byte) 0x3f, (byte) 0x0a, (byte) 0x09,
                        (byte) 0x04, (byte) 0x12, (byte) 0x1a, (byte) 0x15,
                        (byte) 0x1e, (byte) 0x03, (byte) 0x01, (byte) 0x00,
                        (byte) 0x0e, (byte) 0x11, (byte) 0x00, (byte) 0x11,
                        (byte) 0x0e, (byte) 0x00, (byte) 0x15, (byte) 0x0e,
                        (byte) 0x15, (byte) 0x04, (byte) 0x0e, (byte) 0x04,
                        (byte) 0x30, (byte) 0x10, (byte) 0x00, (byte) 0x04,
                        (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x10,
                        (byte) 0x00, (byte) 0x18, (byte) 0x04, (byte) 0x03,
                        (byte) 0x1e, (byte) 0x11, (byte) 0x0f, (byte) 0x12,
                        (byte) 0x1f, (byte) 0x10, (byte) 0x19, (byte) 0x15,
                        (byte) 0x12, (byte) 0x11, (byte) 0x15, (byte) 0x0a,
                        (byte) 0x06, (byte) 0x04, (byte) 0x1f, (byte) 0x17,
                        (byte) 0x15, (byte) 0x09, (byte) 0x0e, (byte) 0x15,
                        (byte) 0x08, (byte) 0x01, (byte) 0x1d, (byte) 0x07,
                        (byte) 0x0a, (byte) 0x15, (byte) 0x0a, (byte) 0x02,
                        (byte) 0x15, (byte) 0x0e, (byte) 0x00, (byte) 0x14,
                        (byte) 0x00, (byte) 0x30, (byte) 0x14, (byte) 0x00,
                        (byte) 0x04, (byte) 0x0a, (byte) 0x11, (byte) 0x14,
                        (byte) 0x14, (byte) 0x14, (byte) 0x11, (byte) 0x0a,
                        (byte) 0x04, (byte) 0x01, (byte) 0x15, (byte) 0x02,
                        (byte) 0x1f, (byte) 0x11, (byte) 0x17, (byte) 0x1e,
                        (byte) 0x05, (byte) 0x1e, (byte) 0x1f, (byte) 0x15,
                        (byte) 0x0a, (byte) 0x0e, (byte) 0x11, (byte) 0x11,
                        (byte) 0x1f, (byte) 0x11, (byte) 0x0e, (byte) 0x1f,
                        (byte) 0x15, (byte) 0x11, (byte) 0x1f, (byte) 0x05,
                        (byte) 0x01, (byte) 0x0e, (byte) 0x11, (byte) 0x1d,
                        (byte) 0x1f, (byte) 0x04, (byte) 0x1f, (byte) 0x11,
                        (byte) 0x1f, (byte) 0x11, (byte) 0x08, (byte) 0x10,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x04, (byte) 0x1b,
                        (byte) 0x1f, (byte) 0x10, (byte) 0x10, (byte) 0x1f,
                        (byte) 0x06, (byte) 0x1f, (byte) 0x1f, (byte) 0x02,
                        (byte) 0x1f, (byte) 0x0e, (byte) 0x11, (byte) 0x0e,
                        (byte) 0x1f, (byte) 0x05, (byte) 0x02, (byte) 0x0e,
                        (byte) 0x19, (byte) 0x1e, (byte) 0x1f, (byte) 0x05,
                        (byte) 0x1a, (byte) 0x16, (byte) 0x15, (byte) 0x0d,
                        (byte) 0x01, (byte) 0x1f, (byte) 0x01, (byte) 0x1f,
                        (byte) 0x10, (byte) 0x1f, (byte) 0x0f, (byte) 0x10,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x0c, (byte) 0x1f,
                        (byte) 0x1b, (byte) 0x04, (byte) 0x1b, (byte) 0x03,
                        (byte) 0x1c, (byte) 0x03, (byte) 0x19, (byte) 0x15,
                        (byte) 0x13, (byte) 0x1f, (byte) 0x11, (byte) 0x00,
                        (byte) 0x03, (byte) 0x04, (byte) 0x18, (byte) 0x11,
                        (byte) 0x1f, (byte) 0x00, (byte) 0x02, (byte) 0x01,
                        (byte) 0x02, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                        (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x18,
                        (byte) 0x14, (byte) 0x1c, (byte) 0x1f, (byte) 0x14,
                        (byte) 0x08, (byte) 0x08, (byte) 0x14, (byte) 0x14,
                        (byte) 0x08, (byte) 0x14, (byte) 0x1f, (byte) 0x0c,
                        (byte) 0x1c, (byte) 0x14, (byte) 0x04, (byte) 0x1e,
                        (byte) 0x05, (byte) 0x2c, (byte) 0x24, (byte) 0x3c,
                        (byte) 0x1f, (byte) 0x04, (byte) 0x18, (byte) 0x00,
                        (byte) 0x1d, (byte) 0x00, (byte) 0x20, (byte) 0x3d,
                        (byte) 0x00, (byte) 0x1f, (byte) 0x08, (byte) 0x14,
                        (byte) 0x00, (byte) 0x1f, (byte) 0x00, (byte) 0x1c,
                        (byte) 0x0c, (byte) 0x1c, (byte) 0x1c, (byte) 0x04,
                        (byte) 0x18, (byte) 0x08, (byte) 0x14, (byte) 0x08,
                        (byte) 0x3c, (byte) 0x14, (byte) 0x08, (byte) 0x08,
                        (byte) 0x14, (byte) 0x3c, (byte) 0x1c, (byte) 0x04,
                        (byte) 0x00, (byte) 0x10, (byte) 0x1c, (byte) 0x04,
                        (byte) 0x04, (byte) 0x1e, (byte) 0x14, (byte) 0x1c,
                        (byte) 0x10, (byte) 0x1c, (byte) 0x0c, (byte) 0x10,
                        (byte) 0x0c, (byte) 0x1c, (byte) 0x18, (byte) 0x1c,
                        (byte) 0x14, (byte) 0x08, (byte) 0x14, (byte) 0x2c,
                        (byte) 0x10, (byte) 0x0c, (byte) 0x04, (byte) 0x1c,
                        (byte) 0x10, (byte) 0x04, (byte) 0x1f, (byte) 0x11,
                        (byte) 0x00, (byte) 0x1f, (byte) 0x00, (byte) 0x11,
                        (byte) 0x1f, (byte) 0x04, (byte) 0x01, (byte) 0x02,
                        (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    },
                    4, 6, 5, 3, 96, 32);
        }
    }

    /**
     * Medium font 6x8
     * This is the same size as the system font, so we use that
     */
    static class MediumFont extends Font
    {

        MediumFont()
        {
            super(
                    new byte[]
                    {
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x5f,
                        (byte) 0x06, (byte) 0x00, (byte) 0x07, (byte) 0x03,
                        (byte) 0x00, (byte) 0x07, (byte) 0x03, (byte) 0x24,
                        (byte) 0x7e, (byte) 0x24, (byte) 0x7e, (byte) 0x24,
                        (byte) 0x24, (byte) 0x2b, (byte) 0x6a, (byte) 0x12,
                        (byte) 0x00, (byte) 0x63, (byte) 0x13, (byte) 0x08,
                        (byte) 0x64, (byte) 0x63, (byte) 0x36, (byte) 0x49,
                        (byte) 0x56, (byte) 0x20, (byte) 0x50, (byte) 0x00,
                        (byte) 0x07, (byte) 0x03, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x3e, (byte) 0x41, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x41, (byte) 0x3e,
                        (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x3e,
                        (byte) 0x1c, (byte) 0x3e, (byte) 0x08, (byte) 0x08,
                        (byte) 0x08, (byte) 0x3e, (byte) 0x08, (byte) 0x08,
                        (byte) 0x00, (byte) 0xe0, (byte) 0x60, (byte) 0x00,
                        (byte) 0x00, (byte) 0x08, (byte) 0x08, (byte) 0x08,
                        (byte) 0x08, (byte) 0x08, (byte) 0x00, (byte) 0x60,
                        (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x20,
                        (byte) 0x10, (byte) 0x08, (byte) 0x04, (byte) 0x02,
                        (byte) 0x3e, (byte) 0x51, (byte) 0x49, (byte) 0x45,
                        (byte) 0x3e, (byte) 0x00, (byte) 0x42, (byte) 0x7f,
                        (byte) 0x40, (byte) 0x00, (byte) 0x62, (byte) 0x51,
                        (byte) 0x49, (byte) 0x49, (byte) 0x46, (byte) 0x22,
                        (byte) 0x49, (byte) 0x49, (byte) 0x49, (byte) 0x36,
                        (byte) 0x18, (byte) 0x14, (byte) 0x12, (byte) 0x7f,
                        (byte) 0x10, (byte) 0x2f, (byte) 0x49, (byte) 0x49,
                        (byte) 0x49, (byte) 0x31, (byte) 0x3c, (byte) 0x4a,
                        (byte) 0x49, (byte) 0x49, (byte) 0x30, (byte) 0x01,
                        (byte) 0x71, (byte) 0x09, (byte) 0x05, (byte) 0x03,
                        (byte) 0x36, (byte) 0x49, (byte) 0x49, (byte) 0x49,
                        (byte) 0x36, (byte) 0x06, (byte) 0x49, (byte) 0x49,
                        (byte) 0x29, (byte) 0x1e, (byte) 0x00, (byte) 0x6c,
                        (byte) 0x6c, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0xec, (byte) 0x6c, (byte) 0x00, (byte) 0x00,
                        (byte) 0x08, (byte) 0x14, (byte) 0x22, (byte) 0x41,
                        (byte) 0x00, (byte) 0x24, (byte) 0x24, (byte) 0x24,
                        (byte) 0x24, (byte) 0x24, (byte) 0x00, (byte) 0x41,
                        (byte) 0x22, (byte) 0x14, (byte) 0x08, (byte) 0x02,
                        (byte) 0x01, (byte) 0x59, (byte) 0x09, (byte) 0x06,
                        (byte) 0x3e, (byte) 0x41, (byte) 0x5d, (byte) 0x55,
                        (byte) 0x1e, (byte) 0x7e, (byte) 0x11, (byte) 0x11,
                        (byte) 0x11, (byte) 0x7e, (byte) 0x7f, (byte) 0x49,
                        (byte) 0x49, (byte) 0x49, (byte) 0x36, (byte) 0x3e,
                        (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x22,
                        (byte) 0x7f, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                        (byte) 0x3e, (byte) 0x7f, (byte) 0x49, (byte) 0x49,
                        (byte) 0x49, (byte) 0x41, (byte) 0x7f, (byte) 0x09,
                        (byte) 0x09, (byte) 0x09, (byte) 0x01, (byte) 0x3e,
                        (byte) 0x41, (byte) 0x49, (byte) 0x49, (byte) 0x7a,
                        (byte) 0x7f, (byte) 0x08, (byte) 0x08, (byte) 0x08,
                        (byte) 0x7f, (byte) 0x00, (byte) 0x41, (byte) 0x7f,
                        (byte) 0x41, (byte) 0x00, (byte) 0x30, (byte) 0x40,
                        (byte) 0x40, (byte) 0x40, (byte) 0x3f, (byte) 0x7f,
                        (byte) 0x08, (byte) 0x14, (byte) 0x22, (byte) 0x41,
                        (byte) 0x7f, (byte) 0x40, (byte) 0x40, (byte) 0x40,
                        (byte) 0x40, (byte) 0x7f, (byte) 0x02, (byte) 0x04,
                        (byte) 0x02, (byte) 0x7f, (byte) 0x7f, (byte) 0x02,
                        (byte) 0x04, (byte) 0x08, (byte) 0x7f, (byte) 0x3e,
                        (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x3e,
                        (byte) 0x7f, (byte) 0x09, (byte) 0x09, (byte) 0x09,
                        (byte) 0x06, (byte) 0x3e, (byte) 0x41, (byte) 0x51,
                        (byte) 0x21, (byte) 0x5e, (byte) 0x7f, (byte) 0x09,
                        (byte) 0x09, (byte) 0x19, (byte) 0x66, (byte) 0x26,
                        (byte) 0x49, (byte) 0x49, (byte) 0x49, (byte) 0x32,
                        (byte) 0x01, (byte) 0x01, (byte) 0x7f, (byte) 0x01,
                        (byte) 0x01, (byte) 0x3f, (byte) 0x40, (byte) 0x40,
                        (byte) 0x40, (byte) 0x3f, (byte) 0x1f, (byte) 0x20,
                        (byte) 0x40, (byte) 0x20, (byte) 0x1f, (byte) 0x3f,
                        (byte) 0x40, (byte) 0x3c, (byte) 0x40, (byte) 0x3f,
                        (byte) 0x63, (byte) 0x14, (byte) 0x08, (byte) 0x14,
                        (byte) 0x63, (byte) 0x07, (byte) 0x08, (byte) 0x70,
                        (byte) 0x08, (byte) 0x07, (byte) 0x71, (byte) 0x49,
                        (byte) 0x45, (byte) 0x43, (byte) 0x00, (byte) 0x00,
                        (byte) 0x7f, (byte) 0x41, (byte) 0x41, (byte) 0x00,
                        (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10,
                        (byte) 0x20, (byte) 0x00, (byte) 0x41, (byte) 0x41,
                        (byte) 0x7f, (byte) 0x00, (byte) 0x04, (byte) 0x02,
                        (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x80,
                        (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                        (byte) 0x00, (byte) 0x03, (byte) 0x07, (byte) 0x00,
                        (byte) 0x00, (byte) 0x20, (byte) 0x54, (byte) 0x54,
                        (byte) 0x54, (byte) 0x78, (byte) 0x7f, (byte) 0x44,
                        (byte) 0x44, (byte) 0x44, (byte) 0x38, (byte) 0x38,
                        (byte) 0x44, (byte) 0x44, (byte) 0x44, (byte) 0x28,
                        (byte) 0x38, (byte) 0x44, (byte) 0x44, (byte) 0x44,
                        (byte) 0x7f, (byte) 0x38, (byte) 0x54, (byte) 0x54,
                        (byte) 0x54, (byte) 0x08, (byte) 0x08, (byte) 0x7e,
                        (byte) 0x09, (byte) 0x09, (byte) 0x00, (byte) 0x18,
                        (byte) 0xa4, (byte) 0xa4, (byte) 0xa4, (byte) 0x7c,
                        (byte) 0x7f, (byte) 0x04, (byte) 0x04, (byte) 0x78,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x7d,
                        (byte) 0x40, (byte) 0x00, (byte) 0x40, (byte) 0x80,
                        (byte) 0x84, (byte) 0x7d, (byte) 0x00, (byte) 0x7f,
                        (byte) 0x10, (byte) 0x28, (byte) 0x44, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x7f, (byte) 0x40,
                        (byte) 0x00, (byte) 0x7c, (byte) 0x04, (byte) 0x18,
                        (byte) 0x04, (byte) 0x78, (byte) 0x7c, (byte) 0x04,
                        (byte) 0x04, (byte) 0x78, (byte) 0x00, (byte) 0x38,
                        (byte) 0x44, (byte) 0x44, (byte) 0x44, (byte) 0x38,
                        (byte) 0xfc, (byte) 0x44, (byte) 0x44, (byte) 0x44,
                        (byte) 0x38, (byte) 0x38, (byte) 0x44, (byte) 0x44,
                        (byte) 0x44, (byte) 0xfc, (byte) 0x44, (byte) 0x78,
                        (byte) 0x44, (byte) 0x04, (byte) 0x08, (byte) 0x08,
                        (byte) 0x54, (byte) 0x54, (byte) 0x54, (byte) 0x20,
                        (byte) 0x04, (byte) 0x3e, (byte) 0x44, (byte) 0x24,
                        (byte) 0x00, (byte) 0x3c, (byte) 0x40, (byte) 0x20,
                        (byte) 0x7c, (byte) 0x00, (byte) 0x1c, (byte) 0x20,
                        (byte) 0x40, (byte) 0x20, (byte) 0x1c, (byte) 0x3c,
                        (byte) 0x60, (byte) 0x30, (byte) 0x60, (byte) 0x3c,
                        (byte) 0x6c, (byte) 0x10, (byte) 0x10, (byte) 0x6c,
                        (byte) 0x00, (byte) 0x9c, (byte) 0xa0, (byte) 0x60,
                        (byte) 0x3c, (byte) 0x00, (byte) 0x64, (byte) 0x54,
                        (byte) 0x54, (byte) 0x4c, (byte) 0x00, (byte) 0x08,
                        (byte) 0x3e, (byte) 0x41, (byte) 0x41, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x77, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x41, (byte) 0x41,
                        (byte) 0x3e, (byte) 0x08, (byte) 0x02, (byte) 0x01,
                        (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x3c,
                        (byte) 0x26, (byte) 0x23, (byte) 0x26, (byte) 0x3c,
                    },
                    6, 8, 7, 5, 96, 32);
        }
    }

    /**
     * Large font 10x16
     */
    static class LargeFont extends Font
    {

        LargeFont()
        {
            super(
                    new byte[]
                    {
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x3e, (byte) 0xff,
                        (byte) 0xff, (byte) 0x3e, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x07, (byte) 0x07, (byte) 0x00,
                        (byte) 0x00, (byte) 0x07, (byte) 0x07, (byte) 0x00,
                        (byte) 0x20, (byte) 0xfe, (byte) 0xfe, (byte) 0x20,
                        (byte) 0x20, (byte) 0xfe, (byte) 0xfe, (byte) 0x20,
                        (byte) 0x38, (byte) 0x3c, (byte) 0x64, (byte) 0xff,
                        (byte) 0xff, (byte) 0x84, (byte) 0x1c, (byte) 0x18,
                        (byte) 0x1e, (byte) 0x33, (byte) 0x1e, (byte) 0xc0,
                        (byte) 0x70, (byte) 0x1c, (byte) 0x07, (byte) 0x01,
                        (byte) 0x1e, (byte) 0xb3, (byte) 0xe1, (byte) 0xe1,
                        (byte) 0xb3, (byte) 0x1e, (byte) 0x00, (byte) 0x80,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0b,
                        (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0xf8, (byte) 0xff,
                        (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x07,
                        (byte) 0xff, (byte) 0xf8, (byte) 0x00, (byte) 0x00,
                        (byte) 0x80, (byte) 0xa0, (byte) 0xe0, (byte) 0xc0,
                        (byte) 0xc0, (byte) 0xe0, (byte) 0xa0, (byte) 0x80,
                        (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xe0,
                        (byte) 0xe0, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                        (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xc0,
                        (byte) 0xf0, (byte) 0x3c, (byte) 0x0f, (byte) 0x03,
                        (byte) 0xfc, (byte) 0xfe, (byte) 0x03, (byte) 0x81,
                        (byte) 0x61, (byte) 0x1b, (byte) 0xfe, (byte) 0xfc,
                        (byte) 0x04, (byte) 0x04, (byte) 0x06, (byte) 0xff,
                        (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x0c, (byte) 0x0e, (byte) 0x03, (byte) 0x01,
                        (byte) 0x81, (byte) 0xc3, (byte) 0x7e, (byte) 0x3c,
                        (byte) 0x0c, (byte) 0x0e, (byte) 0x43, (byte) 0x41,
                        (byte) 0x41, (byte) 0x43, (byte) 0xfe, (byte) 0xbc,
                        (byte) 0x00, (byte) 0xe0, (byte) 0xfc, (byte) 0x1f,
                        (byte) 0x83, (byte) 0x80, (byte) 0x00, (byte) 0x00,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x21, (byte) 0x21,
                        (byte) 0x21, (byte) 0x61, (byte) 0xc1, (byte) 0x81,
                        (byte) 0xe0, (byte) 0xf8, (byte) 0x5c, (byte) 0x46,
                        (byte) 0x43, (byte) 0xc1, (byte) 0x81, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x81, (byte) 0xf1, (byte) 0x7f, (byte) 0x0f,
                        (byte) 0x1c, (byte) 0xbe, (byte) 0xe3, (byte) 0x41,
                        (byte) 0x41, (byte) 0xe3, (byte) 0xbe, (byte) 0x1c,
                        (byte) 0x3c, (byte) 0x7e, (byte) 0xc3, (byte) 0x81,
                        (byte) 0x81, (byte) 0x83, (byte) 0xfe, (byte) 0xfc,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x60,
                        (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x60,
                        (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x80, (byte) 0xc0, (byte) 0x60, (byte) 0x30,
                        (byte) 0x18, (byte) 0x0c, (byte) 0x04, (byte) 0x00,
                        (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                        (byte) 0x04, (byte) 0x0c, (byte) 0x18, (byte) 0x30,
                        (byte) 0x60, (byte) 0xc0, (byte) 0x80, (byte) 0x00,
                        (byte) 0x06, (byte) 0x07, (byte) 0x03, (byte) 0x01,
                        (byte) 0xc1, (byte) 0xf3, (byte) 0x3f, (byte) 0x1e,
                        (byte) 0xfc, (byte) 0xfe, (byte) 0x03, (byte) 0xe1,
                        (byte) 0xf1, (byte) 0x11, (byte) 0x13, (byte) 0xfe,
                        (byte) 0xfc, (byte) 0xfe, (byte) 0x03, (byte) 0x01,
                        (byte) 0x01, (byte) 0x03, (byte) 0xfe, (byte) 0xfc,
                        (byte) 0xff, (byte) 0xff, (byte) 0x41, (byte) 0x41,
                        (byte) 0x41, (byte) 0xe3, (byte) 0xbe, (byte) 0x1c,
                        (byte) 0xfc, (byte) 0xfe, (byte) 0x03, (byte) 0x01,
                        (byte) 0x01, (byte) 0x03, (byte) 0x0e, (byte) 0x0c,
                        (byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x07, (byte) 0xfe, (byte) 0xf8,
                        (byte) 0xff, (byte) 0xff, (byte) 0x41, (byte) 0x41,
                        (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x01,
                        (byte) 0xff, (byte) 0xff, (byte) 0x81, (byte) 0x81,
                        (byte) 0x81, (byte) 0x81, (byte) 0x81, (byte) 0x01,
                        (byte) 0xfc, (byte) 0xfe, (byte) 0x03, (byte) 0x01,
                        (byte) 0x81, (byte) 0x83, (byte) 0x8e, (byte) 0x8c,
                        (byte) 0xff, (byte) 0xff, (byte) 0x40, (byte) 0x40,
                        (byte) 0x40, (byte) 0x40, (byte) 0xff, (byte) 0xff,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0xff,
                        (byte) 0xff, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xff,
                        (byte) 0xff, (byte) 0xff, (byte) 0xc0, (byte) 0xe0,
                        (byte) 0x38, (byte) 0x1c, (byte) 0x0f, (byte) 0x03,
                        (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0xff, (byte) 0xff, (byte) 0x78, (byte) 0xc0,
                        (byte) 0xc0, (byte) 0x78, (byte) 0xff, (byte) 0xff,
                        (byte) 0xff, (byte) 0xff, (byte) 0x1c, (byte) 0x70,
                        (byte) 0xc0, (byte) 0x00, (byte) 0xff, (byte) 0xff,
                        (byte) 0xfc, (byte) 0xfe, (byte) 0x03, (byte) 0x01,
                        (byte) 0x01, (byte) 0x03, (byte) 0xfe, (byte) 0xfc,
                        (byte) 0xff, (byte) 0xff, (byte) 0x81, (byte) 0x81,
                        (byte) 0x81, (byte) 0xc3, (byte) 0x7e, (byte) 0x3c,
                        (byte) 0xfc, (byte) 0xfe, (byte) 0x03, (byte) 0x01,
                        (byte) 0x01, (byte) 0x03, (byte) 0xfe, (byte) 0xfc,
                        (byte) 0xff, (byte) 0xff, (byte) 0x81, (byte) 0x81,
                        (byte) 0x81, (byte) 0xc3, (byte) 0x7e, (byte) 0x3c,
                        (byte) 0x3c, (byte) 0x7e, (byte) 0x63, (byte) 0xe1,
                        (byte) 0xc1, (byte) 0x83, (byte) 0x8e, (byte) 0x0c,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0xff,
                        (byte) 0xff, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xff,
                        (byte) 0x3f, (byte) 0xff, (byte) 0xc0, (byte) 0x00,
                        (byte) 0x00, (byte) 0xc0, (byte) 0xff, (byte) 0x3f,
                        (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0xc0,
                        (byte) 0xc0, (byte) 0x00, (byte) 0xff, (byte) 0xff,
                        (byte) 0x0f, (byte) 0x3f, (byte) 0xf0, (byte) 0xc0,
                        (byte) 0xc0, (byte) 0xf0, (byte) 0x3f, (byte) 0x0f,
                        (byte) 0x3f, (byte) 0xff, (byte) 0xc0, (byte) 0x80,
                        (byte) 0x80, (byte) 0xc0, (byte) 0xff, (byte) 0x3f,
                        (byte) 0x01, (byte) 0x01, (byte) 0x81, (byte) 0xc1,
                        (byte) 0x61, (byte) 0x39, (byte) 0x1f, (byte) 0x07,
                        (byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xff,
                        (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                        (byte) 0x03, (byte) 0x0f, (byte) 0x3c, (byte) 0xf0,
                        (byte) 0xc0, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01,
                        (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x00,
                        (byte) 0x04, (byte) 0x04, (byte) 0x06, (byte) 0x03,
                        (byte) 0x03, (byte) 0x06, (byte) 0x06, (byte) 0x04,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x07,
                        (byte) 0x0b, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x40, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0x20, (byte) 0xe0, (byte) 0xc0,
                        (byte) 0xff, (byte) 0xff, (byte) 0x40, (byte) 0x20,
                        (byte) 0x20, (byte) 0x60, (byte) 0xc0, (byte) 0x80,
                        (byte) 0x80, (byte) 0xc0, (byte) 0x60, (byte) 0x20,
                        (byte) 0x20, (byte) 0x20, (byte) 0x60, (byte) 0x40,
                        (byte) 0x80, (byte) 0xc0, (byte) 0x60, (byte) 0x20,
                        (byte) 0x20, (byte) 0x40, (byte) 0xff, (byte) 0xff,
                        (byte) 0x80, (byte) 0xc0, (byte) 0x60, (byte) 0x20,
                        (byte) 0x20, (byte) 0x60, (byte) 0xc0, (byte) 0x80,
                        (byte) 0x20, (byte) 0x20, (byte) 0xfe, (byte) 0xff,
                        (byte) 0x21, (byte) 0x21, (byte) 0x23, (byte) 0x02,
                        (byte) 0x80, (byte) 0xc0, (byte) 0x60, (byte) 0x20,
                        (byte) 0x20, (byte) 0x40, (byte) 0xe0, (byte) 0xe0,
                        (byte) 0xff, (byte) 0xff, (byte) 0x40, (byte) 0x20,
                        (byte) 0x20, (byte) 0x60, (byte) 0xc0, (byte) 0x80,
                        (byte) 0x00, (byte) 0x20, (byte) 0x20, (byte) 0xe3,
                        (byte) 0xe3, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x20,
                        (byte) 0xe3, (byte) 0xe3, (byte) 0x00, (byte) 0x00,
                        (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x00,
                        (byte) 0x80, (byte) 0xc0, (byte) 0x60, (byte) 0x20,
                        (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0xff,
                        (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0xe0, (byte) 0xe0, (byte) 0x60, (byte) 0xc0,
                        (byte) 0xc0, (byte) 0x60, (byte) 0xe0, (byte) 0xc0,
                        (byte) 0xe0, (byte) 0xe0, (byte) 0x40, (byte) 0x20,
                        (byte) 0x20, (byte) 0x60, (byte) 0xc0, (byte) 0x80,
                        (byte) 0x80, (byte) 0xc0, (byte) 0x60, (byte) 0x20,
                        (byte) 0x20, (byte) 0x60, (byte) 0xc0, (byte) 0x80,
                        (byte) 0xe0, (byte) 0xe0, (byte) 0x40, (byte) 0x20,
                        (byte) 0x20, (byte) 0x60, (byte) 0xc0, (byte) 0x80,
                        (byte) 0x80, (byte) 0xc0, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0x40, (byte) 0xe0, (byte) 0xe0,
                        (byte) 0xe0, (byte) 0xe0, (byte) 0xc0, (byte) 0x40,
                        (byte) 0x60, (byte) 0x60, (byte) 0x60, (byte) 0x40,
                        (byte) 0xc0, (byte) 0xe0, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0x20, (byte) 0x60, (byte) 0x40,
                        (byte) 0x00, (byte) 0x20, (byte) 0x20, (byte) 0xfe,
                        (byte) 0xfe, (byte) 0x20, (byte) 0x20, (byte) 0x00,
                        (byte) 0xe0, (byte) 0xe0, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0xe0, (byte) 0xe0,
                        (byte) 0xe0, (byte) 0xe0, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0xe0, (byte) 0xe0,
                        (byte) 0xe0, (byte) 0xe0, (byte) 0x00, (byte) 0xc0,
                        (byte) 0xc0, (byte) 0x00, (byte) 0xe0, (byte) 0xe0,
                        (byte) 0x60, (byte) 0xe0, (byte) 0x80, (byte) 0x00,
                        (byte) 0x00, (byte) 0x80, (byte) 0xe0, (byte) 0x60,
                        (byte) 0xe0, (byte) 0xe0, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0xe0, (byte) 0xe0,
                        (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0xa0, (byte) 0xe0, (byte) 0x60,
                        (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0xfe,
                        (byte) 0x7f, (byte) 0x01, (byte) 0x01, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff,
                        (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x7f,
                        (byte) 0xfe, (byte) 0x80, (byte) 0x00, (byte) 0x00,
                        (byte) 0x02, (byte) 0x02, (byte) 0x01, (byte) 0x01,
                        (byte) 0x02, (byte) 0x02, (byte) 0x01, (byte) 0x01,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x33,
                        (byte) 0x33, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x01, (byte) 0x1f, (byte) 0x1f, (byte) 0x01,
                        (byte) 0x01, (byte) 0x1f, (byte) 0x1f, (byte) 0x01,
                        (byte) 0x06, (byte) 0x0e, (byte) 0x08, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x09, (byte) 0x0f, (byte) 0x07,
                        (byte) 0x30, (byte) 0x1c, (byte) 0x07, (byte) 0x01,
                        (byte) 0x00, (byte) 0x1e, (byte) 0x33, (byte) 0x1e,
                        (byte) 0x1f, (byte) 0x31, (byte) 0x20, (byte) 0x20,
                        (byte) 0x31, (byte) 0x1f, (byte) 0x1b, (byte) 0x31,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x07, (byte) 0x3f,
                        (byte) 0x78, (byte) 0x40, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x78,
                        (byte) 0x3f, (byte) 0x07, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x02, (byte) 0x03, (byte) 0x01,
                        (byte) 0x01, (byte) 0x03, (byte) 0x02, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
                        (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xb0,
                        (byte) 0x70, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x30,
                        (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x30, (byte) 0x3c, (byte) 0x0f, (byte) 0x03,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x36, (byte) 0x21,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                        (byte) 0x38, (byte) 0x3c, (byte) 0x26, (byte) 0x23,
                        (byte) 0x21, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                        (byte) 0x0c, (byte) 0x1c, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x0f, (byte) 0x0f, (byte) 0x08, (byte) 0x08,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x08, (byte) 0x08,
                        (byte) 0x0c, (byte) 0x1c, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x3c,
                        (byte) 0x3f, (byte) 0x03, (byte) 0x00, (byte) 0x00,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x30,
                        (byte) 0x18, (byte) 0x0e, (byte) 0x07, (byte) 0x01,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x30,
                        (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xb0,
                        (byte) 0x70, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x01, (byte) 0x03, (byte) 0x06,
                        (byte) 0x0c, (byte) 0x18, (byte) 0x10, (byte) 0x00,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x10, (byte) 0x18, (byte) 0x0c, (byte) 0x06,
                        (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x33,
                        (byte) 0x33, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x21,
                        (byte) 0x23, (byte) 0x22, (byte) 0x21, (byte) 0x33,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1c, (byte) 0x0c,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0x38, (byte) 0x1f, (byte) 0x07,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                        (byte) 0x0c, (byte) 0x1c, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x01,
                        (byte) 0x07, (byte) 0x0e, (byte) 0x3c, (byte) 0x30,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x03,
                        (byte) 0x03, (byte) 0x00, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x00,
                        (byte) 0x01, (byte) 0x07, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x20,
                        (byte) 0x60, (byte) 0xf0, (byte) 0x9f, (byte) 0x0f,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x00,
                        (byte) 0x01, (byte) 0x1f, (byte) 0x3e, (byte) 0x20,
                        (byte) 0x0c, (byte) 0x1c, (byte) 0x30, (byte) 0x20,
                        (byte) 0x21, (byte) 0x31, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x00, (byte) 0x01, (byte) 0x0f, (byte) 0x3c,
                        (byte) 0x3c, (byte) 0x0f, (byte) 0x01, (byte) 0x00,
                        (byte) 0x01, (byte) 0x3f, (byte) 0x3e, (byte) 0x01,
                        (byte) 0x01, (byte) 0x3e, (byte) 0x3f, (byte) 0x01,
                        (byte) 0x3c, (byte) 0x3f, (byte) 0x03, (byte) 0x00,
                        (byte) 0x00, (byte) 0x03, (byte) 0x3f, (byte) 0x3c,
                        (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                        (byte) 0x38, (byte) 0x3e, (byte) 0x27, (byte) 0x21,
                        (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                        (byte) 0x00, (byte) 0x00, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x20, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x03, (byte) 0x0f, (byte) 0x3c, (byte) 0x30,
                        (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x20,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                        (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x1e, (byte) 0x3f, (byte) 0x21, (byte) 0x21,
                        (byte) 0x21, (byte) 0x11, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x10, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x20, (byte) 0x30, (byte) 0x10,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x10, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x32, (byte) 0x22,
                        (byte) 0x22, (byte) 0x22, (byte) 0x33, (byte) 0x13,
                        (byte) 0x00, (byte) 0x00, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x47, (byte) 0x4f, (byte) 0xd8, (byte) 0x90,
                        (byte) 0x90, (byte) 0xc8, (byte) 0x7f, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x00, (byte) 0x20, (byte) 0x20, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x20, (byte) 0x20, (byte) 0x00,
                        (byte) 0x00, (byte) 0x40, (byte) 0xc0, (byte) 0x80,
                        (byte) 0xff, (byte) 0x7f, (byte) 0x00, (byte) 0x00,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x06, (byte) 0x07,
                        (byte) 0x0d, (byte) 0x18, (byte) 0x30, (byte) 0x20,
                        (byte) 0x00, (byte) 0x20, (byte) 0x20, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x20, (byte) 0x20, (byte) 0x00,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x1f,
                        (byte) 0x1f, (byte) 0x00, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x30, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0xff, (byte) 0xff, (byte) 0x10, (byte) 0x20,
                        (byte) 0x20, (byte) 0x30, (byte) 0x1f, (byte) 0x0f,
                        (byte) 0x0f, (byte) 0x1f, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0x10, (byte) 0xff, (byte) 0xff,
                        (byte) 0x3f, (byte) 0x3f, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x10, (byte) 0x31, (byte) 0x21, (byte) 0x23,
                        (byte) 0x26, (byte) 0x24, (byte) 0x3c, (byte) 0x18,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1f,
                        (byte) 0x3f, (byte) 0x20, (byte) 0x20, (byte) 0x00,
                        (byte) 0x1f, (byte) 0x3f, (byte) 0x20, (byte) 0x20,
                        (byte) 0x20, (byte) 0x10, (byte) 0x3f, (byte) 0x3f,
                        (byte) 0x03, (byte) 0x07, (byte) 0x1c, (byte) 0x30,
                        (byte) 0x30, (byte) 0x1c, (byte) 0x07, (byte) 0x03,
                        (byte) 0x07, (byte) 0x3f, (byte) 0x38, (byte) 0x07,
                        (byte) 0x07, (byte) 0x38, (byte) 0x3f, (byte) 0x07,
                        (byte) 0x30, (byte) 0x3d, (byte) 0x0d, (byte) 0x07,
                        (byte) 0x07, (byte) 0x0d, (byte) 0x3d, (byte) 0x30,
                        (byte) 0x83, (byte) 0x8f, (byte) 0xdc, (byte) 0x70,
                        (byte) 0x30, (byte) 0x1c, (byte) 0x0f, (byte) 0x03,
                        (byte) 0x30, (byte) 0x38, (byte) 0x2c, (byte) 0x26,
                        (byte) 0x23, (byte) 0x21, (byte) 0x20, (byte) 0x20,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x3f,
                        (byte) 0x7f, (byte) 0x40, (byte) 0x40, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff,
                        (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x40, (byte) 0x40, (byte) 0x7f,
                        (byte) 0x3f, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    },
                    10, 16, 14, 8, 96, 32);
        }
    }

    /**
     * Create a new font for the specified NXT format glyph map
     * @param glyphs the actual bytes of the glyph.
     * @param width The cell width.
     * @param height The cell height.
     * @param glyphWidth The width of the glyph bits.
     */
    Font(byte[] glyphs, int width, int height, int base, int glyphWidth, int count, int first)
    {
        this.glyphs = glyphs;
        this.width = width;
        this.height = height;
        this.base = base;
        this.glyphWidth = glyphWidth;
        this.glyphCount = count;
        this.firstChar = first;
    }

    /**
     * Return the system font.
     * @return current system font object
     */
    public static Font getDefaultFont()
    {
        return systemFont;
    }

    /**
     * return the height of the font in pixels.
     * @return pixel height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Return the base line position in pixels. This is the offset from the
     * top of the font.
     * @return base position.
     */
    public int getBaselinePosition()
    {
        return base;
    }

    /**
     * Return the width of the specified string in pixels
     * @param str
     * @return width of the string
     */
    public int stringWidth(String str)
    {
        return str.length() * width;
    }

    /**
     * Request a particular type and size of font. Currently only the size
     * parameter is used and it should be one of SIZE_SMALL, SIZE_MEDIUM or
     * SIZE_LARGE.
     * @param face
     * @param style
     * @param size
     * @return The requested Font
     */
    public static Font getFont(int face, int style, int size)
    {
        if (size == SIZE_SMALL)
            return getSmallFont();
        else if (size == SIZE_LARGE)
            return getLargeFont();
        else
            //return new MediumFont();
            return getDefaultFont();
    }

    // The following non-standard methods can be used to access the additional
    // fonts. Using them requires less memory than getFont.
    /**
     * Return the small font.
     * @return the small font
     */
    public static synchronized Font getSmallFont()
    {
        if (small == null)
            small = new SmallFont();
        return small;
    }

    /**
     * Return the large font.
     * @return the large font
     */
    public static synchronized Font getLargeFont()
    {
        if (large == null)
            large = new LargeFont();
        return large;
    }
}
