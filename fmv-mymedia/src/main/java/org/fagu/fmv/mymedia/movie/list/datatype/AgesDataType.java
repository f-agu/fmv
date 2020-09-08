package org.fagu.fmv.mymedia.movie.list.datatype;

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
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.movie.age.Ages;
import org.fagu.fmv.mymedia.movie.age.AgesFilm;
import org.fagu.fmv.mymedia.movie.list.DataType;


/**
 * @author Utilisateur
 * @created 4 mai 2018 21:37:47
 */
public class AgesDataType implements DataType<Ages> {

	private final AgesFilm agesFilm;

	public AgesDataType(AgesFilm agesFilm) {
		this.agesFilm = Objects.requireNonNull(agesFilm);
	}

	@Override
	public Optional<Ages> extractData(File file) {
		return agesFilm.getAges(FilenameUtils.getBaseName(file.getName()));
	}
}
