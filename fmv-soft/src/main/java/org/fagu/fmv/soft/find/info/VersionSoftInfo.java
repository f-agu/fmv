package org.fagu.fmv.soft.find.info;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import java.util.Optional;

import org.fagu.fmv.soft.SoftName;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class VersionSoftInfo extends SoftInfo {

	private final Version version;

	/**
	 * @param file
	 * @param softName
	 * @param version
	 */
	public VersionSoftInfo(File file, SoftName softName, Version version) {
		super(file, softName);
		this.version = version;
	}

	/**
	 * @return
	 */
	public Optional<Version> getVersion() {
		return Optional.ofNullable(version);
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SoftInfo o) {
		if(o instanceof VersionSoftInfo) {
			VersionSoftInfo other = (VersionSoftInfo)o;
			if(version == null || other.version == null) {
				if(version == other.version) {
					return 0;
				}
				return version == null ? - 1 : 1;
			}
			return version.compareTo(other.version);
		}
		throw new IllegalArgumentException("Unable to compare " + VersionSoftInfo.class.getSimpleName() + " with " + o.getClass().getName());
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftInfo#getInfo()
	 */
	@Override
	public String getInfo() {
		return version != null ? version.toString() : "";
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(version == null) {
			return super.toString();
		}
		return super.toString() + ' ' + getVersion().get();
	}

}
