package org.fagu.fmv.mymedia.movie.age;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Utilisateur
 * @created 6 mai 2018 14:41:47
 */
public class AgesCache {

	private static final AgesCache INSTANCE = new AgesCache();

	public static class AgesElement {

		private final String title;

		private final boolean alreadySearchAndNotFound;

		private final Ages ages;

		private AgesElement(String title, Ages ages, boolean alreadySearchAndNotFound) {
			this.title = Objects.requireNonNull(title);
			this.ages = ages;
			this.alreadySearchAndNotFound = alreadySearchAndNotFound;
		}

		public static AgesElement knowAges(String title, Ages ages) {
			return new AgesElement(title, ages, true);
		}

		public static AgesElement unknowAges(String title, boolean alreadySearchAndNotFound) {
			return new AgesElement(title, null, alreadySearchAndNotFound);
		}

		public Optional<Ages> getAges() {
			return Optional.ofNullable(ages);
		}

		public boolean isAlreadySearchAndNotFound() {
			return alreadySearchAndNotFound;
		}

		public boolean isPresent() {
			return ages != null;
		}

		@Override
		public String toString() {
			StringJoiner joiner = new StringJoiner("\t").add(title);
			if(alreadySearchAndNotFound && ages == null) {
				joiner.add("-");
			}
			getAges().ifPresent(ages -> joiner
					.add(Integer.toString(ages.getLegal()))
					.add(Integer.toString(ages.getSuggested())));
			return joiner.toString();
		}
	}

	private final Map<String, AgesElement> agesMap;

	private final File file;

	public AgesCache() {
		String cacheFile = System.getProperty("fmv.movie.list.ages.cachefile");
		if(cacheFile == null) {
			agesMap = new TreeMap<>();
			file = null;
			return;
		}
		file = new File(cacheFile);
		if( ! file.exists()) {
			throw new UncheckedIOException(new FileNotFoundException(file.getAbsolutePath()));
		}
		agesMap = load(file);
	}

	public AgesCache(Map<String, AgesElement> agesMap) {
		this.agesMap = Objects.requireNonNull(agesMap);
		file = null;
	}

	public static AgesCache getInstance() {
		return INSTANCE;
	}

	public Optional<File> getCacheFile() {
		return Optional.ofNullable(file);
	}

	public Set<String> getTitles() {
		return Collections.unmodifiableSet(agesMap.keySet());
	}

	public Optional<AgesElement> find(String title) {
		return Optional.ofNullable(agesMap.get(title));
	}

	public void put(String title, Ages ages, boolean alreadySearchAndNotFound) {
		agesMap.put(title, new AgesElement(title, ages, alreadySearchAndNotFound));
	}

	// *********************************************************

	private static Map<String, AgesElement> load(File file) {
		Map<String, AgesElement> ageMap = new TreeMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(StringUtils.isBlank(line) || line.startsWith("#")) {
					continue;
				}
				String[] strs = line.split("\t");
				String title = strs[0];
				String legalAge = strs.length > 1 ? strs[1] : null;
				boolean alreadySearchAndNotFound = "-".equals(legalAge);
				String suggestedAge = strs.length > 2 ? strs[2] : null;
				AgesElement agesElement = null;
				if(StringUtils.isNotEmpty(legalAge) && StringUtils.isNotEmpty(suggestedAge)) {
					agesElement = AgesElement.knowAges(title, new Ages(title, Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 1D));
				} else {
					agesElement = AgesElement.unknowAges(title, alreadySearchAndNotFound);
				}
				ageMap.put(title, agesElement);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return ageMap;
	}
}
