package org.fagu.fmv.soft.exec;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;


/**
 * @author f.agu
 * @created 15 avr. 2019 10:05:54
 */
public class CommandLineUtilsTestCase {

	@Test
	public void testPassword() {
		assertEquals("a b -PASSWORD *******", CommandLineUtils.toLine(Arrays.asList("a", "b", "-PASSWORD", "not-visible")));
	}

}
