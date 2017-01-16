package org.fagu.fmv.mymedia.classify.movie;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.File;

import org.fagu.fmv.mymedia.classify.Classifier;
import org.fagu.fmv.mymedia.file.MovieFinder;


/**
 * @author f.agu
 */
public abstract class MovieClassifier extends Classifier<MovieFinder, Movie> {

	/**
	 * @param finder
	 * @param destFolder
	 */
	public MovieClassifier(MovieFinder finder, File destFolder) {
		super(finder, destFolder);
	}

}
