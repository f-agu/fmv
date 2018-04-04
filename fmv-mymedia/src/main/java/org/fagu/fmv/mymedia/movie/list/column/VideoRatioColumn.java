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
import java.util.Optional;
import java.util.function.Supplier;

import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.mymedia.movie.list.Column;
import org.fagu.fmv.utils.media.Ratio;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class VideoRatioColumn implements Column {

	/**
	 * @see org.fagu.fmv.mymedia.movie.list.Column#title()
	 */
	@Override
	public String title() {
		return "Ratio";
	}

	/**
	 * @see org.fagu.fmv.mymedia.movie.list.Column#value(Path, java.io.File, Supplier)
	 */
	@Override
	public Optional<String> value(Path rootPath, File file, Supplier<Optional<MovieMetadatas>> movieMetadatasOptSupplier) {
		return movieMetadatasOptSupplier.get()
				.map(MovieMetadatas::getVideoStream)
				.map(VideoStream::size)
				.map(Size::getRatio)
				.map(Ratio::toString);
	}

}
