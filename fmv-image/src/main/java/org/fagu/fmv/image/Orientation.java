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
