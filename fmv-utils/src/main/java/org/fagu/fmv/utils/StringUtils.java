package org.fagu.fmv.utils;

/**
 * @author f.agu
 */
public class StringUtils {

	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

	/**
	 * 
	 */
	private StringUtils() {}

	/**
	 * @param bytes
	 * @return
	 */
	public static String toHex(byte[] bytes) {
		final int count = bytes.length;
		final char[] hexChars = new char[count * 2];
		int j = 0;
		for(int i = 0; i < count; i++, j += 2) {
			final int v = bytes[i] & 0xFF;
			hexChars[j] = HEX_ARRAY[v >>> 4];
			hexChars[j + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

}
