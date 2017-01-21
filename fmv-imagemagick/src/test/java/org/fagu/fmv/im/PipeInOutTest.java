package org.fagu.fmv.im;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.im.IMOperation;
import org.fagu.fmv.im.soft.Convert;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.utils.StringUtils;
import org.junit.Test;


/**
 * @author f.agu
 */
public class PipeInOutTest {

	private static final String EXPECTED_SHA1 = "29f7fb4a25165a54354044a0960b380257daa81f";

	/**
	 * 
	 */
	public PipeInOutTest() {}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPipe_no() throws Exception {
		File srcFile = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File destFile = new File(srcFile.getPath() + ".piped." + FilenameUtils.getExtension(srcFile.getName()));

		try {
			Soft convertSoft = Convert.search();
			IMOperation op = new IMOperation();
			op.image(srcFile).autoOrient().quality(60D).image(destFile);
			convertSoft.withParameters(op.toList())
					.logCommandLine(System.out::println)
					.execute();
			assertEquals(EXPECTED_SHA1, sha1Of(destFile));
		} finally {
			srcFile.delete();
			destFile.delete();
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPipeInOut() throws Exception {
		File srcFile = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File destFile = new File(srcFile.getPath() + ".piped." + FilenameUtils.getExtension(srcFile.getName()));

		try {
			Soft convertSoft = Convert.search();
			IMOperation op = new IMOperation();
			op.image("-").autoOrient().quality(60D).image("jpg:-");
			try (InputStream inputStream = new FileInputStream(srcFile);
					OutputStream outputStream = new FileOutputStream(destFile)) {
				convertSoft.withParameters(op.toList())
						.logCommandLine(System.out::println)
						.input(inputStream)
						.out(outputStream)
						.execute();
			}
			assertEquals(EXPECTED_SHA1, sha1Of(destFile));
		} finally {
			srcFile.delete();
			destFile.delete();
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPipeIn() throws Exception {
		File srcFile = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File destFile = new File(srcFile.getPath() + ".piped." + FilenameUtils.getExtension(srcFile.getName()));

		try {
			Soft convertSoft = Convert.search();
			IMOperation op = new IMOperation();
			op.image("-").autoOrient().quality(60D).image(destFile);
			try (InputStream inputStream = new FileInputStream(srcFile)) {
				convertSoft.withParameters(op.toList())
						.logCommandLine(System.out::println)
						.input(inputStream)
						.execute();
			}
			assertEquals(EXPECTED_SHA1, sha1Of(destFile));
		} finally {
			srcFile.delete();
			destFile.delete();
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPipeOut() throws Exception {
		File srcFile = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File destFile = new File(srcFile.getPath() + ".piped." + FilenameUtils.getExtension(srcFile.getName()));

		try {
			Soft convertSoft = Convert.search();
			IMOperation op = new IMOperation();
			op.image(srcFile).autoOrient().quality(60D).image("jpg:-");
			try (OutputStream outputStream = new FileOutputStream(destFile)) {
				convertSoft.withParameters(op.toList())
						.logCommandLine(System.out::println)
						.out(outputStream)
						.execute();
			}
			assertEquals(EXPECTED_SHA1, sha1Of(destFile));
		} finally {
			srcFile.delete();
			destFile.delete();
		}
	}

	// *********************************************************

	/**
	 * @param file
	 * @return
	 * @throws Exception
	 */
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
