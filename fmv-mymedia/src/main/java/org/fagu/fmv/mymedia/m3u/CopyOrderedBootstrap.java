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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;


public class CopyOrderedBootstrap {

	private enum Name {

		FILE_NAME {

			@Override
			public String getName(String firstLine, File sourceFile) {
				return sourceFile.getName();
			}
		},
		FILE_NAME_INDEX_TRUNKED {

			@Override
			public String getName(String firstLine, File sourceFile) {
				return CopyOrderedBootstrap.getName(sourceFile);
			}
		},

		TAG {

			@Override
			public String getName(String firstLine, File sourceFile) {
				return StringUtils.substringAfter(firstLine, ",");
			}
		};

		public abstract String getName(String firstLine, File sourceFile);
	}

	private static final boolean WITH_INDEX = false;

	private static final Name NAME = Name.FILE_NAME;

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
		UnaryOperator<String> nameOperator = UnaryOperator.identity();
		if(WITH_INDEX) {
			AtomicInteger index = new AtomicInteger();
			nameOperator = n -> {
				int idx = index.incrementAndGet();
				return (idx <= 9 ? "0" : "") + idx + ' ' + n;
			};
		}

		// File destFolder = new File(args[1]);
		File destFolder = new File("c:\\tmp\\zic", file.getName());
		FileUtils.forceMkdir(destFolder);

		File srcFolder = file.getParentFile();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1))) {
			AtomicReference<String> previousLine = new AtomicReference<>();
			reader.lines()
					.filter(l -> {
						previousLine.set(l);
						return ! l.startsWith("#");
					})
					.map(l -> searchSource(srcFolder, l))
					.filter(Objects::nonNull)
					.forEach(f -> {
						try {
							File destFile = new File(destFolder, NAME.getName(previousLine.get(), f));
							System.out.println(destFile);
							FileUtils.copyFile(f, destFile);
						} catch(IOException e) {
							e.printStackTrace();
						}
					});
		}
	}

	private static File searchSource(File parentFolder, String path) {
		File parent = parentFolder;
		File file = null;
		while(parent != null && ! (file = new File(parent, path)).exists()) {
			parent = parent.getParentFile();
		}
		if(file == null || ! file.exists()) {
			System.out.println("File not found: " + path);
		}
		return file;
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
