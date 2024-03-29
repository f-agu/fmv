package org.fagu.fmv.ffmpeg.filter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import org.fagu.fmv.utils.media.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class ScaleTestCase {

	private Scale scale;

	@BeforeEach
	void setUp() {
		scale = new Scale();
	}

	@Test
	void testFitToBox() {
		scale.set(Size.HD720, ScaleMode.fitToBox());
		assertEquals("scale=w='1280':h='720'", scale.toString());
	}

	@Test
	void testFitToBoxKeepAspectRatio() {
		scale.set(Size.HD720, ScaleMode.fitToBoxKeepAspectRatio());
		assertEquals("scale=w='if(gt(dar,1280/720),1280,trunc(oh*dar/2)*2)':h='if(gt(dar,1280/720),trunc(ow/dar/2)*2,720)'", scale.toString());
	}

	@Test
	void testFitToHeight() {
		scale.set(Size.HD720, ScaleMode.fitToHeight());
		assertEquals("scale=w='trunc(oh*dar/2)*2':h='720'", scale.toString());
	}

	@Test
	void testFitToWidth() {
		scale.set(Size.HD720, ScaleMode.fitToWidth());
		assertEquals("scale=w='1280':h='trunc(ow/dar/2)*2'", scale.toString());
	}

}
