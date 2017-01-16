package org.fagu.fmv.image;

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
 * @author f.agu
 */
public class Flash {

	private final int value;

	/**
	 * @param value
	 * @param comment
	 */
	private Flash(int value) {
		this.value = value;
	}

	/**
	 * @param value
	 * @return
	 */
	public static Flash valueOf(int value) {
		if(value < 0 || value > 0x7F) {
			throw new IllegalArgumentException("Value msut be between 0 and 127 (0x7F)");
		}
		return new Flash(value);
	}

	/**
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return
	 */
	public boolean isFired() {
		return (value & 1) != 0;
	}

	/**
	 * @return
	 */
	public boolean isLightDetected() {
		return (value & 2) != 0;
	}

	/**
	 * @return
	 */
	// public boolean isLightNotDetected() {
	// return (value & 4) != 0;
	// }

	/**
	 * @return
	 */
	public boolean isCompulsoryMode() {
		return (value & 8) != 0;
	}

	/**
	 * @return
	 */
	public boolean isAuto() {
		return (value & 16) != 0;
	}

	/**
	 * @return
	 */
	public boolean isNoFlashFunction() {
		return (value & 32) != 0;
	}

	/**
	 * @return
	 */
	public boolean isRedEyeReduction() {
		return (value & 64) != 0;
	}

	/**
	 * @return
	 */
	public String getComment() {
		if(value == 0) {
			return "No Flash";
		}
		StringBuilder buf = new StringBuilder(50);
		if(isFired()) {
			buf.append("Fired");
		} else {
			buf.append("Did not fire");
		}
		if(isLightDetected()) {
			append(buf, "Return detected");
		}
		// if(isLightNotDetected()) {
		// append(buf, "Return not detected");
		// }
		if(isCompulsoryMode()) {
			append(buf, "On");
		}
		if(isAuto()) {
			append(buf, "Auto");
		}
		if(isNoFlashFunction()) {
			append(buf, "No flash function");
		}
		if(isRedEyeReduction()) {
			append(buf, "Red-eye reduction");
		}
		return buf.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return value;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Flash) {
			return ((Flash)obj).getValue() == value;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(40);
		buf.append("Flash(").append(value).append(')').append(' ').append(getComment());
		return buf.toString();
	}

	// *********************************************************

	/**
	 * @param buf
	 * @param str
	 */
	private void append(StringBuilder buf, String str) {
		if(buf.length() > 0) {
			buf.append(',').append(' ');
		}
		buf.append(str);
	}
}
