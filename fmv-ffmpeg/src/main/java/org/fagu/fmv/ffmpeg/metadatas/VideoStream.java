package org.fagu.fmv.ffmpeg.metadatas;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.util.Map;
import java.util.Optional;

import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.utils.media.Ratio;
import org.fagu.fmv.utils.media.Rotation;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class VideoStream extends Stream {

	public VideoStream(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		super(movieMetadatas, map);
	}

	public int width() {
		return getInt("width").orElseThrow(RuntimeException::new);
	}

	public int height() {
		return getInt("height").orElseThrow(RuntimeException::new);
	}

	/**
	 * SAR
	 * 
	 * @return
	 */
	public Optional<Ratio> sampleAspectRatio() {
		return getRatio("sample_aspect_ratio");
	}

	/**
	 * DAR
	 * 
	 * @return
	 */
	public Optional<Ratio> displayAspectRatio() {
		return getRatio("display_aspect_ratio");
	}

	public Optional<PixelFormat> pixelFormat() {
		return getPixelFormat("pix_fmt");
	}

	public Optional<Integer> level() {
		return getInt("level");
	}

	public Optional<Integer> bitsPerRawSample() {
		return getInt("bits_per_raw_sample");
	}

	public Size size() {
		int width = width();
		int height = height();
		if(width > 0 && height > 0) {
			return Size.valueOf(width, height);
		}
		return null;
	}

	@Override
	public Type type() {
		return Type.VIDEO;
	}

	public Rotation rotation() {
		Optional<Object> tag = tag("rotate");
		return tag.isPresent() ? Rotation.valueOf("R_" + tag.get()) : Rotation.R_0;
	}

	@Override
	public String toString() {
		return new StringBuilder(100)
				.append("VideoStream[").append(width()).append('x').append(height()).append(']')
				.toString();
	}

}
