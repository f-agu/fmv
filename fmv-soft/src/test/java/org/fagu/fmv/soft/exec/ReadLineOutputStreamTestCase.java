package org.fagu.fmv.soft.exec;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;


/**
 * @author f.agu
 */
public class ReadLineOutputStreamTestCase {

	/**
	 * 
	 */
	public ReadLineOutputStreamTestCase() {}

	/**
	 * @throws IOException
	 */
	@Test
	public void testEmpty() throws IOException {
		try (ReadLineOutputStream outputStream = new ReadLineOutputStream(l -> fail())) {
			// nothing
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void test1Line() throws IOException {
		List<String> expectedList = new ArrayList<>();
		expectedList.add("hello world");
		Iterator<String> expectedIterator = expectedList.iterator();
		try (ReadLineOutputStream outputStream = new ReadLineOutputStream(l -> assertEquals(expectedIterator.next(), l))) {
			outputStream.write("hello world".getBytes());
		}
		assertFalse(expectedIterator.hasNext());
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void test2Lines_cr() throws IOException {
		List<String> expectedList = new ArrayList<>();
		expectedList.add("hello world");
		expectedList.add("2nd line");
		Iterator<String> expectedIterator = expectedList.iterator();
		try (ReadLineOutputStream outputStream = new ReadLineOutputStream(l -> assertEquals(expectedIterator.next(), l))) {
			outputStream.write("hello world\r".getBytes());
			outputStream.write("2nd line".getBytes());
		}
		assertFalse(expectedIterator.hasNext());
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void test2Lines_crlf() throws IOException {
		List<String> expectedList = new ArrayList<>();
		expectedList.add("hello world");
		expectedList.add("2nd line");
		Iterator<String> expectedIterator = expectedList.iterator();
		try (ReadLineOutputStream outputStream = new ReadLineOutputStream(l -> assertEquals(expectedIterator.next(), l))) {
			outputStream.write("hello world\r\n".getBytes());
			outputStream.write("2nd line".getBytes());
		}
		assertFalse(expectedIterator.hasNext());
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void test2Lines_lf() throws IOException {
		List<String> expectedList = new ArrayList<>();
		expectedList.add("hello world");
		expectedList.add("2nd line");
		Iterator<String> expectedIterator = expectedList.iterator();
		try (ReadLineOutputStream outputStream = new ReadLineOutputStream(l -> assertEquals(expectedIterator.next(), l))) {
			outputStream.write("hello world\n".getBytes());
			outputStream.write("2nd line".getBytes());
		}
		assertFalse(expectedIterator.hasNext());
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testEmptyLines() throws IOException {
		List<String> expectedList = new ArrayList<>();
		expectedList.add("");
		expectedList.add("");
		expectedList.add("");
		expectedList.add("");
		expectedList.add("");
		Iterator<String> expectedIterator = expectedList.iterator();
		try (ReadLineOutputStream outputStream = new ReadLineOutputStream(l -> assertEquals(expectedIterator.next(), l))) {
			outputStream.write("\n\r\n\n\r\n\n".getBytes());
		}
		assertFalse(expectedIterator.hasNext());
	}

}
