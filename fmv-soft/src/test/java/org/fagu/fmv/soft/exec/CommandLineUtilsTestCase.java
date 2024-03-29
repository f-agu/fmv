package org.fagu.fmv.soft.exec;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*-
 * #%L
 * fmv-soft
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

import java.util.Arrays;

import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 15 avr. 2019 10:05:54
 */
class CommandLineUtilsTestCase {

	@Test
	void testPassword() {
		assertEquals("a b -PASSWORD *******", CommandLineUtils.toLine(Arrays.asList("a", "b", "-PASSWORD", "not-visible")));
	}

}
