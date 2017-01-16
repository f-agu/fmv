package org.fagu.fmv.ffmpeg.flags;

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
import static org.junit.Assert.assertSame;

import org.junit.Test;


/**
 * @author f.agu
 */
public class FlagsTestCase {

	/**
	 * 
	 */
	public FlagsTestCase() {}

	/**
	 * 
	 */
	@Test
	public void testToString() {
		assertEquals("+direct", Avioflags.DIRECT.toString());
	}

	/**
	 * 
	 */
	@Test
	public void testInverse1ToString() {
		assertEquals("-direct", Avioflags.DIRECT.inverse().toString());
	}

	/**
	 * 
	 */
	@Test
	public void testInverse_more() {
		assertSame(Avioflags.DIRECT, Avioflags.DIRECT.inverse().inverse());
		assertEquals("-direct", Avioflags.DIRECT.inverse().inverse().inverse().toString());
		assertSame(Avioflags.DIRECT, Avioflags.DIRECT.inverse().inverse().inverse().inverse());
	}

}
