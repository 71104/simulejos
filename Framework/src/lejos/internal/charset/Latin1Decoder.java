package lejos.internal.charset;

public class Latin1Decoder implements CharsetDecoder
{
	public int decode(byte[] buf, int offset, int limit)
	{
		return buf[offset] & 0xFF;
	}

	public int estimateByteCount(byte[] buf, int offset, int limit)
	{
		return 1;
	}

	public int getMaxCharLength()
	{
		return 1;
	}
}
