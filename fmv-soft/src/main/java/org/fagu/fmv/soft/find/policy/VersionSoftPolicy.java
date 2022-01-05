package org.fagu.fmv.soft.find.policy;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.fagu.fmv.soft.find.Lines;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class VersionSoftPolicy extends SoftPolicy {

	@Override
	public SoftFound toSoftFound(Object object, Lines lines) {
		VersionSoftInfo versionSoftInfo = (VersionSoftInfo)object;
		if(list.isEmpty()) {
			throw new IllegalStateException("No validator defined !");
		}
		// system properties
		SoftFound byPropertiesFound = byProperties(versionSoftInfo, lines);
		if(byPropertiesFound != null && byPropertiesFound.isFound()) {
			return byPropertiesFound;
		}

		// defined
		return byDefined(versionSoftInfo, lines);
	}

	// ----------------

	public static Predicate<SoftInfo> minVersion(Version minVersion) {
		return new Predicate<SoftInfo>() {

			@Override
			public boolean test(SoftInfo softInfo) {
				VersionSoftInfo versionSoftInfo = (VersionSoftInfo)softInfo;
				Optional<Version> versionOpt = versionSoftInfo.getVersion();
				if(versionOpt.isPresent()) {
					return minVersion.isLowerOrEqualsThan(versionOpt.get());
				}
				return false;
			}

			@Override
			public String toString() {
				return ">= v" + minVersion;
			}
		};
	}

	public static Predicate<SoftInfo> minVersion(int... values) {
		return minVersion(new Version(values));
	}

	public static Predicate<SoftInfo> maxVersion(Version maxVersion) {
		return new Predicate<SoftInfo>() {

			@Override
			public boolean test(SoftInfo softInfo) {
				VersionSoftInfo versionSoftInfo = (VersionSoftInfo)softInfo;
				Optional<Version> versionOpt = versionSoftInfo.getVersion();
				if(versionOpt.isPresent()) {
					return maxVersion.isUpperThan(versionOpt.get());
				}
				return false;
			}

			@Override
			public String toString() {
				return "< v" + maxVersion;
			}
		};
	}

	public static Predicate<SoftInfo> maxVersion(int... values) {
		return maxVersion(new Version(values));
	}

	public static Predicate<SoftInfo> allVersion() {
		return v -> true;
	}

	// ****************************************************

	private SoftFound byDefined(VersionSoftInfo versionSoftInfo, Lines lines) {
		Optional<Version> version = versionSoftInfo.getVersion();
		if(version.isPresent()) {
			for(Pair<OnPlatform, Predicate<SoftInfo>> pair : list) {
				Predicate<SoftInfo> require = pair.getKey().getRequire();
				if(require.test(versionSoftInfo)) {
					Predicate<SoftInfo> predicate = pair.getValue();
					if(predicate.test(versionSoftInfo)) {
						return SoftFound.found(versionSoftInfo);
					}
					return SoftFound.foundBadVersion(versionSoftInfo, predicate.toString());
				}
			}
			throw new RuntimeException("Not implemented for this OS: " + SystemUtils.OS_NAME);
		}
		String msg = lines.values().collect(Collectors.joining(System.lineSeparator()));
		if(StringUtils.isBlank(msg)) {
			msg = "version not parsable";
		}
		return SoftFound.foundError(versionSoftInfo, msg);
	}

	private SoftFound byProperties(VersionSoftInfo versionSoftInfo, Lines lines) {
		Optional<String> propertyMinVersion = getProperty(versionSoftInfo, "minversion");
		if(propertyMinVersion.isPresent()) {
			Version minVersion = Version.parse(propertyMinVersion.get());
			SoftPolicy onAllPlatforms = new VersionSoftPolicy()
					.onAllPlatforms(minVersion(minVersion));
			return ((VersionSoftPolicy)onAllPlatforms).byDefined(versionSoftInfo, lines);
		}
		return null;
	}

}
