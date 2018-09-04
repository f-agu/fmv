package org.fagu.fmv.soft.utils;

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

	public SearchMatching forMatching(String defaultPattern, String... patternKeys) {
		Pattern pattern = Pattern.compile(getOrDefault(defaultPattern, patternKeys));
		return new SearchMatching(pattern);
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
