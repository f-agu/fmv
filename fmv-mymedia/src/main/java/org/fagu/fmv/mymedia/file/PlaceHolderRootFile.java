package org.fagu.fmv.mymedia.file;

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

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.mymedia.logger.Logger;


/**
 * @author Utilisateur
 * @created 3 juin 2018 11:19:12
 */
public class PlaceHolderRootFile {

	private PlaceHolderRootFile() {}

	public static Optional<File> findFile(String path, Logger logger, String prefixMessage) {
		Optional<File> toFileOpt = toFile(path);
		if(toFileOpt.isPresent()) {
			File toFile = toFileOpt.get();
			if(toFile.exists()) {
				return Optional.of(toFile);
			}
			logger.log(prefixMessage + " not found: " + toFile);
		} else {
			logger.log(prefixMessage + " not found: " + path.trim());
		}
		return Optional.empty();
	}

	private static Optional<File> toFile(String path) {
		String p = path.trim();
		if('[' == p.charAt(0)) {
			String diskName = StringUtils.substringBetween(p, "[", "]");
			return FileUtils.getRootByName(diskName)
					.map(f -> new File(f, StringUtils.substringAfter(p, "]")));
		}
		return Optional.of(new File(p));
	}
}
