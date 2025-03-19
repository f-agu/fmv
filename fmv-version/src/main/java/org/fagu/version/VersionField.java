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
import java.util.Objects;


/**
 * Un champs de la version (valeur + position). Cette classe est immuable.
 * 
 * @author f.agu
 */
public class VersionField implements Comparable<VersionField>, Serializable, Cloneable {

	private static final long serialVersionUID = 5971065222769882808L;

	private final int value;

	private final VersionUnit versionUnit;

	public VersionField(VersionUnit versionUnit, int value) {
		this.versionUnit = Objects.requireNonNull(versionUnit);
		if(value < 0) {
			throw new IllegalArgumentException("Value must be positive:" + value);
		}
		this.value = value;
	}

	public VersionField(VersionField versionField) {
		this(versionField.versionUnit, versionField.value);
	}

	public int getValue() {
		return value;
	}

	public VersionUnit getVersionUnit() {
		return versionUnit;
	}

	@Override
	public int hashCode() {
		return 31 * value + versionUnit.getPosition();
	}

	@Override
	public boolean equals(Object obj) {
		if( ! (obj instanceof VersionField)) {
			return false;
		}
		VersionField other = (VersionField)obj;
		return other.value == value && other.versionUnit.equals(versionUnit);
	}

	@Override
	public int compareTo(VersionField other) {
		// moi petit => -1
		// moi grand => 1
		int vunitcomp = other.versionUnit.compareTo(versionUnit);
		if(vunitcomp != 0) {
			return vunitcomp;
		}
		if(value < other.value) {
			return - 1;
		}
		return value == other.value ? 0 : 1;
	}

	@Override
	public Object clone() {
		return new VersionField(this);
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(versionUnit.toString()).append('=').append(value)
				.toString();
	}

	// ******************************************

	public static VersionField valueOf(int position, int value) {
		VersionUnit versionUnit = VersionUnit.parse(position);
		return versionUnit != null ? new VersionField(versionUnit, value) : null;
	}
}
