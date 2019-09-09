package org.fagu.fmv.soft.java;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.fagu.fmv.soft.Soft;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 * @created 5 sept. 2019 12:02:47
 */
public class JavaTest {

	@Test
	@Ignore
	public void testAll() {
		Java.search().getFounds()
				.forEach(sf -> System.out.println(sf.getFile()));
	}

	@Test
	@Ignore
	public void testCurrent() {
		System.out.println(Java.current().getFile());
	}

	@Test
	public void testInput() throws Exception {
		try (InputStream inputStream = new ByteArrayInputStream(new byte[1_000_000]);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			Soft.withExecFile("ping")
					.withParameters("-n", "1", "localhost")
					.output(byteArrayOutputStream)
					.err(byteArrayOutputStream)
					.execute();
			assertTrue(byteArrayOutputStream.size() > 0);
			System.out.println(new String(byteArrayOutputStream.toByteArray()));
		}
	}

}
