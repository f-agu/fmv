package org.fagu.fmv.soft.java;

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
 * @author Oodrive
 * @author f.agu
 * @created 24 mai 2017 12:56:12
 */
public class JavaSpawnTest {

	/**
	 * 
	 */
	public JavaSpawnTest() {}

	/**
	 * @throws IOException
	 */
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

	/**
	 * @throws IOException
	 */
	@Test
	public void testExitUndefined() throws IOException {
		try {
			javaSimpleMain(999).execute();
		} catch(ExecuteException e) {
			assertTrue(e.getMessage().startsWith("Process exited with an error: 999 (Exit value: 999)"));
		}
	}

	/**
	 * @throws IOException
	 */
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

	/**
	 * @throws IOException
	 */
	@Test
	public void testOutputToOutputStream() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		javaSimpleMain(0)
				.output(os)
				.execute();
		assertEquals("out" + System.getProperty("line.separator"), os.toString());
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testOutputToOutputStreamAndErrToReadLine() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AtomicReference<String> err = new AtomicReference<>();
		javaSimpleMain(0)
				.output(out)
				.addErrReadLine(err::set)
				.execute();
		assertEquals("out" + System.getProperty("line.separator"), out.toString());
		assertEquals("err", err.get());
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testOutputReadLineToAndErrToOutputStream() throws IOException {
		AtomicReference<String> out = new AtomicReference<>();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		javaSimpleMain(0)
				.addOutReadLine(out::set)
				.err(err)
				.execute();
		assertEquals("out", out.get());
		assertEquals("err" + System.getProperty("line.separator"), err.toString());
	}

	/**
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void testCommon() throws IOException {
		List<String> common = new ArrayList<>();
		javaSimpleMain(0)
				.addCommonReadLine(common::add)
				.execute();
		System.out.println(common);
	}

	/**
	 * @throws IOException
	 */
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
		assertEquals(2, err.size());
		assertEquals("Exception in thread \"main\" java.lang.Exception", err.get(0));
		assertEquals("\tat org.fagu.fmv.soft.java.ExceptionMain.main(ExceptionMain.java:43)", err.get(1));
	}

	/**
	 * @throws IOException
	 */
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

	/**
	 * @throws IOException
	 */
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

	/**
	 * @throws IOException
	 */
	@Test
	public void testException_out_size0() throws IOException {
		CountingOutputStream outCountingOutputStream = new CountingOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);
		List<String> err = new ArrayList<>();

		javaExceptionMain("out", 0)
				.output(outCountingOutputStream)
				.addErrReadLine(err::add)
				.exitValues(1)
				.execute();

		assertEquals(0, outCountingOutputStream.getByteCount());
		assertEquals(2, err.size());
		assertEquals("xException in thread \"main\" java.lang.Exception", err.get(0)); // start with 'x'
		assertEquals("\tat org.fagu.fmv.soft.java.ExceptionMain.main(ExceptionMain.java:43)", err.get(1));
	}

	/**
	 * @throws IOException
	 */
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
		assertEquals("\tat org.fagu.fmv.soft.java.ExceptionMain.main(ExceptionMain.java:43)", err.get(1));
	}

	/**
	 * @throws IOException
	 */
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
		assertEquals("\tat org.fagu.fmv.soft.java.ExceptionMain.main(ExceptionMain.java:43)", err.get(1));
	}

	// ***************************************

	/**
	 * @param exitValue
	 * @return
	 */
	private SoftExecutor javaSimpleMain(int exitValue) {
		return javaMain(SimpleMain.class, Integer.toString(exitValue));
	}

	/**
	 * @param writeType
	 * @param lenght
	 * @return
	 */
	private SoftExecutor javaExceptionMain(String writeType, long lenght) {
		return javaMain(ExceptionMain.class, writeType, Long.toString(lenght));
	}

	/**
	 * @param cls
	 * @param args
	 * @return
	 */
	private SoftExecutor javaMain(Class<?> cls, String... args) {
		List<String> params = new ArrayList<>();
		params.add("-cp");
		params.add(System.getProperty("java.class.path"));
		params.add(cls.getName());
		params.addAll(Arrays.asList(args));
		return Java.search().withParameters(params);
	}

}
