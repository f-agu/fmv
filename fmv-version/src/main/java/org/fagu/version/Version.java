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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author f.agu
 */
public class Version implements Comparable<Version>, Serializable, Cloneable {

	private static final long serialVersionUID = 6376523197217528603L;

	private final TreeMap<VersionUnit, VersionField> versionMap = new TreeMap<>();

	public static final Version V0 = new Version(0);

	public static final Version V1 = new Version(1);

	public static final Version V2 = new Version(2);

	public static final Version V3 = new Version(3);

	public static final Version V4 = new Version(4);

	public static final Version V5 = new Version(5);

	/**
	 * @param values
	 * @throws IllegalArgumentException
	 */
	public Version(int... values) {
		if(values == null || values.length <= 0) {
			throw new IllegalArgumentException("At least a value must be specified !");
		}
		if(values.length > VersionUnit.upper().getPosition()) {
			throw new IllegalArgumentException("Too many number : " + values.length + " ; max = " + VersionUnit.upper().getPosition() + 1);
		}
		int len = values.length;
		for(int i = 0; i < len; ++i) {
			setIn(VersionUnit.parse(i), values[i]);
		}
	}

	/**
	 * @param collection
	 * @throws IllegalArgumentException
	 */
	public Version(Collection<? extends Number> collection) {
		if(collection == null || collection.isEmpty()) {
			throw new IllegalArgumentException("At least a value must be specified !");
		}
		if(collection.size() > VersionUnit.upper().getPosition()) {
			throw new IllegalArgumentException("Too many number : " + collection.size() + " ; max = " + (VersionUnit.upper().getPosition() + 1)
					+ " : " + collection);
		}
		Iterator<? extends Number> iterator = collection.iterator();
		int count = 0;
		while(iterator.hasNext()) {
			setIn(VersionUnit.parse(count), iterator.next().intValue());
			++count;
		}
	}

	/**
	 * @param versionFields
	 * @throws IllegalArgumentException
	 */
	public Version(VersionField... versionFields) {
		if(versionFields == null || versionFields.length <= 0) {
			throw new IllegalArgumentException("At least one VersionField must be specified !");
		}
		if(versionFields.length > VersionUnit.upper().getPosition()) {
			throw new IllegalArgumentException("Too many number : " + versionFields.length + " ; max = " + VersionUnit.upper().getPosition() + 1);
		}
		// set all fields
		for(VersionField versionField : versionFields) {
			set(versionField);
		}
		// verify continious field
		fillContiniousField();
	}

	/**
	 * @param version
	 */
	public Version(Version version) {
		for(VersionField versionField : version.versionMap.values()) {
			set(new VersionField(versionField));
		}
		// verify continious field
		fillContiniousField();
	}

	/**
	 * @param versionUnit
	 * @return
	 */
	public VersionField getField(VersionUnit versionUnit) {
		return versionMap.get(versionUnit);
	}

	/**
	 * @param versionUnit
	 * @param defaultValue
	 * @return
	 */
	public int getFieldValue(VersionUnit versionUnit, int defaultValue) {
		VersionField versionField = getField(versionUnit);
		return versionField != null ? versionField.getValue() : defaultValue;
	}

	/**
	 * @param versionUnit
	 * @return
	 */
	public boolean contains(VersionUnit versionUnit) {
		return versionMap.containsKey(versionUnit);
	}

	/**
	 * @return
	 */
	public int size() {
		return versionMap.size();
	}

	/**
	 * @return
	 */
	public String getRealValue() {
		return toString();
	}

	/**
	 * Equivalent à isMoreRecentThan()
	 *
	 * @param version
	 * @return
	 */
	public boolean isUpperThan(Version other) {
		return compareTo(other) > 0;
	}

	/**
	 * Equivalent à isMoreRecentOrEqualsThan()
	 *
	 * @param other
	 * @return
	 */
	public boolean isUpperOrEqualsThan(Version other) {
		return compareTo(other) >= 0;
	}

	/**
	 * Equivalent a isOlderThan()
	 *
	 * @param other
	 * @return
	 */
	public boolean isLowerThan(Version other) {
		return compareTo(other) < 0;
	}

	/**
	 * Equivalent a isOlderOrEqualsThan()
	 *
	 * @param other
	 * @return
	 */
	public boolean isLowerOrEqualsThan(Version other) {
		return compareTo(other) <= 0;
	}

	/**
	 * @param versionUnit
	 * @param value
	 * @return
	 */
	public Version add(VersionUnit versionUnit, int value) {
		int previousValue = getFieldValue(versionUnit, 0);
		int newValue = previousValue + value;
		if(newValue < 0) {
			throw new IllegalArgumentException("Value is negative: " + previousValue + " + " + value + " = " + newValue);
		}
		Version version = (Version)clone();
		version.setIn(versionUnit, newValue);
		return version;
	}

	/**
	 * @param versionUnit
	 * @param value
	 * @return
	 */
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

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Version other) {
		// moi petit => -1
		// moi grand => 1
		VersionField othervf;
		Map<VersionUnit, VersionField> longMap;
		Map<VersionUnit, VersionField> shortMap;
		int sign = 1;
		if(size() > other.size()) { // me long, other short
			longMap = versionMap;
			shortMap = other.versionMap;
		} else { // me short, other long
			longMap = other.versionMap;
			shortMap = versionMap;
			sign = - 1;
		}

		for(VersionField versionField : longMap.values()) {
			othervf = shortMap.get(versionField.getVersionUnit());
			if(othervf == null) {
				if(versionField.getValue() == 0) {
					continue;
				}
				return sign;
			}
			if(othervf.getValue() < versionField.getValue()) {
				return sign;
			} else if(othervf.getValue() > versionField.getValue()) {
				return - sign;
			}
		}
		return 0;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 31 * versionMap.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Version ? compareTo((Version)obj) == 0 : false;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		return new Version(this);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
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

	// ******************************************

	/**
	 * @param str
	 * @return
	 * @throws VersionParseException
	 */
	public static Version parse(String str) throws VersionParseException {
		return VersionParserManager.parse(str);
	}

	// ******************************************

	/**
	 * @param versionUnit
	 * @param value
	 * @throws NullPointerException
	 */
	private void setIn(VersionUnit versionUnit, int value) {
		if(versionUnit == null) {
			throw new NullPointerException("VersionUnit is null");
		}
		set(new VersionField(versionUnit, value));
	}

	/**
	 * @param versionField
	 */
	private void set(VersionField versionField) {
		versionMap.put(versionField.getVersionUnit(), versionField);
	}

	/**
	 * Ajouter des zéros si vide
	 */
	private void fillContiniousField() {
		Iterable<VersionUnit> iterable = VersionUnit.iterable(versionMap.firstKey(), versionMap.lastKey());
		for(VersionUnit versionUnit : iterable) {
			if( ! versionMap.containsKey(versionUnit)) {
				set(new VersionField(versionUnit, 0));
			}
		}
	}
}
