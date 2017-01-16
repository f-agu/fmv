package org.fagu.fmv.image;

/*-
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.fagu.fmv.utils.Resources;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 11 janv. 2017 13:02:17
 */
public class DominantColorTestCase {

	/**
	 * 
	 */
	public DominantColorTestCase() {}

	/**
	 * @throws IOException
	 */
	@Test
	public void testRealImageRGB() throws IOException {
		Package pkg = DominantColor.class.getPackage();
		File file = Resources.extractToTempFile(Resources.getResourcePath(pkg, "bad-ass-tattoo-fail.jpg"), ImageMetadatasTestCase.class
				.getSimpleName(), ".jpg");
		try {
			Color dominantColor = DominantColor.getDominantColor(file);
			System.out.println(dominantColor.getRGB());
			System.out.println(new Color(85, 70, 70).getRGB());
			assertEquals(new Color(85, 70, 70), dominantColor);
		} finally {
			if(file != null) {
				file.delete();
			}
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testParseRGB() throws IOException {
		assertColor(DominantColor.parse("srgb(130,125,119)"), 130, 125, 119);
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testParseCMYK() throws IOException {
		assertColor("black", DominantColor.parse("cmyk(0,0,0,255)"), 0, 0, 0);
		assertColor("white", DominantColor.parse("cmyk(0,0,0,0)"), 255, 255, 255);
		assertColor("red", DominantColor.parse("cmyk(0,255,255,0)"), 255, 0, 0);
		assertColor("green", DominantColor.parse("cmyk(255,0,255,0)"), 0, 255, 0);
		assertColor("blue", DominantColor.parse("cmyk(255,255,0,0)"), 0, 0, 255);
		assertColor("yellow", DominantColor.parse("cmyk(0,0,255,0)"), 255, 255, 0);
		assertColor("cyan", DominantColor.parse("cmyk(255,0,0,0)"), 0, 255, 255);
		assertColor("magenta", DominantColor.parse("cmyk(0,255,0,0)"), 255, 0, 255);
	}

	// ***********************************************

	/**
	 * @param color
	 * @param expectedRed
	 * @param expectedGreen
	 * @param expectedBlue
	 */
	private void assertColor(Color color, int expectedRed, int expectedGreen, int expectedBlue) {
		assertColor(null, color, expectedRed, expectedGreen, expectedBlue);
	}

	/**
	 * @param title
	 * @param color
	 * @param expectedRed
	 * @param expectedGreen
	 * @param expectedBlue
	 */
	private void assertColor(String title, Color color, int expectedRed, int expectedGreen, int expectedBlue) {
		assertEquals(title, expectedRed, color.getRed());
		assertEquals(title, expectedGreen, color.getGreen());
		assertEquals(title, expectedBlue, color.getBlue());
	}

}
