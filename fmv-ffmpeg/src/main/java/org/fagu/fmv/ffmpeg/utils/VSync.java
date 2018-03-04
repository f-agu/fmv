package org.fagu.fmv.ffmpeg.utils;

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
public enum VSync {

	/**
	 * Each frame is passed with its timestamp from the demuxer to the muxer.
	 */
	PASSTHROUGH("0"),
	/**
	 * Frames will be duplicated and dropped to achieve exactly the requested constant frame rate.
	 */
	CFR("1"),
	/**
	 * Frames are passed through with their timestamp or dropped so as to prevent 2 frames from having the same
	 * timestamp.
	 */
	VFR("2"),
	/**
	 * As passthrough but destroys all timestamps, making the muxer generate fresh timestamps based on frame-rate.
	 */
	DROP("drop"),
	/**
	 * Chooses between 1 and 2 depending on muxer capabilities. This is the default method.
	 */
	AUTO("-1");

	private final String value;

	/**
	 * @param value
	 */
	private VSync(String value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}
}
