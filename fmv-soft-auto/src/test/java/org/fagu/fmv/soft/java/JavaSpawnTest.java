package org.fagu.fmv.soft.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.exec.ExecuteException;
import org.fagu.fmv.soft.SoftExecutor;
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
			int exitValue = javaMyMain(i)
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
			javaMyMain(999).execute();
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
		javaMyMain(0)
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
		javaMyMain(0)
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
		javaMyMain(0)
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
		javaMyMain(0)
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
	public void testCommon() throws IOException {
		List<String> common = new ArrayList<>();
		javaMyMain(0)
				.addCommonReadLine(common::add)
				.execute();
		System.out.println(common);
	}

	// ***************************************

	/**
	 * @param exitValue
	 * @return
	 */
	private SoftExecutor javaMyMain(int exitValue) {
		return Java.search()
				.withParameters("-cp", System.getProperty("java.class.path"), MyMain.class.getName(), Integer.toString(exitValue));
	}

}
