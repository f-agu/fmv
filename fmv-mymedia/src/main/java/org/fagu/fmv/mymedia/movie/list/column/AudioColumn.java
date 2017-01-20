package org.fagu.fmv.mymedia.movie.list.column;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2016 fagu
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
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.mymedia.movie.list.Column;


/**
 * @author f.agu
 */
public class AudioColumn implements Column {

	/**
	 *
	 */
	public AudioColumn() {}

	/**
	 * @see org.fagu.fmv.mymedia.movie.list.Column#title()
	 */
	@Override
	public String title() {
		return "Piste audio";
	}

	/**
	 * @see org.fagu.fmv.mymedia.movie.list.Column#value(Path, java.io.File, Supplier)
	 */
	@Override
	public String value(Path rootPath, File file, Supplier<MovieMetadatas> movieMetadatasSupplier) {
		List<AudioStream> audioStreams = movieMetadatasSupplier.get().getAudioStreams();
		if(CollectionUtils.isEmpty(audioStreams)) {
			return null;
		}
		StringJoiner joiner = new StringJoiner(", ");
		for(AudioStream audioStream : audioStreams) {
			String name = audioStream.title();
			if(name == null) {
				name = audioStream.language();
			}
			if(name == null || "und".equals(name)) {
				name = "?";
			}
			joiner.add(name);
		}
		String list = joiner.toString();
		return audioStreams.size() + (list.isEmpty() ? "" : ": " + list);
	}

}