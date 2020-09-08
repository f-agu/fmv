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
