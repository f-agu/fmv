package org.fagu.version;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.TreeSet;

import org.junit.Test;


/**
 * @author f.agu
 */
public class VersionTestCase {

	/**
	 *
	 */
	public VersionTestCase() {}

	/**
	 * @throws Exception
	 */
	@Test
	public void testParseEmpty() throws Exception {
		try {
			Version.parse("");
			fail("pas bien");
		} catch(VersionParseException parseException) {}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testParseNull() throws Exception {
		try {
			Version.parse(null);
			fail("pas bien");
		} catch(VersionParseException parseException) {}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testIncrease() throws Exception {
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

	/**
	 * @throws Exception
	 */
	@Test
	public void testVersionField() throws Exception {
		assertOK("1", new Version(VersionField.valueOf(0, 1)), 1);
		assertOK("1.1", new Version(VersionField.valueOf(0, 1), VersionField.valueOf(1, 1)), 1, 1);
		assertOK("1.0.0.0.1", new Version(VersionField.valueOf(4, 1), VersionField.valueOf(0, 1)), 1, 0, 0, 0, 1);
		assertOK("88.0.5.0.99", new Version(VersionField.valueOf(2, 5), VersionField.valueOf(4, 99), VersionField.valueOf(0, 88)), 88, 0, 5, 0, 99);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testSort() throws Exception {
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

		assertSame("0", v0, iterator.next());
		assertSame("0.0.0.0.0.0.0.1", v00000001, iterator.next());
		assertSame("0.8.5", v085, iterator.next());
		assertSame("0.9", v09, iterator.next());
		assertSame("1", v1, iterator.next());
		assertSame("1.1", v11, iterator.next());
		assertSame("1.1.1", v111, iterator.next());
		assertSame("1.1.1.1", v1111, iterator.next());
		assertSame("1.1.2.1", v1121, iterator.next());
		assertSame("1.2", v12, iterator.next());
		assertSame("1.2.8", v128, iterator.next());
		assertSame("2", v2, iterator.next());
		assertSame("3", v3, iterator.next());

		assertFalse("hasNext", iterator.hasNext());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEquals() throws Exception {
		assertEquals(Version.parse("1.9.456"), new Version(1, 9, 456));

		assertEquals("1", new Version(1), new Version(VersionField.valueOf(0, 1)));
		assertEquals("7.6", new Version(7, 6), new Version(VersionField.valueOf(0, 7), VersionField.valueOf(1, 6)));
		assertEquals("7.6 (reverse)", new Version(7, 6), new Version(VersionField.valueOf(1, 6), VersionField.valueOf(0, 7)));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testContains() throws Exception {
		// 88.0.5.0.99
		Version version = new Version(VersionField.valueOf(2, 5), VersionField.valueOf(4, 99), VersionField.valueOf(0, 88));
		assertTrue("0", version.contains(VersionUnit.VF_0_MAJOR));
		assertTrue("1", version.contains(VersionUnit.VF_1_MINOR));
		assertTrue("2", version.contains(VersionUnit.VF_2_REVISION));
		assertTrue("3", version.contains(VersionUnit.VF_3_BUILD));
		assertTrue("4", version.contains(VersionUnit.VF_4));
		assertFalse("5", version.contains(VersionUnit.VF_5));
		assertFalse("6", version.contains(VersionUnit.VF_6));
		assertFalse("7", version.contains(VersionUnit.VF_7));
		assertFalse("8", version.contains(VersionUnit.VF_8));
		assertFalse("9", version.contains(VersionUnit.VF_9));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testIsUpperThan() throws Exception {
		assertTrue("1 > 0.9", new Version(1).isUpperThan(new Version(0, 9)));
		assertTrue("1.0.0.0.0.0.1 > 1", new Version(1, 0, 0, 0, 0, 0, 1).isUpperThan(new Version(1)));
		assertTrue("1.6 > 1.5", new Version(1, 6).isUpperThan(new Version(1, 5)));

		assertFalse("9.4 > 9.4", new Version(9, 4).isUpperThan(new Version(9, 4)));
		assertFalse("9.4 > 9.4.1", new Version(9, 4).isUpperThan(new Version(9, 4, 1)));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testIsUpperOrEqualsThan() throws Exception {
		assertTrue("1 >= 0.9", new Version(1).isUpperOrEqualsThan(new Version(0, 9)));
		assertTrue("1.0.0.0.0.0.1 >= 1", new Version(1, 0, 0, 0, 0, 0, 1).isUpperOrEqualsThan(new Version(1)));
		assertTrue("1.6 >= 1.5", new Version(1, 6).isUpperOrEqualsThan(new Version(1, 5)));
		assertTrue("9.4 >= 9.4", new Version(9, 4).isUpperOrEqualsThan(new Version(9, 4)));

		assertFalse("9.4 >= 9.4.1", new Version(9, 4).isUpperOrEqualsThan(new Version(9, 4, 1)));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testIsLowerThan() throws Exception {
		assertTrue("0.9 < 1", new Version(0, 9).isLowerThan(new Version(1)));
		assertTrue("1 < 1.0.0.0.0.0.1", new Version(1).isLowerThan(new Version(1, 0, 0, 0, 0, 0, 1)));
		assertTrue("1.5 < 1.6", new Version(1, 5).isLowerThan(new Version(1, 6)));

		assertFalse("9.4 < 9.4", new Version(9, 4).isLowerThan(new Version(9, 4)));
		assertFalse("9.4.1 < 9.4", new Version(9, 4, 1).isLowerThan(new Version(9, 4)));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testIsLowerOrEqualsThan() throws Exception {
		assertTrue("0.9 <= 1", new Version(0, 9).isLowerOrEqualsThan(new Version(1)));
		assertTrue("1 <= 1.0.0.0.0.0.1", new Version(1).isLowerOrEqualsThan(new Version(1, 0, 0, 0, 0, 0, 1)));
		assertTrue("1.5 <= 1.6", new Version(1, 5).isLowerOrEqualsThan(new Version(1, 6)));
		assertTrue("9.4 <= 9.4", new Version(9, 4).isLowerOrEqualsThan(new Version(9, 4)));

		assertFalse("9.4.1 <= 9.4", new Version(9, 4, 1).isLowerOrEqualsThan(new Version(9, 4)));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testParse() throws Exception {
		assertOK("1", Version.parse("1"), 1);
		assertOK("1.1", Version.parse("1.1"), 1, 1);
		assertOK("1.0.0.0.1", Version.parse("1.0.0.0.1"), 1, 0, 0, 0, 1);
		assertOK("88.0.5.0.99", Version.parse("88.0.5.0.99"), 88, 0, 5, 0, 99);
		assertOK("5.1.16.", Version.parse("5.1.16."), 5, 1, 16);
		assertOK("1.8.0_20", Version.parse("1.8.0_20"), 1, 8, 0, 20);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testJavaVersion() throws Exception {
		final Version MINIMAL_VERSION = new Version(1, 5);
		String runtimeVersion = System.getProperty("java.runtime.version");
		Version version = Version.parse(runtimeVersion);
		assertTrue(version.isUpperOrEqualsThan(MINIMAL_VERSION));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testVersionSizeDifferent() throws Exception {
		Version v48 = new Version(4, 8);
		Version v480 = new Version(4, 8, 0);
		Version v480000000 = new Version(4, 8, 0, 0, 0, 0, 0, 0, 0);
		Version v481 = new Version(4, 8, 1);
		assertTrue("4.8 == 4.8.0", v48.equals(v480));
		assertTrue("4.8.0 == 4.8", v480.equals(v48));
		assertTrue("4.8 == 4.8.0.0.0.0.0.0.0", v48.equals(v480000000));
		assertTrue("4.8.0.0.0.0.0.0.0 == 4.8", v480000000.equals(v48));
		assertFalse("4.8 != 4.8.1", v48.equals(v481));
		assertFalse("4.8.1 != 4.8", v481.equals(v48));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testCut() throws Exception {
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

	// *****************************************************
	/**
	 * @param message
	 * @param str
	 * @param values
	 */
	private void assertOK(String message, Version version, int... values) {
		final int NOT_SET = Integer.MIN_VALUE;
		final int len = values.length;
		assertEquals("size", version.size(), len);

		for(int i = 0; i < len; ++i) {
			assertEquals("position n°" + i + ".value", version.getFieldValue(VersionUnit.parse(i), NOT_SET), values[i]);
		}

		for(int i = 0; i < len; ++i) {
			VersionUnit unit = VersionUnit.parse(i);
			assertEquals("position n°" + i + ".field", version.getField(unit), new VersionField(unit, values[i]));
		}
	}

}
