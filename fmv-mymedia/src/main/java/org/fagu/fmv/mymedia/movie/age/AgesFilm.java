package org.fagu.fmv.mymedia.movie.age;

/*-
 * #%L
 * fmv-mymedia
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.movie.age.AgesCache.AgesElement;
import org.fagu.fmv.mymedia.movie.age.validator.ScoreOrAskNameValidator;


/**
 * @author f.agu
 * @created 4 mai 2018 14:39:24
 */
public class AgesFilm {

	private final Logger logger;

	private final Map<String, Optional<Ages>> notInCache = new TreeMap<>();

	private final AgesCache agesCache;

	private final NameValidator nameValidator;

	public AgesFilm(Logger logger, AgesCache agesCache) {
		this(logger, agesCache, new ScoreOrAskNameValidator());
	}

	public AgesFilm(Logger logger, AgesCache agesCache, NameValidator nameValidator) {
		this.logger = Objects.requireNonNull(logger);
		this.agesCache = Objects.requireNonNull(agesCache);
		this.nameValidator = Objects.requireNonNull(nameValidator);
	}

	public Optional<Ages> getAges(String movieTitle) {
		return search(movieTitle);
	}

	public Map<String, Optional<Ages>> getAfterSearchNotInCache() {
		return Collections.unmodifiableMap(notInCache);
	}

	// **********************************************

	private Optional<Ages> search(String text) {
		Optional<AgesElement> element = agesCache.find(text);
		if(element.isPresent()) {
			AgesElement agesElement = element.get();
			if(agesElement.isAlreadySearchAndNotFound() || agesElement.isPresent()) {
				return agesElement.getAges();
			}
		}

		logger.log("SEARCH AGES ### not in cache for: " + text);

		Optional<Ages> ages = searchBy(text);
		if( ! ages.isPresent()) {
			ages = searchBy(stripAccents(text).replaceAll("[^A-Za-z ]", " "));
		}
		notInCache.put(text, ages);
		agesCache.put(text, ages.orElse(null), true);
		return ages;
	}

	private Optional<Ages> searchBy(String text) {
		Set<Ages> ages = new TreeSet<>();
		int page = 1;
		try {
			while(search(text, page, ages)) {
				++page;
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		if(ages.isEmpty()) {
			return Optional.empty();
		}
		if(ages.size() > 1) {
			logger.log("SEARCH AGES ### multiple found: " + ages);
		}
		return Optional.of(ages.iterator().next()); // TODO re-filter
	}

	private boolean search(String text, int page, Collection<Ages> ages) throws IOException {
		String lcTxt = text.toLowerCase();
		StringBuilder sb = new StringBuilder();
		sb.append("http://www.filmages.ch/films/recherche/search/").append(URLEncoder.encode(lcTxt, "UTF-8")).append(".html");
		if(page > 1) {
			sb.append("?page=").append(page);
		}
		URL url = new URL(sb.toString());
		URLConnection connection = url.openConnection();
		boolean hasMorePage = false;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String line;
			boolean found = false;
			double score = 0;
			String title = null;
			Integer legal = null;
			Integer suggested = null;
			Map<String, Ages> names = new HashMap<>();
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(found) {
					if("</tr>".equals(line)) {
						if(legal != null || suggested != null) {
							// ages.add();
							names.put(title, new Ages(title, legal, suggested, score));
						}
						found = false;
						title = null;
					} else if(line.startsWith("<td class=\"field age_legal\"")) {
						legal = parseAge(StringUtils.substringBetween(line, "<td class=\"field age_legal\">", " ans</td>"));
					} else if(line.startsWith("<td class=\"field age_suggested\"")) {
						suggested = parseAge(StringUtils.substringBetween(line, "<td class=\"field age_suggested\">", " ans</td>"));
					}
				} else {
					if(line.startsWith("<td class=\"field title_french\">")) {
						String name = StringUtils.substringBetween(line, "ment\">", "</a></td>");
						found = true;
						title = name;

						// score = new JaroWinklerDistance().apply(name, text);
						// if(score > scoreLimit) {
						// found = true;
						// title = name;
						// continue;
						// }
					} else if(line.startsWith("<li><a href=\"films/recherche/search/to.html?page=")) {
						hasMorePage = true;
					}
				}
			}
			if( ! names.isEmpty()) {
				Optional<String> mostValid = nameValidator.getMostValid(text, Collections.unmodifiableMap(names));
				if(mostValid.isPresent()) {
					title = mostValid.get();
					ages.add(names.get(title));
				}
			}
		}
		return hasMorePage;
	}

	public static String stripAccents(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}

	private Integer parseAge(String text) {
		try {
			return Integer.parseInt(text);
		} catch(Exception e) {
			return null;
		}
	}
}
