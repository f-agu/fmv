package org.fagu.fmv.ffmpeg;

/*-
 * #%L
 * fmv-ffmpeg
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

	private ResourceUtils() {}

	public static File extract(String resource) throws IOException {
		return extract(resource, null);
	}

	public static File extract(String resource, File tmpFolder) throws IOException {
		File file = File.createTempFile(FilenameUtils.getBaseName(resource), "." + FilenameUtils.getExtension(resource), tmpFolder);
		try (InputStream inputStream = open(resource);
				OutputStream outputStream = new FileOutputStream(file)) {
			IOUtils.copy(inputStream, outputStream);
		}
		return file;
	}

	public static InputStream open(String resource) throws IOException {
		return FFHelper.class.getResourceAsStream(resource);
	}

}
