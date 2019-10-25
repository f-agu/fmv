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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;


/**
 * @author fagu
 */
public class PlateformFileFilter {

	private PlateformFileFilter() {}

	public static FileFilter plateformAndBasename(String softName) {
		return plateform(f -> softName.equalsIgnoreCase(FilenameUtils.getBaseName(f.getName())));
	}

	public static FileFilter plateform(FileFilter subFileFilter) {
		if(SystemUtils.IS_OS_WINDOWS) {
			FileFilter extensionFilter = extensions("exe", "com", "cmd", "bat");
			return f -> subFileFilter.accept(f) && extensionFilter.accept(f);
		}
		return subFileFilter;
	}

	public static FileFilter baseName(String softName) {
		return f -> softName.equalsIgnoreCase(FilenameUtils.getBaseName(f.getName()));
	}

	public static FileFilter extensions(String... extensions) {
		Set<String> set = new HashSet<>(extensions.length);
		Arrays.stream(extensions)
				.map(String::toLowerCase)
				.forEach(set::add);
		return f -> {
			String extension = FilenameUtils.getExtension(f.getName());
			return extension != null && set.contains(extension.toLowerCase());
		};
	}

}
