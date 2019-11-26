package org.fagu.fmv.utils;

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
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.FileUtils;


/**
 * @author f.agu
 */
public class Resources {

	private Resources() {}

	// ***************************************

	public static String getResourcePath(Package clsPackage, String name) {
		if(clsPackage == null) {
			return name;
		}
		StringBuilder sb = new StringBuilder()
				.append(clsPackage.getName().replace('.', '/'));
		if( ! name.startsWith("/")) {
			sb.append('/');
		}
		return sb.append(name).toString();
	}

	public static String getResourcePath(Class<?> cls) {
		if(cls == null) {
			return null;
		}
		return cls.getName().replace('.', '/') + ".class";
	}

	public static InputStream getResourceInputStream(String resource) throws IOException {
		return Objects.requireNonNull(new Resources().getInputStream(resource), "Resource not found: " + resource);
	}

	public static File extractToTempFile(String resource, String prefix, String suffix) throws IOException {
		File tmpFile = File.createTempFile(prefix, suffix);
		return extractToFile(resource, tmpFile);
	}

	public static File extractToFile(String resource, File file) throws IOException {
		try (InputStream inputStream = getResourceInputStream(resource)) {
			FileUtils.copyInputStreamToFile(inputStream, file);
			return file;
		}
	}

	// **************************************************

	private InputStream getInputStream(String resource) {
		return getClass().getClassLoader().getResourceAsStream(resource);
	}

}
