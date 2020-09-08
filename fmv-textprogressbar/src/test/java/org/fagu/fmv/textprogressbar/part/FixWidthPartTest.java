package org.fagu.fmv.textprogressbar.part;

/*-
 * #%L
 * fmv-textprogressbar
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

import org.junit.Test;


/**
 * @author fagu
 */
public class FixWidthPartTest {

	@Test
	public void testTextNull() {
		FixWidthPart part = FixWidthPart.leftPad(s -> null, 4);
		assertEquals("    ", part.getWith(null));
	}

	@Test
	public void testTextEmpty() {
		FixWidthPart part = FixWidthPart.leftPad(s -> "", 4);
		assertEquals("    ", part.getWith(null));
	}

	@Test
	public void testText_left_aa() {
		FixWidthPart part = FixWidthPart.leftPad(s -> "aa", 4);
		assertEquals("  aa", part.getWith(null));
	}

	@Test
	public void testText_right_aa() {
		FixWidthPart part = FixWidthPart.rightPad(s -> "aa", 4);
		assertEquals("aa  ", part.getWith(null));
	}

	@Test
	public void testText_middle_aa() {
		FixWidthPart part = FixWidthPart.centerPad(s -> "aa", 4);
		assertEquals(" aa ", part.getWith(null));
	}

	@Test
	public void testText_middle_aab() {
		FixWidthPart part = FixWidthPart.centerPad(s -> "aab", 4);
		assertEquals("aab ", part.getWith(null));
	}

	@Test
	public void testText_middle_aabbcc() {
		FixWidthPart part = FixWidthPart.centerPad(s -> "aabbcc", 4);
		assertEquals("aabb", part.getWith(null));
	}

}
