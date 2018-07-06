package org.fagu.fmv.media;

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

	private static final Set<String> IMAGES = toSet("jpg", "jpeg", "png", "tiff", "tif", "bmp", "psd", "tga");

	private static final Set<String> SOUNDS = toSet("mp3", "wma", "ogg", "m4a", "flac", "wav", "aac");

	private static final Set<String> MOVIES = toSet("avi", "mov", "mp4", "wmv", "mpg", "3gp", "flv", "ts", "mkv", "vob");

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
