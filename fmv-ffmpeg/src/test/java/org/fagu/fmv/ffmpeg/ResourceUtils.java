package org.fagu.fmv.ffmpeg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;


/**
 * @author Oodrive
 * @author f.agu
 * @created 25 janv. 2017 12:12:28
 */
public class ResourceUtils {

	/**
	 * 
	 */
	private ResourceUtils() {}

	/**
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static File extract(String resource) throws IOException {
		return extract(resource, null);
	}

	/**
	 * @param tmpFolder
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static File extract(String resource, File tmpFolder) throws IOException {
		File file = File.createTempFile(FilenameUtils.getBaseName(resource), "." + FilenameUtils.getExtension(resource), tmpFolder);
		try (InputStream inputStream = FFHelper.class.getResourceAsStream(resource);
				OutputStream outputStream = new FileOutputStream(file)) {
			IOUtils.copy(inputStream, outputStream);
		}
		return file;
	}

}
