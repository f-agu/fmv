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
public class ChannelLayoutTestCase {

	/**
	 * 
	 */
	public ChannelLayoutTestCase() {}

	/**
	 * 
	 */
	@Test
	@Ignore
	public void generator() {
		System.out.println("/**");
		System.out.println(" * Same channel layout");
		System.out.println(" */");
		System.out.println("public static final ChannelLayout SAME = new ChannelLayout(\"same\");");

		for(ChannelLayout channelLayout : ChannelLayout.available()) {
			String name = channelLayout.getName();
			String fieldName = ClassNameUtils.fieldStatic(name);

			System.out.println("/**");
			System.out.println(" * " + channelLayout.getDescription());
			System.out.println(" */");
			System.out.println("public static final ChannelLayout " + fieldName + " = new ChannelLayout(\"" + name + "\");");
		}
	}

	/**
	 * 
	 */
	// @Test
	// public void testCache() {
	// }

}
