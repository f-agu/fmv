package org.fagu.fmv.ffmpeg.metadatas;

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
