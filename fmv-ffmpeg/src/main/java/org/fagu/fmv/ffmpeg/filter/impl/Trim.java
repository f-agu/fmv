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


import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author f.agu
 */
public abstract class Trim<T> extends AbstractFilter {

	/**
	 * @param name
	 */
	protected Trim(String name) {
		super(name);
	}

	/**
	 * @param startTime
	 * @return
	 */
	public T start(Time startTime) {
		parameter("start", Double.toString(startTime.toSeconds()));
		return getThis();
	}

	/**
	 * @param endTime
	 * @return
	 */
	public T end(Time endTime) {
		parameter("end", Double.toString(endTime.toSeconds()));
		return getThis();
	}

	/**
	 * @param duration
	 * @return
	 */
	public T duration(Duration duration) {
		parameter("duration", Double.toString(duration.toSeconds()));
		return getThis();
	}

	/**
	 * @param startTime
	 * @return
	 */
	public T startPTS(String startPts) {
		parameter("start_pts", startPts);
		return getThis();
	}

	/**
	 * @param endTime
	 * @return
	 */
	public T endPTS(String endPts) {
		parameter("end_pts", endPts);
		return getThis();
	}

	// **********************************************

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private T getThis() {
		return (T)this;
	}

}
