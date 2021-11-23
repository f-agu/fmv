package org.fagu.fmv.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/*
 * #%L
 * fmv-utils
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


/**
 * @author f.agu
 */
class MathTestCase {

	@Test
	void testGreatestCommonDivisor_ok() {
		assertEquals(1, Math.greatestCommonDivisor(1, 2));
		assertEquals(160, Math.greatestCommonDivisor(640, 480));
		assertEquals(80, Math.greatestCommonDivisor(1280, 720));
	}

	@Test
	void testGreatestCommonDivisor_nok() {
		assertThrows(IllegalArgumentException.class, () -> Math.greatestCommonDivisor( - 1, - 2));
	}

}
