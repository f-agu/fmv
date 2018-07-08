package org.fagu.fmv.mymedia.movie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.movie.saga.Saga;


/**
 * @author Utilisateur
 * @created 6 mai 2018 12:10:13
 */
public class Sagas {

	private static final Sagas INSTANCE = new Sagas();

	private final Map<String, Saga> sagaTitleMap;

	private final NavigableSet<Saga> sagas;

	private Sagas() {
		sagaTitleMap = Collections.unmodifiableMap(loadSagas());
		sagas = Collections.unmodifiableNavigableSet(sagaTitleMap.values().stream()
				.distinct()
				.collect(Collectors.toCollection(TreeSet::new)));
	}

	public static Sagas getInstance() {
		return INSTANCE;
	}

	public NavigableSet<Saga> getSagas() {
		return sagas;
	}

	public Optional<Saga> getSagaFor(String title) {
		return Optional.ofNullable(sagaTitleMap.get(title.toLowerCase()));
	}

	public void log(Logger logger) {
		logger.log("Found " + sagas.size() + " sagas:");
		sagas.forEach(s -> logger.log("  " + s.getName() + ": " + s.getFileNames().size() + " movies"));
	}

	// *************************************************

	private static Map<String, Saga> loadSagas() {
		String folders = System.getProperty("fmv.movie.list.sagas.folders");
		if(folders == null) {
			return Collections.emptyMap();
		}
		Map<String, Saga> sagaMap = new HashMap<>();
		for(String folder : folders.split(",")) {
			File file = new File(folder.trim());
			if(file.exists()) {
				searchFile(sagaMap, file);
			} else {
				throw new UncheckedIOException(new FileNotFoundException(file.getAbsolutePath()));
			}
		}
		return sagaMap;
	}

	private static void searchFile(Map<String, Saga> sagaMap, File file) {
		if(file.isFile()) {
			if("saga".equalsIgnoreCase(FilenameUtils.getExtension(file.getName()))) {
				loadSaga(sagaMap, file);
			}
			return;
		}
		File[] files = file.listFiles();
		if(files != null) {
			for(File f : files) {
				searchFile(sagaMap, f);
			}
		}
	}

	private static void loadSaga(Map<String, Saga> sagaMap, File sagaFile) {
		try {
			Saga saga = Saga.load(sagaFile);
			saga.getFileNames().forEach(t -> sagaMap.put(t.toLowerCase(), saga));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
