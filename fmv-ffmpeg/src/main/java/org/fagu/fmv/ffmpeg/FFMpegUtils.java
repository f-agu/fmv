package org.fagu.fmv.ffmpeg;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import java.util.OptionalInt;

import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;


/**
 * @author f.agu
 */
public class FFMpegUtils {

	/**
	 * 
	 */
	private FFMpegUtils() {}

	// **** audio sampleRate

	/**
	 * @param movieMetadatas
	 * @param minOrDefaultSampleRate
	 * @return
	 */
	public static int minAudioSampleRate(MovieMetadatas movieMetadatas, int minOrDefaultSampleRate) {
		if(movieMetadatas == null) {
			return minOrDefaultSampleRate;
		}
		return minAudioSampleRate(movieMetadatas.getAudioStream(), minOrDefaultSampleRate);
	}

	/**
	 * @param audioStream
	 * @param minOrDefaultSampleRate
	 * @return
	 */
	public static int minAudioSampleRate(AudioStream audioStream, int minOrDefaultSampleRate) {
		if(audioStream != null) {
			OptionalInt sampleRate = audioStream.sampleRate();
			if(sampleRate.isPresent()) {
				return Math.min(minOrDefaultSampleRate, sampleRate.getAsInt());
			}
		}
		return minOrDefaultSampleRate;
	}

	// **** audio bitRate

	/**
	 * @param movieMetadatas
	 * @param minOrDefaultBitRate
	 * @return
	 */
	public static int minAudioBitRate(MovieMetadatas movieMetadatas, int minOrDefaultBitRate) {
		if(movieMetadatas == null) {
			return minOrDefaultBitRate;
		}
		return minAudioBitRate(movieMetadatas.getAudioStream(), minOrDefaultBitRate);
	}

	/**
	 * @param audioStream
	 * @param minOrDefaultBitRate
	 * @return
	 */
	public static int minAudioBitRate(AudioStream audioStream, int minOrDefaultBitRate) {
		if(audioStream != null) {
			OptionalInt bitRate = audioStream.bitRate();
			if(bitRate.isPresent()) {
				return Math.min(minOrDefaultBitRate, bitRate.getAsInt());
			}
		}
		return minOrDefaultBitRate;
	}

	// **** audio channel

	/**
	 * @param movieMetadatas
	 * @param minOrDefaultBitRate
	 * @return
	 */
	public static int minAudioChannel(MovieMetadatas movieMetadatas, int minOrDefaultChannel) {
		if(movieMetadatas == null) {
			return minOrDefaultChannel;
		}
		return minAudioBitRate(movieMetadatas.getAudioStream(), minOrDefaultChannel);
	}

	/**
	 * @param audioStream
	 * @param minOrDefaultChannel
	 * @return
	 */
	public static int minAudioChannel(AudioStream audioStream, int minOrDefaultChannel) {
		if(audioStream != null) {
			OptionalInt channel = audioStream.channels();
			if(channel.isPresent()) {
				return Math.min(minOrDefaultChannel, channel.getAsInt());
			}
		}
		return minOrDefaultChannel;
	}

}
