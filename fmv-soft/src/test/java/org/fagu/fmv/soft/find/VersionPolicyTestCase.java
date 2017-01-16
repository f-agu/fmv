package org.fagu.fmv.soft.find;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.fagu.fmv.soft.TestSoftName;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.find.policy.VersionPolicy;
import org.fagu.version.Version;
import org.junit.Test;


/**
 * @author f.agu
 */
public class VersionPolicyTestCase {

	/**
	 * 
	 */
	public VersionPolicyTestCase() {}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testEmpty_versionNull() {
		new VersionPolicy().toSoftFound(versionSoftInfo(null));
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testEmpty_versionV1() {
		new VersionPolicy().toSoftFound(versionSoftInfo(Version.V1));
	}

	/**
	 * 
	 */
	@Test(expected = RuntimeException.class)
	public void testEmpty_properties() {
		System.setProperty("test.minversion", "2");
		try {
			new VersionPolicy().toSoftFound(versionSoftInfo(Version.V1));
		} finally {
			System.getProperties().remove("test.minversion");
		}
	}

	/**
	 * 
	 */
	@Test
	public void testAllOS_versionNull() {
		SoftFound softFound = new VersionPolicy()
				.onAllPlatforms().allVersion()
				.toSoftFound(versionSoftInfo(null));
		assertFalse(softFound.isFound());
		assertEquals(FoundReasons.BAD_SOFT, softFound.getFoundReason());
		assertEquals("version not parsable", softFound.getReason());
	}

	/**
	 * 
	 */
	@Test
	public void testAllOSAllVersion_versionV3() {
		SoftFound softFound = new VersionPolicy()
				.onAllPlatforms().allVersion()
				.toSoftFound(versionSoftInfo(Version.V3));
		assertTrue(softFound.isFound());
	}

	/**
	 * 
	 */
	@Test
	public void testAllOS_VersionV2_versionV3() {
		SoftFound softFound = new VersionPolicy()
				.onAllPlatforms().minVersion(Version.V2)
				.toSoftFound(versionSoftInfo(Version.V3));
		assertTrue(softFound.isFound());
	}

	/**
	 * 
	 */
	@Test
	public void testAllOS_VersionV2_versionV1() {
		SoftFound softFound = new VersionPolicy()
				.onAllPlatforms().minVersion(Version.V2)
				.toSoftFound(versionSoftInfo(Version.V1));
		assertFalse(softFound.isFound());
		assertEquals(FoundReasons.BAD_VERSION, softFound.getFoundReason());
		assertEquals(">= v2", softFound.getReason());
	}

	// ******************************************

	/**
	 * @param version
	 * @return
	 */
	private VersionSoftInfo versionSoftInfo(Version version) {
		return new VersionSoftInfo(new File("."), new TestSoftName("test"), version) {};
	}

}
