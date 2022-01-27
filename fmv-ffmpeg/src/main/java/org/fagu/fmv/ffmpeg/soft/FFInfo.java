package org.fagu.fmv.ffmpeg.soft;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.info.VersionDateSoftInfo;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class FFInfo extends VersionDateSoftInfo {

	private final Version version;

	private final Integer builtVersion;

	private final LocalDate builtDate;

	private final Set<String> configSet;

	private final Map<String, Version> libVersionMap;

	protected FFInfo(File file, Version version, String softName, LocalDate builtDate, Integer builtVersion, Set<String> configSet,
			Map<String, Version> libVersionMap) {
		super(file, softName, null, (Date)null);
		this.version = version;
		this.builtVersion = builtVersion;
		this.builtDate = estimateBuildDate(version, builtVersion, builtDate);
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

	@Override
	public Optional<Version> getVersion() {
		return Optional.ofNullable(version);
	}

	public Integer getBuiltVersion() {
		return builtVersion;
	}

	@Override
	public Optional<LocalDateTime> getLocalDateTime() {
		return Optional.ofNullable(getBuiltDate()).map(ld -> LocalDateTime.of(ld, LocalTime.MIDNIGHT));
	}

	@Override
	public Optional<LocalDate> getLocalDate() {
		return Optional.ofNullable(getBuiltDate());
	}

	public LocalDate getBuiltDate() {
		return builtDate != null ? builtDate : null;
	}

	public Set<String> getConfigs() {
		return configSet;
	}

	public boolean isEnable(String name) {
		return configSet.contains("--enable-" + name);
	}

	public Map<String, Version> getLibVersions() {
		return libVersionMap;
	}

	@Override
	public String getInfo() {
		if(version != null) {
			return version.toString();
		}
		if(builtVersion != null) {
			return builtVersion.toString();
		}
		if(builtDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return dateFormat.format(builtDate);
		}
		return "<version not found>";
	}

	@Override
	public int compareTo(SoftInfo o) {
		if( ! (o instanceof FFInfo)) {
			throw new IllegalArgumentException("Unable to compare FFInfo with " + o.getClass().getName());
		}
		FFInfo off = (FFInfo)o;
		if(version != null) {
			Version otherVersion = off.getVersion().orElse(null);
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
			LocalDate otherBuiltDate = off.getBuiltDate();
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

	// *****************************************************

	private LocalDate estimateBuildDate(Version version, Integer builtVersion, LocalDate defaultDate) {
		if(defaultDate != null) {
			return defaultDate;
		}
		if(version != null) {
			return BuildMapping.versionToLocalDate(version);
		}
		if(builtVersion != null) {
			return BuildMapping.buildToLocalDate(builtVersion);
		}
		return null;
	}

}
