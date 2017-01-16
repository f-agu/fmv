package org.fagu.fmv.soft.ffmpeg;

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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fagu.fmv.soft.SoftName;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class FFInfo extends SoftInfo {

	private final Version version;

	private final Integer builtVersion;

	private final Date builtDate;

	private final Set<String> configSet;

	private final Map<String, Version> libVersionMap;

	/**
	 * @param file
	 * @param version
	 * @param softName
	 * @param builtDate
	 * @param builtVersion
	 * @param configSet
	 * @param libVersionMap
	 */
	protected FFInfo(File file, Version version, SoftName softName, Date builtDate, Integer builtVersion, Set<String> configSet,
			Map<String, Version> libVersionMap) {
		super(file, softName);
		this.version = version;
		this.builtVersion = builtVersion;
		this.builtDate = builtDate;
		if(configSet != null) {
			this.configSet = Collections.unmodifiableSet(new HashSet<>(configSet));
		} else {
			this.configSet = Collections.emptySet();
		}
		if(libVersionMap != null) {
			this.libVersionMap = Collections.unmodifiableMap(new HashMap<>(libVersionMap));
		} else {
			this.libVersionMap = Collections.emptyMap();
		}
	}

	/**
	 * @return
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * @return
	 */
	public Integer getBuiltVersion() {
		return builtVersion;
	}

	/**
	 * @return
	 */
	public Date getBuiltDate() {
		return builtDate;
	}

	/**
	 * @return
	 */
	public Set<String> getConfigs() {
		return configSet;
	}

	/**
	 * @param name
	 * @return
	 */
	public boolean isEnable(String name) {
		return configSet.contains("--enable-" + name);
	}

	/**
	 * @return
	 */
	public Map<String, Version> getLibVersions() {
		return libVersionMap;
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftInfo#getInfo()
	 */
	@Override
	public String getInfo() {
		if(version != null) {
			return version.toString();
		}
		if(builtVersion != null) {
			return builtVersion.toString();
		}
		if(builtDate != null) {
			final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
			return DATE_FORMAT.format(builtDate);
		}
		return "<version not found>";
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SoftInfo o) {
		if( ! (o instanceof FFInfo)) {
			throw new IllegalArgumentException("Unable to compare FFInfo with " + o.getClass().getName());
		}
		FFInfo off = (FFInfo)o;
		if(version != null) {
			Version otherVersion = off.getVersion();
			if(otherVersion != null) {
				return version.compareTo(otherVersion);
			}
			// uncomparable
		}
		if(builtVersion != null) {
			Integer otherBuiltVersion = off.getBuiltVersion();
			if(otherBuiltVersion != null) {
				return builtVersion.compareTo(otherBuiltVersion);
			}
			// uncomparable
		}
		if(builtDate != null) {
			Date otherBuiltDate = off.getBuiltDate();
			if(otherBuiltDate != null) {
				return builtDate.compareTo(otherBuiltDate);
			}
			// never append
		}
		return 0;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(80);
		buf.append(getName());
		if(version != null) {
			buf.append(' ').append('v').append(version);
		}
		if(builtVersion != null) {
			buf.append(' ').append('N').append(builtVersion);
		}
		if(builtDate != null) {
			buf.append(' ').append('(').append(builtDate).append(')');
		}
		buf.append(' ').append('[');
		File file = getFile();
		if(file != null) {
			buf.append(file.getAbsolutePath());
			if( ! file.exists()) {
				buf.append(" (not exists)");
			}
		} else {
			buf.append("File undefined");
		}
		buf.append(']');
		return buf.toString();
	}

}
