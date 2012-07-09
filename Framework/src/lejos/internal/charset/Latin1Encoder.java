package lejos.internal.charset;

public class Latin1Encoder implements CharsetEncoder
{
	public int getMaxCharLength()
	{
		return 1;
	}
	
	public int encode(int codepoint, byte[] target, int offset)
	{
		if (codepoint < 0 || codepoint > 0xFF)
			codepoint = '?';
		
		target[offset] = (byte)codepoint;		
		return offset + 1;
	}

	public int estimateByteCount(int codepoint)
	{
		return 1;
	}
}
