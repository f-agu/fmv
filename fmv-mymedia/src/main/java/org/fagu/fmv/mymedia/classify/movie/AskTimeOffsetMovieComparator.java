package org.fagu.fmv.mymedia.classify.movie;

import org.fagu.fmv.mymedia.classify.AskTimeOffsetComparator;
import org.fagu.fmv.mymedia.file.MovieFinder;


/**
 * @author f.agu
 */
public class AskTimeOffsetMovieComparator extends AskTimeOffsetComparator<Movie> implements MovieTimeComparator {

	/**
	 * @param movieFinder
	 */
	public AskTimeOffsetMovieComparator(MovieFinder movieFinder) {
		super(movieFinder);
	}

}
