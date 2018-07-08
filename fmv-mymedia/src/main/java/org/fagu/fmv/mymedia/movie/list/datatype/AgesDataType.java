package org.fagu.fmv.mymedia.movie.list.datatype;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.movie.age.Ages;
import org.fagu.fmv.mymedia.movie.age.AgesFilm;
import org.fagu.fmv.mymedia.movie.list.DataType;


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
