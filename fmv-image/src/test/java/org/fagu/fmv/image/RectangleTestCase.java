package org.fagu.fmv.image;

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

import org.junit.Test;


/**
 * @author Oodrive
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
		assertFalse(r2.intersects(r1));
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
		r1.isGlued(r2);
	}

	// ******************************************

	private void draw(String title, Rectangle... rectangles) {
		if( ! DEBUG) {
			return;
		}
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		Iterator<Color> iterator = colorIterator();
		for(Rectangle r : rectangles) {
			drawRectangle(r, graphics, iterator.next());
		}
		if(rectangles.length == 2) {
			rectangles[0].intersection(rectangles[1]).ifPresent(r -> drawRectangle(r, graphics, Color.WHITE));
			rectangles[0].union(rectangles[1]).ifPresent(r -> drawRectangle(r, graphics, Color.YELLOW));
			boolean glued = rectangles[0].isGlued(rectangles[1]);
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
