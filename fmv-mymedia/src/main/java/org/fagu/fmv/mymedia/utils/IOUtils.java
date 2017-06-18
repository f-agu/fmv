package org.fagu.fmv.mymedia.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author fagu
 */
public class IOUtils {

	/**
	 * 
	 */
	private IOUtils() {}

	/**
	 * @param input
	 * @param output
	 * @param progress
	 * @throws IOException
	 */
	public static void copy(final InputStream input, final OutputStream output, AtomicLong progress) throws IOException {
		final byte[] buffer = new byte[16 * 1024];
		int n;
		while( - 1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			progress.addAndGet(n);
		}
	}

}
