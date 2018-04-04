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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.fagu.fmv.ffmpeg.metadatas.InfoBase;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.mymedia.movie.list.Column;


/**
 * @author f.agu
 */
public class StreamTypeCountColumn implements Column {

	private final Type type;

	/**
	 * @param type
	 */
	public StreamTypeCountColumn(Type type) {
		this.type = Objects.requireNonNull(type);
	}

	/**
	 * @see org.fagu.fmv.mymedia.movie.list.Column#title()
	 */
	@Override
	public String title() {
		return "Nombre de flux (" + type.name().toLowerCase() + ')';
	}

	/**
	 * @see org.fagu.fmv.mymedia.movie.list.Column#value(Path, java.io.File, Supplier)
	 */
	@Override
	public Optional<String> value(Path rootPath, File file, Supplier<Optional<MovieMetadatas>> movieMetadatasOptSupplier) {
		List<InfoBase> streams = movieMetadatasOptSupplier.get()
				.map(mm -> mm.getStreams(type))
				.orElse(null);
		if(CollectionUtils.isEmpty(streams)) {
			return Optional.empty();
		}
		return Optional.of(Integer.toString(streams.size()));
	}

}
