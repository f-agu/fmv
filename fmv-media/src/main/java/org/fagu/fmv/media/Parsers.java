package org.fagu.fmv.media;

import org.apache.commons.lang3.math.Fraction;


/**
 * @author Oodrive
 * @author f.agu
 * @created 20 nov. 2019 15:18:50
 */
public class Parsers {

	private Parsers() {}

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

	public static Parser<Integer> stringToInteger() {
		return new StringParser<Integer>() {

			@Override
			public Integer parse(Object i) {
				try {
					return Integer.parseInt((String)i);
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
					return Long.parseLong((String)i);
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
					return Float.parseFloat((String)i);
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
					return Double.parseDouble((String)i);
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).doubleValue();
				}
			}
		};
	}

	public static Parser<Number> stringToNumber() {
		return new StringParser<Number>() {

			@Override
			public Number parse(Object i) {
				try {
					return Double.parseDouble((String)i);
				} catch(NumberFormatException e) {
					return Fraction.getFraction((String)i).doubleValue();
				}
			}
		};
	}

	public static Parser<Fraction> stringToFraction() {
		return new StringParser<Fraction>() {

			@Override
			public Fraction parse(Object i) {
				return Fraction.getFraction((String)i);
			}
		};
	}

	// ----------------------------------------------

	public abstract static class StringParser<O> implements Parser<O> {

		@Override
		public boolean accept(Object i) {
			return i instanceof String;
		}

	}
}
