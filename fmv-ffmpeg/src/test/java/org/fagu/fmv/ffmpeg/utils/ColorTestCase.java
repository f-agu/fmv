package org.fagu.fmv.ffmpeg.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class ColorTestCase {

	@Test
	void testValueOf() {
		assertColor(Color.valueOf("#321546"), null, "321546", null);
		assertColor(Color.valueOf("#aaffBB"), null, "AAFFBB", null);
		assertColor(Color.valueOf("0xFf6347"), "Tomato", "FF6347", null);
	}

	@Test
	void testValueOfOpacity() {
		assertColor(Color.valueOf("#321546@FF"), null, "321546", "FF");
		assertColor(Color.valueOf("#aaffBB@0a"), null, "AAFFBB", "0A");
		assertColor(Color.valueOf("yellow@0.2"), "Yellow", "FFFF00", "0.2");
	}

	// ****************************************************

	private void assertColor(Color color, String expectedName, String expectedSequence, String expectedOpacity) {
		assertEquals(expectedName, color.getName());
		assertEquals(expectedSequence, color.getSequence());
		assertEquals(expectedOpacity, color.getOpacity());
	}

}
