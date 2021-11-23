package org.fagu.fmv.utils.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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


/**
 * @author f.agu
 */
class RatioTestCase {

	@Test
	void testConstructor_nok_nm5() {
		assertThrows(IllegalArgumentException.class, () -> Ratio.valueOf( - 5, 1));
	}

	@Test
	void testConstructor_nok_d0() {
		assertThrows(ArithmeticException.class, () -> Ratio.valueOf(1, 0));
	}

	@Test
	void testConstructor_nok_dm5() {
		assertThrows(IllegalArgumentException.class, () -> Ratio.valueOf(1, - 5));
	}

	@Test
	void testSimple_ok() {
		Ratio ratio = Ratio.valueOf(16, 9);
		assertEquals("16:9", ratio.toString());
		assertEquals((double)16 / 9, ratio.toDouble(), 0.00001);
	}

	@Test
	void testEquals() {
		assertEquals(Ratio.valueOf(16, 9), Ratio.valueOf(16, 9));
		assertEquals(Ratio.valueOf(1, 1), Ratio.valueOf(1, 1));
		assertNotEquals(Ratio.valueOf(4, 3), Ratio.valueOf(4, 2));
		assertNotEquals(Ratio.valueOf(4, 3), Ratio.valueOf(8, 6));
	}

	@Test
	void testCalculateWidth() {
		assertEquals(700, Ratio.TRADITIONAL_TELEVISION.calculateWidth(525));
		assertEquals(960, Ratio.TRADITIONAL_TELEVISION.calculateWidth(Size.HD720));
	}

	@Test
	void testCalculateHeight() {
		assertEquals(1080, Ratio._16_9.calculateHeight(1920));
		assertEquals(1067, Ratio._16_9.calculateWidth(Size.SVGA));
	}

	@Test
	void testApproximate_InList() {
		assertEquals(Ratio.valueOf(3, 2), Size.valueOf(3008, 1960).getRatio().approximate());
		assertEquals(Ratio.valueOf(3, 2), Size.valueOf(3008, 2000).getRatio().approximate());
	}

	@Test
	void testApproximate_create() {
		assertEquals(Ratio.valueOf(2.96), Size.valueOf(8000, 2700).getRatio().approximate());
	}

	@Test
	void testApproximate_failed_1() {
		assertThrows(IllegalArgumentException.class, () -> Ratio._16_9.approximate(1));
	}

	@Test
	void testApproximate_failed_0() {
		assertThrows(IllegalArgumentException.class, () -> Ratio._16_9.approximate(0));
	}

	@Test
	void testApproximate_failed_over1() {
		assertThrows(IllegalArgumentException.class, () -> Ratio._16_9.approximate(100));
	}

	@Test
	void testApproximate_failed_under0() {
		assertThrows(IllegalArgumentException.class, () -> Ratio._16_9.approximate( - 8));
	}

	@Test
	void testReduce() {
		assertSame(Ratio._16_9, Ratio._16_9.reduce());
		assertSame(Ratio.ISO_216, Ratio.ISO_216.reduce());
		assertEquals(Ratio.valueOf(8, 5), Ratio.COMMON_COMPUTER_SCREEN.reduce());
	}

	@Test
	void testParse_ddots() {
		assertSame(Ratio._16_9, Ratio.parse("16:9"));
		assertSame(Ratio._16_9, Ratio.parse("16/9"));
		assertEquals(0.618046D, Ratio.parse("1:1.618").toDouble(), 0.000001D);
		assertEquals(0.5D, Ratio.parse("0.5").toDouble(), 0.001D);
	}

	@Test
	void testInvert() {
		assertEquals(Ratio.valueOf(9, 16), Ratio._16_9.invert());
		assertEquals(Ratio.valueOf(0.707106D), Ratio.ISO_216.invert());
	}

	@Test
	void testInvert_failed() {
		assertThrows(ArithmeticException.class, () -> Ratio.valueOf(0, 1).invert());
	}

	@Test
	void testKeepRatio() {
		Ratio ratio = Size.valueOf(467, 700).getRatio();
		assertEquals(Ratio.valueOf(467, 700), ratio);
		Size newSize = ratio.getSizeIn(Size.valueOf(600, 600));
		assertEquals(Size.valueOf(400, 600), newSize);
		assertEquals(Ratio.valueOf(2, 3), newSize.getRatio());
	}

}
