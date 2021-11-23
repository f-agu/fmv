package org.fagu.fmv.im;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*-
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 - 2017 fagu
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

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.fagu.fmv.image.ImageResourceUtils;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 11 janv. 2017 13:02:17
 */
class DominantColorTestCase {

	@Test
	void testRealImageRGB_File() throws IOException {
		File file = ImageResourceUtils.extractFile(ImageResourceUtils.BAD_ASS_TOTTOO_FAIL);
		try {
			Color dominantColor = DominantColor.getInstance().getDominantColor(file, s -> {});
			assertEquals(new Color(85, 70, 70), dominantColor);
		} finally {
			if(file != null) {
				file.delete();
			}
		}
	}

	@Test
	void testRealImageRGB_InputStream() throws IOException {
		// StreamLog.debug(true);
		// Package pkg = DominantColor.class.getPackage();
		// String resourcePath = Resources.getResourcePath(pkg, "bad-ass-tattoo-fail.jpg");
		// ImageResourceUtils.getInputStream(ImageResourceUtils.BAD_ASS_TOTTOO_FAIL);

		Color dominantColor = DominantColor.getInstance()
				.getDominantColor(
						() -> ImageResourceUtils.getInputStream(ImageResourceUtils.BAD_ASS_TOTTOO_FAIL),
						s -> {});
		assertEquals(new Color(85, 70, 70), dominantColor);
	}

	@Test
	void testParseRGB() throws IOException {
		assertColor(DominantColor.parse("srgb(130,125,119)"), 130, 125, 119, 255);
		assertColor(DominantColor.parse("srgb(33.489%,27.3838%,27.5563%)"), 85, 69, 70, 255);
		assertColor(DominantColor.parse("srgba(130,125,119,0.4)"), 130, 125, 119, 102);
		assertColor(DominantColor.parse("srgba(49%,48%,47%,0.785565)"), 124, 122, 119, 200);
	}

	@Test
	void testParseCMYK() throws IOException {
		assertColor("black", DominantColor.parse("cmyk(0,0,0,255)"), 0, 0, 0, 255);
		assertColor("white", DominantColor.parse("cmyk(0,0,0,0)"), 255, 255, 255, 255);
		assertColor("red", DominantColor.parse("cmyk(0,255,255,0)"), 255, 0, 0, 255);
		assertColor("green", DominantColor.parse("cmyk(255,0,255,0)"), 0, 255, 0, 255);
		assertColor("blue", DominantColor.parse("cmyk(255,255,0,0)"), 0, 0, 255, 255);
		assertColor("yellow", DominantColor.parse("cmyk(0,0,255,0)"), 255, 255, 0, 255);
		assertColor("cyan", DominantColor.parse("cmyk(255,0,0,0)"), 0, 255, 255, 255);
		assertColor("magenta", DominantColor.parse("cmyk(0,255,0,0)"), 255, 0, 255, 255);
	}

	// ***********************************************

	private void assertColor(Color color, int expectedRed, int expectedGreen, int expectedBlue, int expectedAlpha) {
		assertColor(null, color, expectedRed, expectedGreen, expectedBlue, expectedAlpha);
	}

	private void assertColor(String title, Color color, int expectedRed, int expectedGreen, int expectedBlue, int expectedAlpha) {
		assertEquals(expectedRed, color.getRed(), title + "[red]");
		assertEquals(expectedGreen, color.getGreen(), title + "[green]");
		assertEquals(expectedBlue, color.getBlue(), title + "[blue]");
		assertEquals(expectedAlpha, color.getAlpha(), title + "[transparency]");
	}

}
