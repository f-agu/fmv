package org.fagu.fmv.im;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;


class ColorTestUtils {

	private ColorTestUtils() {}

	static void assertAround(Color c, int expectedRed, int expectedGreen, int expectedBlue, int round) {
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();
		assertTrue(expectedRed - round <= red && red <= expectedRed + round, "red");
		assertTrue(expectedGreen - round <= green && green <= expectedGreen + round, "green");
		assertTrue(expectedBlue - round <= blue && blue <= expectedBlue + round, "blue");
	}

}
