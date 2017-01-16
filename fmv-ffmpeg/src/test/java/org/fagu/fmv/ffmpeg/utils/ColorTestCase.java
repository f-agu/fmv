package org.fagu.fmv.ffmpeg.utils;

/*
 * #%L
 * fmv-ffmpeg
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

import org.junit.Test;


/**
 * @author f.agu
 */
public class ColorTestCase {

	/**
	 * 
	 */
	public ColorTestCase() {}

	/**
	 * 
	 */
	@Test
	public void testValueOf() {
		assertColor(Color.valueOf("#321546"), null, "321546", null);
		assertColor(Color.valueOf("#aaffBB"), null, "AAFFBB", null);
		assertColor(Color.valueOf("0xFf6347"), "Tomato", "FF6347", null);
	}

	/**
	 * 
	 */
	@Test
	public void testValueOfOpacity() {
		assertColor(Color.valueOf("#321546@FF"), null, "321546", "FF");
		assertColor(Color.valueOf("#aaffBB@0a"), null, "AAFFBB", "0A");
		assertColor(Color.valueOf("yellow@0.2"), "Yellow", "FFFF00", "0.2");
	}

	// ****************************************************

	/**
	 * @param color
	 * @param expectedName
	 * @param expectedSequence
	 * @param expectedOpacity
	 */
	private void assertColor(Color color, String expectedName, String expectedSequence, String expectedOpacity) {
		assertEquals(expectedName, color.getName());
		assertEquals(expectedSequence, color.getSequence());
		assertEquals(expectedOpacity, color.getOpacity());
	}

}
