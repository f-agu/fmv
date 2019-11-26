package org.fagu.fmv.mymedia.file;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang3.StringUtils;


/*
 * #%L
 * fmv-mymedia
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
public class FileUtils {

	private static final Map<String, File> ROOT_BY_NAME_MAP = findRootsByNameMap();

	private FileUtils() {}

	/**
	 * @param name
	 * @return
	 */
	public static Optional<File> getRootByName(String name) {
		return Optional.ofNullable(ROOT_BY_NAME_MAP.get(name.toLowerCase()));
	}

	/**
	 * @return
	 */
	public static Map<String, File> getRootByNameMap() {
		return ROOT_BY_NAME_MAP;
	}

	/**
	 * @return
	 */
	public static Optional<File> findFirstHarddriveFaguVv() {
		return findFirstRootByName("fagu_Vv_1", "fagu_Vv_2");
	}

	/**
	 * @param names
	 * @return
	 */
	public static Optional<File> findFirstRootByName(String... names) {
		return findFirstRootByName(Arrays.asList(names));
	}

	/**
	 * @param names
	 * @return
	 */
	public static Optional<File> findFirstRootByName(Collection<String> names) {
		return FileUtils.getRootByNameMap()
				.entrySet()
				.stream()
				.filter(e -> {
					String name = e.getKey();
					return names.stream().map(String::toLowerCase).anyMatch(name::equalsIgnoreCase);
				})
				.map(Entry::getValue)
				.findFirst();
	}

	// ****************************************************

	private static Map<String, File> findRootsByNameMap() {
		Map<String, File> map = new HashMap<>();
		final Pattern pattern = Pattern.compile("(.*) \\([a-zA-Z]+:\\)");
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		for(File f : File.listRoots()) {
			boolean floppyDrive = fileSystemView.isFloppyDrive(f);
			if(floppyDrive) {
				continue;
			}
			String displayName = fileSystemView.getSystemDisplayName(f);
			if(StringUtils.isBlank(displayName)) {
				continue;
			}
			Matcher matcher = pattern.matcher(displayName);
			if(matcher.matches()) {
				displayName = matcher.group(1);
			}
			if(map.putIfAbsent(displayName.toLowerCase(), f) != null) {
				// impossible to use this map if displayName already exists
				// return Collections.emptyMap();
			}
		}
		return Collections.unmodifiableMap(map);
	}

}
