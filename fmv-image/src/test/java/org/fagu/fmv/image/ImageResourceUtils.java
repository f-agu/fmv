package org.fagu.fmv.image;

/*-
 * #%L
 * fmv-imagemagick
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.utils.Resources;


/**
 * @author f.agu
 */
public class ImageResourceUtils {

	public static final String _104 = "104.jpg";

	public static final String _203 = "203.jpg";

	public static final String ANIMATED_GIF = "animated.gif";

	public static final String BAD_ASS_TOTTOO_FAIL = "bad-ass-tattoo-fail.jpg";

	public static final String MULTIPAGE_TIFF = "multipage_tiff.tif";

	public static final String NO_IMAGE = "no-image";

	public static final String PLAN4_550MPIXELS = "plan4-550Mpixels.tif";

	public static final String RABBITMQ = "rabbitmq.png";

	public static final String WEI_ASS = "wei-ass.jpg";

	private ImageResourceUtils() {}

	public static File extractFile(String filename) throws IOException {
		return extractFile(filename, FilenameUtils.getExtension(filename));
	}

	public static File extractFile(String filename, String extension) throws IOException {
		Package pkg = ImageMetadatas.class.getPackage();
		return Resources.extractToTempFile(Resources.getResourcePath(pkg, filename), ImageBaseMetadatasTest.class
				.getSimpleName(), StringUtils.isEmpty(extension) ? "" : "." + extension);
	}

	public static InputStream getInputStream(String filename) {
		return ImageBaseMetadatasTest.class.getResourceAsStream(filename);
	}

}
