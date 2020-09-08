package org.fagu.fmv.ffmpeg.logo;

/*-
 * #%L
 * fmv-ffmpeg
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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.fagu.fmv.image.Rectangle;


/**
 * 
 * From
 * https://stackoverflow.com/questions/35376726/how-to-detect-a-colored-rectangles-in-an-image?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 * 
 * @author f.agu
 * @created 9 avr. 2018 14:37:39
 */
public class DetectRectangle {

	private final BufferedImage image;

	private final List<Rectangle> rectangles;

	private final int width;

	private final int height;

	public DetectRectangle(BufferedImage image) {
		this.image = Objects.requireNonNull(image);
		width = image.getWidth();
		height = image.getHeight();
		rectangles = new ArrayList<>();
	}

	public List<Rectangle> findRectangles() {
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				if(isMazeColour(image.getRGB(x, y))) {
					Rectangle rect = findRectangle(x, y);
					if(rect != null) {
						x += rect.getWidth();
					}
				}
			}
		}

		return rectangles;
	}

	public boolean isMazeColour(int colour) {
		return colour == Color.WHITE.getRGB();
	}

	// ***************************************************

	private Rectangle findRectangle(int x, int y) {
		// this could be optimized. You could keep a separate collection where
		// you remove rectangles from, once your cursor is below that rectangle
		// find the width of the `Rectangle`
		// for(Rectangle rectangle : rectangles) {
		// if( ! rectangle.contains(x, y)) {
		// return rectangle;
		// }
		// }

		int xD = 0;
		while(x + xD < width && isMazeColour(image.getRGB(x + xD + 1, y))) {
			++xD;
		}

		int yD = 0;
		while(y + yD < height && isMazeColour(image.getRGB(x, y + yD + 1))) {
			++yD;
		}
		if(xD == 0 || yD == 0) {
			return null;
		}

		Rectangle toReturn = new Rectangle(x, y, xD, yD);
		rectangles.add(toReturn);
		return toReturn;
	}

}
