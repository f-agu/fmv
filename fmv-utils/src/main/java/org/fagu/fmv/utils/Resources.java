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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


/**
 * @author f.agu
 */
public class Resources {

	/**
	 * 
	 */
	private Resources() {
		super();
	}

	// ***************************************

	/**
	 * @param clsPackage
	 * @param name
	 * @return
	 */
	public static String getResourcePath(Package clsPackage, String name) {
		if(clsPackage == null) {
			return name;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(clsPackage.getName().replace('.', '/'));
		if( ! name.startsWith("/")) {
			sb.append('/');
		}
		sb.append(name);
		return sb.toString();
	}

	/**
	 * @param cls
	 * @return
	 */
	public static String getResourcePath(Class<?> cls) {
		if(cls == null) {
			return null;
		}
		return cls.getName().replace('.', '/') + ".class";
	}

	/**
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static InputStream getResourceInputStream(String resource) throws IOException {
		Resources resources = new Resources();
		return resources.getInputStream(resource);
	}

	/**
	 * @param resource
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static File extractToTempFile(String resource, String prefix, String suffix) throws IOException {
		File tmpFile = File.createTempFile(prefix, suffix);
		tmpFile = extractToFile(resource, tmpFile);
		return tmpFile;
	}

	/**
	 * @param resource
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static File extractToFile(String resource, File file) throws IOException {
		InputStream inputStream = null;
		try {
			inputStream = getResourceInputStream(resource);
			FileUtils.copyInputStreamToFile(inputStream, file);
			return file;
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	// **************************************************

	/**
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	private InputStream getInputStream(String resource) throws IOException {
		return getClass().getClassLoader().getResourceAsStream(resource);
	}

}
