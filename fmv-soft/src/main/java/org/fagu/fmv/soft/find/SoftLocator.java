package org.fagu.fmv.soft.find;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 fagu
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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.fagu.fmv.soft.SoftName;
import org.fagu.fmv.soft.find.strategy.HighestPrecedenceSorter;


/**
 * @author f.agu
 */
public class SoftLocator {

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	private class Cache {

		private final File file;

		private final long lastModified;

		private final long size;

		/**
		 * @param file
		 */
		private Cache(File file) {
			this.file = file;
			this.lastModified = file.lastModified();
			this.size = file.length();
		}

		/**
		 * @return
		 */
		private boolean isChanged() {
			return file.lastModified() != lastModified || file.length() != size;
		}

	}

	// -----------------------------------------------

	private static final Function<Founds, File> DEFAULT_FOUND_POLICY = founds -> founds.getFirstFound().getFile();

	private static final SoftTester DEFAULT_SOFT_TESTER = (file, locator) -> SoftFound.found(file)
			.setLocalizedBy(locator != null ? locator.toString() : null);

	private final String envName;

	private String softPath;

	private final Sorter sorter;

	private final Map<SoftName, String> pathMap;

	private final Map<SoftName, Cache> cacheFile;

	private final List<Locator> definedLocators;

	/**
	 *
	 */
	public SoftLocator() {
		this(null);
	}

	/**
	 * @param envName
	 */
	public SoftLocator(String envName) {
		this(envName, null);
	}

	/**
	 * @param envName
	 * @param sorter
	 */
	public SoftLocator(String envName, Sorter sorter) {
		this.envName = envName;
		pathMap = new HashMap<>(2);
		cacheFile = new HashMap<>();
		definedLocators = new ArrayList<>();
		this.sorter = sorter != null ? sorter : new HighestPrecedenceSorter();
	}

	/**
	 * @return the softPath
	 */
	public String getSoftPath() {
		return softPath;
	}

	/**
	 * @param softPath
	 */
	public void setSoftPath(String softPath) {
		this.softPath = softPath;
	}

	/**
	 * @return the foundStrategy
	 */
	public Sorter getSorter() {
		return sorter;
	}

	/**
	 * @param softName
	 * @return
	 */
	public String getPath(SoftName softName) {
		return pathMap.get(softName);
	}

	/**
	 * @param softName
	 * @param path
	 */
	public void setPath(SoftName softName, String path) {
		pathMap.put(softName, path);
	}

	/**
	 * @return
	 */
	public SoftLocator addDefaultLocator(SoftName softName) {
		return addLocators(getDefaultLocators(softName, null));
	}

	/**
	 * @param locators
	 * @return
	 */
	public SoftLocator addLocator(Locator... locators) {
		return addLocators(Arrays.asList(locators));
	}

	/**
	 * @param locators
	 */
	public SoftLocator addLocators(Collection<Locator> locators) {
		for(Locator locator : locators) {
			if(locator == null) {
				throw new IllegalArgumentException("null locator");
			}
		}
		definedLocators.addAll(locators);
		return this;
	}

	/**
	 * @param softName
	 * @return
	 */
	public Founds find(SoftName softName) {
		return find(softName, null);
	}

	/**
	 * @param softName
	 * @param softTester
	 * @return
	 */
	public Founds find(SoftName softName, SoftTester softTester) {
		SoftTester tester = firstFoundNotNull(softTester, defaultSoftTester(), DEFAULT_SOFT_TESTER);

		// 1 - system properties
		Locators locators = createLocators(softName);
		List<Locator> locatorList = new ArrayList<>(2);
		locatorList.add(locators.byPropertyPath("fmv." + softName.getName() + ".path"));
		locatorList.add(locators.byPropertyPath(softName.getName() + ".path"));
		Founds founds = find(locatorList, softName, tester);
		if(founds.isFound()) {
			return founds;
		}

		// 2 - env
		if(envName != null) {
			founds = find(Collections.singletonList(locators.byEnv(envName)), softName, tester);
			if(founds.isFound()) {
				return founds;
			}
		}

		// 3 - other
		return find(getLocators(softName, locators), softName, tester);
	}

	/**
	 * @param softName
	 * @return
	 */
	public File findFile(SoftName softName) {
		return findFile(softName, null);
	}

	/**
	 * @param softName
	 * @param softTester
	 * @return
	 */
	public File findFile(SoftName softName, SoftTester softTester) {
		Cache cache = cacheFile.get(softName);
		if(cache != null && ! cache.isChanged()) {
			return cache.file;
		}

		Founds founds = find(softName, softTester);
		File file = null;
		if(founds.isFound()) {
			@SuppressWarnings("unchecked")
			Function<Founds, File> foundPolicy = firstFoundNotNull(getFoundPolicy(), DEFAULT_FOUND_POLICY);
			file = foundPolicy.apply(founds);
			if(file == null) {
				throw new IllegalArgumentException("file is null (" + softName + ')');
			}
			cache = new Cache(file);
			cacheFile.put(softName, cache);
		}
		return file;
	}

	// ************************************************

	/**
	 * @param softName
	 * @return
	 */
	protected FileFilter fileFilter(SoftName softName) {
		return null;
	}

	/**
	 * @param softName
	 * @return
	 */
	protected List<Locator> getLocators(SoftName softName, Locators loc) {
		if( ! definedLocators.isEmpty()) {
			return new ArrayList<>(definedLocators); // defensive copy
		}
		return getDefaultLocators(softName, loc);
	}

	/**
	 * @param softName
	 * @param loc
	 * @return
	 */
	protected List<Locator> getDefaultLocators(SoftName softName, Locators loc) {
		Locators locators = loc != null ? loc : createLocators(softName);
		List<Locator> locatorList = new ArrayList<>(4);
		locatorList.add(locators.byPath(getPath(softName))); // cache
		if(softPath != null) {
			locatorList.add(locators.byPath(softPath));
		}
		locatorList.add(locators.byCurrentPath());
		locatorList.add(locators.byEnvPath());
		return locatorList;
	}

	/**
	 * @param softName
	 * @return
	 */
	protected Locators createLocators(SoftName softName) {
		FileFilter fileFilter = fileFilter(softName);
		if(fileFilter == null) {
			fileFilter = defaultFileFilter(softName);
		}
		return new Locators(fileFilter);
	}

	/**
	 * @return
	 */
	protected SoftTester defaultSoftTester() {
		return DEFAULT_SOFT_TESTER;
	}

	/**
	 * @param softName
	 * @return
	 */
	protected final FileFilter defaultFileFilter(SoftName softName) {
		return softName.getFileFilter();
	}

	/**
	 * @return
	 */
	protected Function<Founds, File> getFoundPolicy() {
		return DEFAULT_FOUND_POLICY;
	}

	// ********************************************

	/**
	 * @param locators
	 * @param softName
	 * @param tester
	 * @return
	 */
	private Founds find(Collection<Locator> locators, SoftName softName, SoftTester tester) {
		List<SoftFound> softFounds = new ArrayList<>();
		Set<File> fileDones = new HashSet<>(4);
		for(Locator locator : locators) {
			for(File file : locator.locate(softName)) {
				if(fileDones.add(file)) {
					SoftFound found = tester.test(file, locator);
					if(found != null) {
						softFounds.add(found);
					}
				}
			}
		}
		NavigableSet<SoftFound> sort = sorter.sort(softFounds);
		return new Founds(softName, sort);
	}

	/**
	 * @param ts
	 * @return
	 */
	private static <T> T firstFoundNotNull(@SuppressWarnings("unchecked") T... ts) {
		return Arrays.stream(ts)
				.filter(Objects::nonNull)
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

}
