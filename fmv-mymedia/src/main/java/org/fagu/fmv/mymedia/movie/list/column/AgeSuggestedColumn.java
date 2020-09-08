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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.movie.age.Ages;


/**
 * @author Utilisateur
 * @created 4 mai 2018 14:37:34
 */
public class AgeSuggestedColumn extends AgeFilteredColumn {

	private final Logger logger;

	public AgeSuggestedColumn(Logger logger) {
		super(Ages::getSuggested);
		this.logger = Objects.requireNonNull(logger);
	}

	@Override
	public String title() {
		return "âge suggéré";
	}

	@Override
	public void close() throws Exception {
		Map<String, Optional<Ages>> notInCache = AGES_FILM.getAfterSearchNotInCache();
		if(notInCache.isEmpty()) {
			return;
		}
		Optional<File> cacheFileOpt = AGES_CACHE.getCacheFile();
		if(cacheFileOpt.isPresent()) {
			logger.log("Age cache to add in: " + cacheFileOpt.get());
		} else {
			logger.log("Age cache to add: (file undefined)");
		}
		notInCache.forEach((k, v) -> {
			StringBuilder sb = new StringBuilder(k);
			v.ifPresent(ages -> sb.append('\t').append(ages.getLegal()).append('\t').append(ages.getSuggested()));
			logger.log(sb.toString());
		});
	}

}
