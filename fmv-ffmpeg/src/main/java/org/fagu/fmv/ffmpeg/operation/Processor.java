package org.fagu.fmv.ffmpeg.operation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fagu.fmv.ffmpeg.coder.Coder;
import org.fagu.fmv.ffmpeg.operation.Parameter.Way;
import org.fagu.fmv.ffmpeg.require.Require;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.ffmpeg.utils.Time;
import org.fagu.fmv.utils.collection.MapMap;
import org.fagu.fmv.utils.collection.MultiValueMaps;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public abstract class Processor<P extends Processor<?>> {

	private final int index;

	protected final IOEntity ioEntity;

	private final IOParameters ioParameters;

	private final Require require;

	private final MapMap<String, Type, Coder<?>> coderMap;

	/**
	 * @param ioParameters
	 * @param ioEntity
	 * @param index
	 * @param require
	 */
	public Processor(IOParameters ioParameters, IOEntity ioEntity, int index, Require require) {
		this.ioParameters = Objects.requireNonNull(ioParameters);
		this.ioEntity = Objects.requireNonNull(ioEntity);
		this.index = index;
		this.require = require;
		coderMap = MultiValueMaps.hashMapHashMap();
	}

	/**
	 * @return the inputIndex
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * When used as an input option (before -i), limit the duration of data read from the input file.
	 *
	 * When used as an output option (before an output filename), stop writing the output after its duration reaches
	 * duration.
	 *
	 * duration may be a number in seconds, or in hh:mm:ss[.xxx] form.
	 *
	 * -to and -t are mutually exclusive and -t has priority.
	 *
	 *
	 * @param frameRate
	 * @return
	 */
	public P duration(Duration duration) {
		add(Parameter.before(ioEntity, "-t", duration.toString()));
		return getThis();
	}

	/**
	 * When used as an input option (before -i), seeks in this input file to position. Note the in most formats it is
	 * not possible to seek exactly, so ffmpeg will seek to the closest seek point before position. When transcoding and
	 * -accurate_seek is enabled (the default), this extra segment between the seek point and position will be decoded
	 * and discarded. When doing stream copy or when -noaccurate_seek is used, it will be preserved.
	 *
	 * When used as an output option (before an output filename), decodes but discards input until the timestamps reach
	 * position.
	 *
	 * position may be either in seconds or in hh:mm:ss[.xxx] form.
	 *
	 *
	 * @param time
	 * @return
	 */
	public P timeSeek(Time time) {
		add(Parameter.before(ioEntity, "-ss", time.toString()));
		return getThis();
	}

	/**
	 * Set frame size.
	 *
	 * As an input option, this is a shortcut for the video_size private option, recognized by some demuxers for which
	 * the frame size is either not stored in the file or is configurable – e.g. raw video or video grabbers.
	 *
	 * As an output option, this inserts the scale video filter to the end of the corresponding filtergraph. Please use
	 * the scale filter directly to insert it at the beginning or some other place.
	 *
	 * The format is ‘wxh’ (default - same as source).
	 *
	 *
	 * @param frameRate
	 * @return
	 */
	public P size(Size size) {
		add(Parameter.before(ioEntity, "-s", size.toString()));
		return getThis();
	}

	/**
	 * @param frameRate
	 * @return
	 */
	public P frameRate(FrameRate frameRate) {
		return frameRate(null, frameRate);
	}

	/**
	 * Set frame rate (Hz value, fraction or abbreviation).
	 *
	 * As an input option, ignore any timestamps stored in the file and instead generate timestamps assuming constant
	 * frame rate fps. This is not the same as the -framerate option used for some input formats like image2 or v4l2 (it
	 * used to be the same in older versions of FFmpeg). If in doubt use -framerate instead of the input option -r.
	 *
	 * As an output option, duplicate or drop input frames to achieve constant output frame rate fps.
	 *
	 * @param type
	 * @param frameRate
	 * @return
	 */
	public P frameRate(Type type, FrameRate frameRate) {
		add(Parameter.before(ioEntity, Type.concat("-r", type), frameRate.toString()));
		return getThis();
	}

	/**
	 * @param format
	 * @return
	 */
	public P format(String format) {
		if(StringUtils.isNotBlank(format)) {
			add(Parameter.before(ioEntity, "-f", format));
		}
		return getThis();
	}

	/**
	 * @param audioCodec
	 * @return
	 */
	public P audioCodec(String audioCodec) {
		return codec(Type.AUDIO, audioCodec);
	}

	/**
	 * @param videoCodec
	 * @return
	 */
	public P videoCodec(String videoCodec) {
		return codec(Type.VIDEO, videoCodec);
	}

	/**
	 * @param type
	 * @param codec
	 * @return
	 */
	public P codecCopy(Type type) {
		return codec(type, "copy");
	}

	/**
	 * Select an encoder (when used before an output file) or a decoder (when used before an input file) for one or more
	 * streams. codec is the name of a decoder/encoder or a special value copy (output only) to indicate that the stream
	 * is not to be re-encoded.
	 *
	 * @param type
	 * @param codec
	 * @return
	 */
	public P codec(Type type, String codec) {
		add(Parameter.before(ioEntity, Type.concat("-codec", type), codec));
		return getThis();
	}

	/**
	 * @param coder
	 * @return
	 */
	public P codec(Coder<?> coder) {
		if( ! coderMap.containsKeys(coder.name(), coder.type())) {
			coderMap.add(coder.name(), coder.type(), coder);
			codec(coder.type(), coder.name());
			coder.eventAdded(this, ioEntity);
			if(coder instanceof LibLog) {
				ioParameters.getOperation().add((LibLog)coder);
			}
		}
		return getThis();
	}

	/**
	 * @param type
	 * @return
	 */
	public Stream<Coder<?>> getCoders(Type type) {
		return coderMap.values()
				.stream()
				.flatMap(map -> map.entrySet().stream())
				.filter(e -> e.getKey() == type)
				.map(Entry::getValue);
	}

	/**
	 * @param coderClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C extends Coder<C>> Stream<C> getCoders(Class<C> coderClass) {
		return coderMap.values()
				.stream()
				.flatMap(map -> map.entrySet().stream())
				.map(Entry::getValue)
				.filter(c -> coderClass.isAssignableFrom(c.getClass()))
				.map(c -> (C)c);
	}

	/**
	 * @param coderClass
	 * @return
	 */
	public java.util.Map<Type, Coder<?>> getCoders(String name) {
		Map<Type, Coder<?>> map = coderMap.get(name);
		return map != null ? Collections.unmodifiableMap(map) : Collections.emptyMap();
	}

	/**
	 * @param type
	 * @param profile
	 * @return
	 */
	public P profile(Type type, String profile) {
		add(Parameter.before(ioEntity, Type.concat("-profile", type), profile));
		return getThis();
	}

	/**
	 * Set the number of times to loop the output. Use -1 for no loop, 0 for looping indefinitely (default).
	 *
	 * @param count
	 * @return
	 */
	public P loop(int count) {
		add(Parameter.before(ioEntity, "-loop", Integer.toString(count)));
		return getThis();
	}

	/**
	 * @param pixelFormat
	 * @return
	 */
	public P pixelFormat(PixelFormat pixelFormat) {
		add(Parameter.before(ioEntity, "-pix_fmt", pixelFormat.toString()));
		return getThis();
	}

	/**
	 * @param parameter
	 * @return
	 */
	public P add(Parameter parameter) {
		ioParameters.add(parameter);
		return getThis();
	}

	/**
	 * @param name
	 * @return
	 */
	public P add(String name) {
		ioParameters.add(Parameter.before(ioEntity, name));
		return getThis();
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public P add(String name, String value) {
		ioParameters.add(Parameter.before(ioEntity, name, value));
		return getThis();
	}

	/**
	 * @return
	 */
	public List<Parameter> getParameters() {
		return getParameters(Way.BEFORE);
	}

	/**
	 * @param way
	 * @return
	 */
	public List<Parameter> getParameters(Way way) {
		return Collections.unmodifiableList(ioParameters.getParameters(ioEntity, way));
	}

	/**
	 * @return
	 */
	public Require require() {
		return require;
	}

	// *******************************************************

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private P getThis() {
		return (P)this;
	}
}
