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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Représente la position d'un nombre dans une version.
 *
 * major[.minor[.revision[.build]]]<br>
 * Cette classe est immuable.
 *
 * @author f.agu
 */
public class VersionUnit implements Comparable<VersionUnit>, Serializable {

	private static final long serialVersionUID = - 2197682375693576196L;

	private static final TreeMap<Integer, VersionUnit> POSITION_MAP = new TreeMap<>();

	private static final Map<String, VersionUnit> NAME_MAP = new HashMap<>();

	// ====================

	/**
	 * major
	 */
	public static final VersionUnit VF_0_MAJOR = new VersionUnit(0, "major");

	/**
	 * minor
	 */
	public static final VersionUnit VF_1_MINOR = new VersionUnit(1, "minor");

	/**
	 * revision
	 */
	public static final VersionUnit VF_2_REVISION = new VersionUnit(2, "revision");

	/**
	 * build
	 */
	public static final VersionUnit VF_3_BUILD = new VersionUnit(3, "build");

	public static final VersionUnit VF_4 = new VersionUnit(4);

	public static final VersionUnit VF_5 = new VersionUnit(5);

	public static final VersionUnit VF_6 = new VersionUnit(6);

	public static final VersionUnit VF_7 = new VersionUnit(7);

	public static final VersionUnit VF_8 = new VersionUnit(8);

	public static final VersionUnit VF_9 = new VersionUnit(9);

	// =========================

	private final String name;

	private final int position;

	protected VersionUnit(int position) {
		this(position, null);
	}

	protected VersionUnit(int position, String name) {
		this.position = position;
		this.name = name == null ? "" : name;
		synchronized(VersionUnit.class) {
			init();
		}
	}

	// **************************************************

	public int getPosition() {
		return position;
	}

	public String getName() {
		return name;
	}

	public VersionUnit next() {
		return parse(position + 1);
	}

	public VersionUnit previous() {
		return parse(position - 1);
	}

	@Override
	public int hashCode() {
		return position * 17;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof VersionUnit vu) {
			return position == vu.position;
		}
		return false;
	}

	@Override
	public int compareTo(VersionUnit versionUnit) {
		// moi petit => -1
		// moi grand => 1
		if(position < versionUnit.position) {
			return - 1;
		}
		return equals(versionUnit) ? 0 : 1;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(position);
		String curName = getName();
		if(curName != null && ! "".equals(curName)) {
			buf.append('/').append(curName);
		}
		return buf.toString();
	}

	// **************************************************

	public static VersionUnit upper() {
		return POSITION_MAP.get(POSITION_MAP.lastKey());
	}

	public static VersionUnit max(VersionUnit vu1, VersionUnit vu2) {
		return vu1.position <= vu2.position ? vu1 : vu2;
	}

	public static VersionUnit min(VersionUnit vu1, VersionUnit vu2) {
		return vu1.position >= vu2.position ? vu1 : vu2;
	}

	public static VersionUnit parse(int position) {
		if(position < 0) {
			throw new IllegalArgumentException("Position must be positive:" + position);
		}
		return POSITION_MAP.get(Integer.valueOf(position));
	}

	public static VersionUnit parse(String name) {
		VersionUnit versionUnit = NAME_MAP.get(name);
		if(versionUnit == null) {
			try {
				versionUnit = parse(Integer.parseInt(name));
			} catch(Exception ignored) {
				// ignore
			}
		}
		return versionUnit;
	}

	public static Iterable<VersionUnit> iterable() {
		return Collections.unmodifiableCollection(POSITION_MAP.values());
	}

	/**
	 * Both unit are included.
	 *
	 * @param firstVersionUnit
	 * @param lastVersionUnit
	 * @return
	 */
	public static Iterable<VersionUnit> iterable(VersionUnit firstVersionUnit, VersionUnit lastVersionUnit) {
		VersionUnit lastunit = lastVersionUnit.next();
		SortedMap<Integer, VersionUnit> sortedMap = null;
		if(lastunit != null) {
			sortedMap = POSITION_MAP.subMap(firstVersionUnit.getPosition(), lastVersionUnit.next().getPosition());
		} else {
			sortedMap = POSITION_MAP.tailMap(firstVersionUnit.getPosition());
		}
		return Collections.unmodifiableCollection(sortedMap.values());
	}

	// **************************************************

	private void init() {
		POSITION_MAP.put(Integer.valueOf(position), this);
		if(name != null && ! "".equals(name)) {
			NAME_MAP.put(name, this);
		}
	}

	/**
	 * Serialization magic to prevent "doppelgangers". This is a performance optimization.
	 *
	 * @return
	 */
	private Object readResolve() {
		synchronized(VersionUnit.class) {
			for(VersionUnit versionUnit : POSITION_MAP.values()) {
				if(this.position == versionUnit.position) {
					return versionUnit;
				}
			}
		}
		// Woops. Whoever sent us this object knows
		// about a new VersionUnit. Add it to our list.
		init();
		return this;
	}
}
