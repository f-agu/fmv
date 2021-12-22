package org.fagu.fmv.utils.file;

/*
 * #%L
 * fmv-utils
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

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class FileUtils {

	private FileUtils() {}

	public static File getTempFolder(String prefix, String suffix) throws IOException {
		return getTempFolder(prefix, suffix, null);
	}

	public static File getTempFolder(String prefix, String suffix, File directory) throws IOException {
		if(prefix == null) {
			throw new NullPointerException();
		}
		if(prefix.length() < 3) {
			throw new IllegalArgumentException("Prefix string too short");
		}
		String s = StringUtils.defaultString(suffix);
		if(directory == null) {
			directory = new File(LazyInitialization.temporaryDirectory);
		}
		File f = null;
		do {
			f = generateFile(prefix, s, directory);
		} while(f.exists());
		org.apache.commons.io.FileUtils.forceMkdir(f);;
		return f;
	}

	// *******************************************************

	private static File generateFile(String prefix, String suffix, File dir) throws IOException {
		long n = LazyInitialization.random.nextLong();
		if(n == Long.MIN_VALUE) {
			n = 0; // corner case
		} else {
			n = Math.abs(n);
		}
		return new File(dir, prefix + Long.toHexString(n) + suffix);
	}

	// ---------------------------------------------------------

	/**
	 * lazy initialization of SecureRandom and temporaryDirectory
	 * 
	 * @author f.agu
	 */
	private static class LazyInitialization {

		static final SecureRandom random = new SecureRandom();

		static final String temporaryDirectory = System.getProperty("java.io.tmpdir");

	}
}
