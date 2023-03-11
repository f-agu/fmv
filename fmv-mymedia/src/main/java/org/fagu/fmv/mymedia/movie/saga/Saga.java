package org.fagu.fmv.mymedia.movie.saga;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 * @created 6 mai 2018 12:07:02
 */
public class Saga implements Comparable<Saga> {

	public static class Movie {

		private final String fileName;

		private final String frenchTitle;

		private Movie(String fileName, String frenchTitle) {
			this.fileName = Objects.requireNonNull(fileName);
			this.frenchTitle = StringUtils.isNotBlank(frenchTitle) ? frenchTitle : fileName;
		}

		public String getFileName() {
			return fileName;
		}

		public String getFrenchTitle() {
			return frenchTitle;
		}
	}

	private final String name;

	private final Set<Movie> movies;

	private Saga(String name, Set<Movie> movies) {
		this.name = name;
		this.movies = movies;
	}

	public static Saga load(File file) throws IOException {
		Set<Movie> movies = new LinkedHashSet<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#")) {
					continue;
				}
				String fileName = StringUtils.substringBefore(line, "|");
				String frenchTitle = StringUtils.substringAfter(line, "|");
				movies.add(new Movie(fileName, frenchTitle));
			}
		}
		return new Saga(FilenameUtils.getBaseName(file.getName()), Collections.unmodifiableSet(movies));
	}

	public String getName() {
		return name;
	}

	public Set<Movie> getMovies() {
		return movies;
	}

	public Set<String> getFileNames() {
		return movies.stream()
				.map(m -> m.fileName)
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public OptionalInt getIndex(String name) {
		int index = 1;
		for(Movie movie : movies) {
			if(movie.fileName.equalsIgnoreCase(name) || movie.frenchTitle.equalsIgnoreCase(name)) {
				return OptionalInt.of(index);
			}
			++index;
		}
		return OptionalInt.empty();
	}

	@Override
	public int compareTo(Saga o) {
		return name.compareTo(o.name);
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("Saga[").append(name).append(", ").append(movies.size()).append(" movies]")
				.toString();
	}

}
