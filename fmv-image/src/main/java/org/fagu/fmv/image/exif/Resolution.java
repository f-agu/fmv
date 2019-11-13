package org.fagu.fmv.image.exif;

import java.util.Arrays;
import java.util.Optional;


/**
 * @author f.agu
 * @created 13 nov. 2019 14:32:11
 */
public enum Resolution {

	NONE(1),
	INCHES(2),
	CM(3);

	private final int value;

	private Resolution(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public String getText() {
		return name().toLowerCase();
	}

	public static Optional<Resolution> valueOfText(String text) {
		return Arrays.stream(values())
				.filter(r -> r.getText().equalsIgnoreCase(text))
				.findFirst();
	}

	public static Optional<Resolution> valueOf(int value) {
		return Arrays.stream(values())
				.filter(r -> r.value == value)
				.findFirst();
	}

}
