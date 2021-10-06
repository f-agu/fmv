package org.fagu.fmv.ffmpeg.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class DeviceTestCase {

	@Test
	@Disabled
	void generator() {
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

	@Test
	void testCache() {
		assertTrue(Devices.LAVFI.isDemuxingSupported());
		assertFalse(Devices.LAVFI.isMuxingSupported());
	}

}
