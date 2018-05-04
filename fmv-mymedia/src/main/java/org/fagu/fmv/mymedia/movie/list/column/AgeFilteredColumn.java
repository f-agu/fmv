package org.fagu.fmv.mymedia.movie.list.column;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.fagu.fmv.mymedia.movie.list.Column;
import org.fagu.fmv.mymedia.movie.list.DataStore;
import org.fagu.fmv.mymedia.movie.list.datatype.AgesDataType;
import org.fagu.fmv.mymedia.movie.list.datatype.ValuesDataType;


/**
 * @author Utilisateur
 * @created 4 mai 2018 22:27:13
 */
public abstract class AgeFilteredColumn implements Column {

	private final String cat0Name;

	private final Function<Ages, Integer> convert;

	public AgeFilteredColumn(Function<Ages, Integer> convert) {
		cat0Name = new CategoryColumn(0).title();
		this.convert = convert;
	}

	@Override
	public final Optional<String> value(Path rootPath, File file, DataStore dataStore) {
		Map<String, String> map = dataStore.getData(ValuesDataType.VALUES).get();
		if( ! "Films HD".equals(map.get(cat0Name))) {
			return Optional.empty();
		}
		Optional<Ages> ages = dataStore.getData(AgesDataType.AGES);
		return ages.map(a -> Integer.toString(convert.apply(a)));
	}

}
