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

	private final CategoryColumn categoryColumn;

	static final AgesCache AGES_CACHE = AgesCache.getInstance();

	static final AgesFilm AGES_FILM = new AgesFilm(AGES_CACHE);

	private static final AgesDataType AGES_DATA_TYPE = new AgesDataType(AGES_FILM);

	public AgeFilteredColumn(Function<Ages, Integer> convert) {
		categoryColumn = new CategoryColumn(0);
		cat0Name = categoryColumn.title();
		this.convert = convert;
	}

	@Override
	public final Optional<String> value(Path rootPath, File file, DataStore dataStore) {
		Map<String, String> map = dataStore.getData(ValuesDataType.VALUES).get();
		String cat0Value = map.get(cat0Name);
		if( ! "Films HD".equals(cat0Value) && ! "Dessins animés".equals(cat0Value)) {
			return Optional.empty();
		}
		Optional<Ages> ages = dataStore.getData(AGES_DATA_TYPE);
		return ages.map(a -> Integer.toString(convert.apply(a)));
	}

	@Override
	public void close() throws Exception {
		categoryColumn.close();
	}

}
