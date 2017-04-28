package org.fagu.fmv.soft.find;

/*-
 * #%L
 * fmv-soft
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
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;


/**
 * @author fagu
 */
public class PlateformFileFilter {

	/**
	 * 
	 */
	private PlateformFileFilter() {}

	/**
	 * @param softName
	 * @return
	 */
	public static FileFilter getFileFilter(String softName) {
		return getFileFilter(f -> softName.equalsIgnoreCase(FilenameUtils.getBaseName(f.getName())));
	}

	/**
	 * @param subFileFilter
	 * @return
	 */
	public static FileFilter getFileFilter(FileFilter subFileFilter) {
		if(SystemUtils.IS_OS_WINDOWS) {
			Set<String> defaultExtensions = new HashSet<>(4);
			defaultExtensions.add("exe");
			defaultExtensions.add("com");
			defaultExtensions.add("cmd");
			defaultExtensions.add("bat");
			return f -> {
				if( ! subFileFilter.accept(f)) {
					return false;
				}
				String extension = FilenameUtils.getExtension(f.getName());
				return extension != null ? defaultExtensions.contains(extension.toLowerCase()) : false;
			};
		}
		return subFileFilter;
	}

}
