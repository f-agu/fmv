package org.fagu.fmv.ffmpeg.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import org.fagu.fmv.ffmpeg.utils.srcgen.ClassNameUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class PixelFormatTestCase {

	@Test
	@Disabled
	void generator() {
		for(PixelFormat pixelFormat : PixelFormat.available()) {
			String name = pixelFormat.getName();
			String fieldName = ClassNameUtils.fieldStatic(name);

			System.out.println("public static final PixelFormat " + fieldName + " = new PixelFormat(\"" + name + "\");");
		}
	}

	@Test
	void testCache() {
		assertEquals(12, PixelFormat.YUV420P.getBitsPerPixel());
		assertEquals(3, PixelFormat.YUV420P.getNbComponents());
		assertTrue(PixelFormat.YUV420P.isSupportedInput());
		assertTrue(PixelFormat.YUV420P.isSupportedOutput());
		assertFalse(PixelFormat.YUV420P.isBitstream());
		assertFalse(PixelFormat.YUV420P.isHardwareAccelerated());
		assertFalse(PixelFormat.YUV420P.isPaletted());
		assertTrue(PixelFormat.MONOW.isSupportedInput());
		assertFalse(PixelFormat.PAL8.isSupportedOutput());
	}
}
