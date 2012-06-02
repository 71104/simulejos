package lejos.internal.charset;

public interface CharsetEncoder
{
	int getMaxCharLength();
	
	/**
	 * Writes the encoding of the codepoint to the byte-array.
	 * The codepoint may be -1 to indicate an undecodable codepoint.
	 * 
	 * @param codepoint the character to encode
	 * @param target target byte array
	 * @param offset index of first byte
	 * @return the offset for the next character  
	 */
	int encode(int codepoint, byte[] target, int offset);
	
	/**
	 * Return the number of bytes needed to encode the given codepoint.
	 * The value returned must not be an underestimation.
	 * 
	 * @param codepoint the Unicode codepoint
	 * @return the estimated number of bytes
	 */
	int estimateByteCount(int codepoint);
}
