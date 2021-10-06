package org.fagu.fmv.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import java.util.Locale;

import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class ByteSizeTestCase {

	@Test
	void test() {
		assertEquals("0 ko", ByteSize.formatSize(0));
		assertEquals("1 o", ByteSize.formatSize(1));
		assertEquals("-2 o", ByteSize.formatSize( - 2));
		assertEquals("12 o", ByteSize.formatSize(12));
		assertEquals("123 o", ByteSize.formatSize(123));
		assertEquals("1,21 ko", ByteSize.formatSize(1234, Locale.FRENCH));
		assertEquals("12,1 ko", ByteSize.formatSize(12345, Locale.FRENCH));
		assertEquals("121 ko", ByteSize.formatSize(123456, Locale.FRENCH));
		assertEquals("1,18 Mo", ByteSize.formatSize(1234567, Locale.FRENCH));
		assertEquals("11.8 MB", ByteSize.formatSize(12345678, Locale.ENGLISH));
		assertEquals("118 Mo", ByteSize.formatSize(123456789, Locale.FRENCH));
		assertEquals("1,15 Go", ByteSize.formatSize(1234567890, Locale.FRENCH));
		assertEquals("11,5 Go", ByteSize.formatSize(12345678901L, Locale.FRENCH));
		assertEquals("115 GB", ByteSize.formatSize(123456789012L, Locale.ENGLISH));
		assertEquals("1.12 TB", ByteSize.formatSize(1234567890123L, Locale.ENGLISH));
	}

}
