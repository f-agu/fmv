package org.fagu.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

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

import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class VersionUnitTestCase {

	@Test
	void testDefined0() throws Exception {
		VersionUnit unit = VersionUnit.VF_0_MAJOR;
		assertEquals(unit.getName(), "major");
		assertEquals(unit.getPosition(), 0);
		assertEquals(unit.next(), VersionUnit.VF_1_MINOR);
		try {
			assertEquals(unit.previous(), null);
			fail("previous");
		} catch(IllegalArgumentException iae) {}
	}

	@Test
	void testDefined1() throws Exception {
		VersionUnit unit = VersionUnit.VF_1_MINOR;
		assertEquals(unit.getName(), "minor");
		assertEquals(unit.getPosition(), 1);
		assertEquals(unit.next(), VersionUnit.VF_2_REVISION);
		assertEquals(unit.previous(), VersionUnit.VF_0_MAJOR);
	}

	@Test
	void testDefined2() throws Exception {
		VersionUnit unit = VersionUnit.VF_2_REVISION;
		assertEquals(unit.getName(), "revision");
		assertEquals(unit.getPosition(), 2);
		assertEquals(unit.next(), VersionUnit.VF_3_BUILD);
		assertEquals(unit.previous(), VersionUnit.VF_1_MINOR);
	}

	@Test
	void testDefined3() throws Exception {
		VersionUnit unit = VersionUnit.VF_3_BUILD;
		assertEquals(unit.getName(), "build");
		assertEquals(unit.getPosition(), 3);
		assertEquals(unit.next(), VersionUnit.VF_4);
		assertEquals(unit.previous(), VersionUnit.VF_2_REVISION);
	}

	@Test
	void testDefined4() throws Exception {
		VersionUnit unit = VersionUnit.VF_4;
		assertEquals(unit.getName(), "");
		assertEquals(unit.getPosition(), 4);
		assertEquals(unit.next(), VersionUnit.VF_5);
		assertEquals(unit.previous(), VersionUnit.VF_3_BUILD);
	}

	@Test
	void testDefined5() throws Exception {
		VersionUnit unit = VersionUnit.VF_5;
		assertEquals(unit.getName(), "");
		assertEquals(unit.getPosition(), 5);
		assertEquals(unit.next(), VersionUnit.VF_6);
		assertEquals(unit.previous(), VersionUnit.VF_4);
	}

	@Test
	void testDefined6() throws Exception {
		VersionUnit unit = VersionUnit.VF_6;
		assertEquals(unit.getName(), "");
		assertEquals(unit.getPosition(), 6);
		assertEquals(unit.next(), VersionUnit.VF_7);
		assertEquals(unit.previous(), VersionUnit.VF_5);
	}

	@Test
	void testDefined7() throws Exception {
		VersionUnit unit = VersionUnit.VF_7;
		assertEquals(unit.getName(), "");
		assertEquals(unit.getPosition(), 7);
		assertEquals(unit.next(), VersionUnit.VF_8);
		assertEquals(unit.previous(), VersionUnit.VF_6);
	}

	@Test
	void testDefined8() throws Exception {
		VersionUnit unit = VersionUnit.VF_8;
		assertEquals(unit.getName(), "");
		assertEquals(unit.getPosition(), 8);
		assertEquals(unit.next(), VersionUnit.VF_9);
		assertEquals(unit.previous(), VersionUnit.VF_7);
	}

	@Test
	void testDefined9() throws Exception {
		VersionUnit unit = VersionUnit.VF_9;
		assertEquals(unit.getName(), "");
		assertEquals(unit.getPosition(), 9);
		assertNull(unit.next());
		assertEquals(unit.previous(), VersionUnit.VF_8);
	}

	@Test
	void testIterable() throws Exception {
		Iterator<VersionUnit> iterator = VersionUnit.iterable().iterator();

		assertSame(iterator.next(), VersionUnit.VF_0_MAJOR);
		assertSame(iterator.next(), VersionUnit.VF_1_MINOR);
		assertSame(iterator.next(), VersionUnit.VF_2_REVISION);
		assertSame(iterator.next(), VersionUnit.VF_3_BUILD);
		assertSame(iterator.next(), VersionUnit.VF_4);
		assertSame(iterator.next(), VersionUnit.VF_5);
		assertSame(iterator.next(), VersionUnit.VF_6);
		assertSame(iterator.next(), VersionUnit.VF_7);
		assertSame(iterator.next(), VersionUnit.VF_8);
		assertSame(iterator.next(), VersionUnit.VF_9);

		assertFalse(iterator.hasNext());
	}

	@Test
	void testIterableBetween() throws Exception {
		Iterator<VersionUnit> iterator = VersionUnit.iterable(VersionUnit.VF_1_MINOR, VersionUnit.VF_4).iterator();

		assertSame(iterator.next(), VersionUnit.VF_1_MINOR);
		assertSame(iterator.next(), VersionUnit.VF_2_REVISION);
		assertSame(iterator.next(), VersionUnit.VF_3_BUILD);
		assertSame(iterator.next(), VersionUnit.VF_4);

		assertFalse(iterator.hasNext());
	}

	@Test
	void testIterableBetweenLast() throws Exception {
		Iterator<VersionUnit> iterator = VersionUnit.iterable(VersionUnit.VF_7, VersionUnit.VF_9).iterator();

		assertSame(iterator.next(), VersionUnit.VF_7);
		assertSame(iterator.next(), VersionUnit.VF_8);
		assertSame(iterator.next(), VersionUnit.VF_9);

		assertFalse(iterator.hasNext());
	}

	@Test
	void testMax() throws Exception {
		assertSame(VersionUnit.max(VersionUnit.VF_0_MAJOR, VersionUnit.VF_1_MINOR), VersionUnit.VF_0_MAJOR);
		assertSame(VersionUnit.max(VersionUnit.VF_1_MINOR, VersionUnit.VF_0_MAJOR), VersionUnit.VF_0_MAJOR);

		assertSame(VersionUnit.max(VersionUnit.VF_1_MINOR, VersionUnit.VF_2_REVISION), VersionUnit.VF_1_MINOR);
		assertSame(VersionUnit.max(VersionUnit.VF_2_REVISION, VersionUnit.VF_1_MINOR), VersionUnit.VF_1_MINOR);
	}

	@Test
	void testMin() throws Exception {
		assertSame(VersionUnit.min(VersionUnit.VF_0_MAJOR, VersionUnit.VF_1_MINOR), VersionUnit.VF_1_MINOR);
		assertSame(VersionUnit.min(VersionUnit.VF_1_MINOR, VersionUnit.VF_0_MAJOR), VersionUnit.VF_1_MINOR);

		assertSame(VersionUnit.min(VersionUnit.VF_1_MINOR, VersionUnit.VF_2_REVISION), VersionUnit.VF_2_REVISION);
		assertSame(VersionUnit.min(VersionUnit.VF_2_REVISION, VersionUnit.VF_1_MINOR), VersionUnit.VF_2_REVISION);
	}

	@Test
	void testParseInt() throws Exception {
		assertSame(VersionUnit.parse(0), VersionUnit.VF_0_MAJOR);
		assertSame(VersionUnit.parse(1), VersionUnit.VF_1_MINOR);
		assertSame(VersionUnit.parse(2), VersionUnit.VF_2_REVISION);
		assertSame(VersionUnit.parse(3), VersionUnit.VF_3_BUILD);
		assertSame(VersionUnit.parse(4), VersionUnit.VF_4);
		assertSame(VersionUnit.parse(5), VersionUnit.VF_5);
		assertSame(VersionUnit.parse(6), VersionUnit.VF_6);
		assertSame(VersionUnit.parse(7), VersionUnit.VF_7);
		assertSame(VersionUnit.parse(8), VersionUnit.VF_8);
		assertSame(VersionUnit.parse(9), VersionUnit.VF_9);
	}

	@Test
	void testParseName() throws Exception {
		assertSame(VersionUnit.parse("major"), VersionUnit.VF_0_MAJOR);
		assertSame(VersionUnit.parse("minor"), VersionUnit.VF_1_MINOR);
		assertSame(VersionUnit.parse("revision"), VersionUnit.VF_2_REVISION);
		assertSame(VersionUnit.parse("build"), VersionUnit.VF_3_BUILD);
	}

	@Test
	void testParseNameNull() throws Exception {
		assertNull(VersionUnit.parse(null));
	}

	@Test
	void testParseNameEmpty() throws Exception {
		assertNull(VersionUnit.parse(""));
	}

	@Test
	void testParseNameUnknown() throws Exception {
		assertNull(VersionUnit.parse("rsdf"));
	}

}
