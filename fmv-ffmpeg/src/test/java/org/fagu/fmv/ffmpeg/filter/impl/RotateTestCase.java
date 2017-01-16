package org.fagu.fmv.ffmpeg.filter.impl;

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

import org.fagu.fmv.utils.media.Rotation;
import org.junit.Test;


/**
 * @author f.agu
 */
public class RotateTestCase {

	/**
	 * 
	 */
	public RotateTestCase() {}

	/**
	 * 
	 */
	@Test
	public void testNull() {
		Rotate rotate = Rotate.create(null);
		assertEquals("", rotate.toString());
	}

	/**
	 * 
	 */
	@Test
	public void testR0() {
		Rotate rotate = Rotate.create(Rotation.R_0);
		assertEquals("", rotate.toString());
	}

	/**
	 * 
	 */
	@Test
	public void testR90() {
		Rotate rotate = Rotate.create(Rotation.R_90);
		assertEquals("transpose=dir=clock", rotate.toString());
	}

	/**
	 * 
	 */
	@Test
	public void testR180() {
		Rotate rotate = Rotate.create(Rotation.R_180);
		assertEquals("transpose=dir=clock,transpose=dir=clock", rotate.toString());
	}

	/**
	 * 
	 */
	@Test
	public void testR270() {
		Rotate rotate = Rotate.create(Rotation.R_270);
		assertEquals("transpose=dir=cclock", rotate.toString());
	}

}
