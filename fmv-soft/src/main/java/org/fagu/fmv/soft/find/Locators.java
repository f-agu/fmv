package org.fagu.fmv.soft.find;

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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class Locators {

	private static final Locator NOTHING = st -> Collections.emptyList();

	private final FileFilter fileFilter;

	/**
	 * @param fileFilter
	 */
	public Locators(FileFilter fileFilter) {
		this.fileFilter = Objects.requireNonNull(fileFilter);
	}

	/**
	 * @param path
	 * @return
	 */
	public Locator byPath(String path) {
		return path == null ? NOTHING : named("path[" + path + ']', softName -> listByPath(path));
	}

	/**
	 * @param propertyName
	 * @return
	 */
	public Locator byPropertyPath(String propertyName) {
		return propertyName == null ? NOTHING : named("property-path[" + propertyName + ']', byPath(System.getProperty(propertyName)));
	}

	/**
	 * @return
	 */
	public Locator byCurrentPath() {
		return byPath(".");
	}

	/**
	 * @param envName
	 * @return
	 */
	public Locator byEnv(String envName) {
		return byEnv(envName, Arrays.asList("bin", "bin32", "bin64"));
	}

	/**
	 * @param envName
	 * @param subFolders
	 * @return
	 */
	public Locator byEnv(String envName, Collection<String> subFolders) {
		if(envName == null) {
			return NOTHING;
		}
		return named("env[" + envName + ']', st -> {
			List<File> files = new ArrayList<>();
			String env = System.getenv(envName);
			if(StringUtils.isNotEmpty(env)) {
				files = listByPath(env);
				if(subFolders != null) {
					Iterator<String> envSubFolders = subFolders.iterator();
					while((files == null || files.isEmpty()) && envSubFolders.hasNext()) {
						files.addAll(listByPath(env + File.separatorChar + envSubFolders.next()));
					}
				}
			}
			return files;
		});
	}

	/**
	 * @return
	 */
	public Locator byEnvPath() {
		return named("env[PATH]", softname -> listByPath(System.getenv("PATH")));
	}

	/**
	 * @return
	 */
	public static Locator nothing() {
		return NOTHING;
	}

	/**
	 * @param name
	 * @param locator
	 * @return
	 */
	public static Locator named(String name, Locator locator) {
		return new Locator() {

			@Override
			public List<File> locate(String softName) {
				return locator.locate(softName);
			}

			@Override
			public String toString() {
				String string = locator.toString();
				return string.startsWith(Locator.class.getName()) ? name : name + "," + string;
			}
		};
	}

	// ***********************************************************

	/**
	 * @param paths
	 * @return
	 */
	private List<File> listByPath(String paths) {
		if(paths != null) {
			List<File> list = new ArrayList<>();
			StringTokenizer stringTokenizer = new StringTokenizer(paths, File.pathSeparator);
			while(stringTokenizer.hasMoreTokens()) {
				File file = new File(stringTokenizer.nextToken());
				if(file.isFile()) {
					if(fileFilter.accept(file)) {
						list.add(file);
					}
				} else if(file.isDirectory()) {
					File[] files = file.listFiles(fileFilter);
					if(files != null && files.length > 0) {
						list.addAll(Arrays.asList(files));
					}
				}
			}
			return list;
		}
		return Collections.emptyList();
	}

}
