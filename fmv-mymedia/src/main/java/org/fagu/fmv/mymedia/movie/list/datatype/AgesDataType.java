package org.fagu.fmv.mymedia.movie.list.datatype;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.movie.list.DataType;
import org.fagu.fmv.mymedia.movie.list.column.Ages;
import org.fagu.fmv.mymedia.movie.list.column.AgesFilm;


/**
 * @author Utilisateur
 * @created 4 mai 2018 21:37:47
 */
public class AgesDataType implements DataType<Ages> {

	private final AgesFilm agesFilm;

	public AgesDataType(AgesFilm agesFilm) {
		this.agesFilm = Objects.requireNonNull(agesFilm);
	}

	@Override
	public Optional<Ages> extractData(File file) {
		return agesFilm.getAges(FilenameUtils.getBaseName(file.getName()));
	}
}
