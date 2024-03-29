package org.fagu.fmv.media;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

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
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.Fraction;


/**
 * @author f.agu
 * @created 20 nov. 2019 15:18:50
 */
public class Parsers {

	private Parsers() {}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@SafeVarargs
	public static <T> Parser<T> combine(Parser<T>... parsers) {
		return combine(Arrays.asList(parsers));
	}

	public static <T> Parser<T> combine(Collection<Parser<T>> parsers) {
		return new Parser<T>() {

			@Override
			public boolean accept(Object o) {
				return parsers.stream().anyMatch(p -> p.accept(o));
			}

			@Override
			public T parse(Object o) {
				return parsers.stream()
						.filter(p -> p.accept(o))
						.filter(Objects::nonNull)
						.findFirst()
						.map(p -> p.parse(o))
						.filter(Objects::nonNull)
						.orElse(null);
			}

		};
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> Parser<T> objectTo(Class<T> cls, Parser<T>... parsers) {
		return new ObjectParser<T>() {

			@Override
			public T parse(Object o) {
				if(o == null) {
					return null;
				}
				if(cls.isAssignableFrom(o.getClass())) {
					return (T)o;
				}
				if(parsers != null) {
					return Arrays.stream(parsers)
							.filter(p -> p.accept(o))
							.filter(Objects::nonNull)
							.map(p -> p.parse(o))
							.filter(Objects::nonNull)
							.findFirst()
							.orElse(null);
				}
				return null;
			}
		};
	}

	public static Parser<String> objectToString() {
		return objectTo(String.class, stringToString(), new Parser<String>() {

			@Override
			public boolean accept(Object i) {
				return true;
			}

			@Override
			public String parse(Object i) {
				return String.valueOf(i);
			}
		});
	}

	public static Parser<Boolean> objectToBoolean() {
		return objectTo(Boolean.class, stringToBoolean());
	}

	public static Parser<Byte> objectToByte() {
		return objectTo(Byte.class, stringToByte(), numberTo(Byte.class));
	}

	public static Parser<Short> objectToShort() {
		return objectTo(Short.class, stringToShort(), numberTo(Short.class));
	}

	public static Parser<Integer> objectToInteger() {
		return objectTo(Integer.class, stringToInteger(), numberTo(Integer.class));
	}

	public static Parser<Long> objectToLong() {
		return objectTo(Long.class, stringToLong(), numberTo(Long.class));
	}

	public static Parser<Float> objectToFloat() {
		return objectTo(Float.class, stringToFloat(), numberTo(Float.class));
	}

	public static Parser<Double> objectToDouble() {
		return objectTo(Double.class, stringToDouble(), numberTo(Double.class));
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public static Optional<String> parseToString(Object object) {
		return Optional.ofNullable(objectToString().parse(object));
	}

	public static Optional<Boolean> parseToBoolean(Object object) {
		return Optional.ofNullable(objectToBoolean().parse(object));
	}

	public static Optional<Byte> parseToByte(Object object) {
		return Optional.ofNullable(objectToByte().parse(object));
	}

	public static Optional<Short> parseToShort(Object object) {
		return Optional.ofNullable(objectToShort().parse(object));
	}

	public static Optional<Integer> parseToInteger(Object object) {
		return Optional.ofNullable(objectToInteger().parse(object));
	}

	public static Optional<Long> parseToLong(Object object) {
		return Optional.ofNullable(objectToLong().parse(object));
	}

	public static Optional<Float> parseToFloat(Object object) {
		return Optional.ofNullable(objectToFloat().parse(object));
	}

	public static Optional<Double> parseToDouble(Object object) {
		return Optional.ofNullable(objectToDouble().parse(object));
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public static Parser<String> stringToString() {
		return new StringParser<String>() {

			@Override
			public String parse(Object i) {
				return (String)i;
			}
		};
	}

	public static Parser<Boolean> stringToBoolean() {
		return new StringParser<Boolean>() {

			@Override
			public Boolean parse(Object i) {
				return Boolean.parseBoolean((String)i);
			}
		};
	}

	public static Parser<Byte> stringToByte() {
		return new StringParser<Byte>() {

			@Override
			public Byte parse(Object i) {
				try {
					return Byte.parseByte(fixNumberEndsWithComa((String)i));
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).byteValue();
				}
			}
		};
	}

	public static Parser<Short> stringToShort() {
		return new StringParser<Short>() {

			@Override
			public Short parse(Object i) {
				try {
					return Short.parseShort(fixNumberEndsWithComa((String)i));
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).shortValue();
				}
			}
		};
	}

	public static Parser<Integer> stringToInteger() {
		return new StringParser<Integer>() {

			@Override
			public Integer parse(Object i) {
				try {
					return Integer.parseInt(fixNumberEndsWithComa((String)i));
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).intValue();
				}
			}
		};
	}

	public static Parser<Long> stringToLong() {
		return new StringParser<Long>() {

			@Override
			public Long parse(Object i) {
				try {
					return Long.parseLong(fixNumberEndsWithComa((String)i));
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).longValue();
				}
			}
		};
	}

	public static Parser<Float> stringToFloat() {
		return new StringParser<Float>() {

			@Override
			public Float parse(Object i) {
				try {
					return Float.parseFloat(fixNumberEndsWithComa((String)i));
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).floatValue();
				}
			}
		};
	}

	public static Parser<Double> stringToDouble() {
		return new StringParser<Double>() {

			@Override
			public Double parse(Object i) {
				try {
					return Double.parseDouble(fixNumberEndsWithComa((String)i).replace(',', '.'));
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).doubleValue();
				}
			}
		};
	}

	public static Parser<Number> stringToNumber() {
		return new StringParser<Number>() {

			@Override
			public Number parse(Object o) {
				try {
					return Double.parseDouble(fixNumberEndsWithComa((String)o).replace(',', '.'));
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)o).doubleValue();
				}
			}
		};
	}

	public static Parser<Fraction> stringToFraction() {
		return new StringParser<Fraction>() {

			@Override
			public Fraction parse(Object o) {
				return Fraction.getFraction((String)o);
			}
		};
	}

	public static String fixNumberEndsWithComa(String s) {
		if(s == null) {
			return null;
		}
		String trimmed = s.trim();
		if(trimmed.endsWith(",")) {
			return StringUtils.substringBeforeLast(trimmed, ",");
		}
		return s;
	}

	public static Parser<LocalDate> objectToLocalDate() {
		return Parsers.objectTo(LocalDate.class, stringToLocalDate());
	}

	public static Optional<LocalDate> parseToLocalDate(Object object) {
		return Optional.ofNullable(objectToLocalDate().parse(object));
	}

	public static Parser<LocalDate> stringToLocalDate() {
		return new StringParser<LocalDate>() {

			@Override
			public LocalDate parse(Object i) {
				try {
					return LocalDate.parse((String)i);
				} catch(DateTimeParseException e) {
					return null;
				}
			}
		};
	}

	public static Parser<LocalTime> objectToLocalTime() {
		return Parsers.objectTo(LocalTime.class, stringToLocalTime());
	}

	public static Optional<LocalTime> parseToLocalTime(Object object) {
		return Optional.ofNullable(objectToLocalTime().parse(object));
	}

	public static Parser<LocalTime> stringToLocalTime() {
		return new StringParser<LocalTime>() {

			@Override
			public LocalTime parse(Object i) {
				try {
					return LocalTime.parse((String)i);
				} catch(DateTimeParseException e) {
					return null;
				}
			}
		};
	}

	public static Parser<LocalDateTime> objectToLocalDateTime() {
		return Parsers.objectTo(LocalDateTime.class, stringToLocalDateTime());
	}

	public static Optional<LocalDateTime> parseToLocalDateTime(Object object) {
		return Optional.ofNullable(objectToLocalDateTime().parse(object));
	}

	public static Parser<LocalDateTime> stringToLocalDateTime() {
		return new StringParser<LocalDateTime>() {

			@Override
			public LocalDateTime parse(Object i) {
				try {
					return LocalDateTime.parse((String)i);
				} catch(DateTimeParseException e) {
					return null;
				}
			}
		};
	}

	public static Parser<OffsetTime> objectToOffsetTime() {
		return Parsers.objectTo(OffsetTime.class, stringToOffsetTime());
	}

	public static Optional<OffsetTime> parseToOffsetTime(Object object) {
		return Optional.ofNullable(objectToOffsetTime().parse(object));
	}

	public static Parser<OffsetTime> stringToOffsetTime() {
		return new StringParser<OffsetTime>() {

			@Override
			public OffsetTime parse(Object i) {
				try {
					return OffsetTime.parse((String)i);
				} catch(DateTimeParseException e) {
					return null;
				}
			}
		};
	}

	public static Parser<OffsetDateTime> objectToOffsetDateTime() {
		return Parsers.objectTo(OffsetDateTime.class, stringToOffsetDateTime());
	}

	public static Optional<OffsetDateTime> parseToOffsetDateTime(Object object) {
		return Optional.ofNullable(objectToOffsetDateTime().parse(object));
	}

	public static Parser<OffsetDateTime> stringToOffsetDateTime() {
		return new StringParser<OffsetDateTime>() {

			@Override
			public OffsetDateTime parse(Object i) {
				try {
					return OffsetDateTime.parse((String)i);
				} catch(DateTimeParseException e) {
					return null;
				}
			}
		};
	}

	public static Parser<ZonedDateTime> objectToZonedDateTime() {
		return Parsers.objectTo(ZonedDateTime.class, stringToZonedDateTime());
	}

	public static Optional<ZonedDateTime> parseToZonedDateTime(Object object) {
		return Optional.ofNullable(objectToZonedDateTime().parse(object));
	}

	public static Parser<ZonedDateTime> stringToZonedDateTime() {
		return new StringParser<ZonedDateTime>() {

			@Override
			public ZonedDateTime parse(Object i) {
				try {
					return ZonedDateTime.parse((String)i);
				} catch(DateTimeParseException e) {
					return null;
				}
			}
		};
	}

	public static Parser<UUID> objectToUUID() {
		return Parsers.objectTo(UUID.class, stringToUUID());
	}

	public static Optional<UUID> parseToUUID(Object object) {
		return Optional.ofNullable(objectToUUID().parse(object));
	}

	public static Parser<UUID> stringToUUID() {
		return new StringParser<UUID>() {

			@Override
			public UUID parse(Object i) {
				try {
					return UUID.fromString((String)i);
				} catch(IllegalArgumentException e) {
					return null;
				}
			}
		};
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public static <N extends Number> Parser<N> numberTo(Class<N> cls) {
		return new Parser<N>() {

			@Override
			public boolean accept(Object o) {
				return o instanceof Number;
			}

			@SuppressWarnings("unchecked")
			@Override
			public N parse(Object o) {
				Number number = (Number)o;
				if(cls.isAssignableFrom(number.getClass())) {
					return (N)number;
				}
				if(cls == Byte.class) {
					return (N)Byte.valueOf(number.byteValue());
				}
				if(cls == Short.class) {
					return (N)Short.valueOf(number.shortValue());
				}
				if(cls == Integer.class) {
					return (N)Integer.valueOf(number.intValue());
				}
				if(cls == Long.class) {
					return (N)Long.valueOf(number.longValue());
				}
				if(cls == Float.class) {
					return (N)Float.valueOf(number.floatValue());
				}
				if(cls == Double.class) {
					return (N)Double.valueOf(number.doubleValue());
				}
				throw new RuntimeException("Undefined type: " + cls);
			}
		};
	}

	// ----------------------------------------------

	public abstract static class ObjectParser<O> implements Parser<O> {

		@Override
		public boolean accept(Object i) {
			return i != null;
		}

	}

	public abstract static class StringParser<O> implements Parser<O> {

		@Override
		public boolean accept(Object i) {
			return i instanceof String;
		}

	}
}
