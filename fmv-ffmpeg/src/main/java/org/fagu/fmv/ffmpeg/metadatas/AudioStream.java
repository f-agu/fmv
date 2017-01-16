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
import java.util.OptionalInt;

import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.AudioSampleFormat;
import org.fagu.fmv.ffmpeg.utils.ChannelLayout;


/**
 * @author f.agu
 */
public class AudioStream extends Stream {

	/**
	 * @param movieMetadatas
	 * @param map
	 */
	public AudioStream(MovieMetadatas movieMetadatas, NavigableMap<String, Object> map) {
		super(movieMetadatas, map);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.metadatas.Stream#type()
	 */
	@Override
	public Type type() {
		return Type.AUDIO;
	}

	/**
	 * @return
	 */
	public OptionalInt sampleRate() {
		return getInt("sample_rate");
	}

	/**
	 * @return
	 */
	public AudioSampleFormat sampleFormat() {
		return get("sample_fmt", s -> AudioSampleFormat.byName(s));
	}

	/**
	 * @return
	 */
	public OptionalInt channels() {
		return getInt("channels");
	}

	/**
	 * @return
	 */
	public ChannelLayout channelLayout() {
		return get("channel_layout", s -> ChannelLayout.byName(s));
	}

	/**
	 * @return
	 */
	public OptionalInt bitsPerSample() {
		return getInt("bits_per_sample");
	}

}
