package org.fagu.fmv.media;

/*-
 * #%L
 * fmv-media
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

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;


/**
 * @author Utilisateur
 * @created 6 juil. 2018 22:42:11
 */
public abstract class FileIs {

	private final FileType fileType;

	private final Set<String> extensions;

	public FileIs(FileType fileType, Collection<String> extensions) {
		this.fileType = Objects.requireNonNull(fileType);
		this.extensions = extensions.stream()
				.filter(Objects::nonNull)
				.map(String::toLowerCase)
				.collect(Collectors.toSet());
	}

	public Optional<FileType> getFor(String fileName) {
		String extension = FilenameUtils.getExtension(fileName);
		return extension != null && extensions.contains(extension.toLowerCase()) ? Optional.of(fileType) : Optional.empty();
	}

	public Optional<FileType> getFor(File file) {
		return file.isFile() ? getFor(file.getName()) : Optional.empty();
	}

	public Optional<FileType> getFor(Path path) {
		return getFor(path.toFile());
	}

}
