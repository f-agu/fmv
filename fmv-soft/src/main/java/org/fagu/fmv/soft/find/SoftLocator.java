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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.apache.commons.lang3.ObjectUtils;
import org.fagu.fmv.soft.SearchListener;
import org.fagu.fmv.soft.find.strategy.HighestPrecedenceSorter;


/**
 * @author f.agu
 */
public class SoftLocator {

	private static final Function<Founds, File> DEFAULT_FOUND_POLICY = founds -> founds.getFirstFound().getFile();

	private static final SoftTester DEFAULT_SOFT_TESTER = (file, locator, softPol) -> SoftFound.found(file)
			.setLocalizedBy(locator != null ? locator.toString() : null);

	private static final Map<String, Founds> CACHE_MAP = new HashMap<>();

	private static final List<Locator> DEFAULT_LOCATORS = new LinkedList<>();

	private final String softName;

	private final FileFilter fileFilter;

	private final Sorter sorter;

	private String envName;

	private String softPath;

	private UnaryOperator<String> cacheNaming;

	private Function<Founds, List<Locator>> cachePopulator;

	private SoftPolicy softPolicy;

	private final List<Locator> definedLocators;

	private final List<SearchListener> searchListeners;

	public SoftLocator(String softName) {
		this(softName, null, null);
	}

	public SoftLocator(String softName, Sorter sorter, FileFilter fileFilter) {
		this.softName = Objects.requireNonNull(softName);
		definedLocators = new ArrayList<>();
		this.sorter = sorter != null ? sorter : new HighestPrecedenceSorter();
		this.fileFilter = fileFilter != null ? fileFilter : PlateformFileFilter.plateformAndBasename(softName);
		this.searchListeners = new ArrayList<>();
	}

	public String getSoftPath() {
		return softPath;
	}

	public void setSoftPath(String softPath) {
		this.softPath = softPath;
	}

	public SoftPolicy getSoftPolicy() {
		return softPolicy;
	}

	public void setSoftPolicy(SoftPolicy softPolicy) {
		this.softPolicy = softPolicy;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public SoftLocator enableCache(UnaryOperator<String> cacheNaming, Function<Founds, List<Locator>> cachePopulator) {
		this.cacheNaming = Objects.requireNonNull(cacheNaming);
		this.cachePopulator = Objects.requireNonNull(cachePopulator);
		return this;
	}

	public SoftLocator enableCacheInSameFolderOfGroup(String groupName) {
		return enableCache(n -> groupName, founds -> {
			if(founds == null) {
				return null;
			}
			SoftFound firstFound = founds.getFirstFound();
			if(firstFound == null) {
				return null;
			}
			File file = firstFound.getFile();
			return file != null ? Collections.singletonList(createLocators().byPath(file.getParent())) : null;
		});
	}

	public Sorter getSorter() {
		return sorter;
	}

	public Locators createLocators() {
		return new Locators(fileFilter);
	}

	public SoftLocator addDefaultLocator() {
		return addLocators(getDefaultLocators(null));
	}

	public SoftLocator addLocator(Locator... locators) {
		return addLocators(Arrays.asList(locators));
	}

	public SoftLocator addLocator(Function<Locators, Locator> locatorFct) {
		return addLocator(locatorFct.apply(createLocators()));
	}

	public SoftLocator addLocators(Function<Locators, Collection<Locator>> locatorsFct) {
		return addLocators(locatorsFct.apply(createLocators()));
	}

	public SoftLocator addLocators(Collection<Locator> locators) {
		for(Locator locator : locators) {
			if(locator == null) {
				throw new IllegalArgumentException("null locator");
			}
		}
		definedLocators.addAll(locators);
		return this;
	}

	public SoftLocator addSearchListener(SearchListener searchListener) {
		if(searchListener != null) {
			searchListeners.add(searchListener);
		}
		return this;
	}

	public SoftLocator addSearchListeners(Collection<SearchListener> listeners) {
		if(listeners != null) {
			listeners.stream()
					.filter(Objects::nonNull)
					.forEach(this.searchListeners::add);
		}
		return this;
	}

	public Founds find(Properties searchProperties) {
		return find(searchProperties, null);
	}

	public Founds find(Properties searchProperties, SoftTester softTester) {
		SoftTester tester = ObjectUtils.firstNonNull(softTester, defaultSoftTester(), DEFAULT_SOFT_TESTER);

		// 1 - cache
		if(cachePopulator != null) {
			Founds founds = CACHE_MAP.get(cacheNaming.apply(softName));
			if(founds != null) {
				List<Locator> locators = cachePopulator.apply(founds);
				if(locators != null && ! locators.isEmpty()) {
					for(Locator locator : locators) {
						founds = finding(locator, tester, searchProperties);
						if(founds.isFound()) {
							return cache(founds);
						}
					}
				}
			}
		}

		// 2 - system properties
		Locators locators = createLocators();
		List<Locator> locatorList = new ArrayList<>(2);
		locatorList.add(locators.byPropertyPath("fmv." + softName + ".path"));
		locatorList.add(locators.byPropertyPath(softName + ".path"));
		Founds founds = finding(locatorList, tester, searchProperties);
		if(founds.isFound()) {
			return cache(founds);
		}

		// 3 - env
		if(envName != null) {
			founds = finding(locators.byEnv(envName), tester, searchProperties);
			if(founds.isFound()) {
				return cache(founds);
			}
		}

		// 4 - other
		return cache(finding(getLocators(locators), tester, searchProperties));
	}

	public static void addDefaultLocator(Locator locator) {
		if(locator != null) {
			DEFAULT_LOCATORS.add(locator);
		}
	}

	public static void evictCache() {
		CACHE_MAP.clear();
	}

	// ************************************************

	protected List<Locator> getLocators(Locators loc) {
		if( ! definedLocators.isEmpty()) {
			return new ArrayList<>(definedLocators); // defensive copy
		}
		return getDefaultLocators(loc);
	}

	protected final List<Locator> getDefaultLocators(Locators loc) {
		Locators locators = loc != null ? loc : createLocators();
		List<Locator> locatorList = new ArrayList<>(4);
		locatorList.addAll(DEFAULT_LOCATORS);
		if(softPath != null) {
			locatorList.add(locators.byPath(softPath));
		}
		locatorList.add(locators.byCurrentPath());
		locatorList.add(locators.byEnvPath());
		locatorList.add(locators.byEnvLdLibraryPath());
		return locatorList;
	}

	protected SoftTester defaultSoftTester() {
		return DEFAULT_SOFT_TESTER;
	}

	protected Function<Founds, File> getFoundPolicy() {
		return DEFAULT_FOUND_POLICY;
	}

	// ********************************************

	private Founds finding(Locator locator, SoftTester tester, Properties searchProperties) {
		return finding(Collections.singletonList(locator), tester, searchProperties);
	}

	private Founds finding(Collection<Locator> locators, SoftTester tester, Properties searchProperties) {
		Collection<Locator> roLocators = Collections.unmodifiableCollection(locators);
		searchListeners.forEach(sl -> sl.eventPreSearch(softName, roLocators, tester, searchProperties));

		List<SoftFound> softFounds = new ArrayList<>();
		Set<File> fileDones = new HashSet<>(4);
		for(Locator locator : locators) {
			searchListeners.forEach(sl -> sl.eventStartLocator(softName, locator, tester, searchProperties));
			for(File file : locator.locate(softName)) {
				if(fileDones.add(file)) {
					SoftFound found = tester.test(file, locator, softPolicy);
					if(found != null) {
						softFounds.add(found);
						searchListeners.forEach(sl -> sl.eventAddSoftFound(softName, locator, found));
					}
				}
			}
		}
		NavigableSet<SoftFound> sort = sorter.sort(softFounds);
		return new Founds(softName, sort, softPolicy, searchProperties);
	}

	private Founds cache(Founds founds) {
		if(cachePopulator != null) {
			CACHE_MAP.putIfAbsent(cacheNaming.apply(softName), founds);
		}
		return founds;
	}

}
