package org.fagu.fmv.soft.find.policy;

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.allVersion;
import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import java.io.File;

import org.fagu.fmv.soft.find.FoundReasons;
import org.fagu.fmv.soft.find.Lines;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.version.Version;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class VersionPolicyTestCase {

	@Test
	void testEmpty_versionNull() {
		assertThrows(IllegalStateException.class, () -> new VersionSoftPolicy().toSoftFound(versionSoftInfo(null), new Lines()));
	}

	@Test
	void testEmpty_versionV1() {
		assertThrows(IllegalStateException.class, () -> new VersionSoftPolicy().toSoftFound(versionSoftInfo(Version.V1), new Lines()));
	}

	@Test
	void testEmpty_properties() {
		System.setProperty("test.minversion", "2");
		try {
			assertThrows(RuntimeException.class, () -> new VersionSoftPolicy().toSoftFound(versionSoftInfo(Version.V1), new Lines()));
		} finally {
			System.getProperties().remove("test.minversion");
		}
	}

	@Test
	void testAllOS_versionNull() {
		SoftFound softFound = new VersionSoftPolicy()
				.onAllPlatforms(allVersion())
				.toSoftFound(versionSoftInfo(null), new Lines());
		assertFalse(softFound.isFound());
		assertEquals(FoundReasons.BAD_SOFT, softFound.getFoundReason());
		assertEquals("version not parsable", softFound.getReason());
	}

	@Test
	void testAllOSAllVersion_versionV3() {
		SoftFound softFound = new VersionSoftPolicy()
				.onAllPlatforms(allVersion())
				.toSoftFound(versionSoftInfo(Version.V3), new Lines());
		assertTrue(softFound.isFound());
	}

	@Test
	void testAllOS_VersionV2_versionV3() {
		SoftFound softFound = new VersionSoftPolicy()
				.onAllPlatforms(minVersion(Version.V2))
				.toSoftFound(versionSoftInfo(Version.V3), new Lines());
		assertTrue(softFound.isFound());
	}

	@Test
	void testAllOS_VersionV2_versionV1() {
		SoftFound softFound = new VersionSoftPolicy()
				.onAllPlatforms(minVersion(Version.V2))
				.toSoftFound(versionSoftInfo(Version.V1), new Lines());
		assertFalse(softFound.isFound());
		assertEquals(FoundReasons.BAD_VERSION, softFound.getFoundReason());
		assertEquals(">= v2", softFound.getReason());
	}

	// ******************************************

	private VersionSoftInfo versionSoftInfo(Version version) {
		return new VersionSoftInfo(new File("."), "test", version) {};
	}

}
