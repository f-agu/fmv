package org.fagu.fmv.soft.find;

import static org.junit.jupiter.api.Assertions.assertSame;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import java.util.Collection;

import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class SoftFoundTestCase {

	@Test
	void testMultipleNull_Varargs() {
		assertSame(SoftFound.notFound(), SoftFound.multiple((SoftFound)null));
	}

	@Test
	void testMultipleNull_Collection() {
		assertSame(SoftFound.notFound(), SoftFound.multiple((Collection<SoftFound>)null));
	}

	@Test
	void testMultiple0() {
		assertSame(SoftFound.notFound(), SoftFound.multiple());
	}

	@Test
	void testMultiple1() {
		SoftFound foundBadSoft = SoftFound.notFound();
		assertSame(foundBadSoft, SoftFound.multiple(foundBadSoft));
	}

	// /**
	// *
	// */
	// @Test
	// public void testMultiple2() {
	// SoftFound foundBadSoft1 = SoftFound.foundBadSoft(new File("."), "1");
	// SoftFound foundBadSoft2 = SoftFound.foundBadVersion(new File("."), "2");
	// SoftFound multiple = SoftFound.multiple(foundBadSoft1, foundBadSoft2);
	// assertFalse(multiple.isFound());
	// assertSame(Found.MULTIPLE, multiple.getFound());
	// assertEquals("BAD_SOFT:.(1),BAD_VERSION:.(2)", multiple.toString());
	// }

}
