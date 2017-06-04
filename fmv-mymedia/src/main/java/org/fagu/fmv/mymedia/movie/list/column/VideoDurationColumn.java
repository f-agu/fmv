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
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.metadatas.Format;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.mymedia.movie.list.Column;


/**
 * @author f.agu
 */
public class VideoDurationColumn implements Column {

	/**
	 *
	 */
	public VideoDurationColumn() {}

	/**
	 * @see org.fagu.fmv.mymedia.movie.list.Column#title()
	 */
	@Override
	public String title() {
		return "Durée";
	}

	/**
	 * @see org.fagu.fmv.mymedia.movie.list.Column#value(Path, java.io.File, Supplier)
	 */
	@Override
	public String value(Path rootPath, File file, Supplier<MovieMetadatas> movieMetadatasSupplier) {
		Format format = movieMetadatasSupplier.get().getFormat();
		Duration duration = format.duration().orElse(null);
		if(duration == null) {
			return null;
		}
		String durstr = duration.toString();
		return StringUtils.substringBefore(durstr, ".");
	}

}
