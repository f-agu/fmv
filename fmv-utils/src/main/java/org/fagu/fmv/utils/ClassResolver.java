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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;


/**
 * @author f.agu
 */
public class ClassResolver {

	public Set<Class<?>> find(String packagePath, Predicate<Class<?>> filter) throws IOException {
		String path = packagePath.replace('.', '/');
		if(path.startsWith("/")) {
			path = path.substring(1);
		}
		ClassLoader cl = getClass().getClassLoader();
		Enumeration<URL> resourceUrls = cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path);

		Set<Class<?>> result = new LinkedHashSet<>(16);
		while(resourceUrls.hasMoreElements()) {
			URL url = resourceUrls.nextElement();
			if(isJarURL(url)) {
				result.addAll(doFindJar(url, filter));
			} else {
				String sfile = URLDecoder.decode(url.getFile(), "UTF-8");
				result.addAll(doFindFile(new File(sfile), filter));
			}
		}
		return result;
	}

	// *********************************************************

	protected boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return "jar".equals(protocol) || "zip".equals(protocol);
	}

	protected Set<Class<?>> doFindFile(File rootDir, Predicate<Class<?>> filter) throws IOException {
		if( ! rootDir.exists()) {
			return Collections.emptySet();
		}
		if( ! rootDir.isDirectory()) {
			return Collections.emptySet();
		}
		if( ! rootDir.canRead()) {
			return Collections.emptySet();
		}

		File[] files = rootDir.listFiles(pathname -> {
			if(pathname.isDirectory()) {
				return true;
			}
			if( ! pathname.isFile()) {
				return false;
			}

			String path = pathname.getAbsolutePath();
			if( ! path.endsWith(".class")) {
				return false;
			}
			if(path.indexOf('$') >= 0) {
				return false;
			}
			return true;
		});

		Set<Class<?>> findSet = new LinkedHashSet<>();
		for(File file : files) {
			if(file.isDirectory()) {
				findSet.addAll(doFindFile(file, filter));
				continue;
			}
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int)file.length());
			FileUtils.copyFile(file, byteArrayOutputStream);

			byte[] b = byteArrayOutputStream.toByteArray();
			ClassReader classReader = new ClassReader(b);

			filterAndAdd(classReader.getClassName(), filter, findSet);
		}
		return findSet;
	}

	protected Set<Class<?>> doFindJar(URL url, Predicate<Class<?>> filter) throws IOException {
		URLConnection con = url.openConnection();
		JarFile jarFile;
		String jarFileUrl;
		String rootEntryPath;
		boolean newJarFile = false;

		if(con instanceof JarURLConnection) {
			// Should usually be the case for traditional JAR files.
			JarURLConnection jarCon = (JarURLConnection)con;
			// ResourceUtils.useCachesIfNecessary(jarCon);
			jarFile = jarCon.getJarFile();
			jarFileUrl = jarCon.getJarFileURL().toExternalForm();
			JarEntry jarEntry = jarCon.getJarEntry();
			rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
		} else {
			// No JarURLConnection -> need to resort to URL file parsing.
			// We'll assume URLs of the format "jar:path!/entry", with the protocol
			// being arbitrary as long as following the entry format.
			// We'll also handle paths with and without leading "file:" prefix.
			final String JAR_URL_SEPARATOR = "!/";
			String urlFile = url.getFile();
			int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
			if(separatorIndex != - 1) {
				jarFileUrl = urlFile.substring(0, separatorIndex);
				rootEntryPath = urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length());
				jarFile = getJarFile(jarFileUrl);
			} else {
				jarFile = new JarFile(urlFile);
				jarFileUrl = urlFile;
				rootEntryPath = "";
			}
			newJarFile = true;
		}

		try {
			if( ! "".equals(rootEntryPath) && ! rootEntryPath.endsWith("/")) {
				// Root entry path must end with slash to allow for proper matching.
				// The Sun JRE does not return a slash here, but BEA JRockit does.
				rootEntryPath = rootEntryPath + "/";
			}
			final String SUFFIX = ".class";
			Set<Class<?>> result = new LinkedHashSet<Class<?>>(8);
			for(Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
				JarEntry entry = entries.nextElement();
				String entryPath = entry.getName();
				if(entryPath.startsWith(rootEntryPath)) {
					if(entryPath.endsWith(SUFFIX)) {
						filterAndAdd(entryPath.substring(0, entryPath.length() - SUFFIX.length()), filter, result);
					}
				}
			}
			return result;
		} finally {
			// Close jar file, but only if freshly obtained -
			// not from JarURLConnection, which might cache the file reference.
			if(newJarFile) {
				jarFile.close();
			}
		}
	}

	protected JarFile getJarFile(String jarFileUrl) throws IOException {
		final String FILE_URL_PREFIX = "file:";
		if(jarFileUrl.startsWith(FILE_URL_PREFIX)) {
			try {
				return new JarFile(new URI(URLDecoder.decode(jarFileUrl, "UTF-8")).getSchemeSpecificPart());
			} catch(URISyntaxException ex) {
				// Fallback for URLs that are not valid URIs (should hardly ever happen).
				return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length()));
			}
		}
		return new JarFile(jarFileUrl);
	}

	protected void filterAndAddX(String className, Predicate<Object> filter, Set<Object> result) {
		Class<?> cls = null;
		try {
			String replace = className.replace('/', '.');
			cls = Class.forName(replace);
		} catch(ClassNotFoundException e) {
			// ignore
		}

		if(cls != null && filter.test(cls)) {
			result.add(cls);
		}
	}

	protected void filterAndAdd(String className, Predicate<Class<?>> filter, Set<Class<?>> result) {
		Class<?> cls = null;
		try {
			String replace = className.replace('/', '.');
			cls = Class.forName(replace);
		} catch(ClassNotFoundException e) {
			// ignore
		}

		if(cls != null && filter.test(cls)) {
			result.add(cls);
		}
	}

}
