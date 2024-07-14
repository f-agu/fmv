package org.fagu.fmv.mymedia.sync;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.mymedia.file.PlaceHolderRootFile;
import org.fagu.fmv.mymedia.logger.Logger;


/**
 * @author f.agu
 * @created 12 mai 2018 16:44:49
 */
public class SyncConfig {

	// ----------------------------------------------

	/**
	 * @author f.agu
	 * @created 12 mai 2018 16:48:11
	 */
	public static class SyncElement {

		private final File sourceFile;

		private final List<File> destFiles;

		public SyncElement(File sourceFile, List<File> destFiles) {
			this.sourceFile = Objects.requireNonNull(sourceFile);
			this.destFiles = Collections.unmodifiableList(new ArrayList<>(destFiles));
		}

		public File getSourceFile() {
			return sourceFile;
		}

		public List<File> getDestFiles() {
			return destFiles;
		}

	}

	// ----------------------------------------------

	private final List<SyncElement> elements;

	private SyncConfig(List<SyncElement> elements) {
		this.elements = Collections.unmodifiableList(elements);
	}

	public static SyncConfig load(File file, Logger logger) throws IOException {
		logger.log("Load config file: " + file);
		List<SyncElement> elements = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			File sourceFile = null;
			List<File> destFiles = new ArrayList<>();
			while((line = reader.readLine()) != null) {
				if(StringUtils.isBlank(line)) {
					continue;
				}
				if(Character.isWhitespace(line.charAt(0))) {
					// destination
					if(sourceFile == null) {
						continue;
					}
					PlaceHolderRootFile.findFile(line, logger, "  Destination").ifPresent(destFiles::add);
				} else {
					// source
					if(sourceFile != null) { // flush previous
						if(destFiles.isEmpty()) {
							logger.log("  No destination found for source: " + sourceFile);
						} else {
							elements.add(new SyncElement(sourceFile, destFiles));
						}
						destFiles.clear();
					}
					Optional<File> findFile = PlaceHolderRootFile.findFile(line, logger, "  Source");
					if(findFile.isPresent()) {
						sourceFile = findFile.get();
					} else {
						sourceFile = null;
					}
				}
			}
			if(sourceFile != null && ! destFiles.isEmpty()) {
				elements.add(new SyncElement(sourceFile, destFiles));
			}
		}
		return new SyncConfig(elements);
	}

	public List<SyncElement> getElements() {
		return elements;
	}

}
