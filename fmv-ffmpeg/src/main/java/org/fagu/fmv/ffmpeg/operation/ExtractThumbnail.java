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

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author f.agu
 */
public class ExtractThumbnail {

	private final List<File> files;

	/**
	 * @param files
	 */
	public ExtractThumbnail(List<File> files) {
		this.files = files;
	}

	/**
	 * @return
	 */
	public List<File> getFiles() {
		return files;
	}

	/**
	 * @param folder
	 * @param spattern
	 * @return
	 */
	public static ExtractThumbnail find(File folder, String spattern) {
		final Pattern pattern = Pattern.compile(spattern);
		File[] files = folder.listFiles(new FileFilter() {

			/**
			 * @see java.io.FileFilter#accept(java.io.File)
			 */
			public boolean accept(File pathname) {
				if( ! pathname.isFile()) {
					return false;
				}
				return pattern.matcher(pathname.getName()).matches();
			}
		});
		Arrays.sort(files);
		return new ExtractThumbnail(Arrays.asList(files));
	}

}
