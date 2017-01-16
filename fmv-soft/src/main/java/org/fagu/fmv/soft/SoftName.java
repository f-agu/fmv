package org.fagu.fmv.soft;

import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.Founds;


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

/**
 * @author f.agu
 */
public interface SoftName {

	/**
	 * @return
	 */
	String getName();

	/**
	 * @return
	 */
	default FileFilter getFileFilter() {
		FileFilter fileFilter = f -> getName().equalsIgnoreCase(FilenameUtils.getBaseName(f.getName()));
		if(SystemUtils.IS_OS_WINDOWS) {
			Set<String> defaultExtensions = new HashSet<>(4);
			defaultExtensions.add("exe");
			defaultExtensions.add("com");
			defaultExtensions.add("cmd");
			defaultExtensions.add("bat");
			return f -> {
				if( ! fileFilter.accept(f)) {
					return false;
				}
				String extension = FilenameUtils.getExtension(f.getName());
				return extension != null ? defaultExtensions.contains(extension.toLowerCase()) : false;
			};
		}
		return fileFilter;
	}

	/**
	 * @param founds
	 * @return
	 */
	Soft create(Founds founds);

	/**
	 * @return
	 */
	default String getDownloadURL() {
		return null;
	}

	/**
	 * @param infoConsumer
	 */
	default void moreInfo(Consumer<String> infoConsumer) {
		String downloadURL = getDownloadURL();
		if(downloadURL != null && ! "".equals(downloadURL.trim())) {
			infoConsumer.accept("    Download " + getName() + " at " + downloadURL + " and add the path in your system environment PATH");
		}
	}
}
