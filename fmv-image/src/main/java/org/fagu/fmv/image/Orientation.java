package org.fagu.fmv.image;

import org.fagu.fmv.utils.media.Size;


/**
 * @author Oodrive
 * @author f.agu
 * @created 31 août 2020 17:13:06
 */
public enum Orientation {

	HORIZONTAL(1), // normal
	MIRROR_HORIZONTAL(2),
	ROTATE_180(3),
	MIRROR_VERTICAL(4),
	MIRROR_HORIZONTAL_AND_ROTATE_270_CW(5),
	ROTATE_90_CW(6),
	MIRROR_HORIZONTAL_AND_ROTATE_90_CW(7),
	ROTATE_270_CW(8);

	private final int value;

	private Orientation(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public Size rotateSize(Size size) {
		switch(this) {
			case HORIZONTAL:
			case MIRROR_HORIZONTAL:
			case MIRROR_VERTICAL:
			case ROTATE_180:
				return size;
			default:
				return size.rotate();
		}
	}

	public static Orientation valueOf(int value) {
		if(value <= 0 || value > 8) {
			throw new IllegalArgumentException("Value must between 1 and 8 included" + value);
		}
		for(Orientation orientation : values()) {
			if(orientation.value == value) {
				return orientation;
			}
		}
		throw new RuntimeException("Never happen!");
	}

}
