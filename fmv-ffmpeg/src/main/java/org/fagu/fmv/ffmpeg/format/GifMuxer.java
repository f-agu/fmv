package org.fagu.fmv.ffmpeg.format;

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

import java.io.File;

import org.fagu.fmv.ffmpeg.ioe.FileMediaOutput;
import org.fagu.fmv.ffmpeg.operation.MediaOutput;


/**
 * @author f.agu
 */
public class GifMuxer extends Muxer<GifMuxer> {

	protected GifMuxer(MediaOutput mediaOutput) {
		super("gif", mediaOutput);
	}

	public static GifMuxer to(File file) {
		return new GifMuxer(new FileMediaOutput(file));
	}

	public GifMuxer loopIndefinitely() {
		return loop(0);
	}

	public GifMuxer noLoop() {
		return loop( - 1);
	}

	/**
	 * Number of times to loop the output: -1 - no loop, 0 - infinite loop (from -1 to 65535) (default 0)
	 * 
	 * @param count
	 * @return
	 */
	public GifMuxer loop(int count) {
		if(count < - 1 || count > 65535) {
			throw new IllegalArgumentException("loop must be between -1 and 65535: " + count);
		}
		parameter("-loop", Integer.toString(count));
		return this;
	}

	/**
	 * Force delay (in centiseconds) after the last frame (from -1 to 65535) (default -1)
	 * 
	 * @param milliseconds
	 * @return
	 */
	public GifMuxer finalDelay(long centiseconds) {
		if(centiseconds < - 1 || centiseconds > 65535) {
			throw new IllegalArgumentException("centiseconds must be between -1 and 65535: " + centiseconds);
		}
		parameter("-final_delay", Long.toString(Math.max( - 1, centiseconds)));
		return this;
	}
}
