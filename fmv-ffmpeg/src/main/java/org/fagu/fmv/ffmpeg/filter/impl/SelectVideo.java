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
import java.util.OptionalInt;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author f.agu
 */
public class SelectVideo extends AbstractFilter {

	/**
	 * 
	 */
	protected SelectVideo() {
		super("select");
	}

	/**
	 * @return
	 */
	public static SelectVideo build() {
		return new SelectVideo();
	}

	/**
	 * @param exp
	 * @return
	 */
	public SelectVideo expr(String exp) {
		parameter("e", exp);
		return this;
	}

	/**
	 * @param number
	 * @return
	 */
	public SelectVideo numberOutputs(int number) {
		if(number < 1) {
			throw new IllegalArgumentException("outputs must be at least 1: " + number);
		}
		parameter("n", Integer.toString(number));
		return this;
	}

	/**
	 * @return
	 */
	public SelectVideo skipAll() {
		return expr("0");
	}

	/**
	 * @param videoStream
	 * @param countFrame
	 * @return
	 */
	public SelectVideo countFrame(VideoStream videoStream, int countFrame) {
		OptionalInt countEstimateFrames = videoStream.countEstimateFrames();
		if(countEstimateFrames.isPresent()) {
			int everyFrame = Math.round((float)countEstimateFrames.getAsInt() / (float)countFrame);
			return everyFrame(everyFrame);
		}
		return null;
	}

	/**
	 * @param every
	 * @return
	 */
	public SelectVideo everyFrame(int every) {
		return expr("'not(mod(n," + Integer.toString(every) + "))'");
	}

	/**
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public SelectVideo onlyBetween(Time startTime, Time endTime) {
		return expr("between(t," + startTime.toSeconds() + "," + endTime.toSeconds() + ")");
	}

	/**
	 * @return
	 */
	public SelectVideo onlyIFrame() {
		return expr("'eq(pict_type,I)'");
	}

	/**
	 * @return
	 */
	public SelectVideo onlyIFrameBetween(Time startTime, Time endTime) {
		return expr("between(t," + startTime.toSeconds() + "," + endTime.toSeconds() + ")*eq(pict_type,I)");
	}

	/**
	 * @param duration
	 * @return
	 */
	public SelectVideo minimumDistance(Duration duration) {
		return expr("'isnan(prev_selected_t)+gte(t-prev_selected_t," + duration.toSeconds() + ")'");
	}

	/**
	 * @param scene
	 * @return
	 */
	public SelectVideo mosaic(double scene) { // best choice between 0.3 and 0.5
		if(scene < 0 || scene > 1) {
			throw new IllegalArgumentException("scene must between 0 and 1");
		}
		return expr("'gt(scene," + scene + ")'");
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

}
