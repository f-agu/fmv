package org.fagu.fmv.ffmpeg.metadatas;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.OptionalInt;
import java.util.function.Function;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.ffmpeg.utils.Time;
import org.fagu.fmv.media.MetadataProperties;
import org.fagu.fmv.utils.media.Ratio;

import com.neovisionaries.i18n.LanguageAlpha3Code;
import com.neovisionaries.i18n.LanguageCode;


/**
 * @author f.agu
 */
public abstract class InfoBase implements MetadataProperties {

	protected final MovieMetadatas movieMetadatas;

	protected final NavigableMap<String, Object> map;

	/**
	 * @param movieMetadatas
	 * @param map
	 */
	protected InfoBase(MovieMetadatas movieMetadatas, NavigableMap<String, Object> map) {
		this.movieMetadatas = movieMetadatas;
		this.map = map;
	}

	/**
	 * @see org.fagu.fmv.media.MetadataProperties#getNames()
	 */
	@Override
	public NavigableSet<String> getNames() {
		return map.navigableKeySet();
	}

	/**
	 * @see org.fagu.fmv.media.MetadataProperties#get(java.lang.String)
	 */
	@Override
	public Object get(String name) {
		Object object = map.get(name);
		if(object instanceof NavigableMap) {
			@SuppressWarnings("unchecked")
			final NavigableMap<String, Object> navigableMap = (NavigableMap<String, Object>)object;
			return new MetadataProperties() {

				/**
				 * @see org.fagu.fmv.media.MetadataProperties#getNames()
				 */
				@Override
				public NavigableSet<String> getNames() {
					return navigableMap.navigableKeySet();
				}

				/**
				 * @see org.fagu.fmv.media.MetadataProperties#get(java.lang.String)
				 */
				@Override
				public Object get(String name) {
					return navigableMap.get(name);
				}

				/**
				 * @see java.lang.Object#toString()
				 */
				@Override
				public String toString() {
					return navigableMap.toString();
				}
			};
		}
		return object;
	}

	/**
	 * @return
	 */
	public OptionalInt bitRate() {
		return getInt("bit_rate");
	}

	/**
	 * @return
	 */
	public Time startTime() {
		return getTime("start_time");
	}

	/**
	 * @return
	 */
	public Duration duration() {
		return getDuration("duration");
	}

	/**
	 * @return
	 */
	public OptionalInt durationTimeBase() {
		return getInt("duration_ts");
	}

	/**
	 * @return
	 */
	public Date creationDate() {
		String tag = (String)tag("creation_time");
		if(tag == null) {
			return null;
		}
		String[] patterns = {"yyyy-MM-dd HH:mm:SS", "yyyy-MM-dd'T'HH:mm:ss.MMZ"};
		for(String pattern : patterns) {
			SimpleDateFormat createDateFormat = new SimpleDateFormat(pattern);
			try {
				return createDateFormat.parse(tag);
			} catch(ParseException e) {
				// ignore
			}
		}

		try {
			return Date.from(LocalDateTime.parse(tag).atZone(ZoneId.systemDefault()).toInstant());
		} catch(DateTimeParseException e) {
			// ignore
		}
		try {
			return DatatypeConverter.parseDateTime(tag).getTime();
		} catch(IllegalArgumentException e) {
			// ignore
		}
		return null;

	}

	/**
	 * @return
	 */
	public String handlerName() {
		return StringUtils.stripToEmpty((String)tag("handler_name"));
	}

	/**
	 * @return
	 */
	public String language() {
		return (String)tag("language");
	}

	/**
	 * @return
	 */
	public String title() {
		return (String)tag("title");
	}

	/**
	 * @return
	 */
	public Locale locale() {
		String language = language();
		if(language == null) {
			return null;
		}
		LanguageAlpha3Code languageAlpha3Code;
		try {
			languageAlpha3Code = LanguageAlpha3Code.valueOf(language);
		} catch(IllegalArgumentException e) {
			return null; // ignore
		}
		LanguageCode alpha2 = languageAlpha3Code.getAlpha2();
		if(alpha2 == null) {
			return null;
		}
		return alpha2.toLocale();
	}

	/**
	 * @return
	 */
	public boolean isTreatedByFMV() {
		Object comObj = tag("comment");
		return comObj == null ? false : String.valueOf(comObj).startsWith("fmv");
	}

	/**
	 * @param name
	 * @return
	 */
	public Map<String, Object> tags() {
		return sub("tags");
	}

	/**
	 * @param name
	 * @return
	 */
	public Object tag(String name) {
		return tags().get(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public Map<String, Object> sub(String name) {
		Map<String, Object> m = getMap(name);
		return m == null ? Collections.<String, Object>emptyMap() : m;
	}

	/**
	 * @param name
	 * @param key
	 * @return
	 */
	public Object sub(String name, String key) {
		return sub(name).get(key);
	}

	// ********************************************

	/**
	 * @param name
	 * @return
	 */
	protected OptionalInt getInt(String name) {
		Object object = map.get(name);
		if(object instanceof Integer) {
			return OptionalInt.of((Integer)object);
		}
		if(object instanceof String) {
			return OptionalInt.of(NumberUtils.toInt((String)object));
		}
		return OptionalInt.empty();
	}

	/**
	 * @param name
	 * @return
	 */
	protected Long getLong(String name) {
		Object object = map.get(name);
		if(object instanceof Number) {
			return ((Number)object).longValue();
		}
		if(object instanceof String) {
			return NumberUtils.toLong((String)object);
		}
		return null;
	}

	/**
	 * @param name
	 * @return
	 */
	protected Double getDouble(String name) {
		String s = getString(name);
		if(s == null) {
			return null;
		}
		return Double.parseDouble(s);
	}

	/**
	 * @param name
	 * @return
	 */
	protected String getString(String name) {
		return (String)map.get(name);
	}

	/**
	 * @param name
	 * @return
	 */
	protected Time getTime(String name) {
		Double d = getDouble(name);
		if(d == null) {
			return null;
		}
		return Time.valueOf(d);
	}

	/**
	 * @param name
	 * @return
	 */
	protected Duration getDuration(String name) {
		Double d = getDouble(name);
		if(d == null) {
			return null;
		}
		return Duration.valueOf(d);
	}

	/**
	 * @param name
	 * @return
	 */
	protected FrameRate getFrameRate(String name) {
		return get(name, FrameRate::parse);
	}

	/**
	 * @param name
	 * @return
	 */
	protected Fraction getFraction(String name) {
		return get(name, Fraction::parse);
	}

	/**
	 * @param name
	 * @return
	 */
	protected Ratio getRatio(String name) {
		return get(name, Ratio::parse);
	}

	/**
	 * @param name
	 * @return
	 */
	protected PixelFormat getPixelFormat(String name) {
		return get(name, PixelFormat::byName);
	}

	/**
	 * @param name
	 * @return
	 */
	protected <R> R get(String name, Function<String, R> function) {
		String s = getString(name);
		if(StringUtils.isBlank(s)) {
			return null;
		}
		try {
			return function.apply(s);
		} catch(IllegalArgumentException e) {
			// ignore
		}
		return null;
	}

	/**
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected NavigableMap<String, Object> getMap(String name) {
		return (NavigableMap<String, Object>)map.get(name);
	}

}
