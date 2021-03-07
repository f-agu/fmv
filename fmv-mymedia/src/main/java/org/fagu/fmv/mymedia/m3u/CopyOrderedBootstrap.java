package org.fagu.fmv.mymedia.m3u;

/*-
 * #%L
 * fmv-mymedia
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;


public class CopyOrderedBootstrap {

	public static void main(String... args) throws Exception {
		for(String arg : args) {
			copy(arg);
		}
	}

	public static void copy(String folder) throws Exception {
		File file = new File(folder);
		if(file.isDirectory()) {
			File[] files = file.listFiles(f -> "m3u".equals(FilenameUtils.getExtension(f.getName())));
			if(ArrayUtils.isEmpty(files)) {
				return;
			}
			file = files[0];
		}

		// File destFolder = new File(args[1]);
		File destFolder = new File("D:\\tmp\\cd-auto\\auto-x2", file.getName());
		FileUtils.forceMkdir(destFolder);

		File srcFolder = file.getParentFile();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1))) {
			AtomicInteger index = new AtomicInteger();
			reader.lines()
					.filter(l -> ! l.startsWith("#"))
					.map(l -> new File(srcFolder, l))
					.forEach(f -> {
						int idx = index.incrementAndGet();
						try {
							File destFile = new File(destFolder, (idx <= 9 ? "0" : "") + idx + ' ' + getName(f));
							System.out.println(destFile);
							FileUtils.copyFile(f, destFile);
						} catch(IOException e) {
							e.printStackTrace();
						}
					});
		}
	}

	private static final char[] IGNORE_CHARS = ".)-_".toCharArray();

	private static String getName(File file) {
		String name = file.getName();
		int pos = - 1;
		for(char c : name.toCharArray()) {
			++pos;
			if( ! Character.isDigit(c)
					&& ! Character.isWhitespace(c)
					&& ! ArrayUtils.contains(IGNORE_CHARS, c)) {
				break;
			}
		}
		return pos >= 0 ? name.substring(pos) : name;
	}

}
