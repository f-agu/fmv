package org.fagu.fmv.mymedia.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * @author f.agu
 */
public class IOStringUtils {

	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

	private IOStringUtils() {}

	public static String toHex(byte[] bytes) {
		return toHex(bytes, 0, bytes.length);
	}

	public static String toHex(byte[] bytes, int offset) {
		return toHex(bytes, offset, bytes.length - offset);
	}

	public static String toHex(byte[] bytes, int offset, int length) {
		if(bytes.length < offset) {
			throw new IllegalArgumentException("Offset " + offset + " is over bytes length " + bytes.length);
		}
		if(bytes.length < offset + length) {
			throw new IllegalArgumentException("Offset + length " + offset + " + " + length + " = " + (offset + length)
					+ " is over bytes length " + bytes.length);
		}
		final int count = offset + length;
		final char[] hexChars = new char[count * 2];
		int j = 0;
		for(int i = offset; i < count; i++, j += 2) {
			final int v = bytes[i] & 0xFF;
			hexChars[j] = HEX_ARRAY[v >>> 4];
			hexChars[j + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String formatBytes(long bytes) {
		if(bytes == 0L) {
			return "0";
		}
		long absBytes = Math.abs(bytes);
		final int BASE = 1024;
		String[] units = {"", "k", "M", "G", "T", "P", "E"};
		int unit = (int)(Math.log(absBytes) / Math.log(BASE));
		double value = absBytes / Math.pow(BASE, unit);
		int fractSize = Math.min(2 - (int)Math.log10(value), 2);
		DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		decimalFormat.setGroupingUsed(false);
		decimalFormat.setMaximumFractionDigits(fractSize > 0 ? fractSize : 0);

		StringBuilder buf = new StringBuilder();
		if(bytes < 0) {
			buf.append('-');
		}
		return buf.append(decimalFormat.format(value)).append(units[unit]).toString();
	}
}
