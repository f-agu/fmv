package org.fagu.fmv.textprogressbar.part;

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
