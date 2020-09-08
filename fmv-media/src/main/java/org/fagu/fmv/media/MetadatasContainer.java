package org.fagu.fmv.media;

/*-
 * #%L
 * fmv-media
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.Fraction;

import com.google.gson.Gson;


/**
 * @author f.agu
 * @created 13 nov. 2019 11:04:26
 */
public interface MetadatasContainer extends Metadatas, MetadataProperties {

	@Override
	default NavigableSet<String> getNames() {
		Map<String, Object> data = getData();
		if(data instanceof NavigableMap) {
			NavigableMap<String, Object> navigableMap = (NavigableMap<String, Object>)data;
			return Collections.unmodifiableNavigableSet(navigableMap.navigableKeySet());
		}
		return Collections.unmodifiableNavigableSet(new TreeSet<>(data.keySet()));
	}

	@Override
	default Object get(String name) {
		return getData().get(name);
	}

	@Override
	default String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(getData());
	}

	default Stream<Object> getObjects(String... propertyNames) {
		Map<String, Object> data = getData();
		return Arrays.stream(propertyNames)
				.map(data::get)
				.filter(Objects::nonNull);
	}

	default Optional<Object> getFirstObject(String... propertyNames) {
		return getObjects(propertyNames).findFirst();
	}

	default <T> Stream<T> getObjects(Class<T> cls, Parser<T> parser, String... propertyNames) {
		Map<String, Object> data = getData();
		return Arrays.stream(propertyNames)
				.map(data::get)
				.filter(Objects::nonNull)
				.map(o -> Parsers.objectTo(cls, parser).parse(o))
				.filter(Objects::nonNull);
	}

	default <T> Optional<T> getFirstObject(Class<T> cls, Parser<T> parser, String... propertyNames) {
		return getObjects(cls, parser, propertyNames).findFirst();
	}

	default Stream<String> getStrings(String... propertyNames) {
		return getObjects(String.class, Parsers.combine(Parsers.stringToString(), Parsers.objectToString()), propertyNames);
	}

	default Optional<String> getFirstString(String... propertyNames) {
		return getStrings(propertyNames).findFirst();
	}

	default Stream<Boolean> getBooleans(String... propertyNames) {
		return getObjects(Boolean.class, Parsers.stringToBoolean(), propertyNames);
	}

	default Optional<Boolean> getFirstBoolean(String... propertyNames) {
		return getBooleans(propertyNames).findFirst();
	}

	default Stream<Byte> getBytes(String... propertyNames) {
		return getObjects(Byte.class, Parsers.combine(Parsers.stringToByte(), Parsers.numberTo(Byte.class)), propertyNames);
	}

	default Optional<Byte> getFirstByte(String... propertyNames) {
		return getBytes(propertyNames).findFirst();
	}

	default Stream<Short> getShorts(String... propertyNames) {
		return getObjects(Short.class, Parsers.combine(Parsers.stringToShort(), Parsers.numberTo(Short.class)), propertyNames);
	}

	default Optional<Short> getFirstShort(String... propertyNames) {
		return getShorts(propertyNames).findFirst();
	}

	default Stream<Integer> getIntegers(String... propertyNames) {
		return getObjects(Integer.class, Parsers.combine(Parsers.stringToInteger(), Parsers.numberTo(Integer.class)), propertyNames);
	}

	default Optional<Integer> getFirstInteger(String... propertyNames) {
		return getIntegers(propertyNames).findFirst();
	}

	default Stream<Long> getLongs(String... propertyNames) {
		return getObjects(Long.class, Parsers.combine(Parsers.stringToLong(), Parsers.numberTo(Long.class)), propertyNames);
	}

	default Optional<Long> getFirstLong(String... propertyNames) {
		return getLongs(propertyNames).findFirst();
	}

	default Stream<Float> getFloats(String... propertyNames) {
		return getObjects(Float.class, Parsers.combine(Parsers.stringToFloat(), Parsers.numberTo(Float.class)), propertyNames);
	}

	default Optional<Float> getFirstFloat(String... propertyNames) {
		return getFloats(propertyNames).findFirst();
	}

	default Stream<Double> getDoubles(String... propertyNames) {
		return getObjects(Double.class, Parsers.combine(Parsers.stringToDouble(), Parsers.numberTo(Double.class)), propertyNames);
	}

	default Optional<Double> getFirstDouble(String... propertyNames) {
		return getDoubles(propertyNames).findFirst();
	}

	default Stream<Number> getNumbers(String... propertyNames) {
		return getObjects(Number.class, Parsers.stringToNumber(), propertyNames);
	}

	default Optional<Number> getFirstNumber(String... propertyNames) {
		return getNumbers(propertyNames).findFirst();
	}

	default Stream<Fraction> getFractions(String... propertyNames) {
		return getObjects(Fraction.class, Parsers.stringToFraction(), propertyNames);
	}

	default Optional<Fraction> getFirstFraction(String... propertyNames) {
		return getFractions(propertyNames).findFirst();
	}

	default Stream<MetadatasContainer> getMetadatass(String... propertyNames) {
		return getObjects(MetadatasContainer.class, new Parser<MetadatasContainer>() {

			@Override
			public boolean accept(Object i) {
				return i instanceof Map;
			}

			@Override
			public MetadatasContainer parse(Object i) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>)i;
				return new NavigableMapMetadatasContainer(map);
			}
		}, propertyNames);
	}

	default Optional<MetadatasContainer> getFirstMetadatas(String... propertyNames) {
		return getMetadatass(propertyNames).findFirst();
	}

	@SafeVarargs
	static <T> Optional<T> findFirstNonNull(Optional<T>... optionals) {
		return Arrays.stream(optionals)
				.filter(Objects::nonNull)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}
}
