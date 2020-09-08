package org.fagu.fmv.mymedia.compare;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;


/**
 * @author Utilisateur
 * @created 3 avr. 2018 22:55:42
 */
public class ImageDiffPercent {

	private ImageDiffPercent() {}

	/**
	 * @param i1
	 * @param i2
	 * @return
	 * @throws IOException
	 */
	public static double getDifferencePercent(InputStream i1, InputStream i2) throws IOException {
		BufferedImage img1 = ImageIO.read(i1);
		BufferedImage img2 = ImageIO.read(i2);
		return getDifferencePercent(img1, img2);
	}

	/**
	 * From http://rosettacode.org/wiki/Percentage_difference_between_images
	 * 
	 * @param img1
	 * @param img2
	 * @return
	 */
	public static double getDifferencePercent(BufferedImage img1, BufferedImage img2) {
		int width = img1.getWidth();
		int height = img1.getHeight();
		int width2 = img2.getWidth();
		int height2 = img2.getHeight();
		if(width != width2 || height != height2) {
			return 100D;
			// throw new IllegalArgumentException(String.format("Images must have the same dimensions: (%d,%d) vs.
			// (%d,%d)", width, height, width2,
			// height2));
		}
		long diff = 0;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
			}
		}
		long maxDiff = 3L * 255 * width * height;

		return 100.0 * diff / maxDiff;
	}

	// **********************************************

	/**
	 * @param rgb1
	 * @param rgb2
	 * @return
	 */
	private static int pixelDiff(int rgb1, int rgb2) {
		int r1 = (rgb1 >> 16) & 0xff;
		int g1 = (rgb1 >> 8) & 0xff;
		int b1 = rgb1 & 0xff;
		int r2 = (rgb2 >> 16) & 0xff;
		int g2 = (rgb2 >> 8) & 0xff;
		int b2 = rgb2 & 0xff;
		return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
	}
}
