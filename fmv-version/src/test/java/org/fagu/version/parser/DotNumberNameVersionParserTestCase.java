package org.fagu.version.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.fagu.version.Version;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class DotNumberNameVersionParserTestCase {

	@Test
	void testTestFailed() {
		assertFalse(DotNumberRLCVersionParser.INSTANCE.test(null));
		assertFalse(DotNumberRLCVersionParser.INSTANCE.test(""));
		assertFalse(DotNumberRLCVersionParser.INSTANCE.test(" "));
		assertFalse(DotNumberRLCVersionParser.INSTANCE.test("0"));
		assertFalse(DotNumberRLCVersionParser.INSTANCE.test("1.69"));
		assertFalse(DotNumberRLCVersionParser.INSTANCE.test("xx"));
		assertFalse(DotNumberRLCVersionParser.INSTANCE.test("SNPAHOST"));
	}

	@Test
	void testTestPassed() {
		assertTrue(DotNumberRLCVersionParser.INSTANCE.test("1-RC4"));
		assertTrue(DotNumberRLCVersionParser.INSTANCE.test("2.54.0-SNAPSHOT"));
		assertTrue(DotNumberRLCVersionParser.INSTANCE.test("10.36.325.698.36-alpha"));
	}

	@Test
	void testParse() {
		Version.parse("1-RC4");
		Version.parse("1");
	}
}
