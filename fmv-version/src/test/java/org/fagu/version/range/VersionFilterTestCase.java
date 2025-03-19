package org.fagu.version.range;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.fagu.version.Version;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class VersionFilterTestCase {

	@Test
	void testNull() {
		assertThrows(NullPointerException.class, () -> VersionRange.parse(null));
	}

	@Test
	void testEmpty() {
		assertSame(VersionFilters.REJECT, VersionFilter.parse(""));
		assertSame(VersionFilters.REJECT, VersionFilter.parse("   "));
	}

	@Test
	void testOne() {
		VersionFilter filter = VersionFilter.parse("1.0.2");
		assertTrue(filter.test(new Version(1, 0, 2)));
		assertFalse(filter.test(new Version(1, 0)));
		assertFalse(filter.test(new Version(1, 0, 1)));
		assertTrue(filter.test(new Version(1, 0, 2)));
		assertFalse(filter.test(new Version(1, 0, 3)));
		assertFalse(filter.test(new Version(2, 0)));
		assertEquals("1.0.2", filter.toString());
	}

	@Test
	void testSingles() {
		VersionFilter filter = VersionFilter.parse("1.0.2 , 2");
		assertFalse(filter.test(new Version(1, 0)));
		assertFalse(filter.test(new Version(1, 0, 1)));
		assertTrue(filter.test(new Version(1, 0, 2)));
		assertFalse(filter.test(new Version(1, 0, 3)));
		assertTrue(filter.test(new Version(2)));
		assertFalse(filter.test(new Version(2, 0, 1)));
		assertEquals("1.0.2,2", filter.toString());
	}

	@Test
	void testSingle_Between_startInc_endInc() {
		VersionFilter filter = VersionFilter.parse("0.9.2,[1.3,2.4]");
		assertFalse(filter.test(new Version(0, 9)));
		assertTrue(filter.test(new Version(0, 9, 2)));
		assertFalse(filter.test(new Version(0, 9, 3)));
		assertFalse(filter.test(new Version(0, 9, 4)));
		assertFalse(filter.test(new Version(0, 9, 5)));
		assertFalse(filter.test(new Version(1, 0)));
		assertFalse(filter.test(new Version(1, 1)));
		assertFalse(filter.test(new Version(1, 2)));
		assertTrue(filter.test(new Version(1, 3)));
		assertTrue(filter.test(new Version(1, 4)));
		assertTrue(filter.test(new Version(2, 0)));
		assertTrue(filter.test(new Version(2, 3)));
		assertTrue(filter.test(new Version(2, 4)));
		assertFalse(filter.test(new Version(2, 5)));
		assertFalse(filter.test(new Version(3, 0)));
		assertEquals("0.9.2,[1.3,2.4]", filter.toString());
	}

	@Test
	void testSingle2_Between_startInc_endInc() {
		VersionFilter filter = VersionFilter.parse("0.9.2,0.9.4,[1.3,2.4]");
		assertFalse(filter.test(new Version(0, 9)));
		assertTrue(filter.test(new Version(0, 9, 2)));
		assertFalse(filter.test(new Version(0, 9, 3)));
		assertTrue(filter.test(new Version(0, 9, 4)));
		assertFalse(filter.test(new Version(0, 9, 5)));
		assertFalse(filter.test(new Version(1, 0)));
		assertFalse(filter.test(new Version(1, 1)));
		assertFalse(filter.test(new Version(1, 2)));
		assertTrue(filter.test(new Version(1, 3)));
		assertTrue(filter.test(new Version(1, 4)));
		assertTrue(filter.test(new Version(2, 0)));
		assertTrue(filter.test(new Version(2, 3)));
		assertTrue(filter.test(new Version(2, 4)));
		assertFalse(filter.test(new Version(2, 5)));
		assertFalse(filter.test(new Version(3, 0)));
		assertEquals("0.9.2,0.9.4,[1.3,2.4]", filter.toString());
	}

	@Test
	void testBetween_startInc_endInc() {
		VersionFilter filter = VersionFilter.parse(" [ 1.3    , 2.4 ]   ");
		assertFalse(filter.test(new Version(1, 0)));
		assertFalse(filter.test(new Version(1, 1)));
		assertFalse(filter.test(new Version(1, 2)));
		assertTrue(filter.test(new Version(1, 3)));
		assertTrue(filter.test(new Version(1, 4)));
		assertTrue(filter.test(new Version(2, 0)));
		assertTrue(filter.test(new Version(2, 3)));
		assertTrue(filter.test(new Version(2, 4)));
		assertFalse(filter.test(new Version(2, 5)));
		assertFalse(filter.test(new Version(3, 0)));
		assertEquals("[1.3,2.4]", filter.toString());
	}

	@Test
	void testBetween_startInc_endExc() {
		VersionFilter filter = VersionFilter.parse(" [ 1.3    , 2.4 )   ");
		assertFalse(filter.test(new Version(1, 0)));
		assertFalse(filter.test(new Version(1, 1)));
		assertFalse(filter.test(new Version(1, 2)));
		assertTrue(filter.test(new Version(1, 3)));
		assertTrue(filter.test(new Version(1, 4)));
		assertTrue(filter.test(new Version(2, 0)));
		assertTrue(filter.test(new Version(2, 3)));
		assertFalse(filter.test(new Version(2, 4)));
		assertFalse(filter.test(new Version(2, 5)));
		assertFalse(filter.test(new Version(3, 0)));
		assertEquals("[1.3,2.4)", filter.toString());
	}

	@Test
	void testBetween_startExc_endInc() {
		VersionFilter filter = VersionFilter.parse(" ( 1.3    , 2.4 ]   ");
		assertFalse(filter.test(new Version(1, 0)));
		assertFalse(filter.test(new Version(1, 1)));
		assertFalse(filter.test(new Version(1, 2)));
		assertFalse(filter.test(new Version(1, 3)));
		assertTrue(filter.test(new Version(1, 4)));
		assertTrue(filter.test(new Version(2, 0)));
		assertTrue(filter.test(new Version(2, 3)));
		assertTrue(filter.test(new Version(2, 4)));
		assertFalse(filter.test(new Version(2, 5)));
		assertFalse(filter.test(new Version(3, 0)));
		assertEquals("(1.3,2.4]", filter.toString());
	}

	@Test
	void testBetween_startExc_endExc() {
		VersionFilter filter = VersionFilter.parse(" ( 1.3    , 2.4 )   ");
		assertFalse(filter.test(new Version(1, 0)));
		assertFalse(filter.test(new Version(1, 1)));
		assertFalse(filter.test(new Version(1, 2)));
		assertFalse(filter.test(new Version(1, 3)));
		assertTrue(filter.test(new Version(1, 4)));
		assertTrue(filter.test(new Version(2, 0)));
		assertTrue(filter.test(new Version(2, 3)));
		assertFalse(filter.test(new Version(2, 4)));
		assertFalse(filter.test(new Version(2, 5)));
		assertFalse(filter.test(new Version(3, 0)));
		assertEquals("(1.3,2.4)", filter.toString());
	}

	@Test
	void testExcludeSingle() {
		VersionFilter filter = VersionFilter.parse("(,1.1),(1.1,)");
		assertTrue(filter.test(new Version(0, 9)));
		assertTrue(filter.test(new Version(1, 0)));
		assertFalse(filter.test(new Version(1, 1)));
		assertTrue(filter.test(new Version(1, 2)));
		assertTrue(filter.test(new Version(1, 3)));
		assertEquals("(,1.1),(1.1,)", filter.toString());
	}

}
