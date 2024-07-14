package org.fagu.fmv.mymedia.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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

import java.util.Scanner;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.fagu.fmv.utils.io.UnclosedInputStream;


/**
 * @author Utilisateur
 * @created 13 mai 2018 14:37:36
 */
public class ScannerHelper {

	private ScannerHelper() {}

	public static Answer yesNo(String question) {
		return yesNo(question, null);
	}

	public static Answer yesNo(String question, YesNo defaultValue) {
		return ask(question, Arrays.asList(YesNo.values()), defaultValue);
	}

	// ------------------------------------------------

	public interface Answer {

		String getValue();

		default Collection<String> getVariants() {
			return Collections.emptySet();
		}
	}

	// ------------------------------------------------

	public enum YesNo implements Answer {

		YES {

			@Override
			public String getValue() {
				return "y";
			}

			@Override
			public Collection<String> getVariants() {
				return Collections.singleton("yes");
			}
		},

		NO {

			@Override
			public String getValue() {
				return "n";
			}

			@Override
			public Collection<String> getVariants() {
				return Collections.singleton("no");
			}
		}
	}

	// ------------------------------------------------

	public static Answer ask(String question, Collection<Answer> answers, Answer defaultAnswer) {
		Map<String, Answer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		BiConsumer<String, Answer> appender = (s, a) -> {
			if(map.put(s, a) != null) {
				throw new IllegalArgumentException("Char or variant " + s + " already exists");
			}
		};
		answers.stream()
				.forEach(a -> {
					appender.accept(a.getValue(), a);
					Collection<String> variants = a.getVariants();
					if(variants != null) {
						for(String variant : variants) {
							appender.accept(variant, a);
						}
					}
				});

		String prompt = question + " ? " + answers.stream()
				.map(a -> a == defaultAnswer ? a.getValue().toUpperCase() : a.getValue().toLowerCase())
				.map(c -> c.toString())
				.collect(Collectors.joining("/", "[", "]")) + ' ';
		System.out.print(prompt);
		try (Scanner scanner = new Scanner(new UnclosedInputStream(System.in))) {
			String line = null;
			while((line = scanner.nextLine()) != null) {
				line = line.trim();
				if(line.isEmpty() && defaultAnswer != null) {
					return defaultAnswer;
				}
				Answer answer = map.get(line);
				if(answer != null) {
					return answer;
				}
				System.out.print(prompt);
			}
		}
		throw new IllegalStateException();
	}

}
