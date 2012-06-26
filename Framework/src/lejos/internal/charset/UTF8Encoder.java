package lejos.internal.charset;

public class UTF8Encoder implements CharsetEncoder
{
	public int getMaxCharLength()
	{
		return 4;
	}
	
	public int encode(int codepoint, byte[] target, int offset)
	{
		if (codepoint < 0 || codepoint > 0x1FFFFF)
			target[offset++] = (byte)'?';
		else if (codepoint <= 0x7F)
			target[offset++] = (byte)codepoint;
		else if (codepoint <= 0x7FF)
		{
			target[offset++] = (byte)((codepoint >> 6) | 0xC0);
			target[offset++] = (byte)(codepoint & 0x3F | 0x80);
		}
		else if (codepoint <= 0xFFFF)
		{
			target[offset++] = (byte)((codepoint >> 12) | 0xE0);
			target[offset++] = (byte)((codepoint >> 6) & 0x3F | 0x80);
			target[offset++] = (byte)(codepoint & 0x3F | 0x80);
		}
		else
		{
			target[offset++] = (byte)((codepoint >> 18) | 0xF0);
			target[offset++] = (byte)((codepoint >> 12) & 0x3F | 0x80);
			target[offset++] = (byte)((codepoint >> 6) & 0x3F | 0x80);
			target[offset++] = (byte)(codepoint & 0x3F | 0x80);
		}
		return offset;
	}

	public int estimateByteCount(int codepoint)
	{
		if (codepoint <= 0x7F || codepoint > 0x1FFFFF)
			return 1;
		if (codepoint <= 0x7FF)
			return 2;
		if (codepoint <= 0xFFFF)
			return 3;
		
		return 4;
	}

}
