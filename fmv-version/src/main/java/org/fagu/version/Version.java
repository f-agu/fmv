package org.fagu.version;

/*
 * #%L
 * fmv-utils
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import org.fagu.version.parser.DotNumberRLCVersionParser;


/**
 * @author f.agu
 */
public class Version implements Comparable<Version>, Serializable, Cloneable {

	private static final long serialVersionUID = 6376523197217528603L;

	// --------------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class VersionBuilder {

		private NavigableMap<VersionUnit, VersionField> versionMap;

		private ReleaseLifeCycle releaseLifeCycle;

		private String text;

		private VersionBuilder() {}

		public static VersionBuilder v(int... values) {
			VersionBuilder versionBuilder = new VersionBuilder();
			versionBuilder.versionMap = numbersToMap(values);
			return versionBuilder;
		}

		public static VersionBuilder major(int value) {
			VersionBuilder versionBuilder = new VersionBuilder();
			versionBuilder.versionMap = new TreeMap<>();
			return versionBuilder.unit(VersionUnit.VF_0_MAJOR, value);
		}

		public VersionBuilder minor(int value) {
			return unit(VersionUnit.VF_1_MINOR, value);
		}

		public VersionBuilder revision(int value) {
			return unit(VersionUnit.VF_2_REVISION, value);
		}

		public VersionBuilder text(String text) {
			this.text = text;
			return this;
		}

		public VersionBuilder unit(VersionUnit versionUnit, int value) {
			versionMap.put(versionUnit, new VersionField(versionUnit, value));
			return this;
		}

		public VersionBuilder rc(int value) {
			return releaseLifeCycle(ReleaseLifeCycle.rc(value));
		}

		public VersionBuilder releaseLifeCycle(ReleaseLifeCycle releaseLifeCycle) {
			this.releaseLifeCycle = releaseLifeCycle;
			return this;
		}

		public Version build() {
			if(versionMap.isEmpty()) {
				throw new IllegalStateException("Not contains any value");
			}
			if(text == null && releaseLifeCycle == null && versionMap.size() == 1 && versionMap.firstKey() == VersionUnit.VF_0_MAJOR) {
				int value = versionMap.firstEntry().getValue().getValue();
				switch(value) {
					case 0:
						return V0;
					case 1:
						return V1;
					case 2:
						return V2;
					case 3:
						return V3;
					case 4:
						return V4;
					case 5:
						return V5;
					default:
				}
			}
			return new Version(versionMap, text, releaseLifeCycle);
		}
	}

	// --------------------------------------------------------------

	private final String text;

	private final NavigableMap<VersionUnit, VersionField> versionMap;

	private final ReleaseLifeCycle releaseLifeCycle;

	public static final Version V0 = new Version(0);

	public static final Version V1 = new Version(1);

	public static final Version V2 = new Version(2);

	public static final Version V3 = new Version(3);

	public static final Version V4 = new Version(4);

	public static final Version V5 = new Version(5);

	private Version(Map<VersionUnit, VersionField> map) {
		TreeMap<VersionUnit, VersionField> vmap = new TreeMap<>();
		vmap.putAll(map);
		fillContiniousField(vmap);
		this.versionMap = Collections.unmodifiableNavigableMap(vmap);
		text = null;
		releaseLifeCycle = ReleaseLifeCycle.EMPTY;
	}

	public Version(int... values) {
		if(values == null || values.length <= 0) {
			throw new IllegalArgumentException("At least a value must be specified !");
		}
		if(values.length > VersionUnit.upper().getPosition()) {
			throw new IllegalArgumentException("Too many number : " + values.length + " ; max = " + VersionUnit.upper().getPosition() + 1);
		}
		text = null;
		versionMap = Collections.unmodifiableNavigableMap(numbersToMap(values));
		releaseLifeCycle = ReleaseLifeCycle.EMPTY;
	}

	public Version(Collection<? extends Number> collection) {
		if(collection == null || collection.isEmpty()) {
			throw new IllegalArgumentException("At least a value must be specified !");
		}
		if(collection.size() > VersionUnit.upper().getPosition()) {
			throw new IllegalArgumentException("Too many number : " + collection.size() + " ; max = " + (VersionUnit.upper().getPosition() + 1)
					+ " : " + collection);
		}
		text = null;
		Iterator<? extends Number> iterator = collection.iterator();
		int count = 0;
		NavigableMap<VersionUnit, VersionField> map = new TreeMap<>();
		while(iterator.hasNext()) {
			setIn(map, VersionUnit.parse(count), iterator.next().intValue());
			++count;
		}
		versionMap = Collections.unmodifiableNavigableMap(map);
		releaseLifeCycle = ReleaseLifeCycle.EMPTY;
	}

	public Version(VersionField... versionFields) {
		if(versionFields == null || versionFields.length <= 0) {
			throw new IllegalArgumentException("At least one VersionField must be specified !");
		}
		if(versionFields.length > VersionUnit.upper().getPosition()) {
			throw new IllegalArgumentException("Too many number : " + versionFields.length + " ; max = " + VersionUnit.upper().getPosition() + 1);
		}
		text = null;
		NavigableMap<VersionUnit, VersionField> map = new TreeMap<>();
		// set all fields
		for(VersionField versionField : versionFields) {
			set(map, versionField);
		}
		// verify continious field
		fillContiniousField(map);
		versionMap = Collections.unmodifiableNavigableMap(map);
		releaseLifeCycle = ReleaseLifeCycle.EMPTY;
	}

	public Version(Version version) {
		this(null, version);
	}

	public Version(String realValue, Version version) {
		this.text = realValue;
		NavigableMap<VersionUnit, VersionField> map = new TreeMap<>();
		for(VersionField versionField : version.versionMap.values()) {
			set(map, new VersionField(versionField));
		}
		// verify continious field
		fillContiniousField(map);
		versionMap = Collections.unmodifiableNavigableMap(map);
		releaseLifeCycle = getReleaseLifeCycle(realValue);
	}

	private Version(NavigableMap<VersionUnit, VersionField> map, String text, ReleaseLifeCycle releaseLifeCycle) {
		this.versionMap = Collections.unmodifiableNavigableMap(Objects.requireNonNull(map));
		this.text = text;
		this.releaseLifeCycle = releaseLifeCycle;
	}

	public VersionField getField(VersionUnit versionUnit) {
		return versionMap.get(versionUnit);
	}

	public int getFieldValue(VersionUnit versionUnit, int defaultValue) {
		VersionField versionField = getField(versionUnit);
		return versionField != null ? versionField.getValue() : defaultValue;
	}

	public int getMajorValue() {
		return getFieldValue(VersionUnit.VF_0_MAJOR, 0);
	}

	public int getMinorValue() {
		return getFieldValue(VersionUnit.VF_1_MINOR, 0);
	}

	public int getRevisionValue() {
		return getFieldValue(VersionUnit.VF_2_REVISION, 0);
	}

	public int getBuildValue() {
		return getFieldValue(VersionUnit.VF_3_BUILD, 0);
	}

	public boolean contains(VersionUnit versionUnit) {
		return versionMap.containsKey(versionUnit);
	}

	public int size() {
		return versionMap.size();
	}

	public String getText() {
		return text;
	}

	public String getDigitValue() {
		StringBuilder buf = new StringBuilder();
		Iterator<VersionField> i = versionMap.values().iterator();
		boolean hasNext = i.hasNext();
		while(hasNext) {
			VersionField versionField = i.next();
			buf.append(versionField.getValue());
			hasNext = i.hasNext();
			if(hasNext) {
				buf.append('.');
			}
		}
		return buf.toString();
	}

	public boolean isUpperThan(Version other) {
		return compareTo(other) > 0;
	}

	public boolean isUpperOrEqualsThan(Version other) {
		return compareTo(other) >= 0;
	}

	public boolean isLowerThan(Version other) {
		return compareTo(other) < 0;
	}

	public boolean isLowerOrEqualsThan(Version other) {
		return compareTo(other) <= 0;
	}

	public Version add(VersionUnit versionUnit, int value) {
		int previousValue = getFieldValue(versionUnit, 0);
		int newValue = previousValue + value;
		if(newValue < 0) {
			throw new IllegalArgumentException("Value is negative: " + previousValue + " + " + value + " = " + newValue);
		}
		NavigableMap<VersionUnit, VersionField> map = new TreeMap<>(versionMap);
		setIn(map, versionUnit, newValue);
		return new Version(map, null, releaseLifeCycle);
	}

	public Version set(VersionUnit versionUnit, int value) {
		if(value < 0) {
			throw new IllegalArgumentException("Value is negative: " + value);
		}
		Version version = (Version)clone();
		version.set(versionUnit, value);
		return version;
	}

	/**
	 * Coupe la version a l'unite specifiee.<br>
	 * Exemple :<br>
	 * Avec 1.5.6.4.8, cut(VersionUnit.VF_3_BUILD) donne 1.5.6.4
	 *
	 * @param versionUnit
	 * @param includUnitInResult
	 * @return
	 */
	public Version cut(VersionUnit limitVersionUnit) {
		List<VersionField> list = new ArrayList<>();
		for(VersionField versionField : versionMap.values()) {
			if(versionField.getVersionUnit().compareTo(limitVersionUnit) <= 0) {
				list.add(versionField);
			}
		}
		return new Version(list.toArray(new VersionField[list.size()]));
	}

	public Version cutIgnoreZeroAtEnd() {
		return cutMapIgnoreZeroAtEnd().map(Version::new).orElse(this);
	}

	public Version removeText() {
		Collection<VersionField> values = versionMap.values();
		return new Version(values.toArray(new VersionField[values.size()]));
	}

	public ReleaseLifeCycle getReleaseLifeCycle() {
		return releaseLifeCycle;
	}

	public NavigableMap<VersionUnit, VersionField> toMap() {
		return versionMap;
	}

	@Override
	public int compareTo(Version other) {
		return VersionComparatorHelper.compare(this, other);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(text != null && ! text.equals(getDigitValue())) {
			result = prime * result + text.hashCode();
		}
		result = prime * result + ((releaseLifeCycle == null || releaseLifeCycle == ReleaseLifeCycle.EMPTY) ? 0 : releaseLifeCycle.hashCode());
		result = prime * result + ((versionMap == null) ? 0 : cutMapIgnoreZeroAtEnd().map(Object::hashCode).orElse(versionMap.hashCode()));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if( ! (obj instanceof Version)) {
			return false;
		}
		return compareTo((Version)obj) == 0;
	}

	@Override
	public Object clone() {
		return new Version(this);
	}

	@Override
	public String toString() {
		if(text != null) {
			return text;
		}
		StringBuilder buf = new StringBuilder();
		buf.append(getDigitValue());
		if(releaseLifeCycle != null) {
			String rlc = releaseLifeCycle.toString();
			if(rlc != null && ! "".equals(rlc)) {
				buf.append('-').append(releaseLifeCycle);
			}
		}
		return buf.toString();
	}

	// ******************************************

	public static Version parse(String str) throws VersionParseException {
		return VersionParserManager.parse(str);
	}

	// ******************************************

	private static void setIn(NavigableMap<VersionUnit, VersionField> versionMap, VersionUnit versionUnit, int value) {
		set(versionMap, new VersionField(Objects.requireNonNull(versionUnit), value));
	}

	private static void set(NavigableMap<VersionUnit, VersionField> versionMap, VersionField versionField) {
		versionMap.put(versionField.getVersionUnit(), versionField);
	}

	/**
	 * Ajouter des zeros si vide
	 *
	 * @param versionMap
	 */
	private static void fillContiniousField(NavigableMap<VersionUnit, VersionField> versionMap) {
		Iterable<VersionUnit> iterable = VersionUnit.iterable(versionMap.firstKey(), versionMap.lastKey());
		for(VersionUnit versionUnit : iterable) {
			if( ! versionMap.containsKey(versionUnit)) {
				set(versionMap, new VersionField(versionUnit, 0));
			}
		}
	}

	private static ReleaseLifeCycle getReleaseLifeCycle(String text) {
		String rlcText = text != null ? DotNumberRLCVersionParser.getReleaseLifeCycle(text) : "";
		return ReleaseLifeCycle.parse(rlcText);
	}

	private static NavigableMap<VersionUnit, VersionField> numbersToMap(int... values) {
		NavigableMap<VersionUnit, VersionField> map = new TreeMap<>();
		final int LENGTH = values.length;
		for(int i = 0; i < LENGTH; ++i) {
			setIn(map, VersionUnit.parse(i), values[i]);
		}
		return map;
	}

	private Optional<NavigableMap<VersionUnit, VersionField>> cutMapIgnoreZeroAtEnd() {
		TreeMap<VersionUnit, VersionField> reverse = new TreeMap<>(Collections.reverseOrder());
		reverse.putAll(versionMap);
		Iterator<Entry<VersionUnit, VersionField>> iterator = reverse.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<VersionUnit, VersionField> entry = iterator.next();
			if(entry.getValue().getValue() == 0) {
				iterator.remove();
			}
		}
		if(versionMap.size() == reverse.size()) {
			return Optional.empty();
		}
		return Optional.of(reverse);
	}

}
