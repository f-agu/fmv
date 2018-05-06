package org.fagu.fmv.mymedia.movie.list.column;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.OptionalInt;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;


/**
 * @author Utilisateur
 * @created 6 mai 2018 12:07:02
 */
public class Saga {

	private final String name;

	private final Set<String> titles;

	/**
	 * @param name
	 * @param titles
	 */
	private Saga(String name, Set<String> titles) {
		this.name = name;
		this.titles = titles;
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Saga load(File file) throws IOException {
		Set<String> titles = new LinkedHashSet<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#")) {
					continue;
				}
				titles.add(line);
			}
		}
		return new Saga(FilenameUtils.getBaseName(file.getName()), Collections.unmodifiableSet(titles));
	}

	public String getName() {
		return name;
	}

	public Set<String> getTitles() {
		return titles;
	}

	public OptionalInt getIndex(String title) {
		int index = 1;
		for(String t : titles) {
			if(t.equalsIgnoreCase(title)) {
				return OptionalInt.of(index);
			}
			++index;
		}
		return OptionalInt.empty();
	}

}
