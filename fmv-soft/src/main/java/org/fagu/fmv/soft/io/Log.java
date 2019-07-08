package org.fagu.fmv.soft.io;

import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 * @created 8 juil. 2019 16:32:02
 */
class Log {

	private static final int MAX_LENGTH = 50;

	private Log() {}

	static String reference(Object ref) {
		return "#" + Integer.toHexString(ref.hashCode());
	}

	static String data(int data) {
		return data(new byte[] {(byte)data}, 0, 1);
	}

	static String data(byte[] data, int off, int len) {
		StringJoiner joiner = new StringJoiner(" ", "0x", len > MAX_LENGTH ? "..." : StringUtils.EMPTY);
		for(int i = off; i < Math.min(len, MAX_LENGTH); ++i) {
			joiner.add(StringUtils.leftPad(Integer.toHexString(data[i] & 0xFF), 2, '0'));
		}
		return joiner.toString();
	}

}
