package org.fagu.fmv.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.Color;

import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class ColorUtilsTestCase {

	@Test
	void testName() {
		assertSame(Color.black, ColorUtils.parse("black"));
		assertNull(ColorUtils.parse("x"));
	}

	@Test
	void testAlpha() {
		Color color = ColorUtils.parse("#00000088");
		assertEquals(0x88, color.getAlpha());

		assertEquals("#00000088", ColorUtils.toHex(color));
		assertEquals("#0000ff", ColorUtils.toHex(Color.BLUE));
	}

}
