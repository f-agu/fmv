package org.fagu.fmv.mymedia.movie.saga;

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
import org.apache.commons.lang.StringUtils;


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

	/**
	 * @param name
	 * @param movies
	 */
	private Saga(String name, Set<Movie> movies) {
		this.name = name;
		this.movies = movies;
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
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
