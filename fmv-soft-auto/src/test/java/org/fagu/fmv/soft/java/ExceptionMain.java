package org.fagu.fmv.soft.java;

import java.io.PrintStream;
import java.util.Random;


/**
 * @author f.agu
 * @created 24 mai 2017 12:56:27
 */
public class ExceptionMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		PrintStream fullStream;
		PrintStream smallStream;
		if("out".equals(args[0])) {
			fullStream = System.out;
			smallStream = System.err;
		} else if("err".equals(args[0])) {
			fullStream = System.err;
			smallStream = System.out;
		} else {
			throw new Error();
		}

		smallStream.write('x');

		final int bufferSize = 16 * 1024;
		byte[] buf = new byte[bufferSize];
		long count = 0;
		long total = Long.parseLong(args[1]);
		Random random = new Random();
		while(count < total) {
			random.nextBytes(buf);
			int len = (int)Math.min(bufferSize, total - count);
			fullStream.write(buf, 0, len);
			count += len;
		}

		throw new Exception();
	}

}
