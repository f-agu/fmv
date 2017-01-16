package org.fagu.fmv.ffmpeg.operation;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 fagu
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.soft.exec.ReadLine;
import org.fagu.fmv.soft.ffmpeg.FFMpeg;
import org.junit.Before;
import org.junit.Test;


/**
 * @author f.agu
 */
// @Ignore
public class PixelFormatGenerator {

	/**
	 * 
	 */
	public PixelFormatGenerator() {}

	/**
	 * 
	 */
	@Before
	public void setUp() {
		String userName = System.getProperty("user.name");
		if("f.agu".equals(userName)) { // taf
			// FFLocator.setFFPath("C:/Program Files/FFMpeg/bin");
		} else if("personne".equals(userName)) { // home
			// FFLocator.setFFPath("C:/Program Files/FFMpeg");
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void generate() throws IOException {
		final Pattern PATTERN = Pattern.compile("(I|\\.)(O|\\.)(H|\\.)(P|\\.)(B|\\.)\\s(\\w+)\\s+(\\d+)\\s+(\\d+).*");
		List<String> arguments = Arrays.asList("-v", "quiet", "-pix_fmts");
		final Map<String, String> map = new TreeMap<String, String>();
		ReadLine readLine = new ReadLine() {

			/**
			 * @see org.fagu.fmv.utils.exec.ReadLine#read(java.lang.String)
			 */
			@Override
			public void read(String line) {

				StringBuilder buf = new StringBuilder();
				buf.append("public static final PixelFormat ");

				// System.out.println(line);
				Matcher matcher = PATTERN.matcher(line);
				if(matcher.matches()) {
					boolean supportedInput = "I".equals(matcher.group(1));
					boolean supportedOutput = "O".equals(matcher.group(2));
					boolean hardwareAccelerated = "H".equals(matcher.group(3));
					boolean paletted = "P".equals(matcher.group(4));
					boolean bitstream = "B".equals(matcher.group(5));

					String name = matcher.group(6);
					int nbComponents = Integer.parseInt(matcher.group(7));
					int bitsPerPixel = Integer.parseInt(matcher.group(8));

					if(Character.isDigit(name.charAt(0))) {
						buf.append('_');
					}
					buf.append(name.toUpperCase());
					buf.append(" = new PixelFormat(").append(supportedInput).append(", ").append(supportedOutput).append(", ");
					buf.append(hardwareAccelerated).append(", ").append(paletted).append(", ");
					buf.append(bitstream).append(", \"").append(name).append("\", ");
					buf.append(nbComponents).append(", ").append(bitsPerPixel).append(");");
					map.put(name, buf.toString());
				}

			}
		};
		FFMpeg.search() //
				.withParameters(arguments)//
				.addCommonReadLine(readLine)//
				.execute();

		for(String str : map.values()) {
			System.out.println(str);
		}
	}
}
