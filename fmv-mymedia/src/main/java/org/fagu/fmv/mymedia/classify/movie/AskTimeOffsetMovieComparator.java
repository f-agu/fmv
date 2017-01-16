package org.fagu.fmv.mymedia.classify.movie;

/*
 * #%L
 * fmv-mymedia
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

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.mymedia.classify.AskTimeOffsetComparator;
import org.fagu.fmv.mymedia.file.MovieFinder;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class AskTimeOffsetMovieComparator extends AskTimeOffsetComparator<Movie> implements MovieTimeComparator {

	/**
	 * @param movieFinder
	 */
	public AskTimeOffsetMovieComparator(MovieFinder movieFinder) {
		super(movieFinder);
	}

	/**
	 * @param metadatas
	 * @return
	 */
	public String getMetadatasKey(Metadatas metadatas) {
		MovieMetadatas movieMetadatas = (MovieMetadatas)metadatas;
		List<String> keys = new ArrayList<>();

		VideoStream videoStream = movieMetadatas.getVideoStream();
		if(videoStream != null) {
			String handlerName = videoStream.handlerName();
			if(StringUtils.isNotEmpty(handlerName)) {
				keys.add(handlerName);
			}
			Size size = videoStream.size();
			if(size != null) {
				keys.add(size.toString());
			}
		}

		AudioStream audioStream = movieMetadatas.getAudioStream();
		if(audioStream != null) {
			String handlerName = audioStream.handlerName();
			if(StringUtils.isNotEmpty(handlerName)) {
				keys.add(handlerName);
			}
			OptionalInt sampleRate = audioStream.sampleRate();
			if(sampleRate.isPresent()) {
				keys.add(sampleRate.getAsInt() + "Hz");
			}
		}
		return keys.stream().collect(Collectors.joining(" "));
	}

}
