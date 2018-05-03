package org.fagu.fmv.ffmpeg.operation;

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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.fagu.fmv.ffmpeg.Resampler;
import org.fagu.fmv.ffmpeg.Scaler;
import org.fagu.fmv.ffmpeg.coder.AAC;
import org.fagu.fmv.ffmpeg.coder.Encoders;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.require.Require;
import org.fagu.fmv.ffmpeg.require.RequiredException;
import org.fagu.fmv.ffmpeg.utils.BitStreamFilter;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.ffmpeg.utils.VSync;


/**
 * @author f.agu
 */
public class OutputProcessor extends Processor<OutputProcessor> {

	private final MediaOutput output;

	private final OutputParameters outputParameters;

	private final FilterNaming filterNaming;

	private Scaler scaler;

	private Resampler resampler;

	private Map map;

	/**
	 * @param outputParameters
	 * @param output
	 * @param filterNaming
	 * @param index
	 * @param require
	 */
	protected OutputProcessor(OutputParameters outputParameters, MediaOutput output, FilterNaming filterNaming, int index, Require require) {
		super(outputParameters, output, index, require);
		this.outputParameters = outputParameters;
		this.output = output;
		this.filterNaming = filterNaming;

		MetadataVersion.add(this);
	}

	/**
	 * @return
	 */
	public MediaOutput getMediaOutput() {
		return output;
	}

	/**
	 * @return
	 */
	public OutputParameters getOutputParameters() {
		return outputParameters;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Processor#codec(org.fagu.fmv.ffmpeg.operation.Type, java.lang.String)
	 */
	@Override
	public OutputProcessor codec(Type type, String codec) {
		require().encoder(codec);
		return super.codec(type, codec);
	}

	/**
	 * @param videoCodec
	 * @return
	 */
	public OutputProcessor videoCodecCopy() {
		return videoCodec("copy");
	}

	/**
	 * @param type
	 * @param rate
	 * @return
	 */
	public OutputProcessor bitRate(Type type, String rate) {
		outputParameters.add(Parameter.before(output, Type.concat("-b", type), rate));
		return this;
	}

	/**
	 * @param rate
	 * @return
	 */
	public OutputProcessor audioBitRate(int rate) {
		if(rate <= 0) {
			throw new IllegalArgumentException("Rate must be positive: " + rate);
		}
		return bitRate(Type.AUDIO, Integer.toString(rate));
	}

	/**
	 * @param rate
	 * @return
	 */
	public OutputProcessor audioBitRate(String rate) {
		return bitRate(Type.AUDIO, rate);
	}

	/**
	 * @param count
	 * @return
	 */
	public OutputProcessor audioChannel(int count) {
		outputParameters.add(Parameter.before(output, "-ac", Integer.toString(count)));
		return this;
	}

	/**
	 * @param frequency
	 * @return
	 */
	public OutputProcessor audioSamplingFrequency(int frequency) {
		return frameRate(Type.AUDIO, FrameRate.perSecond(frequency));
	}

	/**
	 * @param frequency
	 * @return
	 */
	public OutputProcessor videoRate(int frequency) {
		return frameRate(Type.VIDEO, FrameRate.perSecond(frequency));
	}

	/**
	 * Video sync method. For compatibility reasons old values can be specified as numbers. Newly added values will have
	 * to be specified as strings always.
	 *
	 * @param vSync
	 * @return
	 */
	public OutputProcessor videoSync(VSync vSync) {
		outputParameters.add(Parameter.before(output, "-vsync", vSync.getValue()));
		return this;
	}

	/**
	 * Disable video recording
	 *
	 * @return
	 */
	public OutputProcessor disableVideo() {
		outputParameters.add(Parameter.before(output, "-vn"));
		return this;
	}

	/**
	 * Finish encoding when the shortest input stream ends.
	 *
	 * @return
	 */
	public OutputProcessor shortest() {
		outputParameters.add(Parameter.before(output, "-shortest"));
		return this;
	}

	/**
	 * Overwrite output files without asking.
	 *
	 * @return
	 */
	public OutputProcessor overwrite() {
		outputParameters.add(Parameter.before(output, "-y"));
		return this;
	}

	/**
	 * Do not overwrite output files, and exit immediately if a specified output file already exists.
	 *
	 * @return
	 */
	public OutputProcessor doNotOverwrite() {
		outputParameters.add(Parameter.before(output, "-n"));
		return this;
	}

	/**
	 * Set the file size limit, expressed in bytes.
	 *
	 * @param size
	 * @return
	 */
	public OutputProcessor limitSizeBytes(long size) {
		outputParameters.add(Parameter.before(output, "-fs", Long.toString(size)));
		return this;
	}

	/**
	 * @param scale
	 * @return
	 */
	public OutputProcessor numberOfVideoFrameToRecord(int number) {
		return numberOfFrameToRecord(Type.VIDEO, number);
	}

	/**
	 * Stop writing to the stream after framecount frames.
	 *
	 * @param scale
	 * @return
	 */
	public OutputProcessor numberOfFrameToRecord(Type type, int number) {
		outputParameters.add(Parameter.before(output, Type.concat("-frames", type), Integer.toString(number)));
		return this;
	}

	/**
	 * @param scale
	 * @return
	 */
	public OutputProcessor qualityScale(int scale) {
		return qualityScaleAudio(scale).qualityScaleVideo(scale);
	}

	/**
	 * @param scale
	 * @return
	 */
	public OutputProcessor qualityScaleAudio(int scale) {
		return qualityScale(Type.AUDIO, scale);
	}

	/**
	 * @param scale
	 * @return
	 */
	public OutputProcessor qualityScaleVideo(int scale) {
		return qualityScale(Type.VIDEO, scale);
	}

	/**
	 * Use fixed quality scale (VBR). The meaning of q/qscale is codec-dependent. If qscale is used without a
	 * stream_specifier then it applies only to the video stream, this is to maintain compatibility with previous
	 * behavior and as specifying the same codec specific value to 2 different codecs that is audio and video generally
	 * is not what is intended when no stream_specifier is used.
	 *
	 * @param type
	 * @param scale
	 * @return
	 */
	public OutputProcessor qualityScale(Type type, int scale) {
		if(scale < 0 || scale > 31) {
			throw new IllegalArgumentException("Scale must between 0 and 31");
		}
		outputParameters.add(Parameter.before(output, Type.concat("-q", type), Integer.toString(scale)));
		return this;
	}

	/**
	 * @param metadata
	 * @return
	 */
	public OutputProcessor metadataStream(Type type, String key, String value) {
		return metadataStream(type, - 1, key, value);
	}

	/**
	 * @param type
	 * @param streamIndex
	 * @param key
	 * @param value
	 * @return
	 */
	public OutputProcessor metadataStream(Type type, int streamIndex, String key, String value) {
		return metadata('s', type, streamIndex, key, value);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public OutputProcessor metadataGlobal(String key, String value) {
		return metadata('g', null, null, key, value);
	}

	/**
	 * @param chapterIndex
	 * @param key
	 * @param value
	 * @return
	 */
	public OutputProcessor metadataChapter(int chapterIndex, String key, String value) {
		return metadata('c', null, chapterIndex, key, value);
	}

	/**
	 * @return
	 */
	public OutputProcessor codecAutoSelectAAC() {
		if(Encoders.AAC.exists()) {
			try {
				return codec(AAC.build());
			} catch(RequiredException e) {
				// ignore
			}
		}
		return this;
	}

	/**
	 * @param scaler
	 * @return
	 */
	public OutputProcessor scaler(Scaler scaler) {
		if(this.scaler == null) {
			this.scaler = scaler;
			scaler.eventAdded(this, output);
		}
		return this;
	}

	/**
	 * @param resampler
	 * @return
	 */
	public OutputProcessor resampler(Resampler resampler) {
		if(this.resampler == null) {
			this.resampler = resampler;
			resampler.eventAdded(this, output);
		}
		return this;
	}

	/**
	 * @return
	 */
	public OutputProcessor disableChapters() {
		return mapChapters( - 1);
	}

	/**
	 * @param type
	 * @param bitStreamFilters
	 * @return
	 */
	public OutputProcessor bitStream(Type type, BitStreamFilter... bitStreamFilters) {
		return bitStream(type, Arrays.asList(bitStreamFilters));
	}

	/**
	 * @param type
	 * @param bitStreamFilters
	 * @return
	 */
	public OutputProcessor bitStream(Type type, Collection<BitStreamFilter> bitStreamFilters) {
		if(CollectionUtils.isNotEmpty(bitStreamFilters)) {
			outputParameters.add(Parameter.before(output, "-bsf:" + type.code(), bitStreamFilters.stream().map(BitStreamFilter::getName).collect(
					Collectors.joining(","))));
		}
		return this;
	}

	/**
	 * @return
	 */
	public Map map() {
		if(map == null) {
			map = new Map(this, filterNaming);
		}
		return map;
	}

	/**
	 * @param inputProcessor
	 * @return
	 * @throws IOException
	 */
	public OutputProcessor mapAllStreams(InputProcessor inputProcessor) throws IOException {
		return mapStreams(stream -> true, inputProcessor);
	}

	/**
	 * @param type
	 * @param inputProcessor
	 * @return
	 * @throws IOException
	 */
	public OutputProcessor mapStreamsBy(Type type, InputProcessor inputProcessor) throws IOException {
		return mapStreams(stream -> stream.is(type), inputProcessor);
	}

	/**
	 * @param predicate
	 * @param forInputProcessor
	 * @return
	 * @throws IOException
	 */
	public OutputProcessor mapStreams(Predicate<Stream> predicate, InputProcessor forInputProcessor) throws IOException {
		Map myMap = map();
		MovieMetadatas movieMetadatas = forInputProcessor.getMovieMetadatas();
		for(Stream stream : movieMetadatas.getStreams()) {
			if(predicate.test(stream)) {
				myMap.streams(stream).input(forInputProcessor);
			}
		}
		return this;
	}

	// *******************************************************

	/**
	 * @param outputFile
	 * @return
	 */
	private OutputProcessor mapChapters(int inputFileIndex) {
		outputParameters.add(Parameter.before(output, "map_chapters", Integer.toString(inputFileIndex)));
		return this;
	}

	/**
	 * @param perMetadata
	 * @param type
	 * @param streamIndex
	 * @param key
	 * @param value
	 * @return
	 */
	private OutputProcessor metadata(char perMetadata, Type type, Integer streamIndex, String key, String value) {
		StringBuilder buf = new StringBuilder();
		buf.append("-metadata:").append(perMetadata);
		if(type != null) {
			buf.append(':').append(type.code());
		}
		if(streamIndex != null) {
			buf.append(':').append(streamIndex >= 0 ? streamIndex : getIndex());
		}
		outputParameters.add(Parameter.before(output, buf.toString(), key + '=' + value));
		return this;
	}
}
