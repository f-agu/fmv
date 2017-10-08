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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Test;


/**
 * @author f.agu
 */
public class VersionUnitTestCase {

	/**
	 *
	 */
	public VersionUnitTestCase() {}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined0() throws Exception {
		VersionUnit unit = VersionUnit.VF_0_MAJOR;
		assertEquals("name", unit.getName(), "major");
		assertEquals("position", unit.getPosition(), 0);
		assertEquals("next", unit.next(), VersionUnit.VF_1_MINOR);
		try {
			assertEquals("previous", unit.previous(), null);
			fail("previous");
		} catch(IllegalArgumentException iae) {}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined1() throws Exception {
		VersionUnit unit = VersionUnit.VF_1_MINOR;
		assertEquals("name", unit.getName(), "minor");
		assertEquals("position", unit.getPosition(), 1);
		assertEquals("next", unit.next(), VersionUnit.VF_2_REVISION);
		assertEquals("previous", unit.previous(), VersionUnit.VF_0_MAJOR);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined2() throws Exception {
		VersionUnit unit = VersionUnit.VF_2_REVISION;
		assertEquals("name", unit.getName(), "revision");
		assertEquals("position", unit.getPosition(), 2);
		assertEquals("next", unit.next(), VersionUnit.VF_3_BUILD);
		assertEquals("previous", unit.previous(), VersionUnit.VF_1_MINOR);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined3() throws Exception {
		VersionUnit unit = VersionUnit.VF_3_BUILD;
		assertEquals("name", unit.getName(), "build");
		assertEquals("position", unit.getPosition(), 3);
		assertEquals("next", unit.next(), VersionUnit.VF_4);
		assertEquals("previous", unit.previous(), VersionUnit.VF_2_REVISION);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined4() throws Exception {
		VersionUnit unit = VersionUnit.VF_4;
		assertEquals("name", unit.getName(), "");
		assertEquals("position", unit.getPosition(), 4);
		assertEquals("next", unit.next(), VersionUnit.VF_5);
		assertEquals("previous", unit.previous(), VersionUnit.VF_3_BUILD);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined5() throws Exception {
		VersionUnit unit = VersionUnit.VF_5;
		assertEquals("name", unit.getName(), "");
		assertEquals("position", unit.getPosition(), 5);
		assertEquals("next", unit.next(), VersionUnit.VF_6);
		assertEquals("previous", unit.previous(), VersionUnit.VF_4);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined6() throws Exception {
		VersionUnit unit = VersionUnit.VF_6;
		assertEquals("name", unit.getName(), "");
		assertEquals("position", unit.getPosition(), 6);
		assertEquals("next", unit.next(), VersionUnit.VF_7);
		assertEquals("previous", unit.previous(), VersionUnit.VF_5);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined7() throws Exception {
		VersionUnit unit = VersionUnit.VF_7;
		assertEquals("name", unit.getName(), "");
		assertEquals("position", unit.getPosition(), 7);
		assertEquals("next", unit.next(), VersionUnit.VF_8);
		assertEquals("previous", unit.previous(), VersionUnit.VF_6);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined8() throws Exception {
		VersionUnit unit = VersionUnit.VF_8;
		assertEquals("name", unit.getName(), "");
		assertEquals("position", unit.getPosition(), 8);
		assertEquals("next", unit.next(), VersionUnit.VF_9);
		assertEquals("previous", unit.previous(), VersionUnit.VF_7);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined9() throws Exception {
		VersionUnit unit = VersionUnit.VF_9;
		assertEquals("name", unit.getName(), "");
		assertEquals("position", unit.getPosition(), 9);
		assertNull("next", unit.next());
		assertEquals("previous", unit.previous(), VersionUnit.VF_8);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testIterable() throws Exception {
		Iterator<VersionUnit> iterator = VersionUnit.iterable().iterator();

		assertSame("0", iterator.next(), VersionUnit.VF_0_MAJOR);
		assertSame("1", iterator.next(), VersionUnit.VF_1_MINOR);
		assertSame("2", iterator.next(), VersionUnit.VF_2_REVISION);
		assertSame("3", iterator.next(), VersionUnit.VF_3_BUILD);
		assertSame("4", iterator.next(), VersionUnit.VF_4);
		assertSame("5", iterator.next(), VersionUnit.VF_5);
		assertSame("6", iterator.next(), VersionUnit.VF_6);
		assertSame("7", iterator.next(), VersionUnit.VF_7);
		assertSame("8", iterator.next(), VersionUnit.VF_8);
		assertSame("9", iterator.next(), VersionUnit.VF_9);

		assertFalse("hasNext", iterator.hasNext());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testIterableBetween() throws Exception {
		Iterator<VersionUnit> iterator = VersionUnit.iterable(VersionUnit.VF_1_MINOR, VersionUnit.VF_4).iterator();

		assertSame("1", iterator.next(), VersionUnit.VF_1_MINOR);
		assertSame("2", iterator.next(), VersionUnit.VF_2_REVISION);
		assertSame("3", iterator.next(), VersionUnit.VF_3_BUILD);
		assertSame("4", iterator.next(), VersionUnit.VF_4);

		assertFalse("hasNext", iterator.hasNext());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testIterableBetweenLast() throws Exception {
		Iterator<VersionUnit> iterator = VersionUnit.iterable(VersionUnit.VF_7, VersionUnit.VF_9).iterator();

		assertSame("7", iterator.next(), VersionUnit.VF_7);
		assertSame("8", iterator.next(), VersionUnit.VF_8);
		assertSame("9", iterator.next(), VersionUnit.VF_9);

		assertFalse("hasNext", iterator.hasNext());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testMax() throws Exception {
		assertSame("0 ? 1", VersionUnit.max(VersionUnit.VF_0_MAJOR, VersionUnit.VF_1_MINOR), VersionUnit.VF_0_MAJOR);
		assertSame("1 ? 0", VersionUnit.max(VersionUnit.VF_1_MINOR, VersionUnit.VF_0_MAJOR), VersionUnit.VF_0_MAJOR);

		assertSame("1 ? 2", VersionUnit.max(VersionUnit.VF_1_MINOR, VersionUnit.VF_2_REVISION), VersionUnit.VF_1_MINOR);
		assertSame("2 ? 1", VersionUnit.max(VersionUnit.VF_2_REVISION, VersionUnit.VF_1_MINOR), VersionUnit.VF_1_MINOR);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testMin() throws Exception {
		assertSame("0 ? 1", VersionUnit.min(VersionUnit.VF_0_MAJOR, VersionUnit.VF_1_MINOR), VersionUnit.VF_1_MINOR);
		assertSame("1 ? 0", VersionUnit.min(VersionUnit.VF_1_MINOR, VersionUnit.VF_0_MAJOR), VersionUnit.VF_1_MINOR);

		assertSame("1 ? 2", VersionUnit.min(VersionUnit.VF_1_MINOR, VersionUnit.VF_2_REVISION), VersionUnit.VF_2_REVISION);
		assertSame("2 ? 1", VersionUnit.min(VersionUnit.VF_2_REVISION, VersionUnit.VF_1_MINOR), VersionUnit.VF_2_REVISION);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testParseInt() throws Exception {
		assertSame("0", VersionUnit.parse(0), VersionUnit.VF_0_MAJOR);
		assertSame("1", VersionUnit.parse(1), VersionUnit.VF_1_MINOR);
		assertSame("2", VersionUnit.parse(2), VersionUnit.VF_2_REVISION);
		assertSame("3", VersionUnit.parse(3), VersionUnit.VF_3_BUILD);
		assertSame("4", VersionUnit.parse(4), VersionUnit.VF_4);
		assertSame("5", VersionUnit.parse(5), VersionUnit.VF_5);
		assertSame("6", VersionUnit.parse(6), VersionUnit.VF_6);
		assertSame("7", VersionUnit.parse(7), VersionUnit.VF_7);
		assertSame("8", VersionUnit.parse(8), VersionUnit.VF_8);
		assertSame("9", VersionUnit.parse(9), VersionUnit.VF_9);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testParseName() throws Exception {
		assertSame("0", VersionUnit.parse("major"), VersionUnit.VF_0_MAJOR);
		assertSame("1", VersionUnit.parse("minor"), VersionUnit.VF_1_MINOR);
		assertSame("2", VersionUnit.parse("revision"), VersionUnit.VF_2_REVISION);
		assertSame("3", VersionUnit.parse("build"), VersionUnit.VF_3_BUILD);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testParseNameNull() throws Exception {
		assertNull("null", VersionUnit.parse(null));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testParseNameEmpty() throws Exception {
		assertNull("empty", VersionUnit.parse(""));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testParseNameUnknown() throws Exception {
		assertNull("unknown", VersionUnit.parse("rsdf"));
	}

}
