package org.fagu.fmv.image;

/*-
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import org.fagu.fmv.image.exif.Flash;
import org.junit.Test;


/**
 * @author f.agu
 * @created 7 nov. 2019 14:03:16
 */
public class FlashTestCase {

	@Test
	public void testComment() {
		assertComment(0x0, "No Flash");
		assertComment(0x1, "Fired");
		assertComment(0x5, "Fired, Return not detected");
		assertComment(0x7, "Fired, Return detected");
		assertComment(0x8, "On, Did not fire");
		assertComment(0x9, "On, Fired");
		assertComment(0xd, "On, Fired, Return not detected");
		assertComment(0xf, "On, Fired, Return detected");
		assertComment(0x10, "Off, Did not fire");
		assertComment(0x14, "Off, Did not fire, Return not detected");
		assertComment(0x18, "Auto, Did not fire");
		assertComment(0x19, "Auto, Fired");
		assertComment(0x1d, "Auto, Fired, Return not detected");
		assertComment(0x1f, "Auto, Fired, Return detected");
		assertComment(0x20, "Did not fire, No flash function");
		assertComment(0x30, "Off, Did not fire, No flash function");
		assertComment(0x41, "Fired, Red-eye reduction");
		assertComment(0x45, "Fired, Red-eye reduction, Return not detected");
		assertComment(0x47, "Fired, Red-eye reduction, Return detected");
		assertComment(0x49, "On, Fired, Red-eye reduction");
		assertComment(0x4d, "On, Fired, Red-eye reduction, Return not detected");
		assertComment(0x4f, "On, Fired, Red-eye reduction, Return detected");
		assertComment(0x50, "Off, Did not fire, Red-eye reduction");
		assertComment(0x58, "Auto, Did not fire, Red-eye reduction");
		assertComment(0x59, "Auto, Fired, Red-eye reduction");
		assertComment(0x5d, "Auto, Fired, Red-eye reduction, Return not detected");
		assertComment(0x5f, "Auto, Fired, Red-eye reduction, Return detected");
	}

	private void assertComment(int flashValue, String expectedText) {
		Flash flash = Flash.valueOf(flashValue);
		// System.out.println("====== " + StringUtils.rightPad(Integer.toString(flashValue), 2)
		// + " 0x" + Integer.toHexString(flashValue)
		// + " " + StringUtils.leftPad(Integer.toBinaryString(flashValue), 8, '0') + 'b');
		// System.out.println(" Fired: " + flash.isFired());
		// System.out.println(" Return: " + flash.getReturnedLight());
		// System.out.println(" Mode: " + flash.getMode());
		// System.out.println(" Function: " + flash.isNoFlashFunction());
		// System.out.println(" Red-eye: " + flash.isRedEyeReduction());
		assertEquals(flash.toString(), expectedText, flash.getComment());
	}

}
