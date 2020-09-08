package org.fagu.fmv.ffmpeg.metadatas;

import java.util.HashSet;
/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import java.util.Optional;
import java.util.Set;

import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author Oodrive
 * @author f.agu
 * @created 24 avr. 2017 13:58:20
 */
public class MovieMetadatasUtils {

	/**
	 * 
	 */
	private MovieMetadatasUtils() {}

	/**
	 * @param movieMetadatas
	 * @return
	 */
	public static Optional<Duration> getDuration(MovieMetadatas movieMetadatas) {
		VideoStream videoStream = movieMetadatas.getVideoStream();
		if(videoStream != null) {
			return videoStream.duration();
		}
		AudioStream audioStream = movieMetadatas.getAudioStream();
		if(audioStream != null) {
			return audioStream.duration();
		}
		return Optional.empty();
	}

	/**
	 * @param movieMetadatas
	 * @return
	 */
	public static Set<Type> getTypes(MovieMetadatas movieMetadatas) {
		Set<Type> types = new HashSet<>(8);
		for(Type type : Type.values()) {
			if( ! movieMetadatas.getStreams(type).isEmpty()) {
				types.add(type);
			}
		}
		return types;
	}

}
