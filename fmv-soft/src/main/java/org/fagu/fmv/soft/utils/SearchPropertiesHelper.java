package org.fagu.fmv.soft.utils;

import java.util.Arrays;
import java.util.Collection;

/*-
 * #%L
 * fmv-soft
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.utils.PlaceHolder;
import org.fagu.fmv.utils.Replacers;


/**
 * @author f.agu
 * @created 3 sept. 2018 14:20:23
 */
public class SearchPropertiesHelper {

	private static final List<String> PROP_FOR_PATTERNS = Arrays.asList(
			"fmv.soft.${group.name}.search",
			"fmv.soft.${soft.name}.search",
			"fmv.soft.${group.name}.${soft.name}.search");

	private static final String SUFFIX_VERSION = ".versionPattern";

	private static final String SUFFIX_DATE = ".datePattern";

	private static Properties globalProperties;

	private final Map<String, String> searchProperties;

	private final SoftProvider softProvider;

	public SearchPropertiesHelper(Properties searchProperties, SoftProvider softProvider) {
		Map<String, String> map = new HashMap<>();
		BiConsumer<? super Object, ? super Object> bicon = (k, v) -> map.put((String)k, (String)v);
		System.getProperties().forEach(bicon);
		if(searchProperties != null) {
			searchProperties.forEach(bicon);
		}
		if(globalProperties != null) {
			globalProperties.forEach(bicon);
		}
		this.searchProperties = Collections.unmodifiableMap(map);
		this.softProvider = Objects.requireNonNull(softProvider);
	}

	public String getOrDefault(String defaultValue, String... keys) {
		return getOrDefault(defaultValue, Arrays.asList(keys));
	}

	public String getOrDefault(String defaultValue, Collection<String> keys) {
		Replacers replacerChain = replacer(softProvider.getGroupName(), softProvider.getName())
				.map(searchProperties);
		String value = defaultValue;
		for(String key : keys) {
			String nKey = PlaceHolder.with(key).format(replacerChain);
			if(searchProperties.containsKey(nKey)) {
				value = searchProperties.get(nKey);
				break;
			}
		}
		return PlaceHolder.with(value).format(replacerChain);
	}

	public Pattern toPattern(String defaultValue, String... patternKeys) {
		return toPattern(defaultValue, Arrays.asList(patternKeys));
	}

	public Pattern toPattern(String defaultValue, Collection<String> patternKeys) {
		return Pattern.compile(
				getOrDefault(defaultValue, patternKeys),
				Pattern.CASE_INSENSITIVE);
	}

	public Pattern toPatternVersion(String defaultValue) {
		return toPattern(defaultValue, PROP_FOR_PATTERNS.stream()
				.map(s -> s.concat(SUFFIX_VERSION))
				.collect(Collectors.toList()));
	}

	public Pattern toPatternDate(String defaultValue) {
		return toPattern(defaultValue, PROP_FOR_PATTERNS.stream()
				.map(s -> s.concat(SUFFIX_DATE))
				.collect(Collectors.toList()));
	}

	public SearchMatching forMatching(String defaultValue, String... patternKeys) {
		return forMatching(defaultValue, Arrays.asList(patternKeys));
	}

	public SearchMatching forMatching(String defaultValue, Collection<String> patternKeys) {
		return new SearchMatching(toPattern(defaultValue, patternKeys));
	}

	public SearchMatching forMatchingVersion(String defaultValue) {
		return new SearchMatching(toPatternVersion(defaultValue));
	}

	public SearchMatching forMatchingDate(String defaultValue) {
		return new SearchMatching(toPatternDate(defaultValue));
	}

	public static Replacers replacer(String groupName, String softName) {
		return Replacers.chain()
				.keyValue("group.name", groupName)
				.keyValue("soft.name", softName);
	}

	public static Properties getGlobalProperties() {
		return globalProperties;
	}

	public static void setGlobalProperties(Properties globalProperties) {
		SearchPropertiesHelper.globalProperties = globalProperties;
	}

	// ------------------------------------------

	public static class SearchMatching {

		private final Pattern pattern;

		private SearchMatching(Pattern pattern) {
			this.pattern = pattern;
		}

		public <T> Optional<T> ifMatches(String line, Function<Matcher, Optional<T>> func) {
			Matcher matcher = pattern.matcher(line);
			if(matcher.matches()) {
				return func.apply(matcher);
			}
			return Optional.empty();
		}

	}

}
