package org.fagu.fmv.mymedia.movie.age.validator;

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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.SimilarityScore;
import org.fagu.fmv.mymedia.movie.age.Ages;
import org.fagu.fmv.mymedia.movie.age.NameValidator;


/**
 * @author Utilisateur
 * @created 7 juil. 2018 12:11:50
 */
public class ScoreOrAskNameValidator implements NameValidator {

	private final SimilarityScore<Double> similarityScore;

	private final double scoreLimit;

	public ScoreOrAskNameValidator() {
		this(new JaroWinklerDistance(), 0.9D);
	}

	public ScoreOrAskNameValidator(SimilarityScore<Double> similarityScore, double scoreLimit) {
		this.similarityScore = Objects.requireNonNull(similarityScore);
		if(scoreLimit < 0 || scoreLimit > 1) {
			throw new IllegalArgumentException("scoreLimit must be between 0 and 1: " + scoreLimit);
		}
		this.scoreLimit = scoreLimit;
	}

	@Override
	public Optional<String> getMostValid(String title, Map<String, Ages> names) {
		NavigableMap<Double, Set<String>> map = new TreeMap<>(Collections.reverseOrder());
		for(String foundText : names.keySet()) {
			map.computeIfAbsent(similarityScore.apply(foundText, title), k -> new HashSet<>()).add(foundText);
		}
		if(map.isEmpty()) {
			return Optional.empty();
		}
		Entry<Double, Set<String>> firstEntry = map.firstEntry();
		if(firstEntry.getKey() < scoreLimit || map.size() > 1) {
			return askBest(title, names);
		}
		Set<String> value = firstEntry.getValue();
		return value.size() == 1 ? Optional.of(value.iterator().next()) : Optional.empty();
	}

	// ******************************************************

	private Optional<String> askBest(String title, Map<String, Ages> names) {
		Map<String, Ages> sortedNames = new TreeMap<>(names);
		ListFrame listFrame = new ListFrame(title);
		for(Entry<String, Ages> entry : sortedNames.entrySet()) {
			listFrame.addButton(entry.getKey(), entry.getKey());
		}
		listFrame.addButton("?", null);
		try {
			String selectedValue = listFrame.getSelectedValue().get();
			return Optional.ofNullable(selectedValue);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
