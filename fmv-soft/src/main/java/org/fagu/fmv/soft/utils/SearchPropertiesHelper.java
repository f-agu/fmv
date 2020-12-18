package org.fagu.fmv.soft.utils;

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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.utils.PlaceHolder;
import org.fagu.fmv.utils.Replacers;


/**
 * @author f.agu
 * @created 3 sept. 2018 14:20:23
 */
public class SearchPropertiesHelper {

	private final Map<String, String> searchProperties;

	private final String softName;

	public SearchPropertiesHelper(Properties searchProperties, String softName) {
		Map<String, String> map = new HashMap<>();
		BiConsumer<? super Object, ? super Object> bicon = (k, v) -> map.put((String)k, (String)v);
		System.getProperties().forEach(bicon);
		if(searchProperties != null) {
			searchProperties.forEach(bicon);
		}
		this.searchProperties = Collections.unmodifiableMap(map);
		this.softName = Objects.requireNonNull(softName);
	}

	public String getOrDefault(String defaultValue, String... keys) {
		Replacers replacerChain = Replacers.chain()
				.keyValue("soft.name", softName)
				.map(searchProperties);
		for(String key : keys) {
			String nKey = PlaceHolder.with(key).format(replacerChain);
			if(searchProperties.containsKey(nKey)) {
				return searchProperties.get(nKey);
			}
		}
		return PlaceHolder.with(defaultValue).format(replacerChain);
	}

	public Pattern toPattern(String defaultPattern, String... patternKeys) {
		return Pattern.compile(getOrDefault(defaultPattern, patternKeys));
	}

	public SearchMatching forMatching(String defaultPattern, String... patternKeys) {
		return new SearchMatching(toPattern(defaultPattern, patternKeys));
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
