package org.fagu.fmv.soft.exec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.exec.CommandLine;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 14 avr. 2019 16:26:58
 */
class CommandLineToStringTestCase {

	@Test
	void testWith_CL_null() {
		assertThrows(NullPointerException.class, () -> CommandLineToString.with((CommandLine)null));
	}

	@Test
	void testWith_Array_null() {
		assertThrows(NullPointerException.class, () -> CommandLineToString.with((String[])null));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	void testWith_Collection_null() {
		assertThrows(NullPointerException.class, () -> CommandLineToString.with((Collection)null));
	}

	@Test
	void testWith_Array_empty() {
		assertEquals("", CommandLineToString.with(new String[0]).build().toString());
	}

	@Test
	void testWith_Collection_empty() {
		assertEquals("", CommandLineToString.with(Collections.emptyList()).build().toString());
	}

	@Test
	void test1() {
		assertEquals("a", CommandLineToString.with(Arrays.asList("a")).build().toString());
	}

	@Test
	void test2() {
		assertEquals("a b", CommandLineToString.with(Arrays.asList("a", "b")).build().toString());
	}

	@Test
	void test6() {
		assertEquals("a b c d e f", CommandLineToString.with(Arrays.asList("a", "b", "c", "d", "e", "f")).build().toString());
	}

	@Test
	void testSpecialChar_space() {
		assertEquals("\"a A\" \"b B\" \"c C\"", CommandLineToString.with(Arrays.asList("a A", "b B", "c C")).build().toString());
	}

	@Test
	void testSpecialChar_multi() {
		assertEquals("\"a=A\" \"b'B\" c\"C", CommandLineToString.with(Arrays.asList("a=A", "b'B", "c\"C")).build().toString());
	}

	@Test
	void testReplace3_me_replace() {
		CommandLineToString commandLineToString = CommandLineToString.with(Arrays.asList("bin", "password", "end"))
				.whenArg().equalsTo("password").replaceBy("XXX")
				.build();

		assertEquals("bin XXX end", commandLineToString.toString());
	}

	@Test
	void testReplace3_me_hide() {
		CommandLineToString commandLineToString = CommandLineToString.with(Arrays.asList("bin", "password", "end"))
				.whenArg().equalsTo("password").hide()
				.build();

		assertEquals("bin ******* end", commandLineToString.toString());
	}

	@Test
	void testReplace3_next_replace() {
		CommandLineToString commandLineToString = CommandLineToString.with(Arrays.asList("bin", "-password", "end"))
				.whenArg().equalsTo("-password").replaceNextBy("XXX")
				.build();

		assertEquals("bin -password XXX", commandLineToString.toString());
	}

	@Test
	void testReplace3_next_hide() {
		CommandLineToString commandLineToString = CommandLineToString.with(Arrays.asList("bin", "-password", "end"))
				.whenArg().equalsTo("-password").hideNext()
				.build();

		assertEquals("bin -password *******", commandLineToString.toString());
	}

}
