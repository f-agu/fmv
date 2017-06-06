package org.fagu.fmv.utils.time;

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

import org.fagu.fmv.utils.time.Duration;
import org.junit.Test;


/**
 * @author f.agu
 */
public class DurationTestCase {

	/**
	 * 
	 */
	public DurationTestCase() {}

	/**
	 * 
	 */
	@Test
	public void testParse() {
		assertEquals(Duration.valueOf(0), Duration.parse("0"));
		assertEquals(Duration.valueOf(0), Duration.parse("0.0"));
		assertEquals(Duration.valueOf(1.00001), Duration.parse("1.00001"));
		assertEquals(Duration.valueOf(27.767000), Duration.parse("27.767000"));
		assertEquals(new Duration(0, 58, 59.988), Duration.parse("58:59.988"));
		assertEquals(new Duration(81, 58, 59.988), Duration.parse("81:58:59.988"));
	}

	/**
	 * 
	 */
	@Test
	public void testParseWithSign() {
		assertEquals(Duration.valueOf(0), Duration.parse("+0"));
		assertEquals(Duration.valueOf(0), Duration.parse("-0"));
		assertEquals(Duration.valueOf(0), Duration.parse("+0.0"));
		assertEquals(Duration.valueOf(0), Duration.parse("-0.0"));
		assertEquals(Duration.valueOf(1.00001), Duration.parse("+1.00001"));
		assertEquals(Duration.valueOf( - 1.00001), Duration.parse("-1.00001"));
		assertEquals(Duration.valueOf(27.767000), Duration.parse("+27.767000"));
		assertEquals(Duration.valueOf( - 27.767000), Duration.parse("-27.767000"));
		assertEquals(new Duration(0, 58, 59.988, false), Duration.parse("+58:59.988"));
		assertEquals(new Duration(0, 58, 59.988, true), Duration.parse("-58:59.988"));
		assertEquals(new Duration(81, 58, 59.988, false), Duration.parse("+81:58:59.988"));
		assertEquals(new Duration(81, 58, 59.988, true), Duration.parse("-81:58:59.988"));
	}

	/**
	 * 
	 */
	@Test
	public void testToSeconds() {
		// assertEquals(0, Duration.valueOf(0).toSeconds(), 0.01D);
		// assertEquals(1.23, Duration.valueOf(1.23).toSeconds(), 0.01D);
		assertEquals( - 9.87, Duration.valueOf( - 9.87).toSeconds(), 0.01D);
	}

	/**
	 * 
	 */
	@Test
	public void testUnlimited() {
		Duration unlimited = Duration.unlimited();
		System.out.println(unlimited);
	}

}
