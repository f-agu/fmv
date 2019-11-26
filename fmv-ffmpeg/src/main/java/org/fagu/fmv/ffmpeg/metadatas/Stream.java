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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.fagu.fmv.ffmpeg.format.Formats;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public abstract class Stream extends InfoBase {

	private static final Map<String, StreamFactory> STREAM_FACTORY_MAP = getStreamFactories();

	protected Stream(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		super(movieMetadatas, map);
	}

	public static Stream create(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		String codecType = (String)map.get("codec_type");
		Stream stream = createStream(codecType, movieMetadatas, map);
		if(stream != null) {
			return stream;
		}
		stream = createStream(null, movieMetadatas, map);
		if(stream != null) {
			return stream;
		}
		return new UnknownStream(movieMetadatas, map);
	}

	public static void setFactory(String type, StreamFactory streamFactory) {
		STREAM_FACTORY_MAP.put(type, streamFactory);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(type().name().toLowerCase()) + "Stream-" + index();
	}

	public boolean is(Type type) {
		return type == type();
	}

	public int index() {
		return getInt("index").orElseThrow(RuntimeException::new);
	}

	public boolean isCodec(Formats formats) {
		Optional<String> codec = codecName();
		if(codec.isPresent()) {
			return codec.get().equalsIgnoreCase(formats.getName());
		}
		return false;
	}

	public Optional<String> codecName() {
		return getString("codec_name");
	}

	public Optional<String> codecLongName() {
		return getString("codec_long_name");
	}

	public Optional<String> codecType() {
		return getString("codec_type");
	}

	public Optional<Fraction> codecTimeBase() {
		return getFraction("codec_time_base");
	}

	public Optional<String> codecTag() {
		return getString("codec_tag");
	}

	public Object codecTagString() {
		return get("codec_tag_string");
	}

	public Optional<FrameRate> frameRate() {
		return getFrameRate("r_frame_rate");
	}

	public Optional<FrameRate> averageFrameRate() {
		return getFrameRate("avg_frame_rate");
	}

	public Optional<Fraction> timeBase() {
		return getFraction("time_base");
	}

	public Optional<Integer> numberOfFrames() {
		return getInt("nb_frames");
	}

	public Optional<Integer> countEstimateFrames() {
		Optional<Integer> count = numberOfFrames();
		if(count.isPresent()) {
			return count;
		}
		FrameRate frameRate = frameRate().orElse(null);
		if(frameRate == null) {
			frameRate = averageFrameRate().orElse(null);
		}
		if(frameRate == null) {
			return Optional.empty();
		}
		Duration duration = duration().orElse(null);
		if(duration == null) {
			Optional<Object> totDurObj = movieMetadatas.getFormat().tag("totalduration");
			if(totDurObj.isPresent()) {
				int totDur = NumberUtils.toInt(String.valueOf(totDurObj.get()));
				if(totDur > 0) {
					duration = Duration.valueOf(totDur);
				}
			}
		}
		if(duration == null) {
			duration = movieMetadatas.getFormat().duration().orElse(null);
		}

		if(duration == null) {
			Optional<Integer> dts = durationTimeBase();
			if( ! dts.isPresent()) {
				return Optional.empty();
			}
			Fraction timeBase = timeBase().orElse(null);
			if(timeBase == null) {
				return Optional.empty();
			}
			duration = Duration.valueOf(dts.get() * timeBase.doubleValue());
		}

		return Optional.of((int)(frameRate.doubleValue() * duration.toSeconds()));
	}

	public Optional<Integer> startPresentationTimeStamp() {
		return getInt("start_pts");
	}

	public Map<String, Object> dispositions() {
		return sub("disposition");
	}

	public Object disposition(String name) {
		return dispositions().get(name);
	}

	public boolean containsAttachedPicture() {
		return Integer.valueOf(1).equals(disposition("attached_pic"));
	}

	public boolean isDefaultStream() {
		return "1".equals(disposition("default"));
	}

	@Override
	public String toString() {
		return new StringBuilder(100)
				.append("Stream[").append(codecType().orElse("?")).append(']')
				.toString();
	}

	// **********************************************

	public abstract Type type();

	// **********************************************

	private static Map<String, StreamFactory> getStreamFactories() {
		Map<String, StreamFactory> map = new HashMap<>(8);
		map.put("attachments", new AttachmentsStreamFactory());
		map.put("audio", new AudioStreamFactory());
		map.put("data", new DataStreamFactory());
		map.put("subtitle", new SubtitleStreamFactory());
		map.put("video", new VideoStreamFactory());
		map.put(null, new UnknownStreamFactory());
		return map;
	}

	private static Stream createStream(String code, MovieMetadatas movieMetadatas, Map<String, Object> map) {
		StreamFactory streamFactory = STREAM_FACTORY_MAP.get(code);
		if(streamFactory != null) {
			Stream stream = streamFactory.create(movieMetadatas, map);
			if(stream != null) {
				return stream;
			}
		}
		return null;
	}
}
