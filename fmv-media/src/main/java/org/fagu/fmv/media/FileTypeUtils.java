package org.fagu.fmv.media;

/*-
 * #%L
 * fmv-media
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

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;


/**
 * @author f.agu
 * @created 6 juil. 2018 22:42:00
 */
public class FileTypeUtils {

	private static final Set<String> IMAGES = toSet("jpg", "jpeg", "png", "tiff", "tif", "bmp", "psd", "tga", "webp");

	private static final Set<String> SOUNDS = toSet("mp3", "wma", "ogg", "m4a", "flac", "wav", "aac", "vqf", "wv", "opus");

	private static final Set<String> MOVIES = toSet("avi", "mov", "mp4", "wmv", "mpg", "3gp", "flv", "ts", "mkv", "vob");

	private static final Set<String> COMIC_BOOKS = toSet("cbz");

	private FileTypeUtils() {}

	public static FileIs with(FileType fileType) {
		Set<String> extensions = null;
		switch(fileType) {
			case AUDIO:
				extensions = SOUNDS;
				break;
			case IMAGE:
				extensions = IMAGES;
				break;
			case VIDEO:
				extensions = MOVIES;
				break;
			case COMIC_BOOK:
				extensions = COMIC_BOOKS;
				break;
			default:
				throw new RuntimeException("Undefined fileType: " + fileType);
		}
		return new FileIs(extensions);
	}

	// -----------------------------------------

	public static class FileIs {

		private final Set<String> extensions;

		private FileIs(Set<String> extensions) {
			this.extensions = Objects.requireNonNull(extensions);
		}

		public boolean verify(String fileName) {
			String extension = FilenameUtils.getExtension(fileName);
			return extension != null && extensions.contains(extension.toLowerCase());
		}

		public boolean verify(File file) {
			return file.isFile() && verify(file.getName());
		}

		public boolean verify(Path path) {
			return verify(path.toFile());
		}

	}

	// ********************************************

	private static Set<String> toSet(String... strings) {
		return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(strings)));
	}

}
