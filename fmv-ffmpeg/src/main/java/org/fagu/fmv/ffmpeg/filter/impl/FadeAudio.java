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
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class FadeAudio extends AbstractFilter {

	// -----------------------------------------------------------

	public enum Curve {

		LINEAR("tri"), //
		QUARTER_OF_SINE_WAVE("qsin"), //
		HALF_OF_SINE_WAVE("hsin"), //
		EXPONENTIAL_SINE_WAVE("esin"), //
		LOGARITHMIC("log"), //
		INVERTED_PARABOLA("par"), //
		QUADRATIC("qua"), //
		CUBIC("cub"), //
		SQUARE_ROOT("squ"), //
		CUBIC_ROOT("cbr"); //

		private final String value;

		private Curve(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	// -----------------------------------------------------------

	protected FadeAudio() {
		super("afade");
	}

	public static FadeAudio build() {
		return new FadeAudio();
	}

	public static FadeAudio with(FadeType fadeType) {
		return new FadeAudio().type(fadeType);
	}

	public static FadeAudio in() {
		return with(FadeType.IN);
	}

	public static FadeAudio out() {
		return with(FadeType.OUT);
	}

	public FadeAudio type(FadeType fadeType) {
		parameter("t", fadeType.name().toLowerCase());
		return this;
	}

	public FadeAudio startSample(int startSample) {
		if(startSample < 0) {
			throw new IllegalArgumentException("startSample must be positive");
		}
		removeParameter("st");
		parameter("ss", Integer.toString(startSample));
		return this;
	}

	public FadeAudio numberSample(int nbSamples) {
		if(nbSamples < 0) {
			throw new IllegalArgumentException("nbSamples must be positive");
		}
		removeParameter("d");
		parameter("ns", Integer.toString(nbSamples));
		return this;
	}

	public FadeAudio startTime(Time startTime) {
		removeParameter("s");
		parameter("st", Double.toString(startTime.toSeconds()));
		return this;
	}

	public FadeAudio duration(Duration duration) {
		removeParameter("n");
		parameter("d", Double.toString(duration.toSeconds()));
		return this;
	}

	public FadeAudio curve(Curve curve) {
		parameter("curve", curve.getValue());
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}

}
