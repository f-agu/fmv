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
public class Volume extends AbstractFilter {

	// ------------------------------------------

	/**
	 * Represents the mathematical precision
	 *
	 * @author f.agu
	 */
	public enum Precision {
		FIXED, FLOAT, DOUBLE;
	}

	// ------------------------------------------

	/**
	 * Choose the behaviour on encountering ReplayGain side data in input frames
	 *
	 * @author f.agu
	 */
	public enum ReplayGain {
		DROP, IGNORE, TRACK, ALBUM;
	}

	// ------------------------------------------

	/**
	 * Set when the volume expression is evaluated.
	 *
	 * @author f.agu
	 */
	public enum Eval {
		ONCE, FRAME;
	}

	// ------------------------------------------

	/**
	 *
	 */
	protected Volume() {
		super("volume");
	}

	/**
	 * @return
	 */
	public static Volume build() {
		return new Volume();
	}

	/**
	 * @param volumeDetected
	 * @return
	 */
	public Volume increaseToMax(VolumeDetected volumeDetected) {
		float max = volumeDetected.getMax();
		if(max >= 0) {
			return same();
		}
		return expr(Float.toString( - max) + "dB");
	}

	/**
	 * @return
	 */
	public Volume same() {
		return expr("1");
	}

	/**
	 * @param factor
	 * @return
	 */
	public Volume expr(String expr) {
		parameter("volume", "'" + expr + "'");
		return this;
	}

	/**
	 * @param factor
	 * @return
	 */
	public Volume volume(double factor) {
		if(factor < 0) {
			throw new IllegalArgumentException("factor must be positive");
		}
		return expr(Double.toString(factor));
	}

	/**
	 * @param precision
	 * @return
	 */
	public Volume precision(Precision precision) {
		parameter("precision", precision.name().toLowerCase());
		return this;
	}

	/**
	 * @param replayGain
	 * @return
	 */
	public Volume replayGain(ReplayGain replayGain) {
		parameter("replaygain", replayGain.name().toLowerCase());
		return this;
	}

	/**
	 * @param count
	 * @return
	 */
	public Volume replayGainPreAmplification(double value) {
		parameter("replaygain_preamp", Double.toString(value));
		return this;
	}

	/**
	 * @param eval
	 * @return
	 */
	public Volume replayGain(Eval eval) {
		parameter("eval", eval.name().toLowerCase());
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}

}
