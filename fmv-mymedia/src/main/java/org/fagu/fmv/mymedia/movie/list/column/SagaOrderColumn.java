package org.fagu.fmv.mymedia.movie.list.column;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalInt;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.movie.list.Column;
import org.fagu.fmv.mymedia.movie.list.DataStore;
import org.fagu.fmv.mymedia.movie.list.Sagas;


/**
 * @author Utilisateur
 * @created 4 mai 2018 14:37:34
 */
public class SagaOrderColumn implements Column {

	@Override
	public String title() {
		return "Saga ordre";
	}

	@Override
	public Optional<String> value(Path rootPath, File file, DataStore dataStore) {
		String title = FilenameUtils.getBaseName(file.getName());
		return Sagas.getInstance()
				.getSagaFor(title)
				.map(s -> {
					OptionalInt intOpt = s.getIndex(title);
					return intOpt.isPresent() ? Integer.toString(intOpt.getAsInt()) : null;
				});
	}
}
