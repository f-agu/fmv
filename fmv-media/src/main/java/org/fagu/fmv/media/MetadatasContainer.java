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
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.Fraction;

import net.sf.json.JSONObject;


/**
 * @author f.agu
 * @created 13 nov. 2019 11:04:26
 */
public interface MetadatasContainer extends Metadatas, MetadataProperties {

	static final Pattern FRACTION_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+))/(\\d+(?:\\.\\d+))");

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

	default Optional<Object> getFirstObject(String... propertyNames) {
		Map<String, Object> data = getData();
		return Arrays.stream(propertyNames)
				.map(data::get)
				.filter(Objects::nonNull)
				.findFirst();
	}

	@SuppressWarnings("unchecked")
	default <T> Optional<T> getFirstObject(Class<T> cls, Parser<T> parser, String... propertyNames) {
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
				.filter(Objects::nonNull)
				.map(o -> o)
				.findFirst();
	}

	default Optional<String> getFirstString(String... propertyNames) {
		return getFirstObject(String.class, new StringParser<String>() {

			@Override
			public String parse(Object i) {
				return (String)i;
			}
		}, propertyNames);
	}

	default Optional<Boolean> getFirstBoolean(String... propertyNames) {
		return getFirstObject(Boolean.class, new StringParser<Boolean>() {

			@Override
			public Boolean parse(Object i) {
				return Boolean.parseBoolean((String)i);
			}
		}, propertyNames);
	}

	default Optional<Integer> getFirstInteger(String... propertyNames) {
		return getFirstObject(Integer.class, new StringParser<Integer>() {

			@Override
			public Integer parse(Object i) {
				try {
					return Integer.parseInt((String)i);
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).intValue();
				}
			}
		}, propertyNames);
	}

	default Optional<Long> getFirstLong(String... propertyNames) {
		return getFirstObject(Long.class, new StringParser<Long>() {

			@Override
			public Long parse(Object i) {
				try {
					return Long.parseLong((String)i);
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).longValue();
				}
			}
		}, propertyNames);
	}

	default Optional<Float> getFirstFloat(String... propertyNames) {
		return getFirstObject(Float.class, new StringParser<Float>() {

			@Override
			public Float parse(Object i) {
				try {
					return Float.parseFloat((String)i);
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).floatValue();
				}
			}
		}, propertyNames);
	}

	default Optional<Double> getFirstDouble(String... propertyNames) {
		return getFirstObject(Double.class, new StringParser<Double>() {

			@Override
			public Double parse(Object i) {
				try {
					return Double.parseDouble((String)i);
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).doubleValue();
				}
			}
		}, propertyNames);
	}

	default Optional<Number> getFirstNumber(String... propertyNames) {
		return getFirstObject(Number.class, new StringParser<Number>() {

			@Override
			public Number parse(Object i) {
				try {
					return Double.parseDouble((String)i);
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).doubleValue();
				}
			}
		}, propertyNames);
	}

	default Optional<Fraction> getFirstFraction(String... propertyNames) {
		return getFirstObject(Fraction.class, new StringParser<Fraction>() {

			@Override
			public Fraction parse(Object i) {
				return Fraction.getFraction((String)i);
			}
		}, propertyNames);
	}

	default Optional<MetadatasContainer> getFirstMetadatas(String... propertyNames) {
		return getFirstObject(MetadatasContainer.class, new Parser<MetadatasContainer>() {

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

	// -------------------------

	interface Parser<O> {

		boolean accept(Object i);

		O parse(Object i);

	}

	abstract class StringParser<O> implements Parser<O> {

		@Override
		public boolean accept(Object i) {
			return i instanceof String;
		}

	}
}
