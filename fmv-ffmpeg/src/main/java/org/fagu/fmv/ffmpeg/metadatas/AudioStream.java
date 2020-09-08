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
import java.util.StringJoiner;

import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.AudioSampleFormat;
import org.fagu.fmv.ffmpeg.utils.ChannelLayout;


/**
 * @author f.agu
 */
public class AudioStream extends Stream {

	public AudioStream(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		super(movieMetadatas, map);
	}

	@Override
	public Type type() {
		return Type.AUDIO;
	}

	public Optional<Integer> sampleRate() {
		return getInt("sample_rate");
	}

	public Optional<AudioSampleFormat> sampleFormat() {
		return get("sample_fmt", AudioSampleFormat::byName);
	}

	public Optional<Integer> channels() {
		return getInt("channels");
	}

	public Optional<ChannelLayout> channelLayout() {
		return get("channel_layout", ChannelLayout::byName);
	}

	public Optional<Integer> bitsPerSample() {
		return getInt("bits_per_sample");
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",");
		language().ifPresent(joiner::add);
		codecName().ifPresent(joiner::add);
		return new StringBuilder(100)
				.append("AudioStream[").append(joiner.toString()).append(']')
				.toString();
	}

}
