package org.fagu.fmv.mymedia.movie.saga;

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
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.FileTypeUtils;
import org.fagu.fmv.mymedia.file.PlaceHolderRootFile;
import org.fagu.fmv.mymedia.file.filter.ExcludeFMVFilefilter;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.movie.Sagas;
import org.fagu.fmv.mymedia.movie.saga.Saga.Movie;
import org.fagu.fmv.mymedia.utils.ScannerHelper;
import org.fagu.fmv.mymedia.utils.ScannerHelper.YesNo;


/**
 * @author Utilisateur
 * @created 2 juin 2018 14:41:57
 */
public class Bootstrap {

	private static final String LOG_FILE_PROPERTY = "fmv.movie.saga.logfile";

	private final Logger logger;

	private final Map<String, File> movieNameMap;

	private final File destFolder;

	public Bootstrap(Logger logger, File srcFolder) {
		this.logger = Objects.requireNonNull(logger);
		this.destFolder = new File(srcFolder, "_Sagas");
		movieNameMap = findMovie(srcFolder, destFolder);
	}

	public void doIt(Saga saga) throws IOException {
		int index = 0;
		Set<Movie> movies = saga.getMovies();
		int maxLength = Integer.toString(movies.size()).length();
		File sagaRoot = new File(destFolder, saga.getName());
		Set<File> existingFiles = filesToSet(sagaRoot.listFiles(FileTypeUtils.with(FileType.VIDEO)::verify));
		FileUtils.forceMkdir(sagaRoot);
		for(Movie movie : movies) {
			++index;
			File file = movieNameMap.get(movie.getFileName());
			if(file == null) {
				logger.log("Movie not found: " + movie.getFileName());
				continue;
			}
			String title = new StringBuilder()
					.append(StringUtils.leftPad(Integer.toString(index), maxLength, '0'))
					.append(" - ")
					.append(movie.getFrenchTitle())
					.append('.').append(FilenameUtils.getExtension(file.getName()))
					.toString();
			File destFile = new File(sagaRoot, title);
			// System.out.println(file + " -> " + destFile);
			if(destFile.exists()) {
				if( ! isSimilar(file, destFile)) {
					logger.log("Not similar files: " + file + " -> " + destFile);
				}
				existingFiles.remove(destFile);
				createSubtitleLink(destFile, file);
				continue;
			}
			createLink(destFile, file);
		}
		existingFiles.forEach(f -> {
			if(YesNo.YES.equals(ScannerHelper.yesNo("Delete " + f))) {
				f.delete();
			}
		});
	}

	private void createLink(File linkFile, File existingFile) throws IOException {
		Files.createLink(linkFile.toPath(), existingFile.toPath());
		createSubtitleLink(linkFile, existingFile);
	}

	private void createSubtitleLink(File linkFile, File existingFile) throws IOException {
		File existingSubtitleFile = getSubtitleFile(existingFile);
		if(existingSubtitleFile.exists()) {
			File linkSubtitleFile = getSubtitleFile(linkFile);
			if(linkSubtitleFile.exists()) {
				if(isSimilar(existingSubtitleFile, linkSubtitleFile)) {
					return;
				}
				if( ! linkSubtitleFile.delete()) {
					logger.log("Unable to delete: " + linkSubtitleFile);
				}
			}
			Files.createLink(linkSubtitleFile.toPath(), existingSubtitleFile.toPath());
		}
	}

	private File getSubtitleFile(File file) {
		return new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName()) + ".srt");
	}

	private boolean isSimilar(File srcFile, File destFile) {
		return srcFile.length() == destFile.length();
	}

	private Set<File> filesToSet(File[] files) {
		if(files == null || files.length == 0) {
			return new HashSet<>();
		}
		return new HashSet<>(Arrays.asList(files));
	}

	// *******************

	private Map<String, File> findMovie(File folder, File destFolder) {
		Map<String, File> map = new HashMap<>();
		findMovie(folder, map, destFolder);
		return Collections.unmodifiableMap(map);
	}

	private void findMovie(File file, Map<String, File> map, File destFolder) {
		File[] files = file.listFiles(ExcludeFMVFilefilter.INSTANCE);
		if(files != null) {
			for(File f : files) {
				if(f == destFolder) {
					continue;
				}
				if(FileTypeUtils.with(FileType.VIDEO).verify(f)) {
					map.put(FilenameUtils.getBaseName(f.getName()), f);
				} else if(f.isDirectory()) {
					findMovie(f, map, destFolder);
				} else {
					// throw new RuntimeException("Not implemented ! " + f);
				}
			}
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private static Logger openLogger() throws IOException {
		String property = System.getProperty(LOG_FILE_PROPERTY);
		if(property == null) {
			property = "sync.log";
		}
		return LoggerFactory.openLogger(new File(property));
	}

	// *********************************

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Sagas sagas = Sagas.getInstance();
		try (Logger logger = openLogger()) {
			Logger forkLogger = Loggers.fork(logger, Loggers.systemOut());
			sagas.log(forkLogger);
			Set<Saga> sagaList = sagas.getSagas();
			if(sagaList.isEmpty()) {
				logger.log("Sagas not found");
				return;
			}
			for(String arg : args) {
				Optional<File> findFile = PlaceHolderRootFile.findFile(arg, logger, "Path");
				if(findFile.isPresent()) {
					Bootstrap bootstrap = new Bootstrap(forkLogger, findFile.get());
					for(Saga saga : sagaList) {
						bootstrap.doIt(saga);
					}
				}
			}
		}
	}

}
