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
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.Locators;
import org.fagu.fmv.soft.find.SoftLocator;


/**
 * @author f.agu
 * @created 25 avr. 2017 13:45:00
 */
public class ProgramFilesLocatorSupplier {

	// -------------------------------------

	/**
	 * @author f.agu
	 * @created 25 avr. 2017 14:06:37
	 */
	public static class ProgramFilesLocatorBuilder {

		private final Consumer<Locator> locatorConsumer;

		private Locators locators;

		private ProgramFilesLocatorBuilder() {
			this.locatorConsumer = null;
		}

		private ProgramFilesLocatorBuilder(Consumer<Locator> locatorConsumer, Locators locators) {
			this.locatorConsumer = Objects.requireNonNull(locatorConsumer);
			this.locators = locators;
		}

		public ProgramFilesLocatorBuilder withLocators(Locators locators) {
			this.locators = Objects.requireNonNull(locators);
			return this;
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
			return new ProgramFilesLocatorSupplier(locatorConsumer, locators, foldersToAnalyze);
		}

	}

	// -------------------------------------

	private final Locators locators;

	private final Consumer<Locator> locatorConsumer;

	private final Function<File, Collection<File>> foldersToAnalyze;

	/**
	 * @param locatorConsumer
	 * @param locators
	 * @param foldersToAnalyze
	 */
	private ProgramFilesLocatorSupplier(Consumer<Locator> locatorConsumer, Locators locators, Function<File, Collection<File>> foldersToAnalyze) {
		this.locatorConsumer = locatorConsumer;
		this.locators = locators;
		this.foldersToAnalyze = foldersToAnalyze;
	}

	/**
	 * @param softLocator
	 * @return
	 */
	public static ProgramFilesLocatorBuilder with(SoftLocator softLocator) {
		if(SystemUtils.IS_OS_WINDOWS) {
			return new ProgramFilesLocatorBuilder(softLocator::addLocator, softLocator.createLocators());
		}
		return new ProgramFilesLocatorBuilder();
	}

	/**
	 * @param softLocator
	 * @return
	 */
	public static ProgramFilesLocatorBuilder with(Consumer<Locator> locatorConsumer, Locators locators) {
		if(SystemUtils.IS_OS_WINDOWS) {
			return new ProgramFilesLocatorBuilder(locatorConsumer, locators);
		}
		return new ProgramFilesLocatorBuilder();
	}

	/**
	 * 
	 */
	public void supplyIn() {
		if(locatorConsumer != null && locators != null) {
			for(File programPath : getProgramPaths()) {
				for(File folder : foldersToAnalyze.apply(programPath)) {
					if(folder.exists()) {
						Locator byPath = locators.byPath(folder.getAbsolutePath());
						locatorConsumer.accept(Locators.named("win[" + programPath.getName() + "]", byPath));
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
