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


/**
 * {@link https://www.ffmpeg.org/ffmpeg-filters.html#setpts_002c-asetpts}<br>
 * 
 * PTS=N/(FRAME_RATE*TB)<br>
 * 
 * FRAME_RATE: frame rate, only defined for constant frame-rate video<br>
 * PTS: The presentation timestamp in input<br>
 * N: The count of the input frame for video or the number of consumed samples, not including the current frame for
 * audio, starting from 0.<br>
 * NB_CONSUMED_SAMPLES: The number of consumed samples, not including the current frame (only audio)<br>
 * NB_SAMPLES, S: The number of samples in the current frame (only audio)<br>
 * SAMPLE_RATE, SR: The audio sample rate.<br>
 * STARTPTS: The PTS of the first frame.<br>
 * STARTT: the time in seconds of the first frame<br>
 * INTERLACED: State whether the current frame is interlaced.<br>
 * T: the time in seconds of the current frame<br>
 * POS: original position in the file of the frame, or undefined if undefined for the current frame<br>
 * PREV_INPTS: The previous input PTS.<br>
 * PREV_INT: previous input time in seconds<br>
 * PREV_OUTPTS: The previous output PTS.<br>
 * PREV_OUTT: previous output time in seconds<br>
 * RTCTIME: The wallclock (RTC) time in microseconds.. This is deprecated, use time(0) instead.<br>
 * RTCSTART: The wallclock (RTC) time at the start of the movie in microseconds.<br>
 * TB: The timebase of the input timestamps.<br>
 * 
 * @author f.agu
 */
public abstract class SetPTS<T> extends AbstractFilter {

	protected SetPTS(String name) {
		super(name);
	}

	public T startAtFirstFrame() {
		setMainParameter("PTS-STARTPTS");
		return getThis();
	}

	public T speed(float multiplyBy) {
		setMainParameter("1/" + multiplyBy + "*PTS");
		return getThis();
	}

	// **********************************************

	@SuppressWarnings("unchecked")
	private T getThis() {
		return (T)this;
	}

}
