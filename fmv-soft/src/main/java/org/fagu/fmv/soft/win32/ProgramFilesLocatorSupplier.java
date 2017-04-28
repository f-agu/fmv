package org.fagu.fmv.soft.win32;

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
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.Locators;
import org.fagu.fmv.soft.find.SoftLocator;


/**
 * @author Oodrive
 * @author f.agu
 * @created 25 avr. 2017 13:45:00
 */
public class ProgramFilesLocatorSupplier {

	/**
	 * @author Oodrive
	 * @author f.agu
	 * @created 25 avr. 2017 14:06:37
	 */
	public static class ProgramFilesLocatorBuilder {

		private final Locators locators;

		private ProgramFilesLocatorBuilder() {
			locators = null;
		}

		private ProgramFilesLocatorBuilder(Locators locators) {
			this.locators = Objects.requireNonNull(locators);
		}

		public ProgramFilesLocatorSupplier findFolder(String folderName) {
			return find(programFile -> {
				File folder = new File(programFile, folderName);
				return folder.exists() ? Collections.singletonList(folder) : Collections.emptyList();
			});
		}

		public ProgramFilesLocatorSupplier findFolder(FileFilter fileFilter) {
			return find(programFile -> {
				File[] files = programFile.listFiles(fileFilter);
				return files != null ? Arrays.asList(files) : Collections.emptyList();
			});
		}

		public ProgramFilesLocatorSupplier find(Function<File, Collection<File>> foldersToAnalyze) {
			return new ProgramFilesLocatorSupplier(locators, foldersToAnalyze);
		}

	}

	private final Locators locators;

	private final Function<File, Collection<File>> foldersToAnalyze;

	/**
	 * @param locators
	 * @param foldersToAnalyze
	 */
	private ProgramFilesLocatorSupplier(Locators locators, Function<File, Collection<File>> foldersToAnalyze) {
		this.locators = locators;
		this.foldersToAnalyze = foldersToAnalyze;
	}

	/**
	 * @param locators
	 * @return
	 */
	public static ProgramFilesLocatorBuilder with(Locators locators) {
		if(SystemUtils.IS_OS_WINDOWS) {
			return new ProgramFilesLocatorBuilder(locators);
		}
		return new ProgramFilesLocatorBuilder();
	}

	/**
	 * @param locatorsFileFilter
	 * @return
	 */
	public static ProgramFilesLocatorBuilder with(FileFilter locatorsFileFilter) {
		return with(new Locators(locatorsFileFilter));
	}

	/**
	 * @param softLocator
	 */
	public void supplyIn(SoftLocator softLocator) {
		if(locators != null) {
			for(File programPath : getProgramPaths()) {
				for(File folder : foldersToAnalyze.apply(programPath)) {
					if(folder.exists()) {
						Locator byPath = locators.byPath(folder.getAbsolutePath());
						softLocator.addLocator(Locators.named("win[" + programPath.getName() + "]", byPath));
					}
				}
			}
		}
	}

	// ******************************************************

	/**
	 * @return
	 */
	private static Set<File> getProgramPaths() {
		Set<File> paths = new HashSet<>(4);
		addIfNotNullAndExists(paths, System.getenv("ProgramFiles"));
		addIfNotNullAndExists(paths, System.getenv("ProgramFiles(x86)"));
		addIfNotNullAndExists(paths, System.getenv("ProgramW6432"));
		return paths;
	}

	/**
	 * @param paths
	 * @param strToAdd
	 */
	private static void addIfNotNullAndExists(Set<File> paths, String strToAdd) {
		if(strToAdd != null) {
			File file = new File(strToAdd);
			if(file.exists()) {
				paths.add(file);
			}
		}
	}

}
