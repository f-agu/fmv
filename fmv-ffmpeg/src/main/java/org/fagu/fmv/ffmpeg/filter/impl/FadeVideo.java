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
import org.fagu.fmv.ffmpeg.utils.Color;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author f.agu
 */
public class FadeVideo extends AbstractFilter {

	/**
	 * 
	 */
	protected FadeVideo() {
		super("fade");
	}

	/**
	 * @return
	 */
	public static FadeVideo build() {
		return new FadeVideo();
	}

	/**
	 * @param fadeType
	 * @return
	 */
	public static FadeVideo with(FadeType fadeType) {
		return new FadeVideo().type(fadeType);
	}

	/**
	 * 
	 */
	public static FadeVideo in() {
		return with(FadeType.IN);
	}

	/**
	 * 
	 */
	public static FadeVideo out() {
		return with(FadeType.OUT);
	}

	/**
	 * @param fadeType
	 * @return
	 */
	public FadeVideo type(FadeType fadeType) {
		parameter("t", fadeType.name().toLowerCase());
		return this;
	}

	/**
	 * @param startFrame
	 * @return
	 */
	public FadeVideo startFrame(int startFrame) {
		if(startFrame < 0) {
			throw new IllegalArgumentException("startFrame must be positive");
		}
		removeParameter("st");
		parameter("s", Integer.toString(startFrame));
		return this;
	}

	/**
	 * @param countFrame
	 * @return
	 */
	public FadeVideo countFrame(int countFrame) {
		if(countFrame < 0) {
			throw new IllegalArgumentException("countFrame must be positive");
		}
		removeParameter("d");
		parameter("n", Integer.toString(countFrame));
		return this;
	}

	/**
	 * @param startTime
	 * @return
	 */
	public FadeVideo startTime(Time startTime) {
		removeParameter("s");
		parameter("st", Double.toString(startTime.toSeconds()));
		return this;
	}

	/**
	 * @param duration
	 * @return
	 */
	public FadeVideo duration(Duration duration) {
		removeParameter("n");
		parameter("d", Double.toString(duration.toSeconds()));
		return this;
	}

	/**
	 * @param alphaChannel
	 * @return
	 */
	public FadeVideo alpha(boolean alphaChannel) {
		parameter("alpha", alphaChannel ? "1" : "0");
		return this;
	}

	/**
	 * @param color
	 * @return
	 */
	public FadeVideo color(Color color) {
		parameter("c", color.toString());
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