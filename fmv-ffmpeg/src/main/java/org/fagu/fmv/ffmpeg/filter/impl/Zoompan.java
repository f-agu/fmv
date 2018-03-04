package org.fagu.fmv.ffmpeg.filter.impl;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import org.fagu.fmv.utils.media.Size;


/**
 * https://ffmpeg.org/ffmpeg-filters.html#toc-zoompan
 * 
 * @author f.agu
 */
public class Zoompan extends AbstractFilter {

	/**
	 * 
	 */
	protected Zoompan() {
		super("zoompan");
	}

	/**
	 * @return
	 */
	public static Zoompan build() {
		return new Zoompan();
	}

	/**
	 * Set the zoom expression (default "1")
	 * 
	 * @param zoom
	 * @return
	 */
	public Zoompan zoom(String zoom) {
		parameter("zoom", zoom);
		return this;
	}

	/**
	 * @param factor
	 * @param durationInFrames
	 * @return
	 */
	public Zoompan zoomInSmooth(int factor, int durationInFrames) {
		if(durationInFrames <= 0) {
			throw new IllegalArgumentException("durationInFrames must be at least 0: " + Integer.toString(durationInFrames));
		}
		if(factor < 1) {
			throw new IllegalArgumentException("factor must be at least 1: " + Integer.toString(factor));
		}
		float inc = (factor - 1F) / durationInFrames;
		return zoom("zoom+" + inc).duration(durationInFrames);
	}

	/**
	 * Set the x expression (default "0")
	 * 
	 * @param x
	 * @return
	 */
	public Zoompan x(String x) {
		parameter("x", x);
		return this;
	}

	/**
	 * Set the y expression (default "0")
	 * 
	 * @param y
	 * @return
	 */
	public Zoompan y(String y) {
		parameter("y", y);
		return this;
	}

	/**
	 * @return
	 */
	public Zoompan left() {
		return x("0");
	}

	/**
	 * @return
	 */
	public Zoompan right() {
		return x("iw");
	}

	/**
	 * @return
	 */
	public Zoompan top() {
		return y("0");
	}

	/**
	 * @return
	 */
	public Zoompan bottom() {
		return y("ih");
	}

	/**
	 * Set the duration expression in number of frames. This sets for how many number of frames effect will last for
	 * single input image. (default "90")
	 * 
	 * @param d
	 * @return
	 */
	public Zoompan duration(String d) {
		parameter("d", d);
		return this;
	}

	/**
	 * @param countFrames
	 * @return
	 */
	public Zoompan duration(int countFrames) {
		if(countFrames <= 0) {
			throw new IllegalArgumentException(Integer.toString(countFrames));
		}
		return duration(Integer.toString(countFrames));
	}

	/**
	 * Set the output image size (default "hd720")
	 * 
	 * @param s
	 * @return
	 */
	public Zoompan s(Size s) {
		parameter("s", s.toString());
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}
}
