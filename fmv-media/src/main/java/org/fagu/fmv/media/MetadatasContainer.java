package org.fagu.fmv.media;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.Fraction;

import net.sf.json.JSONObject;


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
		return JSONObject.fromObject(getData()).toString();
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

	@SuppressWarnings("unchecked")
	default <T> Stream<T> getObjects(Class<T> cls, Parser<T> parser, String... propertyNames) {
		Map<String, Object> data = getData();
		return Arrays.stream(propertyNames)
				.map(data::get)
				.map(o -> {
					if(o == null) {
						return null;
					}
					if(cls.isAssignableFrom(o.getClass())) {
						return (T)o;
					}
					if(parser != null && parser.accept(o)) {
						return parser.parse(o);
					}
					return null;
				})
				.filter(Objects::nonNull);
	}

	default <T> Optional<T> getFirstObject(Class<T> cls, Parser<T> parser, String... propertyNames) {
		return getObjects(cls, parser, propertyNames).findFirst();
	}

	default Stream<String> getStrings(String... propertyNames) {
		return getObjects(String.class, Parsers.stringToString(), propertyNames);
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

	default Stream<Integer> getIntegers(String... propertyNames) {
		return getObjects(Integer.class, Parsers.stringToInteger(), propertyNames);
	}

	default Optional<Integer> getFirstInteger(String... propertyNames) {
		return getIntegers(propertyNames).findFirst();
	}

	default Stream<Long> getLongs(String... propertyNames) {
		return getObjects(Long.class, Parsers.stringToLong(), propertyNames);
	}

	default Optional<Long> getFirstLong(String... propertyNames) {
		return getLongs(propertyNames).findFirst();
	}

	default Stream<Float> getFloats(String... propertyNames) {
		return getObjects(Float.class, Parsers.stringToFloat(), propertyNames);
	}

	default Optional<Float> getFirstFloat(String... propertyNames) {
		return getFloats(propertyNames).findFirst();
	}

	default Stream<Double> getDoubles(String... propertyNames) {
		return getObjects(Double.class, Parsers.stringToDouble(), propertyNames);
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
				NavigableMap<String, Object> nvMap = null;
				if(map instanceof NavigableMap) {
					nvMap = (NavigableMap<String, Object>)map;
				} else {
					nvMap = new TreeMap<>(map);
				}

				return new NavigableMapMetadatasContainer(nvMap);
			}
		}, propertyNames);
	}

	default Optional<MetadatasContainer> getFirstMetadatas(String... propertyNames) {
		return getMetadatass(propertyNames).findFirst();
	}

}
