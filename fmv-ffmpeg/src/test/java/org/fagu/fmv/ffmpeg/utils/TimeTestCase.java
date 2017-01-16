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
public class TimeTestCase {

	/**
	 * 
	 */
	public TimeTestCase() {}

	/**
	 * 
	 */
	@Test
	public void testSecondsInt_ToString() {
		assertEquals("00:00:00.000", Time.valueOf(0).toString());
		assertEquals("00:00:01.000", Time.valueOf(1).toString());
		assertEquals("00:00:59.000", Time.valueOf(59).toString());
		assertEquals("00:01:00.000", Time.valueOf(60).toString());
		assertEquals("00:01:01.000", Time.valueOf(61).toString());
		assertEquals("00:02:03.000", Time.valueOf(123).toString());
		assertEquals("00:59:59.000", Time.valueOf(60 * 60 - 1).toString());
		assertEquals("01:00:00.000", Time.valueOf(60 * 60).toString());
		assertEquals("01:00:01.000", Time.valueOf(60 * 60 + 1).toString());
		assertEquals("01:59:53.000", Time.valueOf(2 * 60 * 60 - 7).toString());
		assertEquals("56:49:02.000", Time.valueOf(55 * 60 * 60 + 6542).toString());
		assertEquals("596523:14:07.000", Time.valueOf(Integer.MAX_VALUE).toString());
	}

	/**
	 * 
	 */
	@Test
	public void testSecondsDouble_ToString() {
		assertEquals("00:00:00.100", Time.valueOf(0.1).toString());
		assertEquals("00:00:01.123", Time.valueOf(1.123).toString());
		assertEquals("00:59:59.988", Time.valueOf(60D * 60D - 1D + 0.98765D).toString());
	}

	/**
	 * 
	 */
	@Test
	public void testParse() {
		assertEquals(Time.valueOf(0), Time.parse("0"));
		assertEquals(Time.valueOf(0), Time.parse("0.0"));
		assertEquals(Time.valueOf(1.00001), Time.parse("1.00001"));
		assertEquals(Time.valueOf(27.767000), Time.parse("27.767000"));
		assertEquals(new Time(0, 58, 59.988), Time.parse("58:59.988"));
		assertEquals(new Time(81, 58, 59.988), Time.parse("81:58:59.988"));
	}

}
