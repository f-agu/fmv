package org.fagu.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;

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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.fagu.version.Version.VersionBuilder;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class VersionTestCase {

	@Test
	void testParseEmpty() throws Exception {
		try {
			Version.parse("");
			fail("pas bien");
		} catch(VersionParseException parseException) {}
	}

	@Test
	void testParseNull() throws Exception {
		try {
			Version.parse(null);
			fail("pas bien");
		} catch(VersionParseException parseException) {}
	}

	@Test
	void testIncrease() throws Exception {
		assertOK("1", new Version(1), 1);
		assertOK("1.2", new Version(1, 2), 1, 2);
		assertOK("1.2.3", new Version(1, 2, 3), 1, 2, 3);
		assertOK("1.2.3.4", new Version(1, 2, 3, 4), 1, 2, 3, 4);
		assertOK("1.2.3.4.5", new Version(1, 2, 3, 4, 5), 1, 2, 3, 4, 5);
		assertOK("1.2.3.4.5.6", new Version(1, 2, 3, 4, 5, 6), 1, 2, 3, 4, 5, 6);
		assertOK("1.2.3.4.5.6.7", new Version(1, 2, 3, 4, 5, 6, 7), 1, 2, 3, 4, 5, 6, 7);
		assertOK("1.2.3.4.5.6.7.8", new Version(1, 2, 3, 4, 5, 6, 7, 8), 1, 2, 3, 4, 5, 6, 7, 8);
		assertOK("1.2.3.4.5.6.7.8.9", new Version(1, 2, 3, 4, 5, 6, 7, 8, 9), 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	void testVersionField() throws Exception {
		assertOK("1", new Version(VersionField.valueOf(0, 1)), 1);
		assertOK("1.1", new Version(VersionField.valueOf(0, 1), VersionField.valueOf(1, 1)), 1, 1);
		assertOK("1.0.0.0.1", new Version(VersionField.valueOf(4, 1), VersionField.valueOf(0, 1)), 1, 0, 0, 0, 1);
		assertOK("88.0.5.0.99", new Version(VersionField.valueOf(2, 5), VersionField.valueOf(4, 99), VersionField.valueOf(0, 88)), 88, 0, 5, 0, 99);
	}

	@Test
	void testSort() throws Exception {
		Version v1 = new Version(1);
		Version v2 = new Version(2);
		Version v3 = new Version(3);
		Version v11 = new Version(1, 1);
		Version v111 = new Version(1, 1, 1);
		Version v12 = new Version(1, 2);
		Version v128 = new Version(1, 2, 8);
		Version v1111 = new Version(1, 1, 1, 1);
		Version v1121 = new Version(1, 1, 2, 1);
		Version v09 = new Version(0, 9);
		Version v085 = new Version(0, 8, 5);
		Version v00000001 = new Version(0, 0, 0, 0, 0, 0, 0, 1);
		Version v0 = new Version(0);

		TreeSet<Version> set = new TreeSet<Version>();
		set.add(v1);
		set.add(v2);
		set.add(v3);
		set.add(v11);
		set.add(v12);
		set.add(v128);
		set.add(v1111);
		set.add(v111);
		set.add(v1121);
		set.add(v09);
		set.add(v085);
		set.add(v00000001);
		set.add(v0);

		Iterator<Version> iterator = set.iterator();

		assertSame(v0, iterator.next());
		assertSame(v00000001, iterator.next());
		assertSame(v085, iterator.next());
		assertSame(v09, iterator.next());
		assertSame(v1, iterator.next());
		assertSame(v11, iterator.next());
		assertSame(v111, iterator.next());
		assertSame(v1111, iterator.next());
		assertSame(v1121, iterator.next());
		assertSame(v12, iterator.next());
		assertSame(v128, iterator.next());
		assertSame(v2, iterator.next());
		assertSame(v3, iterator.next());

		assertFalse(iterator.hasNext());
	}

	@Test
	void testEquals() throws Exception {
		assertEquals(Version.parse("1.9.456"), new Version(1, 9, 456));

		assertEquals(new Version(1), new Version(VersionField.valueOf(0, 1)));
		assertEquals(new Version(7, 6), new Version(VersionField.valueOf(0, 7), VersionField.valueOf(1, 6)));
		assertEquals(new Version(7, 6), new Version(VersionField.valueOf(1, 6), VersionField.valueOf(0, 7)));
	}

	@Test
	void testEquals2() throws Exception {
		assertEquals(new Version(2), new Version(2, 0));
		assertEquals(new Version(2), new Version(2, 0, 0));
		assertEquals(new Version(2), new Version(2, 0, 0, 0));
		assertEquals(new Version(2), new Version(2, 0, 0, 0, 0, 0, 0, 0));
	}

	@Test
	void testHashCode() {
		assertEquals(Version.V1.hashCode(), Version.parse("1").hashCode());
		assertEquals(Version.V1.hashCode(), new Version(1).hashCode());
		assertEquals(Version.V1.hashCode(), VersionBuilder.v(1).build().hashCode());
		assertEquals(Version.V1.hashCode(), VersionBuilder.major(1).build().hashCode());
	}

	@Test
	void testContains() throws Exception {
		// 88.0.5.0.99
		Version version = new Version(VersionField.valueOf(2, 5), VersionField.valueOf(4, 99), VersionField.valueOf(0, 88));
		assertTrue(version.contains(VersionUnit.VF_0_MAJOR));
		assertTrue(version.contains(VersionUnit.VF_1_MINOR));
		assertTrue(version.contains(VersionUnit.VF_2_REVISION));
		assertTrue(version.contains(VersionUnit.VF_3_BUILD));
		assertTrue(version.contains(VersionUnit.VF_4));
		assertFalse(version.contains(VersionUnit.VF_5));
		assertFalse(version.contains(VersionUnit.VF_6));
		assertFalse(version.contains(VersionUnit.VF_7));
		assertFalse(version.contains(VersionUnit.VF_8));
		assertFalse(version.contains(VersionUnit.VF_9));
	}

	@Test
	void testSetContains1() throws Exception {
		Set<Version> set = new HashSet<>();
		set.add(new Version(2));
		assertTrue(set.contains(new Version(2)));
		assertTrue(set.contains(new Version(2, 0)));
		assertTrue(set.contains(new Version(2, 0, 0, 0)));
	}

	@Test
	void testSetContains2() throws Exception {
		Set<Version> set = new HashSet<>();
		set.add(new Version(2, 0, 0, 0));
		assertTrue(set.contains(new Version(2)));
		assertTrue(set.contains(new Version(2, 0)));
		assertTrue(set.contains(new Version(2, 0, 0, 0)));
	}

	@Test
	void testIsUpperThan() throws Exception {
		assertTrue(new Version(1).isUpperThan(new Version(0, 9)));
		assertTrue(new Version(1, 0, 0, 0, 0, 0, 1).isUpperThan(new Version(1)));
		assertTrue(new Version(1, 6).isUpperThan(new Version(1, 5)));

		assertFalse(new Version(9, 4).isUpperThan(new Version(9, 4)));
		assertFalse(new Version(9, 4).isUpperThan(new Version(9, 4, 1)));
	}

	@Test
	void testIsUpperOrEqualsThan() throws Exception {
		assertTrue(new Version(1).isUpperOrEqualsThan(new Version(0, 9)));
		assertTrue(new Version(1, 0, 0, 0, 0, 0, 1).isUpperOrEqualsThan(new Version(1)));
		assertTrue(new Version(1, 6).isUpperOrEqualsThan(new Version(1, 5)));
		assertTrue(new Version(9, 4).isUpperOrEqualsThan(new Version(9, 4)));

		assertFalse(new Version(9, 4).isUpperOrEqualsThan(new Version(9, 4, 1)));
	}

	@Test
	void testIsLowerThan() throws Exception {
		assertTrue(new Version(0, 9).isLowerThan(new Version(1)));
		assertTrue(new Version(1).isLowerThan(new Version(1, 0, 0, 0, 0, 0, 1)));
		assertTrue(new Version(1, 5).isLowerThan(new Version(1, 6)));

		assertFalse(new Version(9, 4).isLowerThan(new Version(9, 4)));
		assertFalse(new Version(9, 4, 1).isLowerThan(new Version(9, 4)));
	}

	@Test
	void testIsLowerOrEqualsThan() throws Exception {
		assertTrue(new Version(0, 9).isLowerOrEqualsThan(new Version(1)));
		assertTrue(new Version(1).isLowerOrEqualsThan(new Version(1, 0, 0, 0, 0, 0, 1)));
		assertTrue(new Version(1, 5).isLowerOrEqualsThan(new Version(1, 6)));
		assertTrue(new Version(9, 4).isLowerOrEqualsThan(new Version(9, 4)));

		assertFalse(new Version(9, 4, 1).isLowerOrEqualsThan(new Version(9, 4)));
	}

	@Test
	void testParse() throws Exception {
		assertOK("1", Version.parse("1"), 1);
		assertOK("1.1", Version.parse("1.1"), 1, 1);
		assertOK("1.0.0.0.1", Version.parse("1.0.0.0.1"), 1, 0, 0, 0, 1);
		assertOK("88.0.5.0.99", Version.parse("88.0.5.0.99"), 88, 0, 5, 0, 99);
		assertOK("5.1.16.", Version.parse("5.1.16."), 5, 1, 16);
	}

	@Test
	void testJavaVersion() throws Exception {
		final Version MINIMAL_VERSION = new Version(1, 5);
		String runtimeVersion = System.getProperty("java.runtime.version");
		Version version = Version.parse(runtimeVersion);
		assertTrue(version.isUpperOrEqualsThan(MINIMAL_VERSION));
	}

	@Test
	void testVersionSizeDifferent() throws Exception {
		Version v48 = new Version(4, 8);
		Version v480 = new Version(4, 8, 0);
		Version v480000000 = new Version(4, 8, 0, 0, 0, 0, 0, 0, 0);
		Version v481 = new Version(4, 8, 1);
		assertEquals(v48, v480);
		assertEquals(v480, v48);
		assertEquals(v48, v480000000);
		assertEquals(v480000000, v48);
		assertNotEquals(v48, v481);
		assertNotEquals(v481, v48);
	}

	@Test
	void testCut() throws Exception {
		assertOK("1 - 0", new Version(1).cut(VersionUnit.VF_0_MAJOR), 1);
		assertOK("1 - 1", new Version(1).cut(VersionUnit.VF_1_MINOR), 1);
		assertOK("1 - 4", new Version(1).cut(VersionUnit.VF_4), 1);
		assertOK("1 - 9", new Version(1).cut(VersionUnit.VF_9), 1);
		assertOK("1.1 - 0", new Version(1, 1).cut(VersionUnit.VF_0_MAJOR), 1);
		assertOK("1.1 - 1", new Version(1, 1).cut(VersionUnit.VF_1_MINOR), 1, 1);
		assertOK("1.1 - 2", new Version(1, 1).cut(VersionUnit.VF_2_REVISION), 1, 1);
		assertOK("1.1 - 9", new Version(1, 1).cut(VersionUnit.VF_9), 1, 1);
		assertOK("9.8.7.6.5.4 - 0", new Version(9, 8, 7, 6, 5, 4).cut(VersionUnit.VF_0_MAJOR), 9);
		assertOK("9.8.7.6.5.4 - 4", new Version(9, 8, 7, 6, 5, 4).cut(VersionUnit.VF_4), 9, 8, 7, 6, 5);
		assertOK("9.8.7.6.5.4 - 5", new Version(9, 8, 7, 6, 5, 4).cut(VersionUnit.VF_5), 9, 8, 7, 6, 5, 4);
		assertOK("9.8.7.6.5.4 - 6", new Version(9, 8, 7, 6, 5, 4).cut(VersionUnit.VF_6), 9, 8, 7, 6, 5, 4);
		assertOK("9.8.7.6.5.4 - 9", new Version(9, 8, 7, 6, 5, 4).cut(VersionUnit.VF_9), 9, 8, 7, 6, 5, 4);

		assertOK("5.1.2", new Version(5, 1, 2).cut(VersionUnit.VF_2_REVISION), 5, 1, 2);
		assertOK("5.1.2", new Version(5, 1, 2).cut(VersionUnit.VF_3_BUILD), 5, 1, 2);
		assertOK("5.1.2.3", new Version(5, 1, 2, 3).cut(VersionUnit.VF_2_REVISION), 5, 1, 2);
		assertOK("5.1.2.3", new Version(5, 1, 2, 3).cut(VersionUnit.VF_3_BUILD), 5, 1, 2, 3);
	}

	@Test
	void testCompareRLC() throws Exception {

		Version v100 = Version.parse("1.0.0");
		Version v100snapshot = Version.parse("1.0.0-SNAPSHOT");
		Version v110snapshot = Version.parse("1.1.0-SNAPSHOT");
		Version v100xxx = Version.parse("1.0.0-XXX");

		// Set<Version> set = new TreeSet<>();
		// set.add(Version.parse("1.0.0"));
		// set.add(Version.parse("1.0.0-XXX"));
		// set.add(v100snapshot);
		// set.add(v110snapshot);
		// System.out.println(set);

		assertEquals(0, v100snapshot.compareTo(v100snapshot));
		assertTrue(v110snapshot.compareTo(v100snapshot) > 0);
		assertTrue(v100snapshot.compareTo(v110snapshot) < 0);

		assertTrue(v100snapshot.compareTo(v100xxx) < 0);
		assertTrue(v110snapshot.compareTo(v100xxx) > 0);
		assertTrue(v100.compareTo(v100xxx) < 0);
		assertTrue(v100.compareTo(v100snapshot) > 0);

	}

	@Test
	void testRemoveExtraValue() throws Exception {
		Version v100snapshot = Version.parse("1.0.0-SNAPSHOT");
		assertEquals(new Version(1), v100snapshot.removeText());
		assertEquals(new Version(1, 0), v100snapshot.removeText());
		assertEquals(new Version(1, 0, 0), v100snapshot.removeText());
	}

	// *****************************************************

	private void assertOK(String message, Version version, int... values) {
		final int NOT_SET = Integer.MIN_VALUE;
		final int len = values.length;
		assertEquals(version.size(), len);

		for(int i = 0; i < len; ++i) {
			assertEquals(version.getFieldValue(VersionUnit.parse(i), NOT_SET), values[i], "position n°" + i + ".value");
		}

		for(int i = 0; i < len; ++i) {
			VersionUnit unit = VersionUnit.parse(i);
			assertEquals(version.getField(unit), new VersionField(unit, values[i]), "position n°" + i + ".field");
		}
	}

}
