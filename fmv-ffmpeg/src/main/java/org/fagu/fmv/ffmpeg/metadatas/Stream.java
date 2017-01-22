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
import java.util.NavigableMap;
import java.util.OptionalInt;

import org.apache.commons.lang3.math.NumberUtils;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.ffmpeg.utils.FrameRate;

import net.sf.json.JSONObject;


/**
 * @author f.agu
 */
public abstract class Stream extends InfoBase {

	private static final Map<String, StreamFactory> STREAM_FACTORY_MAP = getStreamFactories();

	/**
	 * @param movieMetadatas
	 * @param map
	 */
	protected Stream(MovieMetadatas movieMetadatas, NavigableMap<String, Object> map) {
		super(movieMetadatas, map);
	}

	/**
	 * @param jsonObject
	 * @param movieMetadatas
	 * @return
	 */
	public static Stream create(JSONObject jsonObject, MovieMetadatas movieMetadatas) {
		NavigableMap<String, Object> map = MovieMetadatas.createMap(jsonObject);
		String codecType = (String)map.get("codec_type");
		Stream stream = createStream(codecType, map, movieMetadatas);
		if(stream != null) {
			return stream;
		}
		stream = createStream(null, map, movieMetadatas);
		if(stream != null) {
			return stream;
		}
		return new UnknownStream(movieMetadatas, map);
	}

	/**
	 * @param type
	 * @param streamFactory
	 */
	public static void setFactory(String type, StreamFactory streamFactory) {
		STREAM_FACTORY_MAP.put(type, streamFactory);
	}

	/**
	 * @param type
	 * @return
	 */
	public boolean is(Type type) {
		return type == type();
	}

	/**
	 * @return
	 */
	public int index() {
		return getInt("index").getAsInt();
	}

	/**
	 * @return
	 */
	public String codecName() {
		return getString("codec_name");
	}

	/**
	 * @return
	 */
	public String codecLongName() {
		return getString("codec_long_name");
	}

	/**
	 * @return
	 */
	public String codecType() {
		return getString("codec_type");
	}

	/**
	 * @return
	 */
	public Fraction codecTimeBase() {
		return getFraction("codec_time_base");
	}

	/**
	 * @return
	 */
	public String codecTag() {
		return getString("codec_tag");
	}

	/**
	 * @return
	 */
	public String codecTagString() {
		return getString("codec_tag_string");
	}

	/**
	 * @return
	 */
	public FrameRate frameRate() {
		return getFrameRate("r_frame_rate");
	}

	/**
	 * @return
	 */
	public FrameRate averageFrameRate() {
		return getFrameRate("avg_frame_rate");
	}

	/**
	 * @return
	 */
	public Fraction timeBase() {
		return getFraction("time_base");
	}

	/**
	 * @return
	 */
	public OptionalInt numberOfFrames() {
		return getInt("nb_frames");
	}

	/**
	 * @return
	 */
	public OptionalInt countEstimateFrames() {
		OptionalInt count = numberOfFrames();
		if(count.isPresent()) {
			return count;
		}
		FrameRate frameRate = frameRate();
		if(frameRate == null) {
			frameRate = averageFrameRate();
		}
		if(frameRate == null) {
			return OptionalInt.empty();
		}
		Duration duration = duration();
		if(duration == null) {
			Object totDurObj = movieMetadatas.getFormat().tag("totalduration");
			if(totDurObj != null) {
				int totDur = NumberUtils.toInt(String.valueOf(totDurObj));
				if(totDur > 0) {
					duration = Duration.valueOf(totDur);
				}
			}
		}
		if(duration == null) {
			duration = movieMetadatas.getFormat().duration();
		}

		if(duration == null) {
			OptionalInt dts = durationTimeBase();
			if( ! dts.isPresent()) {
				return OptionalInt.empty();
			}
			Fraction timeBase = timeBase();
			if(timeBase == null) {
				return OptionalInt.empty();
			}
			duration = Duration.valueOf(dts.getAsInt() * timeBase.doubleValue());
		}

		return OptionalInt.of((int)(frameRate.doubleValue() * duration.toSeconds()));
	}

	/**
	 * PTS
	 *
	 * @return
	 */
	public OptionalInt startPresentationTimeStamp() {
		return getInt("start_pts");
	}

	/**
	 * @param name
	 * @return
	 */
	public Map<String, Object> dispositions() {
		return sub("disposition");
	}

	/**
	 * @param name
	 * @return
	 */
	public Object disposition(String name) {
		return dispositions().get(name);
	}

	/**
	 * @return
	 */
	public boolean containsAttachedPicture() {
		return Integer.valueOf(1).equals(disposition("attached_pic"));
	}

	/**
	 * @return
	 */
	public boolean isDefaultStream() {
		return "1".equals(disposition("default"));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append("Stream[").append(codecType()).append(',').append(']');
		return buf.toString();
	}

	// **********************************************

	/**
	 * @return
	 */
	public abstract Type type();

	// **********************************************

	/**
	 * @return
	 */
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

	/**
	 * @param code
	 * @param map
	 * @param movieMetadatas
	 * @return
	 */
	private static Stream createStream(String code, NavigableMap<String, Object> map, MovieMetadatas movieMetadatas) {
		StreamFactory streamFactory = STREAM_FACTORY_MAP.get(code);
		if(streamFactory != null) {
			Stream stream = streamFactory.create(map, movieMetadatas);
			if(stream != null) {
				return stream;
			}
		}
		return null;
	}
}
