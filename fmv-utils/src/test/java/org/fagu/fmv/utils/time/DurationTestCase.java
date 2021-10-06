package org.fagu.fmv.utils.time;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class DurationTestCase {

	@Test
	void testParse() {
		assertEquals(Duration.valueOf(0), Duration.parse("0"));
		assertEquals(Duration.valueOf(0), Duration.parse("0.0"));
		assertEquals(Duration.valueOf(1.00001), Duration.parse("1.00001"));
		assertEquals(Duration.valueOf(27.767000), Duration.parse("27.767000"));
		assertEquals(new Duration(0, 58, 59.988), Duration.parse("58:59.988"));
		assertEquals(new Duration(81, 58, 59.988), Duration.parse("81:58:59.988"));
	}

	@Test
	void testParseWithSign() {
		assertEquals(Duration.valueOf(0), Duration.parse("+0"));
		assertEquals(Duration.valueOf(0), Duration.parse("-0"));
		assertEquals(Duration.valueOf(0), Duration.parse("+0.0"));
		assertEquals(Duration.valueOf(0), Duration.parse("-0.0"));
		assertEquals(Duration.valueOf(1.00001), Duration.parse("+1.00001"));
		assertEquals(Duration.valueOf( - 1.00001), Duration.parse("-1.00001"));
		assertEquals(Duration.valueOf(27.767000), Duration.parse("+27.767000"));
		assertEquals(Duration.valueOf( - 27.767000), Duration.parse("-27.767000"));
		assertEquals(new Duration(0, 58, 59.988, false), Duration.parse("+58:59.988"));
		assertEquals(new Duration(0, 58, 59.988, true), Duration.parse("-58:59.988"));
		assertEquals(new Duration(81, 58, 59.988, false), Duration.parse("+81:58:59.988"));
		assertEquals(new Duration(81, 58, 59.988, true), Duration.parse("-81:58:59.988"));
	}

	@Test
	void testToSeconds() {
		// assertEquals(0, Duration.valueOf(0).toSeconds(), 0.01D);
		// assertEquals(1.23, Duration.valueOf(1.23).toSeconds(), 0.01D);
		assertEquals( - 9.87, Duration.valueOf( - 9.87).toSeconds(), 0.01D);
	}

	@Test
	void testUnlimited() {
		Duration unlimited = Duration.unlimited();
		System.out.println(unlimited);
	}

}
