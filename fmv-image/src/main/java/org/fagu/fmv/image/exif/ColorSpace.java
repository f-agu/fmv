package org.fagu.fmv.image.exif;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * @author f.agu
 * @created 7 nov. 2019 16:01:42
 */
public class ColorSpace {

	private static final Map<Integer, String> VALUES = new HashMap<>();

	static {
		VALUES.put(0x1, "sRGB");
		VALUES.put(0x2, "Adobe RGB");
		VALUES.put(0xfffd, "Wide Gamut RGB");
		VALUES.put(0xfffe, "ICC Profile");
		VALUES.put(0xffff, "Uncalibrated");
	}

	private ColorSpace() {}

	public static Optional<String> getText(int value) {
		return Optional.ofNullable(VALUES.get(value));
	}

}
