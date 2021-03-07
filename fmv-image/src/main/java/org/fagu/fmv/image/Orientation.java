package org.fagu.fmv.image;

/*-
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 - 2020 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.fagu.fmv.utils.media.Size;


/**
 * See http://www.exif.org/Exif2-2.PDF, page 18
 * 
 * See https://i.stack.imgur.com/Jsl5V.gif
 * 
 * See values with commandline: magick identify -list Orientation
 * 
 * @author f.agu
 * @created 31 août 2020 17:13:06
 */
public enum Orientation {
	// 1 -> 8 -> 3 -> 6
	// mirror : 2 -> 7 -> 4 -> 5

	UNDEFINED(0, "Undefined", 0),
	HORIZONTAL(1, "TopLeft", 0), // Normal
	MIRROR_HORIZONTAL(2, "TopRight", 0),
	ROTATE_180(3, "BottomRight", 180),
	MIRROR_VERTICAL(4, "BottomLeft", 180),
	MIRROR_HORIZONTAL_AND_ROTATE_270_CW(5, "LeftTop", 270),
	ROTATE_90_CW(6, "RightTop", 270),
	MIRROR_HORIZONTAL_AND_ROTATE_90_CW(7, "RightBottom", 90),
	ROTATE_270_CW(8, "LeftBottom", 90);

	private final int value;

	private final String exifTerm;

	private final int rotationValue;

	private Orientation(int value, String exifTerm, int rotationValue) {
		this.value = value;
		this.exifTerm = exifTerm;
		this.rotationValue = rotationValue;
	}

	public int getValue() {
		return value;
	}

	public String getExifTerm() {
		return exifTerm;
	}

	public Size rotateSize(Size size) {
		switch(this) {
			case UNDEFINED:
			case HORIZONTAL:
			case MIRROR_HORIZONTAL:
			case MIRROR_VERTICAL:
			case ROTATE_180:
				return size;
			default:
				return size.rotate();
		}
	}

	public boolean isMirror() {
		return this == MIRROR_HORIZONTAL
				|| this == MIRROR_VERTICAL
				|| this == MIRROR_HORIZONTAL_AND_ROTATE_270_CW
				|| this == MIRROR_HORIZONTAL_AND_ROTATE_90_CW;
	}

	public int getRotationValue() {
		return rotationValue;
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

	public static Orientation parse(String value) {
		if(value.length() == 1) {
			char c = value.charAt(0);
			if('0' <= c && c <= '8') {
				return valueOf(Integer.parseInt(value));
			}
		}
		for(Orientation orientation : values()) {
			if(value.equalsIgnoreCase(orientation.exifTerm)) {
				return orientation;
			}
		}
		throw new IllegalArgumentException("Undefined orientation: " + value);
	}

}
