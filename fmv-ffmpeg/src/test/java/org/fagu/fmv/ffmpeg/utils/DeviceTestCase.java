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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class DeviceTestCase {

	/**
	 * 
	 */
	public DeviceTestCase() {}

	/**
	 * 
	 */
	@Test
	@Ignore
	public void generator() {
		for(Devices device : Devices.available()) {
			String name = device.getName();
			String fieldName = name.toUpperCase();
			if(Character.isDigit(fieldName.charAt(0))) {
				fieldName = '_' + fieldName;
			}
			System.out.println("// " + device.getDescription());
			System.out.println("public static final Device " + fieldName + " = new Device(\"" + name + "\");");
		}
	}

	/**
	 * 
	 */
	@Test
	public void testCache() {
		assertTrue(Devices.LAVFI.isDemuxingSupported());
		assertFalse(Devices.LAVFI.isMuxingSupported());
	}

}
