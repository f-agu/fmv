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
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.mymedia.movie.list.Column;
import org.fagu.fmv.utils.collection.MapList;


/**
 * @author f.agu
 */
public class StreamsTypeCountColumn implements Column {

	/**
	 * @see org.fagu.fmv.mymedia.movie.list.Column#title()
	 */
	@Override
	public String title() {
		return "Nombre de flux";
	}

	/**
	 * @see org.fagu.fmv.mymedia.movie.list.Column#value(Path, java.io.File, Supplier)
	 */
	@Override
	public Optional<String> value(Path rootPath, File file, Supplier<Optional<MovieMetadatas>> movieMetadatasOptSupplier) {
		MovieMetadatas movieMetadatas = movieMetadatasOptSupplier.get().orElse(null);
		if(movieMetadatas == null) {
			return Optional.empty();
		}
		MapList<Type, Stream> typeMap = movieMetadatas.toTypeMap();
		if(typeMap.isEmpty()) {
			return Optional.empty();
		}
		StringJoiner joiner = new StringJoiner(", ");
		Type[] values = {Type.VIDEO, Type.AUDIO, Type.SUBTITLE, Type.DATA, Type.ATTACHEMENTS, Type.UNKNOWN};
		for(Type type : values) {
			List<Stream> list = typeMap.get(type);
			if(list != null) {
				joiner.add(type.name().toLowerCase() + "(" + list.size() + ")");
			}
		}
		return Optional.of(joiner.toString());
	}

}
