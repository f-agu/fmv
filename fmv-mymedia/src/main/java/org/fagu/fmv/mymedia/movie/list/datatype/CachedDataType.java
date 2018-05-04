package org.fagu.fmv.mymedia.movie.list.datatype;

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
