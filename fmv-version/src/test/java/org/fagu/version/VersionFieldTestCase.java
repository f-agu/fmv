package org.fagu.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/*-
 * #%L
 * fmv-version
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


/**
 * @author fagu
 */
class VersionFieldTestCase {

	@Test
	void testConstructorNull() throws Exception {
		try {
			new VersionField(null, 0);
			fail("versionUnit null");
		} catch(NullPointerException npe) {}
	}

	@Test
	void testConstructorNegative() throws Exception {
		try {
			new VersionField(VersionUnit.VF_0_MAJOR, - 1);
			fail("value -1");
		} catch(IllegalArgumentException npe) {}

		try {
			new VersionField(VersionUnit.VF_9, Integer.MIN_VALUE);
			fail("value Integer.MIN_VALUE");
		} catch(IllegalArgumentException npe) {}
	}

	@Test
	void testSimple() throws Exception {
		assertOK("0", new VersionField(VersionUnit.VF_0_MAJOR, 1), VersionUnit.VF_0_MAJOR, 1);
		assertOK("1", new VersionField(VersionUnit.VF_1_MINOR, 54), VersionUnit.VF_1_MINOR, 54);
		assertOK("2", new VersionField(VersionUnit.VF_2_REVISION, 10), VersionUnit.VF_2_REVISION, 10);
		assertOK("3", new VersionField(VersionUnit.VF_3_BUILD, 777), VersionUnit.VF_3_BUILD, 777);
		assertOK("4", new VersionField(VersionUnit.VF_4, 666), VersionUnit.VF_4, 666);
		assertOK("5", new VersionField(VersionUnit.VF_5, 888), VersionUnit.VF_5, 888);
	}

	@Test
	void testValueOfEquals() throws Exception {
		assertEquals(new VersionField(VersionUnit.VF_0_MAJOR, 1), VersionField.valueOf(0, 1));
		assertEquals(new VersionField(VersionUnit.VF_1_MINOR, 54), VersionField.valueOf(1, 54));
		assertEquals(new VersionField(VersionUnit.VF_2_REVISION, 10), VersionField.valueOf(2, 10));
		assertEquals(new VersionField(VersionUnit.VF_3_BUILD, 777), VersionField.valueOf(3, 777));
		assertEquals(new VersionField(VersionUnit.VF_4, 666), VersionField.valueOf(4, 666));
		assertEquals(new VersionField(VersionUnit.VF_5, 888), VersionField.valueOf(5, 888));
	}

	@Test
	void testValueOfNagetivePosition() throws Exception {
		try {
			VersionField.valueOf( - 1, 1);
		} catch(IllegalArgumentException iae) {}
	}

	@Test
	void testValueOfNagetiveValue() throws Exception {
		try {
			VersionField.valueOf(1, - 1);
		} catch(IllegalArgumentException iae) {}
	}

	// **************************************************************************

	private void assertOK(String message, VersionField versionField, VersionUnit versionUnit, int value) {
		assertEquals(versionField.getValue(), value, message);
		assertSame(versionField.getVersionUnit(), versionUnit, message);
		assertEquals(versionField.toString(), versionUnit.toString() + "=" + value, message);
	}

}
