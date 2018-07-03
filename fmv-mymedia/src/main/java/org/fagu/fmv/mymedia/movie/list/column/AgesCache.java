package org.fagu.fmv.mymedia.movie.list.column;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Utilisateur
 * @created 6 mai 2018 14:41:47
 */
public class AgesCache {

	private static final AgesCache INSTANCE = new AgesCache();

	public static class AgesElement {

		private final Ages ages;

		private AgesElement(Ages ages) {
			this.ages = ages;
		}

		public Optional<Ages> getAges() {
			return Optional.ofNullable(ages);
		}
	}

	private final Map<String, AgesElement> agesMap;

	private final File file;

	private AgesCache() {
		String cacheFile = System.getProperty("fmv.movie.list.ages.cachefile");
		if(cacheFile == null) {
			agesMap = new HashMap<>();
			file = null;
			return;
		}
		file = new File(cacheFile);
		if( ! file.exists()) {
			throw new UncheckedIOException(new FileNotFoundException(file.getAbsolutePath()));
		}
		agesMap = load(file);
	}

	public static AgesCache getInstance() {
		return INSTANCE;
	}

	public Optional<File> getCacheFile() {
		return Optional.ofNullable(file);
	}

	public Optional<AgesElement> find(String title) {
		return Optional.ofNullable(agesMap.get(title));
	}

	public void put(String title, Ages ages) {
		agesMap.put(title, new AgesElement(ages));
	}

	// *********************************************************

	private static Map<String, AgesElement> load(File file) {
		Map<String, AgesElement> ageMap = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(StringUtils.isBlank(line) || line.startsWith("#")) {
					continue;
				}
				String[] strs = line.split("\t");
				String title = strs[0];
				String legalAge = strs.length > 1 ? strs[1] : null;
				String suggestedAge = strs.length > 1 ? strs[2] : null;
				if(StringUtils.isNotEmpty(legalAge) && StringUtils.isNotEmpty(suggestedAge)) {
					ageMap.put(title, new AgesElement(new Ages(title, Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 1D)));
				} else {
					ageMap.put(title, new AgesElement(null));
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return ageMap;
	}
}
