package org.fagu.fmv.image;

import java.util.Optional;
import java.util.StringJoiner;


/*
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 fagu
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

/**
 * https://web.archive.org/web/20190624045241if_/http://www.cipa.jp:80/std/documents/e/DC-008-Translation-2019-E.pdf
 * page 61
 * 
 * https://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/EXIF.html#Flash
 * 
 * @author f.agu
 */
public class Flash {

	public enum ReturnedLight {
		NO_STROBE_RETURN_DETECTION_FUNCTION(null),
		RESERVED(null),
		STROBE_RETURN_LIGHT_NOT_DETECTED("Return not detected"),
		STROBE_RETURN_LIGHT_DETECTED("Return detected");

		private final String text;

		private ReturnedLight(String text) {
			this.text = text;
		}

		public Optional<String> getText() {
			return Optional.ofNullable(text);
		}
	}

	public enum FlashMode {
		UNKNOWN(null), // 00b
		COMPULSORY_FLASH_FIRING("On"), // 01b
		COMPULSORY_FLASH_SUPPRESSION("Off"), // 10b
		AUTO_MODE("Auto"); // 11b

		private final String text;

		private FlashMode(String text) {
			this.text = text;
		}

		public Optional<String> getText() {
			return Optional.ofNullable(text);
		}
	}

	private final int value;

	private Flash(int value) {
		this.value = value;
	}

	public static Flash valueOf(int value) {
		if(value < 0 || value > 0x7F) {
			throw new IllegalArgumentException("Value must be between 0 and 127 (0x7F)");
		}
		return new Flash(value);
	}

	public int getValue() {
		return value;
	}

	public boolean isFired() {
		return (value & 1) != 0;
	}

	public ReturnedLight getReturnedLight() {
		return ReturnedLight.values()[(value & 6) >> 1];
	}

	public FlashMode getMode() {
		return FlashMode.values()[(value & 24) >> 3];
	}

	public boolean isNoFlashFunction() {
		return (value & 32) != 0;
	}

	public boolean isRedEyeReduction() {
		return (value & 64) != 0;
	}

	public String getComment() {
		if(value == 0) {
			return "No Flash";
		}
		StringJoiner joiner = new StringJoiner(", ");
		FlashMode mode = getMode();
		mode.getText().ifPresent(joiner::add);
		if(isFired()) {
			joiner.add("Fired");
		} else {
			joiner.add("Did not fire");
		}
		if(isNoFlashFunction()) {
			joiner.add("No flash function");
		}
		if(isRedEyeReduction()) {
			joiner.add("Red-eye reduction");
		}
		getReturnedLight().getText().ifPresent(joiner::add);
		return joiner.toString();
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Flash) {
			return ((Flash)obj).getValue() == value;
		}
		return false;
	}

	@Override
	public String toString() {
		return new StringBuilder(40)
				.append("Flash(").append(value).append(')').append(' ').append(getComment())
				.toString();
	}
}
