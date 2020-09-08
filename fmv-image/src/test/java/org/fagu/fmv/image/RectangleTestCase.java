package org.fagu.fmv.image;

/*-
 * #%L
 * fmv-image
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 * @created 10 avr. 2018 13:25:10
 */
public class RectangleTestCase {

	private static final boolean DEBUG = true;

	@Test
	public void testFieldValues() {
		Rectangle r = new Rectangle(1, 2, 3, 4);
		assertEquals(1, r.getX());
		assertEquals(2, r.getY());
		assertEquals(3, r.getWidth());
		assertEquals(4, r.getHeight());
		assertEquals(3, r.getMaxX());
		assertEquals(5, r.getMaxY());
	}

	@Test
	public void testContains() {
		int count = 0;
		Rectangle r = new Rectangle(10, 20, 3, 4);
		for(int x = 10; x < 13; ++x) {
			for(int y = 20; y < 24; ++y) {
				assertTrue("[" + x + ", " + y + "]", r.contains(x, y));
				++count;
			}
		}
		assertEquals(3 * 4, count);

		for(int x = 0; x < 50; ++x) {
			for(int y = 0; y < 50; ++y) {
				boolean expected = 10 <= x && x <= 12 && 20 <= y && y <= 23;
				assertEquals("[" + x + ", " + y + "]", expected, r.contains(x, y));
			}
		}
	}

	@Test
	public void testIntersects_outside() {
		Rectangle r1 = new Rectangle(10, 20, 30, 40);
		Rectangle r2 = new Rectangle(50, 50, 10, 10);
		draw("Intersects_outside", r1, r2);
		assertFalse(r1.intersects(r2));
		assertFalse(r2.intersects(r1));
	}

	@Test
	public void testIntersects_inside() {
		Rectangle r1 = new Rectangle(10, 20, 30, 40); // bigger
		Rectangle r2 = new Rectangle(11, 21, 10, 10); // smaller
		draw("Intersects_inside", r1, r2);
		assertTrue(r1.intersects(r2));
		assertTrue(r2.intersects(r1));
	}

	@Test
	public void testIntersects_north() {
		Rectangle r1 = new Rectangle(10, 20, 30, 40);
		Rectangle r2 = new Rectangle(11, 18, 10, 50);
		draw("Intersects_north", r1, r2);
		assertTrue(r1.intersects(r2));
		assertTrue(r2.intersects(r1));
	}

	@Test
	public void testIntersects_south() {
		Rectangle r1 = new Rectangle(10, 20, 30, 40);
		Rectangle r2 = new Rectangle(11, 21, 10, 50);
		draw("Intersects_south", r1, r2);
		assertTrue(r1.intersects(r2));
		assertTrue(r2.intersects(r1));
	}

	@Test
	public void testCanJoinWith() {
		Rectangle r1 = new Rectangle(10, 20, 30, 40);
		Rectangle r2 = new Rectangle(5, 10, 14, 10);
		draw("CanJoinWith", r1, r2);
	}

	@Test
	public void testIntersect() {
		Rectangle r1 = new Rectangle(285, 285, 95, 95);
		Rectangle r2 = new Rectangle(386, 236, 103, 103);
		draw("Intersect", r1, r2);
	}

	@Test
	@Ignore
	public void testSample() {
		Rectangle r1 = new Rectangle(912, 658, 361, 55);
		Rectangle r2 = new Rectangle(957, 658, 21, 54);
		Rectangle r3 = new Rectangle(981, 658, 2, 54);
		Rectangle r4 = new Rectangle(989, 658, 10, 54);
		Rectangle r5 = new Rectangle(1018, 658, 1, 54);
		Rectangle r6 = new Rectangle(1040, 658, 4, 54);
		Rectangle r7 = new Rectangle(1062, 658, 45, 54);
		Rectangle r8 = new Rectangle(1115, 658, 4, 55);
		Rectangle r9 = new Rectangle(1121, 658, 9, 55);
		Rectangle r10 = new Rectangle(1133, 658, 130, 55);
		draw("Sample-r1-r2", r1, r2);
		draw("Sample-all", r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
	}

	// ******************************************

	private void draw(String title, Rectangle... rectangles) {
		draw(title, Arrays.asList(rectangles));
	}

	private void draw(String title, List<Rectangle> rectangles) {
		if( ! DEBUG) {
			return;
		}
		int maxX = 0;
		int maxY = 0;
		for(Rectangle r : rectangles) {
			maxX = Math.max(maxX, r.getMaxX());
			maxY = Math.max(maxY, r.getMaxY());
		}
		maxX += 10;
		maxY += 10;

		BufferedImage image = new BufferedImage(maxX, maxY, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		Iterator<Color> iterator = colorIterator();
		for(Rectangle r : rectangles) {
			drawRectangle(r, graphics, iterator.next());
		}
		if(rectangles.size() == 2) {
			Rectangle r1 = rectangles.get(0);
			Rectangle r2 = rectangles.get(1);
			assertEquals(r1.intersection(r2), r2.intersection(r1));
			r1.intersection(r2).ifPresent(r -> drawRectangle(r, graphics, Color.WHITE));
			r1.union(r2).ifPresent(r -> drawRectangle(r, graphics, Color.YELLOW));
			boolean glued = r1.isGlued(r2);
			System.out.println((glued ? "" : "NOT ") + "GLUED: " + title);
		}

		try {
			File output = new File("rectangles-" + title + ".png");
			System.out.println(output.getAbsolutePath());
			ImageIO.write(image, "png", output);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void drawRectangle(Rectangle r, Graphics2D graphics, Color color) {
		graphics.setColor(color);
		graphics.drawLine(r.getX(), r.getY(), r.getX(), r.getMaxY());
		graphics.drawLine(r.getX(), r.getY(), r.getMaxX(), r.getY());
		graphics.drawLine(r.getX(), r.getMaxY(), r.getMaxX(), r.getMaxY());
		graphics.drawLine(r.getMaxX(), r.getY(), r.getMaxX(), r.getMaxY());
	}

	private Iterator<Color> colorIterator() {
		List<Color> colors = Arrays.asList(Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.PINK, Color.YELLOW, Color.CYAN,
				Color.MAGENTA, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY);
		return new Iterator<Color>() {

			private Iterator<Color> iterator = colors.iterator();

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Color next() {
				if( ! iterator.hasNext()) {
					iterator = colors.iterator();
				}
				return iterator.next();
			}
		};
	}

}
