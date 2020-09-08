package org.fagu.fmv.soft.java;

/*-
 * #%L
 * fmv-soft-auto
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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.fagu.fmv.soft.SoftExecutor;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 * @created 24 mai 2017 12:56:12
 */
public class JavaSpawnTest {

	@Test
	public void testExit0to3() throws IOException {
		// new TreeMap<>(System.getProperties()).forEach((k, v) -> System.out.println(k + " = " + v));
		for(int i = 0; i < 4; ++i) {
			final int fi = i;
			int exitValue = javaSimpleMain(i)
					.exitValues(new int[] {fi})
					.execute()
					.getExitValue();
			assertEquals(i, exitValue);
		}
	}

	@Test
	public void testExitUndefined() throws IOException {
		try {
			javaSimpleMain(999).execute();
		} catch(ExecuteException e) {
			assertTrue(e.getMessage().startsWith("Process exited with an error: 999 (Exit value: 999)"));
		}
	}

	@Test
	public void testReadLineOutputErr() throws IOException {
		AtomicReference<String> out = new AtomicReference<>();
		AtomicReference<String> err = new AtomicReference<>();
		javaSimpleMain(0)
				.addOutReadLine(out::set)
				.addErrReadLine(err::set)
				.execute();
		assertEquals("out", out.get());
		assertEquals("err", err.get());
	}

	@Test
	public void testOutputToOutputStream() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		javaSimpleMain(0)
				.output(os)
				.execute();
		assertEquals("out" + System.getProperty("line.separator"), os.toString());
	}

	@Test
	public void testOutputToOutputStreamAndErrToReadLine() throws IOException {
		AtomicReference<String> err = new AtomicReference<>();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			javaSimpleMain(0)
					.output(out)
					.addErrReadLine(l -> err.compareAndSet(null, l))
					.execute();
			assertEquals("out" + System.getProperty("line.separator"), out.toString());
		}
		assertEquals("err", err.get());
	}

	@Test
	public void testOutputReadLineToAndErrToOutputStream() throws IOException {
		AtomicReference<String> out = new AtomicReference<>();
		try (ByteArrayOutputStream err = new ByteArrayOutputStream()) {
			javaSimpleMain(0)
					.addOutReadLine(l -> out.compareAndSet(null, l))
					.err(err)
					.execute();
			assertEquals("err" + System.getProperty("line.separator"), err.toString());
		}
		assertEquals("out", out.get());
	}

	@Test
	@Ignore
	public void testCommon() throws IOException {
		List<String> common = new ArrayList<>();
		javaSimpleMain(0)
				.addCommonReadLine(common::add)
				.execute();
		System.out.println(common);
	}

	@Test
	public void testException_err_size0() throws IOException {
		CountingOutputStream outCountingOutputStream = new CountingOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);
		List<String> err = new ArrayList<>();

		javaExceptionMain("err", 0)
				.output(outCountingOutputStream)
				.addErrReadLine(err::add)
				.exitValues(1)
				.execute();

		assertEquals(0, outCountingOutputStream.getByteCount());
		assertEquals(3, err.size());
		assertEquals("Exception in thread \"main\" java.lang.Exception", err.get(0));
		assertTrue(err.get(1).contains("\tat org.fagu.fmv.soft.java.ExceptionMain.main(ExceptionMain.java:"));
		assertEquals("", err.get(2));
	}

	@Test
	public void testException_err_size10() throws IOException {
		CountingOutputStream outCountingOutputStream = new CountingOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);
		CountingOutputStream errCountingOutputStream = new CountingOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);

		final int size = 10;
		javaExceptionMain("err", size)
				.output(outCountingOutputStream)
				.err(errCountingOutputStream)
				.exitValues(1)
				.execute();

		assertEquals(0, outCountingOutputStream.getByteCount());
		assertTrue(errCountingOutputStream.getByteCount() > (size + 115) && errCountingOutputStream.getByteCount() < (size + 125));
	}

	@Test
	public void testException_err_size100M() throws IOException {
		CountingOutputStream outCountingOutputStream = new CountingOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);
		CountingOutputStream errCountingOutputStream = new CountingOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);

		final int size = 100 * 1024 * 1024;
		javaExceptionMain("err", size)
				.output(outCountingOutputStream)
				.err(errCountingOutputStream)
				.exitValues(1)
				.execute();

		assertEquals(0, outCountingOutputStream.getByteCount());
		assertTrue(errCountingOutputStream.getByteCount() > (size + 115) && errCountingOutputStream.getByteCount() < (size + 125));
	}

	@Test
	public void testException_out_size0() throws IOException {
		try (CountingOutputStream outCountingOutputStream = new CountingOutputStream(NullOutputStream.NULL_OUTPUT_STREAM)) {
			List<String> err = new ArrayList<>();

			javaExceptionMain("out", 0)
					.output(outCountingOutputStream)
					.addErrReadLine(err::add)
					.exitValues(1)
					.execute();

			assertEquals(0, outCountingOutputStream.getByteCount());
			assertEquals(3, err.size());
			assertEquals("xException in thread \"main\" java.lang.Exception", err.get(0)); // start with 'x'
			assertTrue(err.get(1).contains("\tat org.fagu.fmv.soft.java.ExceptionMain.main(ExceptionMain.java:"));
			assertEquals("", err.get(2));
		}
	}

	@Test
	public void testException_out_size10() throws IOException {
		CountingOutputStream outCountingOutputStream = new CountingOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);
		List<String> err = new ArrayList<>();

		final int size = 10;
		javaExceptionMain("out", size)
				.output(outCountingOutputStream)
				.addErrReadLine(err::add)
				.exitValues(1)
				.execute();

		assertEquals(size, outCountingOutputStream.getByteCount());
		assertEquals("xException in thread \"main\" java.lang.Exception", err.get(0)); // start with 'x'
		assertTrue(err.get(1).contains("\tat org.fagu.fmv.soft.java.ExceptionMain.main(ExceptionMain.java:"));
	}

	@Test
	public void testException_out_size100M() throws IOException {
		CountingOutputStream outCountingOutputStream = new CountingOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);
		List<String> err = new ArrayList<>();

		final int size = 100 * 1024 * 1024;
		javaExceptionMain("out", size)
				.output(outCountingOutputStream)
				.addErrReadLine(err::add)
				.exitValues(1)
				.execute();

		assertEquals(size, outCountingOutputStream.getByteCount());
		assertEquals("xException in thread \"main\" java.lang.Exception", err.get(0)); // start with 'x'
		assertTrue(err.get(1).contains("\tat org.fagu.fmv.soft.java.ExceptionMain.main(ExceptionMain.java:"));
	}

	// ***************************************

	private SoftExecutor javaSimpleMain(int exitValue) {
		return javaMain(SimpleMain.class, Integer.toString(exitValue));
	}

	private SoftExecutor javaExceptionMain(String writeType, long lenght) {
		return javaMain(ExceptionMain.class, writeType, Long.toString(lenght));
	}

	private SoftExecutor javaMain(Class<?> cls, String... args) {
		List<String> params = new ArrayList<>();
		params.add("-cp");
		params.add(System.getProperty("java.class.path"));
		params.add(cls.getName());
		params.addAll(Arrays.asList(args));
		return Java.search().withParameters(params);
	}

}
