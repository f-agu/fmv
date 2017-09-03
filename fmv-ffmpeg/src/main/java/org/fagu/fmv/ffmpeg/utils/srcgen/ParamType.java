package org.fagu.fmv.ffmpeg.utils.srcgen;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 fagu
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

import java.lang.reflect.Field;
import java.math.BigInteger;

import org.fagu.fmv.ffmpeg.utils.AudioSampleFormat;
import org.fagu.fmv.ffmpeg.utils.Binary;
import org.fagu.fmv.ffmpeg.utils.ChannelLayout;
import org.fagu.fmv.ffmpeg.utils.Color;
import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.utils.media.Size;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public class ParamType<P> {

	public static final ParamType<Binary> BINARY = new ParamType<Binary>(Binary.class);

	public static final ParamType<ChannelLayout> CHANNEL_LAYOUT = new ParamType<ChannelLayout>(ChannelLayout.class);

	public static final ParamType<Color> COLOR = new ParamType<Color>(Color.class);

	public static final ParamType<Double> DOUBLE = new ParamType<Double>(Double.class) {

		@Override
		public Double parse(String s) {
			return parseNumber(s).doubleValue();
		}

		@Override
		public boolean isMax(Double p) {
			return p == Double.MAX_VALUE;
		}

		@Override
		public boolean isMin(Double p) {
			return p == Double.MIN_VALUE;
		}
	};

	public static final ParamType<Duration> DURATION = new ParamType<Duration>(Duration.class);

	public static final ParamType<Object> FLAGS = new ParamType<Object>(Object.class);

	public static final ParamType<Float> FLOAT = new ParamType<Float>(Float.class) {

		@Override
		public Float parse(String s) {
			return parseNumber(s).floatValue();
		}

		@Override
		public boolean isMax(Float p) {
			return p == Float.MAX_VALUE;
		}

		@Override
		public boolean isMin(Float p) {
			return p == Float.MIN_VALUE;
		}
	};

	public static final ParamType<Size> IMAGE_SIZE = new ParamType<Size>(Size.class);

	public static final ParamType<Integer> INT = new ParamType<Integer>(Integer.class) {

		@Override
		public Integer parse(String s) {
			return parseNumber(s).intValue();
		}

		@Override
		public boolean isMax(Integer p) {
			return p == Integer.MAX_VALUE;
		}

		@Override
		public boolean isMin(Integer p) {
			return p == Integer.MIN_VALUE;
		}
	};

	public static final ParamType<Long> INT64 = new ParamType<Long>(Long.class) {

		@Override
		public Long parse(String s) {
			return parseNumber(s).longValue();
		}

		@Override
		public boolean isMax(Long p) {
			return p == Long.MAX_VALUE;
		}

		@Override
		public boolean isMin(Long p) {
			return p == Long.MIN_VALUE;
		}
	};

	public static final ParamType<BigInteger> UINT64 = new ParamType<BigInteger>(BigInteger.class) {

		@Override
		public BigInteger parse(String s) {
			return new BigInteger(s, 10);
		}

		@Override
		public boolean isMax(BigInteger p) {
			return false; // p == Long.MAX_VALUE;
		}

		@Override
		public boolean isMin(BigInteger p) {
			return false; // return p == Long.MIN_VALUE;
		}
	};

	public static final ParamType<PixelFormat> PIX_FMT = new ParamType<PixelFormat>(PixelFormat.class);

	public static final ParamType<Fraction> RATIONAL = new ParamType<Fraction>(Fraction.class) {

		/**
		 * @see org.fagu.fmv.ffmpeg.utils.srcgen.ParamType#parse(java.lang.String)
		 */
		@Override
		public Fraction parse(String s) {
			return Fraction.parse(s);
		}
	};

	public static final ParamType<AudioSampleFormat> SAMPLE_FMT = new ParamType<AudioSampleFormat>(AudioSampleFormat.class);

	public static final ParamType<String> STRING = new ParamType<String>(String.class) {

		/**
		 * @see org.fagu.fmv.ffmpeg.utils.srcgen.ParamType#parse(java.lang.String)
		 */
		@Override
		public String parse(String s) {
			return s;
		}
	};

	public static final ParamType<Boolean> BOOLEAN = new ParamType<Boolean>(Boolean.class) {

		/**
		 * @see org.fagu.fmv.ffmpeg.utils.srcgen.ParamType#parse(java.lang.String)
		 */
		@Override
		public Boolean parse(String s) {
			return Boolean.parseBoolean(s);
		}

	};

	public static final ParamType<FrameRate> VIDEO_RATE = new ParamType<>(FrameRate.class);

	/**
	 *
	 */
	private final Class<?> cls;

	/**
	 * @param cls
	 */
	private ParamType(Class<P> cls) {
		this.cls = cls;
	}

	/**
	 * @return the cls
	 */
	public Class<?> getCls() {
		return cls;
	}

	/**
	 * @param s
	 * @return
	 */
	public P parse(String s) {
		return null;
	}

	/**
	 * @param p
	 * @return
	 */
	public boolean isMin(P p) {
		return false;
	}

	/**
	 * @param p
	 * @return
	 */
	public boolean isMax(P p) {
		return false;
	}

	public static ParamType<?> valueOf(String name) {
		try {
			Field declaredField = ParamType.class.getDeclaredField(name);
			return (ParamType<?>)declaredField.get(ParamType.class);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	// *******************************************************************

	/**
	 * @param s
	 * @return
	 */
	public static Number parseNumber(String s) {
		if("INT_MIN".equals(s)) {
			return Integer.MIN_VALUE;
		}
		if("INT_MAX".equals(s)) {
			return Integer.MAX_VALUE;
		}
		if("I64_MIN".equals(s)) {
			return Long.MIN_VALUE;
		}
		if("I64_MAX".equals(s)) {
			return Long.MAX_VALUE;
		}
		if("FLT_MIN".equals(s) || "-FLT_MAX".equals(s)) {
			return Float.MIN_VALUE;
		}
		if("FLT_MAX".equals(s)) {
			return Float.MAX_VALUE;
		}
		if("-DBL_MAX".equals(s)) {
			return Double.MIN_VALUE;
		}
		if("DBL_MAX".equals(s)) {
			return Double.MAX_VALUE;
		}

		if(s.contains("e+") || s.contains("e-") || s.contains(".")) {
			long l = Double.valueOf(Double.parseDouble(s)).longValue();
			if(l == 2147480000) {
				return Integer.MAX_VALUE;
			} else if(l == - 2147480000) {
				return Integer.MIN_VALUE;
			}
			return l;
		}
		return Long.parseLong(s);
	}
}
