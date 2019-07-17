package org.fagu.fmv.utils.media;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 fagu
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class SizeTestCase {

	/**
	 * 
	 */
	public SizeTestCase() {}

	/**
	 * 
	 */
	@Test
	@Ignore
	public void testLoad() {
		for(int w = 1; w < 100000; w++) {
			for(int h = 1; h < 100000; h++) {
				Size.valueOf(w, h);
			}
		}
	}

	/**
	 * 
	 */
	@Test
	public void testRatio() {
		Size size = Size.HD720.getRatio().getSizeByHeight(720 / 2);
		assertEquals(230400, size.countPixel());
		assertEquals(408960, Size.HD480.countPixel());
	}

	/**
	 * 
	 */
	@Test
	public void testFitTo() {
		Size size = Size.HD1080;
		Size maxSize = Size.valueOf(100, 100);
		assertEquals(Size.valueOf(100, 56), size.fitAndKeepRatioTo(maxSize));
	}

	/**
	 * 
	 */
	@Test
	public void testFitToAround() {
		// System.out.println(1080 * 9 / 16);
		// System.out.println(1080F * 9F / 16F);
		// System.out.println(Math.floor(1080F * 9F / 16F));
		// System.out.println(Math.ceil(a)floorDiv(1080 * 9, 16));

		Size size = Size.HD720.rotate();
		Size maxSize = Size.HD1080;
		assertEquals(Size.valueOf(607, 1080), size.fitAndKeepRatioTo(maxSize));
	}

	/**
	 * 
	 */
	@Test
	public void testParse() {
		assertEquals(Size.valueOf(1280, 43), Size.parse("1280x43"));
		assertEquals(Size.valueOf(1280, 43), Size.parse(" 1280 x 43 "));
		assertSame(Size.HD720, Size.parse("hd720"));
		assertSame(Size.HD720, Size.parse("Hd720"));
		assertSame(Size.HD720, Size.parse("HD720"));
		assertSame(Size.HD720, Size.parse(" Hd720 \t"));
		assertSame(Size._2K, Size.parse("2k    "));
	}

	/**
	 * 
	 */
	@Test
	public void testComparatorByCountPixel() {
		SortedSet<Size> set = new TreeSet<Size>(Size.comparatorByCountPixel());
		set.add(Size.HD1080);
		set.add(Size.HD480);
		set.add(Size.HD720);

		Iterator<Size> iterator = set.iterator();
		assertSame(Size.HD480, iterator.next());
		assertSame(Size.HD720, iterator.next());
		assertSame(Size.HD1080, iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 * 
	 */
	@Test
	public void testComparatorByDiagonal() {
		SortedSet<Size> set = new TreeSet<Size>(Size.comparatorByDiagonal());
		set.add(Size.HD1080);
		set.add(Size.HD480);
		set.add(Size.HD720);

		Iterator<Size> iterator = set.iterator();
		assertSame(Size.HD480, iterator.next());
		assertSame(Size.HD720, iterator.next());
		assertSame(Size.HD1080, iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testIsInside() {
		assertTrue(Size.HD720.isInside(Size.HD1080));
		assertFalse(Size.valueOf(4, 4).isInside(Size.valueOf(2, 6)));
		assertFalse(Size.valueOf(4, 4).isInside(Size.valueOf(6, 2)));
		assertTrue(Size.HD720.isInside(Size.HD720));
	}

	@Test(expected = NullPointerException.class)
	public void testIsInside_NPE() {
		Size.HD720.isInside(null);
	}

	@Test
	public void testIsOutside() {
		assertFalse(Size.HD720.isOutside(Size.HD1080));
		assertFalse(Size.valueOf(4, 4).isOutside(Size.valueOf(2, 6)));
		assertFalse(Size.valueOf(4, 4).isOutside(Size.valueOf(6, 2)));
		assertFalse(Size.HD720.isOutside(Size.HD720));
	}

	@Test(expected = NullPointerException.class)
	public void testIsOutside_NPE() {
		Size.HD720.isOutside(null);
	}

	@Test
	public void testIsPartialOutside() {
		assertFalse(Size.HD720.isPartialOutside(Size.HD1080));
		assertTrue(Size.valueOf(4, 4).isPartialOutside(Size.valueOf(2, 6)));
		assertTrue(Size.valueOf(4, 4).isPartialOutside(Size.valueOf(6, 2)));
		assertFalse(Size.HD720.isPartialOutside(Size.HD720));
	}

	@Test(expected = NullPointerException.class)
	public void testIsPartialOutside_NPE() {
		Size.HD720.isPartialOutside(null);
	}

	/**
	 * 
	 */
	// @Test
	// public void testShrink() throws ParseException {
	// Size size = Size.HD1080.rotate90();
	// size.
	// }

}
