package org.fagu.fmv.mymedia.movie.list.column;

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
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalInt;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.movie.Sagas;
import org.fagu.fmv.mymedia.movie.list.Column;
import org.fagu.fmv.mymedia.movie.list.DataStore;


/**
 * @author Utilisateur
 * @created 4 mai 2018 14:37:34
 */
public class SagaOrderColumn implements Column {

	@Override
	public String title() {
		return "Saga ordre";
	}

	@Override
	public Optional<String> value(Path rootPath, File file, DataStore dataStore) {
		String title = FilenameUtils.getBaseName(file.getName());
		return Sagas.getInstance()
				.getSagaFor(title)
				.map(s -> {
					OptionalInt intOpt = s.getIndex(title);
					return intOpt.isPresent() ? Integer.toString(intOpt.getAsInt()) : null;
				});
	}
}
