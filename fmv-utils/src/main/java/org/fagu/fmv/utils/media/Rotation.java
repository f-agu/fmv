package org.fagu.fmv.utils.media;

/*
 * #%L
 * fmv-ffmpeg
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
public enum Rotation {
	R_0(0), R_90(90), R_180(180), R_270(270);

	private final int value;

	/**
	 * @param value
	 */
	private Rotation(int value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param size
	 * @return
	 */
	public Size resize(Size size) {
		return value % 180 == 0 ? size : size.rotate();
	}

	/**
	 * @return
	 */
	public Rotation opposite() {
		switch(this) {
			case R_90:
				return R_270;
			case R_270:
				return R_90;
			case R_180:
			case R_0:
			default:
				return this;
		}
	}

}
