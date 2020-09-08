package org.fagu.fmv.mymedia.movie.list.datatype;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.util.Optional;

import org.fagu.fmv.ffmpeg.FFHelper;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.mymedia.movie.list.DataType;


/**
 * @author Utilisateur
 * @created 4 mai 2018 21:41:23
 */
public class FFDataType implements DataType<MovieMetadatas> {

	public static final DataType<MovieMetadatas> FF = new CachedDataType<>(new FFDataType());

	private FFDataType() {}

	@Override
	public Optional<MovieMetadatas> extractData(File file) {
		try {
			return Optional.of(FFHelper.videoMetadatas(file));
		} catch(Exception e) {
			return Optional.empty();
		}
	}

}
