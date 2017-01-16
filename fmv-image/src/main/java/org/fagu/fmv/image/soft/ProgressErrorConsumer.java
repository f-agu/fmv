package org.fagu.fmv.image.soft;

/*-
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 - 2016 fagu
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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.im4java.process.ErrorConsumer;


/**
 * @author Oodrive
 * @author f.agu
 * @created 21 nov. 2016 14:23:42
 */
public class ProgressErrorConsumer implements ErrorConsumer {

	private static final Pattern PATTERN = Pattern.compile("(.*)\\[(.*)\\]: (\\d+) of (\\d+), (\\d+)% complete");

	private final Charset charset;

	private String taskName;

	private String fileName;

	private int percent;

	private long value;

	private long max;

	/**
	 * 
	 */
	public ProgressErrorConsumer() {
		this(null);
	}

	/**
	 * @param charset
	 */
	public ProgressErrorConsumer(Charset charset) {
		this.charset = charset;
	}

	/**
	 * @see org.im4java.process.ErrorConsumer#consumeError(java.io.InputStream)
	 */
	@Override
	public void consumeError(InputStream inputStream) throws IOException {
		try (BufferedReader reader = toBufferedReader(inputStream)) {
			String line;
			while((line = reader.readLine()) != null) {
				// System.out.println(line);
				Matcher matcher = PATTERN.matcher(line);
				if(matcher.matches()) {
					taskName = matcher.group(1);
					fileName = matcher.group(2);
					value = Long.parseLong(matcher.group(3));
					max = Long.parseLong(matcher.group(4));
					percent = Integer.parseInt(matcher.group(5));
				}
			}
		}
	}

	/**
	 * @return
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return
	 */
	public int getPercent() {
		return percent;
	}

	/**
	 * @return
	 */
	public long getMax() {
		return max;
	}

	/**
	 * @return
	 */
	public long getValue() {
		return value;
	}

	// *****************************************

	/**
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private BufferedReader toBufferedReader(InputStream inputStream) throws IOException {
		InputStreamReader isr = null;
		if(charset == null) {
			isr = new InputStreamReader(inputStream);
		} else {
			isr = new InputStreamReader(inputStream, charset);
		}
		return new BufferedReader(isr);
	}

}
