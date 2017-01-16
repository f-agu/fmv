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

import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.impl.AudioMix.MixAudioDuration;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.Duration;


/**
 * https://www.ffmpeg.org/ffmpeg-filters.html#overlay-1
 * 
 * @author f.agu
 */
public class Overlay extends FilterComplex {

	// ---------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum EOFAction {
		/**
		 * Repeat the last frame (the default)
		 */
		REPEAT,
		/**
		 * End both streams
		 */
		ENDALL,
		/**
		 * Pass the main input through
		 */
		PASS
	}

	// ---------------------------------------------

	/**
	 * 
	 */
	protected Overlay() {
		super("overlay");
	}

	/**
	 * @param filterInput1
	 * @param filterInput2
	 */
	protected Overlay(FilterInput filterInput1, FilterInput filterInput2) {
		this();
		addInput(filterInput1);
		addInput(filterInput2);
	}

	/**
	 * @return
	 */
	public static Overlay build() {
		return new Overlay();
	}

	/**
	 * @param filterInput1
	 * @param filterInput2
	 * @return
	 */
	public static Overlay with(FilterInput filterInput1, FilterInput filterInput2) {
		return new Overlay(filterInput1, filterInput2);
	}

	/**
	 * @param x
	 * @return
	 */
	public Overlay x(int x) {
		return x(Integer.toString(x));
	}

	/**
	 * @param x
	 * @return
	 */
	public Overlay x(String x) {
		parameter("x", x);
		return this;
	}

	/**
	 * @param y
	 * @return
	 */
	public Overlay y(int y) {
		return y(Integer.toString(y));
	}

	/**
	 * @param x
	 * @return
	 */
	public Overlay y(String y) {
		parameter("y", y);
		return this;
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public Overlay position(int x, int y) {
		return x(x).y(y);
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public Overlay position(String x, String y) {
		return x(x).y(y);
	}

	/**
	 * @param shortest
	 * @return
	 */
	public Overlay shortest(boolean shortest) {
		parameter("shortest", Integer.toString(shortest ? 1 : 0));
		return this;
	}

	/**
	 * @param startSecond
	 * @param speed
	 * @return
	 */
	public Overlay scrollLeftToRight(float startSecond, float speed) {
		StringBuilder buf = new StringBuilder(30);
		buf.append("'if(gte(t,").append(startSecond).append("-w+(t-").append(startSecond);
		buf.append(")*").append(speed).append(", NAN)'");
		return x(buf.toString());
	}

	/**
	 * @param startSecond
	 * @param speed
	 * @return
	 */
	public Overlay scrollTopToBottom(float startSecond, float speed) {
		StringBuilder buf = new StringBuilder(30);
		buf.append("'if(gte(t,").append(startSecond).append("-h+(t-").append(startSecond);
		buf.append(")*").append(speed).append(", NAN)'");
		return y(buf.toString());
	}

	/**
	 * @return
	 */
	public Overlay positionMiddle() {
		return x("(W-w)/2").y("(H-h)/2");
	}

	/**
	 * @param eofAction
	 * @return
	 */
	public Overlay eofAction(EOFAction eofAction) {
		parameter("eof_action", eofAction.name().toLowerCase());
		return this;
	}

	/**
	 * @param mixAudioDuration
	 * @return
	 */
	public Overlay duration(MixAudioDuration mixAudioDuration) {
		parameter("duration", mixAudioDuration.name().toLowerCase());
		return this;
	}

	/**
	 * @param duration
	 * @return
	 */
	public Overlay dropoutTransition(Duration duration) {
		parameter("dropout_transition", Double.toString(duration.toSeconds()));
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Type.valuesSet(this);
	}

}
