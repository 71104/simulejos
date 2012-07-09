package lejos.nxt;

import java.io.OutputStream;

/**
 * A simple output stream that implements console output.
 * It writes to the bottom line of the screen, scrolling the
 * LCD up one line when writing to character position 0, 
 * and starting a new line when the position reaches 16
 * or a new line character is written. 
 * 
 * Used by System.out.println.
 * 
 * @author Lawrie Griffiths
 *
 */
public class LCDOutputStream extends OutputStream {
	private int col = 0;
	private int line = 0;
	
	@Override
	public void write(int c) {
		char x = (char)(c & 0xFF);
		switch (x)
		{
			case '\t':
				col = col + 8 - col % 8; 
				break;
			case '\n': 
				incLine();
			case '\r':
				col = 0;
				break;
			default:
				if (col >= LCD.DISPLAY_CHAR_WIDTH)
				{
					col = 0;
					incLine();
				}
				LCD.drawChar(x, col++, line);
		}
		
	}

	private void incLine() {
		if (line < LCD.DISPLAY_CHAR_DEPTH - 1)
			line++;
		else
			LCD.scroll();
	}
}
