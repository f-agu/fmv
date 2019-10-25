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

import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class VersionSoftInfo extends SoftInfo {

	private final Version version;

	public VersionSoftInfo(File file, String softName, Version version) {
		super(file, softName);
		this.version = version;
	}

	public Optional<Version> getVersion() {
		return Optional.ofNullable(version);
	}

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
			int compare = version.compareTo(other.version);
			return compare == 0 ? super.compareTo(o) : compare;
		}
		throw new IllegalArgumentException("Unable to compare " + VersionSoftInfo.class.getSimpleName() + " with " + o.getClass().getName());
	}

	@Override
	public String getInfo() {
		return version != null ? version.toString() : "";
	}

	@Override
	public String toString() {
		Optional<Version> versionOpt = getVersion();
		if(versionOpt.isPresent()) {
			return super.toString() + ' ' + versionOpt.get();
		}
		return super.toString();
	}

}
