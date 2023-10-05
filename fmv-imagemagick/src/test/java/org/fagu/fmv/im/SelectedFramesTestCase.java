package org.fagu.fmv.im;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 5 oct. 2023 16:02:22
 */
class SelectedFramesTestCase {

	@Test
	void testAll() {
		assertEquals("", SelectedFrames.all().toString());
		assertEquals("[0]", SelectedFrames.first().toString());
		assertEquals("[4]", SelectedFrames.one(4).toString());
		assertEquals("[4-7]", SelectedFrames.sequence(4, 7).toString());
		assertEquals("[4,7,9]", SelectedFrames.of(4, 7, 9).toString());
	}
}
