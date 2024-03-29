package org.fagu.fmv.ffmpeg.utils;

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

import org.fagu.fmv.ffmpeg.format.Formats;
import org.fagu.fmv.ffmpeg.utils.srcgen.ClassNameUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class FormatsTestCase {

	@Test
	@Disabled
	void generator() {
		for(Formats muxFormat : Formats.available()) {
			String name = muxFormat.getName();
			String fieldName = ClassNameUtils.fieldStatic(name);

			System.out.println("/**");
			System.out.println(" * " + muxFormat.getDescription());
			System.out.println(" */");
			System.out.println("public static final Formats " + fieldName + " = new Formats(\"" + name + "\");");
		}
	}

	@Test
	void testCache() {
		assertTrue(Formats.MP4.isDemuxingSupported());
		assertTrue(Formats.MP4.isMuxingSupported());
		assertFalse(Formats.MP2.isDemuxingSupported());
		assertTrue(Formats.OGG.isMuxingSupported());
	}

}
