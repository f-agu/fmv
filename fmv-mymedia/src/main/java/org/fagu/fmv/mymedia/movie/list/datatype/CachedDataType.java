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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.fagu.fmv.mymedia.movie.list.DataType;


/**
 * @author Utilisateur
 * @created 4 mai 2018 21:43:27
 */
public class CachedDataType<T> implements DataType<T> {

	private final Map<File, Optional<T>> cacheMap;

	private final DataType<T> delegated;

	public CachedDataType(DataType<T> delegated) {
		this(delegated, 10);
	}

	public CachedDataType(DataType<T> delegated, int maxSize) {
		this.delegated = Objects.requireNonNull(delegated);
		cacheMap = new LinkedHashMap<File, Optional<T>>() {

			private static final long serialVersionUID = - 3158591302887326534L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<File, Optional<T>> eldest) {
				return size() > maxSize;
			}
		};
	}

	@Override
	public Optional<T> extractData(File file) {
		return cacheMap.computeIfAbsent(file, delegated::extractData);
	}

}
