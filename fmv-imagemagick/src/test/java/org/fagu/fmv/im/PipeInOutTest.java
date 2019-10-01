package org.fagu.fmv.im;

/*-
 * #%L
 * fmv-imagemagick
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.im.soft.Convert;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.utils.StringUtils;
import org.junit.Test;


/**
 * @author f.agu
 */
public class PipeInOutTest {

	private static final String EXPECTED_SHA1 = "a9d6644b49a3f9c660a702f46bd44640967999ae";

	@Test
	public void testPipe_no() throws Exception {
		File srcFile = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File destFile = new File(srcFile.getPath() + ".piped." + FilenameUtils.getExtension(srcFile.getName()));

		try {
			Soft convertSoft = Convert.search();
			IMOperation op = new IMOperation();
			op.image(srcFile)
					.autoOrient()
					.quality(60D)
					.image(destFile);
			convertSoft.withParameters(op.toList())
					// .logCommandLine(System.out::println)
					.execute();
			assertEquals(EXPECTED_SHA1, sha1Of(destFile));
		} finally {
			srcFile.delete();
			destFile.delete();
		}
	}

	@Test
	public void testPipeInOut() throws Exception {
		File srcFile = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File destFile = new File(srcFile.getPath() + ".piped." + FilenameUtils.getExtension(srcFile.getName()));

		try {
			Soft convertSoft = Convert.search();
			IMOperation op = new IMOperation();
			op.image("-") // <-------- means standard input
					.autoOrient()
					.quality(60D)
					.image("jpg:-"); // <-------- means standard output
			try (InputStream inputStream = new FileInputStream(srcFile);
					OutputStream outputStream = new FileOutputStream(destFile)) {
				convertSoft.withParameters(op.toList())
						.logCommandLine(System.out::println)
						.input(inputStream) // <--------- in
						.output(outputStream) // <--------- out
						.execute();
			}
			assertEquals(EXPECTED_SHA1, sha1Of(destFile));
		} finally {
			srcFile.delete();
			destFile.delete();
		}
	}

	@Test
	public void testPipeIn() throws Exception {
		File srcFile = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File destFile = new File(srcFile.getPath() + ".piped." + FilenameUtils.getExtension(srcFile.getName()));

		try {
			Soft convertSoft = Convert.search();
			IMOperation op = new IMOperation();
			op.image("-") // <-------- means standard input
					.autoOrient()
					.quality(60D)
					.image(destFile);
			try (InputStream inputStream = new FileInputStream(srcFile)) {
				convertSoft.withParameters(op.toList())
						.logCommandLine(System.out::println)
						.input(inputStream) // <--------- in
						.execute();
			}
			assertEquals(EXPECTED_SHA1, sha1Of(destFile));
		} finally {
			srcFile.delete();
			destFile.delete();
		}
	}

	@Test
	public void testPipeOut() throws Exception {
		File srcFile = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File destFile = new File(srcFile.getPath() + ".piped." + FilenameUtils.getExtension(srcFile.getName()));

		try {
			Soft convertSoft = Convert.search();
			IMOperation op = new IMOperation();
			op.image(srcFile)
					.autoOrient()
					.quality(60D)
					.image("jpg:-"); // <-------- means standard output
			try (OutputStream outputStream = new FileOutputStream(destFile)) {
				convertSoft.withParameters(op.toList())
						// .logCommandLine(System.out::println)
						.output(outputStream) // <--------- out
						.execute();
			}
			assertEquals(EXPECTED_SHA1, sha1Of(destFile));
		} finally {
			srcFile.delete();
			destFile.delete();
		}
	}

	// *********************************************************

	private static String sha1Of(File file) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		try (InputStream inputStream = new FileInputStream(file)) {
			byte[] buf = new byte[8192];
			int count = 0;
			while((count = inputStream.read(buf, 0, buf.length)) > 0) {
				digest.update(buf, 0, count);
			}
		}
		return StringUtils.toHex(digest.digest());
	}
}
