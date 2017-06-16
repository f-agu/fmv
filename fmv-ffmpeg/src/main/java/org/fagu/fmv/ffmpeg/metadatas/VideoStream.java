package org.fagu.fmv.ffmpeg.metadatas;

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

import java.util.NavigableMap;
import java.util.Optional;
import java.util.OptionalInt;

import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.utils.media.Ratio;
import org.fagu.fmv.utils.media.Rotation;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class VideoStream extends Stream {

	/**
	 * @param movieMetadatas
	 * @param map
	 */
	public VideoStream(MovieMetadatas movieMetadatas, NavigableMap<String, Object> map) {
		super(movieMetadatas, map);
	}

	/**
	 * @return
	 */
	public int width() {
		return getInt("width").getAsInt();
	}

	/**
	 * @return
	 */
	public int height() {
		return getInt("height").getAsInt();
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
		return getRatio("sample_aspect_ratio");
	}

	/**
	 * @return
	 */
	public Optional<PixelFormat> pixelFormat() {
		return getPixelFormat("pix_fmt");
	}

	/**
	 * @return
	 */
	public OptionalInt level() {
		return getInt("level");
	}

	/**
	 * @return
	 */
	public OptionalInt bitsPerRawSample() {
		return getInt("bits_per_raw_sample");
	}

	/**
	 * @return
	 */
	public Size size() {
		return Size.valueOf(width(), height());
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.metadatas.Stream#type()
	 */
	@Override
	public Type type() {
		return Type.VIDEO;
	}

	/**
	 * @return
	 */
	public Rotation rotate() {
		Optional<Object> tag = tag("rotate");
		return tag.isPresent() ? Rotation.valueOf("R_" + tag.get()) : Rotation.R_0;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append("VideoStream[").append(width()).append('x').append(height()).append(']');
		return buf.toString();
	}

}
