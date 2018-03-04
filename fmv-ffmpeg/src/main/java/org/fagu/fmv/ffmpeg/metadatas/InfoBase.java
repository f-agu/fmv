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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.media.MetadataProperties;
import org.fagu.fmv.utils.media.Ratio;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;

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
	public Optional<Time> startTime() {
		return getTime("start_time");
	}

	/**
	 * @return
	 */
	public Optional<Duration> duration() {
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
	public Optional<Date> creationDate() {
		String tag = tagString("creation_time").orElse(null);
		if(tag == null) {
			return Optional.empty();
		}
		String[] patterns = {"yyyy-MM-dd HH:mm:SS", "yyyy-MM-dd'T'HH:mm:ss.MMZ"};
		for(String pattern : patterns) {
			SimpleDateFormat createDateFormat = new SimpleDateFormat(pattern);
			try {
				return Optional.of(createDateFormat.parse(tag));
			} catch(ParseException e) {
				// ignore
			}
		}

		try {
			return Optional.of(Date.from(LocalDateTime.parse(tag).atZone(ZoneId.systemDefault()).toInstant()));
		} catch(DateTimeParseException e) {
			// ignore
		}
		try {
			return Optional.of(DatatypeConverter.parseDateTime(tag).getTime());
		} catch(IllegalArgumentException e) {
			// ignore
		}
		return Optional.empty();

	}

	/**
	 * @return
	 */
	public Optional<String> handlerName() {
		return tagString("handler_name").map(StringUtils::stripToEmpty);
	}

	/**
	 * @return
	 */
	public Optional<String> language() {
		return tagString("language");
	}

	/**
	 * @return
	 */
	public Optional<String> title() {
		return tagString("title");
	}

	/**
	 * @return
	 */
	public Optional<Locale> locale() {
		Optional<String> language = language();
		if( ! language.isPresent()) {
			return Optional.empty();
		}
		LanguageAlpha3Code languageAlpha3Code;
		try {
			languageAlpha3Code = LanguageAlpha3Code.valueOf(language.get());
		} catch(IllegalArgumentException e) {
			return Optional.empty(); // ignore
		}
		LanguageCode alpha2 = languageAlpha3Code.getAlpha2();
		if(alpha2 == null) {
			return Optional.empty();
		}
		return Optional.of(alpha2.toLocale());
	}

	/**
	 * @return
	 */
	public boolean isTreatedByFMV() {
		Optional<Object> comObj = tag("comment");
		return comObj.isPresent() ? String.valueOf(comObj.get()).startsWith("fmv") : false;
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
	@SuppressWarnings("unchecked")
	public <R> Optional<R> tag(String name) {
		return Optional.ofNullable((R)tags().get(name));
	}

	/**
	 * @param name
	 * @return
	 */
	public Optional<String> tagString(String name) {
		return Optional.ofNullable((String)tags().get(name));
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
	protected OptionalLong getLong(String name) {
		Object object = map.get(name);
		if(object instanceof Number) {
			return OptionalLong.of(((Number)object).longValue());
		}
		if(object instanceof String) {
			return OptionalLong.of(NumberUtils.toLong((String)object));
		}
		return OptionalLong.empty();
	}

	/**
	 * @param name
	 * @return
	 */
	protected Optional<Double> getDouble(String name) {
		return getString(name).map(Double::parseDouble);
	}

	/**
	 * @param name
	 * @return
	 */
	protected Optional<String> getString(String name) {
		return Optional.ofNullable((String)map.get(name));
	}

	/**
	 * @param name
	 * @return
	 */
	protected Optional<Time> getTime(String name) {
		return getDouble(name).map(Time::valueOf);
	}

	/**
	 * @param name
	 * @return
	 */
	protected Optional<Duration> getDuration(String name) {
		return getDouble(name).map(Duration::valueOf);
	}

	/**
	 * @param name
	 * @return
	 */
	protected Optional<FrameRate> getFrameRate(String name) {
		return get(name, FrameRate::parse);
	}

	/**
	 * @param name
	 * @return
	 */
	protected Optional<Fraction> getFraction(String name) {
		return get(name, Fraction::parse);
	}

	/**
	 * @param name
	 * @return
	 */
	protected Optional<Ratio> getRatio(String name) {
		return get(name, Ratio::parse);
	}

	/**
	 * @param name
	 * @return
	 */
	protected Optional<PixelFormat> getPixelFormat(String name) {
		return get(name, PixelFormat::byName);
	}

	/**
	 * @param name
	 * @return
	 */
	protected <R> Optional<R> get(String name, Function<String, R> function) {
		String s = getString(name).orElse(null);
		if(StringUtils.isBlank(s)) {
			return Optional.empty();
		}
		try {
			return Optional.ofNullable(function.apply(s));
		} catch(IllegalArgumentException e) {
			// ignore
		}
		return Optional.empty();
	}

	/**
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected NavigableMap<String, Object> getMap(String name) {
		return (NavigableMap<String, Object>)map.get(name);
	}

	/**
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <E> List<E> getList(String name) {
		return (List<E>)map.get(name);
	}

}
