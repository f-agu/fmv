package org.fagu.fmv.mymedia.classify.duplicate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.fagu.fmv.mymedia.logger.Logger;


/**
 * @author Oodrive
 * @author f.agu
 * @created 18 mars 2025 14:34:28
 */
public abstract class AbstractDuplicatedFiles<K> implements DuplicatedFiles<K> {

	protected final Logger logger;

	private final String title;

	private BiFunction<K, List<FileInfosFile>, String> chapterTitleFunction;

	private BiFunction<K, List<FileInfosFile>, String> detailsFunction;

	protected Map<K, List<FileInfosFile>> cache;

	protected AbstractDuplicatedFiles(Logger logger, String title, BiFunction<K, List<FileInfosFile>, String> chapterTitleFunction,
			BiFunction<K, List<FileInfosFile>, String> detailsFunction) {
		this.logger = Objects.requireNonNull(logger);
		this.title = Objects.requireNonNull(title);
		this.chapterTitleFunction = Objects.requireNonNull(chapterTitleFunction);
		this.detailsFunction = Objects.requireNonNull(detailsFunction);
	}

	@Override
	public Map<K, List<FileInfosFile>> getDuplicateds() {
		if(cache == null) {
			cache = loadCache();
			AtomicInteger totalToDelete = new AtomicInteger();
			cache.forEach((key, infosFiles) -> {
				if(infosFiles.size() > 1) {
					totalToDelete.addAndGet(infosFiles.size() - 1);
				}
			});
			logger.log(getClass().getSimpleName() + " can delete " + totalToDelete + " file(s)");
		}
		return cache;
	}

	@Override
	public boolean analyze() {
		Map<K, List<FileInfosFile>> map = getDuplicateds();
		if(map.isEmpty()) {
			return false;
		}
		showAndLog("");
		showAndLog("Somes files have the same " + title + " :");
		map.forEach((key, infosFiles) -> {
			showAndLog("  " + chapterTitleFunction.apply(key, infosFiles));
			showAndLog("     " + detailsFunction.apply(key, infosFiles));
		});
		return true;
	}

	// ************************************************

	protected void showAndLog(String msg) {
		logger.log(msg);
		System.out.println(msg);
	}

	protected Map<K, List<FileInfosFile>> loadCache() {
		Map<K, List<FileInfosFile>> map = getMap();
		Supplier<Map<K, List<FileInfosFile>>> mapSupplier = HashMap::new;
		if(map instanceof SortedMap) {
			mapSupplier = TreeMap::new;
		}
		Map<K, List<FileInfosFile>> retMap = mapSupplier.get();
		map.forEach((key, infosFiles) -> {
			if(infosFiles.size() > 1) {
				retMap.put(key, infosFiles);
			}
		});
		return retMap;
	}

}
