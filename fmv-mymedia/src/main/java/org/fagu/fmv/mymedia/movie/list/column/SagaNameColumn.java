package org.fagu.fmv.mymedia.movie.list.column;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.movie.list.Column;
import org.fagu.fmv.mymedia.movie.list.DataStore;


/**
 * @author Utilisateur
 * @created 4 mai 2018 14:37:34
 */
public class SagaNameColumn implements Column {

	@Override
	public String title() {
		return "Saga";
	}

	@Override
	public Optional<String> value(Path rootPath, File file, DataStore dataStore) {
		return Sagas.getInstance()
				.getSagaFor(FilenameUtils.getBaseName(file.getName()))
				.map(Saga::getName);
	}
}
