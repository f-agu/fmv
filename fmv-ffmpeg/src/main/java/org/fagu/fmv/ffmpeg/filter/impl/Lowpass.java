package org.fagu.fmv.ffmpeg.filter.impl;

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

import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class Lowpass extends AbstractFilter {

	// ------------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum WidthType {

		HZ("h"),
		Q_FACTOR("q"),
		OCTAVE("o"),
		SLOPE("s");

		private final String code;

		private WidthType(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	// ------------------------------------------------------------

	protected Lowpass() {
		super("lowpass");
	}

	public static Lowpass build() {
		return new Lowpass();
	}

	public Lowpass frequency(int freq) {
		if(freq < 0) {
			throw new IllegalArgumentException("frequency must be positive");
		}
		parameter("f", Integer.toString(freq));
		return this;
	}

	public Lowpass poles(int count) {
		if(count < 0) {
			throw new IllegalArgumentException("frequency must be positive");
		}
		parameter("p", Integer.toString(count));
		return this;
	}

	public Lowpass widthType(WidthType widthType) {
		parameter("width_type", widthType.getCode());
		return this;
	}

	public Lowpass bandwidth(double value) {
		parameter("w", Double.toString(value));
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}

}
