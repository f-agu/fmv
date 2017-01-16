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


import org.fagu.fmv.ffmpeg.utils.srcgen.ClassNameUtils;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class BitStreamFilterTestCase {

	/**
	 * 
	 */
	public BitStreamFilterTestCase() {}

	/**
	 * 
	 */
	@Test
	@Ignore
	public void generator() {
		for(BitStreamFilter bitStreamFilter : BitStreamFilter.available()) {
			String name = bitStreamFilter.getName();
			String fieldName = ClassNameUtils.fieldStatic(name);

			System.out.println("public static final BitStreamFilter " + fieldName + " = new BitStreamFilter(\"" + name + "\");");
		}
	}

	/**
	 * 
	 */
	// @Test
	// public void testCache() {
	// }

}
