package org.fagu.fmv.im;

/*-
 * #%L
 * fmv-imagemagick
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.im.ImageMetadatas;
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
