package org.fagu.fmv.mymedia.movie.list;

import java.io.File;
import java.util.Optional;


/**
 * @author Utilisateur
 * @created 4 mai 2018 21:33:48
 */
public interface DataType<T> {

	/**
	 * @param file
	 * @return
	 */
	Optional<T> extractData(File file);
}
