package org.fagu.fmv.mymedia.classify;

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
import java.io.IOException;

import org.fagu.fmv.im.Image;
import org.fagu.fmv.mymedia.classify.image.AskTimeOffsetImageComparator;
import org.fagu.fmv.mymedia.classify.movie.AskTimeOffsetMovieComparator;
import org.fagu.fmv.mymedia.classify.movie.Movie;
import org.fagu.fmv.mymedia.file.ImageFinder;
import org.fagu.fmv.mymedia.file.MovieFinder;


/**
 * @author f.agu
 */
public class ClassifierFactories {

	private ClassifierFactories() {}

	public static ClassifierFactory<ImageFinder, Image> imageSejour() {
		final String PATTERN = "${yyyy}-${MM}-${dd}/${counter}.${extension}";
		return new ClassifierFactory<ImageFinder, Image>() {

			@Override
			public String getTitle() {
				return "Sejour (" + PATTERN + ')';
			}

			@Override
			public Classifier<ImageFinder, Image> create(ImageFinder finder, File destFolder) throws IOException {
				MediaTimeComparator<Image> mediaTimeComparator = new AskTimeOffsetImageComparator(finder);
				return new ByPatternClassifier<ImageFinder, Image>(finder, destFolder, mediaTimeComparator, PATTERN, ReplacerMaps.counterGlobal());
			}
		};
	}

	public static ClassifierFactory<ImageFinder, Image> imageMonth() {
		final String PATTERN = "${yyyy}-${MM}/${filename}";
		return new ClassifierFactory<ImageFinder, Image>() {

			@Override
			public String getTitle() {
				return "Mois (" + PATTERN + ')';
			}

			@Override
			public Classifier<ImageFinder, Image> create(ImageFinder finder, File destFolder) throws IOException {
				MediaTimeComparator<Image> mediaTimeComparator = new AskTimeOffsetImageComparator(finder);
				return new ByPatternClassifier<ImageFinder, Image>(finder, destFolder, mediaTimeComparator, PATTERN, ReplacerMaps.counterGlobal());
			}
		};
	}

	public static ClassifierFactory<ImageFinder, Image> imagesToMovie() {
		final String PATTERN = "img_${counter}.${extension}";
		return new ClassifierFactory<ImageFinder, Image>() {

			@Override
			public String getTitle() {
				return "Images -> Video";
			}

			@Override
			public Classifier<ImageFinder, Image> create(ImageFinder finder, File destFolder) throws IOException {
				MediaTimeComparator<Image> mediaTimeComparator = new MediaTimeComparator<Image>() {};
				return new ByPatternClassifier<ImageFinder, Image>(finder, destFolder, mediaTimeComparator, PATTERN, ReplacerMaps.counterGlobal());
			}
		};
	}

	public static ClassifierFactory<MovieFinder, Movie> movie() {
		final String PATTERN = "${yyyy}-${MM}-${dd} (${counter}).${extension}";
		return new ClassifierFactory<MovieFinder, Movie>() {

			@Override
			public String getTitle() {
				return "Basic (" + PATTERN + ')';
			}

			@Override
			public Classifier<MovieFinder, Movie> create(MovieFinder finder, File destFolder) throws IOException {
				MediaTimeComparator<Movie> mediaTimeComparator = new AskTimeOffsetMovieComparator(finder);
				return new ByPatternClassifier<MovieFinder, Movie>(finder, destFolder, mediaTimeComparator, PATTERN, ReplacerMaps.counterByDay());
			}
		};
	}

}
