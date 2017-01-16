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

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.find.policy.VersionPolicy.VersionOnPlatform;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class VersionPolicy extends SoftPolicy<VersionSoftInfo, VersionOnPlatform, VersionPolicy> {

	// -------------------------------------------------

	/**
	 * @author f.agu
	 */
	@SuppressWarnings("rawtypes")
	public class VersionOnPlatform extends org.fagu.fmv.soft.find.SoftPolicy.OnPlatform {

		@SuppressWarnings("unchecked")
		public VersionOnPlatform(String name, Predicate<VersionSoftInfo> condition) {
			super(name, condition);
		}

		public VersionPolicy validate(Predicate<VersionSoftInfo> versionValidate) {
			return with(this, versionValidate);
		}

		public VersionPolicy minVersion(Version minVersion) {
			return validate(new Predicate<VersionSoftInfo>() {

				@Override
				public boolean test(VersionSoftInfo versionSoftInfo) {
					return minVersion.isLowerOrEqualsThan(versionSoftInfo.getVersion().get());
				}

				@Override
				public String toString() {
					return ">= v" + minVersion;
				}
			});
		}

		public VersionPolicy allVersion() {
			return validate(v -> true);
		}

	}

	// -------------------------------------------------

	/**
	 * 
	 */
	public VersionPolicy() {
		super();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftPolicy#on(java.lang.String, java.util.function.Predicate)
	 */
	@Override
	public VersionOnPlatform on(String name, Predicate<VersionSoftInfo> condition) {
		return new VersionOnPlatform(name, condition);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftPolicy#toSoftFound(org.fagu.fmv.soft.find.SoftInfo)
	 */
	public SoftFound toSoftFound(Object object) {
		VersionSoftInfo versionSoftInfo = (VersionSoftInfo)object;
		if(list.isEmpty()) {
			throw new IllegalStateException("No validator defined !");
		}
		// system properties
		SoftFound byPropertiesFound = byProperties(versionSoftInfo);
		if(byPropertiesFound != null && byPropertiesFound.isFound()) {
			return byPropertiesFound;
		}

		// defined
		return byDefined(versionSoftInfo);
	}

	// ****************************************************

	/**
	 * @param versionSoftInfo
	 * @return
	 */
	private SoftFound byDefined(VersionSoftInfo versionSoftInfo) {
		Optional<Version> version = versionSoftInfo.getVersion();
		if(version.isPresent()) {
			for(Pair<VersionOnPlatform, Predicate<VersionSoftInfo>> pair : list) {
				@SuppressWarnings("unchecked")
				Predicate<VersionSoftInfo> condition = pair.getKey().getCondition();
				if(condition.test(versionSoftInfo)) {
					Predicate<VersionSoftInfo> predicate = pair.getValue();
					if(predicate.test(versionSoftInfo)) {
						return SoftFound.found(versionSoftInfo);
					}
					return SoftFound.foundBadVersion(versionSoftInfo, predicate.toString());
				}
			}
			throw new RuntimeException("Not implemented for this OS: " + SystemUtils.OS_NAME);
		}
		return SoftFound.foundBadSoft(versionSoftInfo, "version not parsable");
	}

	/**
	 * @param versionSoftInfo
	 * @return
	 */
	private SoftFound byProperties(VersionSoftInfo versionSoftInfo) {
		Optional<String> propertyMinVersion = getProperty(versionSoftInfo, "minversion");
		if(propertyMinVersion.isPresent()) {
			Version minVersion = Version.parse(propertyMinVersion.get());

			return new VersionPolicy().onAllPlatforms().minVersion(minVersion).byDefined(versionSoftInfo);
		}
		return null;
	}

}
