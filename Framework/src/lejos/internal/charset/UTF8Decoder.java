package lejos.internal.charset;

public class UTF8Decoder implements CharsetDecoder
{
	private static final int MIN_NON_ASCII = 0x80;
	private static final int MIN_UTF8_SEQ2 = 0xC0;
	private static final int MIN_UTF8_SEQ3 = 0xE0;
	private static final int MIN_UTF8_SEQ4 = 0xF0;
	private static final int MIN_UTF8_SEQ5 = 0xF8;

	public int decode(byte[] source, int offset, int limit)
	{
		// assert limit > offset;

		int first = source[offset] & 0xFF;
		if (first < MIN_NON_ASCII)
			return first;
		if (first < MIN_UTF8_SEQ2 || first >= MIN_UTF8_SEQ5)
			return '?';

		int len;
		if (first < MIN_UTF8_SEQ3)
			len = 2;
		else if (first < MIN_UTF8_SEQ4)
			len = 3;
		else
			len = 4;

		if (len > limit - offset)
			return '?';

		first &= 0x3F >> len;
		for (int i = 1; i < len; i++)
		{
			int b = source[offset + i];
			if ((b & 0xC0) != 0x80)
				return '?';
			first = (first << 6) | (b & 0x3F);
		}

		return first;
	}

	public int estimateByteCount(byte[] source, int offset, int limit)
	{
		if (offset >= limit)
			return 1;

		int first = source[offset] & 0xFF;
		if (first < MIN_UTF8_SEQ2 || first >= MIN_UTF8_SEQ5)
			return 1;

		int len;
		if (first < MIN_UTF8_SEQ3)
			len = 2;
		else if (first < MIN_UTF8_SEQ4)
			len = 3;
		else
			len = 4;

		// how many bytes must and can be tested?
		int maxlen = limit - offset;
		if (maxlen > len)
			maxlen = len;

		if (maxlen > 1 && (source[offset + 1] & 0xC0) != 0x80)
			return 1;
		if (maxlen > 2 && (source[offset + 2] & 0xC0) != 0x80)
			return 2;
		if (maxlen > 3 && (source[offset + 3] & 0xC0) != 0x80)
			return 3;

		// there's not enough in the buffer ...
		if (len > maxlen)
			// ... so return conservative estimate
			return maxlen + 1;

		return len;
	}

	public int getMaxCharLength()
	{
		return 4;
	}
}
