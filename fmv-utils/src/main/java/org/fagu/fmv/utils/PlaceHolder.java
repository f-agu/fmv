package org.fagu.fmv.utils;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;


/**
 * @author f.agu
 */
public class PlaceHolder {

	/**
	 * Default placeholder prefix: "${"
	 */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

	/**
	 * Default placeholder suffix: "}"
	 */
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

	private final String pattern;

	private String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

	private String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

	private Replacer circularKeyReplacer;

	/**
	 * @param pattern
	 */
	private PlaceHolder(String pattern) {
		this.pattern = pattern; // maybe null !
		circularKeyReplacer = getDefaultCircularKeyNameFunction();
	}

	/**
	 * @param pattern
	 * @return
	 */
	public static PlaceHolder with(String pattern) {
		return new PlaceHolder(pattern);
	}

	/**
	 * Set the prefix that a placeholder string starts with. The default is "${".
	 */
	public PlaceHolder prefix(String placeholderPrefix) {
		if(placeholderPrefix == null || "".equals(placeholderPrefix)) {
			throw new IllegalArgumentException();
		}
		this.placeholderPrefix = placeholderPrefix;
		return this;
	}

	/**
	 * Set the suffix that a placeholder string ends with. The default is "}".
	 */
	public PlaceHolder suffix(String placeholderSuffix) {
		if(placeholderSuffix == null || "".equals(placeholderSuffix)) {
			throw new IllegalArgumentException();
		}
		this.placeholderSuffix = placeholderSuffix;
		return this;
	}

	/**
	 * @return
	 */
	public PlaceHolder circularKeyThrows() {
		return circular(getDefaultCircularKeyNameFunction());
	}

	/**
	 * @return
	 */
	public PlaceHolder circularKeyCopy() {
		return circular(this::wrap);
	}

	/**
	 * @param circularKeyFunction
	 * @return
	 */
	public PlaceHolder circular(Replacer circularKeyFunction) {
		this.circularKeyReplacer = Objects.requireNonNull(circularKeyFunction);
		return this;
	}

	/**
	 * Around {@code placeHolderName} with the current prefix and suffix.<br>
	 * <blockquote>
	 *
	 * <pre>
	 * prefix + placeHolderName + suffix
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * @param placeHolderName
	 * @return
	 */
	public String wrap(String placeHolderName) {
		return new StringBuilder(placeHolderName.length() + placeholderPrefix.length() + placeholderSuffix.length())
				.append(placeholderPrefix)
				.append(placeHolderName)
				.append(placeholderSuffix).toString();
	}

	/**
	 * @param findConsumer
	 * @return
	 */
	public PlaceHolder listNames(Consumer<String> findConsumer) {
		format(Replacers.chain()
				.eventFind(findConsumer)
				.unresolvableIgnored());
		return this;
	}

	/**
	 * @return
	 */
	public List<String> listNames() {
		List<String> names = new ArrayList<>();
		listNames(names::add);
		return names;
	}

	/**
	 * @param replacer
	 * @return
	 */
	public String format(Replacer replacer) {
		replacer.init(this);
		return format(pattern, replacer, new HashSet<>());
	}

	/**
	 * @param replacerMap
	 * @return
	 */
	public String format(Map<String, String> replacerMap) {
		return format(Replacers.chain()
				.map(replacerMap)
				.unresolvableThrow());
	}

	/**
	 * @param replacerMap
	 * @param notFoundReplacer
	 * @return
	 */
	public String format(Map<String, String> replacerMap, Replacer notFoundReplacer) {
		return format(Replacers.chain()
				.map(replacerMap)
				.of(notFoundReplacer)
				.unresolvableThrow());
	}

	/**
	 * @param replacerMap
	 * @return
	 */
	public String formatUnresolvableIgnored(Map<String, String> replacerMap) {
		return format(replacerMap, Replacers.chain().unresolvableIgnored());
	}

	/**
	 * @param replacerMap
	 * @return
	 */
	public String formatUnresolvableThrows(Map<String, String> replacerMap) {
		return format(replacerMap, Replacers.chain().unresolvableThrow());
	}

	// ********

	/**
	 * @param placeHolderName
	 * @return
	 */
	public static String wrapKey(String placeHolderName) {
		return DEFAULT_PLACEHOLDER_PREFIX + placeHolderName + DEFAULT_PLACEHOLDER_SUFFIX;
	}

	/**
	 * @param pattern
	 * @return
	 */
	public static String format(String pattern) {
		Replacer replacer = Replacers.chain()
				.ofSystemProperties()
				.ofEnv()
				.unresolvableThrow();
		return PlaceHolder.with(pattern).format(replacer);
	}

	/**
	 * @param pattern
	 * @param replacerMap
	 * @return
	 */
	public static String format(String pattern, Map<String, String> replacerMap) {
		return PlaceHolder.with(pattern)
				.format(Replacers.chain()
						.map(replacerMap)
						.unresolvableThrow());
	}

	/**
	 * @param pattern
	 * @param replacer
	 * @return
	 */
	public static String format(String pattern, Replacer replacer) {
		return PlaceHolder.with(pattern).format(replacer);
	}

	// *******************************************************

	/**
	 * @param pattern
	 * @param replacer
	 * @param visitedPlaceholders
	 * @return
	 */
	private String format(String pattern, Replacer replacer, Set<String> visitedPlaceholders) {
		if(pattern == null || "".equals(pattern)) {
			return "";
		}
		Deque<StringBuilder> stack = new ArrayDeque<>(4);
		stack.push(new StringBuilder());
		for(int j = 0; j < pattern.length(); ++j) {
			if(find(pattern, placeholderPrefix, j)) { // find ${
				stack.add(new StringBuilder());
				j += placeholderPrefix.length() - 1;
				continue;
			}
			if(find(pattern, placeholderSuffix, j) && stack.size() > 1) { // find }
				String key = stack.pollLast().toString();
				String replaced = replace(key, replacer, visitedPlaceholders);
				stack.peekLast().append(replaced);
				j += placeholderSuffix.length() - 1;
				continue;
			}
			stack.peekLast().append(pattern.charAt(j));
		}
		if(stack.size() != 1) {
			throw new PlaceHolderException("Unclosed placeholder: " + pattern);
		}
		return stack.peek().toString();
	}

	/**
	 * @param keyName
	 * @param replacer
	 * @param visitedPlaceholders
	 * @return
	 */
	private String replace(String keyName, Replacer replacer, Set<String> visitedPlaceholders) {
		String apply = null;
		if( ! visitedPlaceholders.add(keyName)) {
			apply = circularKeyReplacer.replace(keyName);
		} else {
			apply = replacer.replace(keyName);
			apply = format(apply, replacer, visitedPlaceholders);
		}
		visitedPlaceholders.remove(keyName);
		return apply;
	}

	/**
	 * @param pattern
	 * @param toFind
	 * @param currentIndex
	 * @return
	 */
	private static boolean find(String pattern, String toFind, final int currentIndex) {
		int i = 0;
		for(; i < toFind.length() && currentIndex + i < pattern.length(); ++i) {
			if(pattern.charAt(currentIndex + i) != toFind.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return
	 */
	private static Replacer getDefaultCircularKeyNameFunction() {
		return s -> {
			throw new PlaceHolderException("Circular placeholder reference '" + s + "' in property definitions");
		};
	}
}
