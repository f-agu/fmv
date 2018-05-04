package org.fagu.fmv.mymedia.movie.list;

import java.util.Optional;


/**
 * @author Utilisateur
 * @created 4 mai 2018 21:33:38
 */
public interface DataStore {

	/**
	 * @param dataType
	 * @return
	 */
	<T> Optional<T> getData(DataType<T> dataType);
}
