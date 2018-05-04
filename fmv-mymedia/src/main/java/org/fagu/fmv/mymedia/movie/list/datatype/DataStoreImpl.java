package org.fagu.fmv.mymedia.movie.list.datatype;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.fagu.fmv.mymedia.movie.list.DataStore;
import org.fagu.fmv.mymedia.movie.list.DataType;


/**
 * @author Utilisateur
 * @created 4 mai 2018 21:38:49
 */
public class DataStoreImpl implements DataStore {

	private final File file;

	private final Map<String, String> values;

	public DataStoreImpl(File file, Map<String, String> values) {
		this.file = Objects.requireNonNull(file);
		this.values = values;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> getData(DataType<T> dataType) {
		if(dataType == ValuesDataType.VALUES) {
			return Optional.of((T)values);
		}
		return dataType.extractData(file);
	}

}
