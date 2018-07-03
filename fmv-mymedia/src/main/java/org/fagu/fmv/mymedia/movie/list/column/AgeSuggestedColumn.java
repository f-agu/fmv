package org.fagu.fmv.mymedia.movie.list.column;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.fagu.fmv.mymedia.logger.Logger;


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
		Map<String, Optional<Ages>> notInCache = AGES_FILM.getNotInCache();
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
