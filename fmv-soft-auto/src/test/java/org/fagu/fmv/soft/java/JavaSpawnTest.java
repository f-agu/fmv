package org.fagu.fmv.soft.java;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

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
			int exitValue = Java.search()
					.withParameters("-cp", System.getProperty("java.class.path"), MyMain.class.getName(), Integer.toString(i))
					// .logCommandLine(System.out::println)
					.customizeExecutor(e -> e.setExitValues(new int[] {fi}))
					.execute()
					.getExitValue();
			assertEquals(i, exitValue);
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testReadLineOutputErr() throws IOException {
		AtomicReference<String> out = new AtomicReference<>();
		AtomicReference<String> err = new AtomicReference<>();
		Java.search()
				.withParameters("-cp", System.getProperty("java.class.path"), MyMain.class.getName(), "0")
				// .logCommandLine(System.out::println)
				.customizeExecutor(e -> e.setExitValues(new int[] {0}))
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
		Java.search()
				.withParameters("-cp", System.getProperty("java.class.path"), MyMain.class.getName(), "0")
				// .logCommandLine(System.out::println)
				.customizeExecutor(e -> e.setExitValues(new int[] {0}))
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
		Java.search()
				.withParameters("-cp", System.getProperty("java.class.path"), MyMain.class.getName(), "0")
				// .logCommandLine(System.out::println)
				.customizeExecutor(e -> e.setExitValues(new int[] {0}))
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
		Java.search()
				.withParameters("-cp", System.getProperty("java.class.path"), MyMain.class.getName(), "0")
				// .logCommandLine(System.out::println)
				.customizeExecutor(e -> e.setExitValues(new int[] {0}))
				.addOutReadLine(out::set)
				.err(err)
				.execute();
		assertEquals("out", out.get());
		assertEquals("err" + System.getProperty("line.separator"), err.toString());
	}

}
