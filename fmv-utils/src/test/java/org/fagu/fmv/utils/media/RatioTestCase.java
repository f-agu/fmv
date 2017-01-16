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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;


/**
 * @author f.agu
 */
public class RatioTestCase {

	/**
	 * 
	 */
	public RatioTestCase() {}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_nok_nm5() {
		Ratio.valueOf( - 5, 1);
	}

	/**
	 * 
	 */
	@Test(expected = ArithmeticException.class)
	public void testConstructor_nok_d0() {
		Ratio.valueOf(1, 0);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_nok_dm5() {
		Ratio.valueOf(1, - 5);
	}

	/**
	 * 
	 */
	@Test
	public void testSimple_ok() {
		Ratio ratio = Ratio.valueOf(16, 9);
		assertEquals("16:9", ratio.toString());
		assertEquals((double)16 / 9, ratio.toDouble(), 0.00001);
	}

	/**
	 * 
	 */
	@Test
	public void testEquals() {
		assertEquals(Ratio.valueOf(16, 9), Ratio.valueOf(16, 9));
		assertEquals(Ratio.valueOf(1, 1), Ratio.valueOf(1, 1));
		assertNotEquals(Ratio.valueOf(4, 3), Ratio.valueOf(4, 2));
		assertNotEquals(Ratio.valueOf(4, 3), Ratio.valueOf(8, 6));
	}

	/**
	 * 
	 */
	@Test
	public void testCalculateWidth() {
		assertEquals(700, Ratio.TRADITIONAL_TELEVISION.calculateWidth(525));
		assertEquals(960, Ratio.TRADITIONAL_TELEVISION.calculateWidth(Size.HD720));
	}

	/**
	 * 
	 */
	@Test
	public void testCalculateHeight() {
		assertEquals(1080, Ratio._16_9.calculateHeight(1920));
		assertEquals(1066, Ratio._16_9.calculateWidth(Size.SVGA));
	}

	/**
	 * 
	 */
	@Test
	public void testApproximate_InList() {
		assertEquals(Ratio.valueOf(3, 2), Size.valueOf(3008, 1960).getRatio().approximate());
		assertEquals(Ratio.valueOf(3, 2), Size.valueOf(3008, 2000).getRatio().approximate());
	}

	/**
	 * 
	 */
	@Test
	public void testApproximate_create() {
		assertEquals(Ratio.valueOf(2.96), Size.valueOf(8000, 2700).getRatio().approximate());
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testApproximate_failed_1() {
		Ratio._16_9.approximate(1);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testApproximate_failed_0() {
		Ratio._16_9.approximate(0);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testApproximate_failed_over1() {
		Ratio._16_9.approximate(100);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testApproximate_failed_under0() {
		Ratio._16_9.approximate( - 8);
	}

	/**
	 * 
	 */
	@Test
	public void testReduce() {
		assertSame(Ratio._16_9, Ratio._16_9.reduce());
		assertSame(Ratio.ISO_216, Ratio.ISO_216.reduce());
		assertEquals(Ratio.valueOf(8, 5), Ratio.COMMON_COMPUTER_SCREEN.reduce());
	}

	/**
	 * 
	 */
	@Test
	public void testParse_ddots() {
		assertSame(Ratio._16_9, Ratio.parse("16:9"));
		assertSame(Ratio._16_9, Ratio.parse("16/9"));
		assertEquals(0.618046D, Ratio.parse("1:1.618").toDouble(), 0.000001D);
		assertEquals(0.5D, Ratio.parse("0.5").toDouble(), 0.001D);
	}

	/**
	 * 
	 */
	@Test
	public void testInvert() {
		assertEquals(Ratio.valueOf(9, 16), Ratio._16_9.invert());
		assertEquals(Ratio.valueOf(0.707106D), Ratio.ISO_216.invert());
	}

	/**
	 * 
	 */
	@Test(expected = ArithmeticException.class)
	public void testInvert_failed() {
		Ratio.valueOf(0, 1).invert();
	}

}
