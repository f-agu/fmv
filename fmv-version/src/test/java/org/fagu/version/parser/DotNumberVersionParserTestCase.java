package org.fagu.version.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.fagu.version.Version;
import org.fagu.version.VersionParseException;
import org.fagu.version.VersionUnit;
import org.junit.jupiter.api.Test;


/**
 * @author Fabrice AGU
 */
class DotNumberVersionParserTestCase {

	@Test
	void testNull() throws Exception {
		assertFailed("null", null);
	}

	@Test
	void testEmpty() throws Exception {
		assertFailed("empty", "");
	}

	@Test
	void testSpace() throws Exception {
		assertFailed("space", "  ");
	}

	@Test
	void test_1() throws Exception {
		assertFailed(".1", ".1");
	}

	@Test
	void testXXX() throws Exception {
		assertFailed("XXX", "XXX");
	}

	@Test
	void test1() throws Exception {
		assertOK("1", "1", "1", 1);
	}

	@Test
	void test564() throws Exception {
		assertOK("564", "564", "564", 564);
	}

	@Test
	void test123() throws Exception {
		assertOK("1.2.3", "1.2.3", "1.2.3", 1, 2, 3);
	}

	// **********************************************************************

	private void assertFailed(String message, String str) throws Exception {
		DotNumberVersionParser versionParser = new DotNumberVersionParser();
		assertFalse(versionParser.test(str));
		try {
			versionParser.parse(str);
		} catch(VersionParseException e) {
			return;
		}
		fail(message);
	}

	private void assertOK(String message, String str, String rval, int... values) {
		DotNumberVersionParser versionParser = new DotNumberVersionParser();
		assertTrue(versionParser.test(str));
		Version version = null;
		try {
			version = versionParser.parse(str);
		} catch(VersionParseException e) {
			fail(e.toString());
		}

		final int NOT_SET = Integer.MIN_VALUE;
		final int len = values.length;
		for(int i = 0; i < len; ++i) {
			assertEquals(version.getFieldValue(VersionUnit.parse(i), NOT_SET), values[i], "position " + i);
		}

		assertEquals(version.toString(), str);
		assertEquals(version.getText(), rval);
	}

}
