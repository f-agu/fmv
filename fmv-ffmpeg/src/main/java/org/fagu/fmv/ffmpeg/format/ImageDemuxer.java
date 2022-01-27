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

import org.fagu.fmv.ffmpeg.flags.PatternType;
import org.fagu.fmv.ffmpeg.flags.TimestampFromFile;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public abstract class ImageDemuxer<M> extends Demuxer<M> {

	public ImageDemuxer(String name, MediaInput mediaInput) {
		super(name, mediaInput);
	}

	/**
	 * Set the video framerate (default "25")
	 * 
	 * @param framerate
	 * @return
	 */
	public M frameRate(FrameRate framerate) {
		parameter("-r", framerate.toString());
		return getMThis();
	}

	/**
	 * Force loop over input file sequence (from 0 to 1) (default 0)
	 * 
	 * @param loop
	 * @return
	 */
	public M loop(boolean loop) {
		parameter("-loop", Integer.toString(loop ? 1 : 0));
		return getMThis();
	}

	/**
	 * Set pattern type (from 0 to INT_MAX) (default 0)
	 * 
	 * @param patternType
	 * @return
	 */
	public M patternType(PatternType patternType) {
		parameter("-pattern_type", patternType.flag());
		return getMThis();
	}

	/**
	 * Set video pixel format
	 * 
	 * @param pixelFormat
	 * @return
	 */
	public M pixelFormat(PixelFormat pixelFormat) {
		parameter("-pixel_format", pixelFormat.toString());
		return getMThis();
	}

	/**
	 * Set first number in the sequence (from 0 to INT_MAX) (default 0)
	 * 
	 * @param startNumber
	 * @return
	 */
	public M startNumber(int startNumber) {
		if(startNumber < 0) {
			throw new IllegalArgumentException("startNumber must be at least 0: " + startNumber);
		}
		parameter("-start_number", Integer.toString(startNumber));
		return getMThis();
	}

	/**
	 * Set range for looking at the first sequence number (from 1 to INT_MAX) (default 5)
	 * 
	 * @param startNumberRange
	 * @return
	 */
	public M startNumberRange(int startNumberRange) {
		if(startNumberRange < 1) {
			throw new IllegalArgumentException("startNumberRange must be at least 1: " + startNumberRange);
		}
		parameter("-start_number_range", Integer.toString(startNumberRange));
		return getMThis();
	}

	/**
	 * Set video size
	 * 
	 * @param videoSize
	 * @return
	 */
	public M videoSize(Size videoSize) {
		parameter("-video_size", videoSize.toString());
		return getMThis();
	}

	/**
	 * Force frame size in bytes (from 0 to INT_MAX) (default 0)
	 * 
	 * @param frameSize
	 * @return
	 */
	public M frameSize(int frameSize) {
		if(frameSize < 0) {
			throw new IllegalArgumentException("frameSize must be at least 0: " + frameSize);
		}
		parameter("-frame_size", Integer.toString(frameSize));
		return getMThis();
	}

	/**
	 * Set frame timestamp from file's one (from 0 to 2) (default 0)
	 * 
	 * @param timestampFromFile
	 * @return
	 */
	public M tsFromFile(TimestampFromFile timestampFromFile) {
		parameter("-ts_from_file", timestampFromFile.flag());
		return getMThis();
	}
}
