package org.fagu.fmv.image;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.utils.Resources;


/**
 * @author f.agu
 */
class ImageResourceUtils {

	/**
	 * 
	 */
	private ImageResourceUtils() {}

	/**
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	static File extractFile(String filename) throws IOException {
		return extractFile(filename, FilenameUtils.getExtension(filename));
	}

	/**
	 * @param filename
	 * @param extension
	 * @return
	 * @throws IOException
	 */
	static File extractFile(String filename, String extension) throws IOException {
		Package pkg = ImageMetadatas.class.getPackage();
		return Resources.extractToTempFile(Resources.getResourcePath(pkg, filename), ImageMetadatasTestCase.class
				.getSimpleName(), "." + extension);
	}

}
