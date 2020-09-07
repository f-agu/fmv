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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.media.MetadataProperties;
import org.fagu.fmv.media.Parsers;
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

	protected InfoBase(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		this.movieMetadatas = movieMetadatas;
		this.map = DeepCopy.copy(map);
	}

	public abstract String getName();

	public NavigableMap<String, Object> getData() {
		return map;
	}

	@Override
	public NavigableSet<String> getNames() {
		return map.navigableKeySet();
	}

	@Override
	public Object get(String name) {
		Object object = map.get(name);
		if(object instanceof NavigableMap) {
			@SuppressWarnings("unchecked")
			final NavigableMap<String, Object> navigableMap = (NavigableMap<String, Object>)object;
			return new MetadataProperties() {

				@Override
				public NavigableSet<String> getNames() {
					return navigableMap.navigableKeySet();
				}

				@Override
				public Object get(String name) {
					return navigableMap.get(name);
				}

				@Override
				public String toString() {
					return navigableMap.toString();
				}
			};
		}
		return object;
	}

	public Optional<Integer> bitRate() {
		return getInt("bit_rate");
	}

	public Optional<Time> startTime() {
		return getTime("start_time");
	}

	public Optional<Duration> duration() {
		return Optional.ofNullable(getDuration("duration")
				.orElseGet(() -> tag("totalduration")
						.filter(Objects::nonNull)
						.map(o -> NumberUtils.toInt(String.valueOf(o)))
						.filter(Objects::nonNull)
						.filter(i -> i > 0)
						.map(Duration::valueOf)
						.orElse(null)));

	}

	public Optional<Integer> durationTimeBase() {
		return getInt("duration_ts");
	}

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

	public Optional<OffsetDateTime> createDateTime() {
		String tag = tagString("creation_time").orElse(null);
		if(tag == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(OffsetDateTime.parse(tag));
		} catch(DateTimeParseException e) { // ignore
		}
		SimpleDateFormat createDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		try {
			Date date = createDateFormat.parse(tag);
			Instant instant = date.toInstant();
			ZoneOffset offset = OffsetDateTime.ofInstant(instant, ZoneId.systemDefault()).getOffset();
			return Optional.of(instant.atOffset(offset));
		} catch(ParseException e) {
			// ignore
		}

		try {
			GregorianCalendar c = (GregorianCalendar)DatatypeConverter.parseDateTime(tag);
			return Optional.of(c.toZonedDateTime().toOffsetDateTime());
		} catch(IllegalArgumentException e) {
			// ignore
		}
		return Optional.empty();
	}

	public Optional<String> handlerName() {
		return tagString("handler_name").map(StringUtils::stripToEmpty);
	}

	public Optional<String> language() {
		return tagString("language");
	}

	public Optional<String> title() {
		return tagString("title");
	}

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

	public boolean isTreatedByFMV() {
		Optional<Object> comObj = tag("comment");
		return comObj.isPresent() ? String.valueOf(comObj.get()).startsWith("fmv") : false;
	}

	public Map<String, Object> tags() {
		return sub("tags");
	}

	@SuppressWarnings("unchecked")
	public <R> Optional<R> tag(String name) {
		return Optional.ofNullable((R)tags().get(name));
	}

	public Optional<String> tagString(String name) {
		return Optional.ofNullable((String)tags().get(name));
	}

	public Optional<String> tagFirstString(String... names) {
		Map<String, Object> tags = tags();
		return Arrays.stream(names)
				.map(tags::get)
				.filter(Objects::nonNull)
				.filter(o -> o instanceof String)
				.map(o -> (String)o)
				.findFirst();
	}

	public Map<String, Object> sub(String name) {
		Map<String, Object> m = getMap(name);
		return m == null ? Collections.<String, Object>emptyMap() : m;
	}

	public Object sub(String name, String key) {
		return sub(name).get(key);
	}

	// ********************************************

	protected Optional<Integer> getInt(String name) {
		return Parsers.parseToInteger(map.get(name));
	}

	protected Optional<Long> getLong(String name) {
		return Parsers.parseToLong(map.get(name));
	}

	protected Optional<Double> getDouble(String name) {
		return Parsers.parseToDouble(map.get(name));
	}

	protected Optional<String> getString(String name) {
		return Parsers.parseToString(map.get(name));
	}

	protected Optional<Time> getTime(String name) {
		return getDouble(name).map(Time::valueOf);
	}

	protected Optional<Duration> getDuration(String name) {
		return getDouble(name).map(Duration::valueOf);
	}

	protected Optional<FrameRate> getFrameRate(String name) {
		return get(name, FrameRate::parse);
	}

	protected Optional<Fraction> getFraction(String name) {
		return get(name, Fraction::parse);
	}

	protected Optional<Ratio> getRatio(String name) {
		return get(name, Ratio::parse);
	}

	protected Optional<PixelFormat> getPixelFormat(String name) {
		return get(name, PixelFormat::byName);
	}

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

	@SuppressWarnings("unchecked")
	protected NavigableMap<String, Object> getMap(String name) {
		return (NavigableMap<String, Object>)map.get(name);
	}

	@SuppressWarnings("unchecked")
	protected <E> List<E> getList(String name) {
		return (List<E>)map.get(name);
	}

}
