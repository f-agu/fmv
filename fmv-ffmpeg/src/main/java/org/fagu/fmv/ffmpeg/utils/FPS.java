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
public class FPS {

	private final double countFrameBySeconds;

	private final String fps;

	/**
	 * @param fps
	 * @param countFrameBySeconds
	 */
	private FPS(String fps, double countFrameBySeconds) {
		this.fps = fps;
		this.countFrameBySeconds = countFrameBySeconds;
	}

	/**
	 * @param f
	 * @return
	 */
	public static FPS inOneSecond(int f) {
		return new FPS(Integer.toString(f), f);
	}

	/**
	 * 1/60 : every minute<br>
	 * 1/600 : every 10 minutes<br>
	 * 
	 * @param second
	 * @return
	 */
	public static FPS every(int second) {
		return new FPS("1/" + Integer.toString(second), 1D / second);
	}

	/**
	 * @return
	 */
	public double countFrameBySeconds() {
		return countFrameBySeconds;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return fps;
	}

}
