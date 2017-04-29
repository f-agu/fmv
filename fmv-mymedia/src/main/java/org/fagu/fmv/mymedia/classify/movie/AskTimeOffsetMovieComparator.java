package org.fagu.fmv.mymedia.classify.movie;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
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
