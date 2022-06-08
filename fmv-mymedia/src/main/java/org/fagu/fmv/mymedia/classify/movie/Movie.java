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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class Movie implements Media {

	private final File file;

	private final MovieMetadatas videoMetadatas;

	public Movie(File file, MovieMetadatas infos) {
		this.file = file;
		this.videoMetadatas = infos;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public MovieMetadatas getMetadatas() {
		return videoMetadatas;
	}

	@Override
	public long getTime() {
		Optional<Date> creationDate = videoMetadatas.getVideoStream().creationDate();
		return creationDate.isPresent() ? creationDate.get().getTime() : 0;
	}

	@Override
	public String getDevice() {
		List<String> keys = new ArrayList<>();

		VideoStream videoStream = videoMetadatas.getVideoStream();
		if(videoStream != null) {
			String handlerName = videoStream.handlerName().orElse(null);
			if(StringUtils.isNotEmpty(handlerName)) {
				keys.add(handlerName);
			}
			Size size = videoStream.size();
			if(size != null) {
				keys.add(size.toString());
			}
		}

		AudioStream audioStream = videoMetadatas.getAudioStream();
		if(audioStream != null) {
			String handlerName = audioStream.handlerName().orElse(null);
			if(StringUtils.isNotEmpty(handlerName)) {
				keys.add(handlerName);
			}
			Optional<Integer> sampleRate = audioStream.sampleRate();
			if(sampleRate.isPresent()) {
				keys.add(sampleRate.get() + "Hz");
			}
		}
		return keys.stream().collect(Collectors.joining(" "));
	}

}
