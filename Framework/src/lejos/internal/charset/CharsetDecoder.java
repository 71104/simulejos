package lejos.internal.charset;

public interface CharsetDecoder
{
	int getMaxCharLength();
	
	/**
	 * Calculates a Unicode codepoint from the bytes in the buffer.
	 * If the bytes in the buffer cannot be decoded, because they are
	 * malformed or because there is not enough data, then the '?' character
	 * should be returned.
	 * 
	 * @param buf the array of bytes
	 * @param offset the index of the first byte to be read
	 * @param limit the index after the last byte that is to be read
	 * @return the Unicode codepoint or '?'
	 */
	int decode(byte[] buf, int offset, int limit);
	
	/**
	 * Calculates the number of bytes that are needed to decode a full Unicode codepoint
	 * at the given position in the byte array.
	 * 
	 * The function must handle three cases:
	 * a) the given array does contain the full encoding of a Unicode codepoint
	 * b) the given data is too short to be the encoding of a Unicode codepoint
	 * c) the given data is malformed
	 * 
	 * In cases a) and c), the function must return the number of
	 * bytes that must be skipped after the decoding of the character.
	 * In case b), the function should return an estimation of the
	 * number of bytes needed to decode the next Unicode codepoint.
	 * Yet, this must not be an overestimation. In case b), the return
	 * value should be at least (limit - offset + 1).
	 *  
	 * @param buf the array of bytes
	 * @param offset the index of the first byte to be read
	 * @param limit the index after the last byte that is to be read
	 * @return the estimated number of bytes
	 */
	int estimateByteCount(byte[] buf, int offset, int limit);
}
