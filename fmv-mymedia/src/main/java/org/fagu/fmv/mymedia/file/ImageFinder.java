package org.fagu.fmv.mymedia.file;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 fagu
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
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.fagu.fmv.im.IMIdentifyImageMetadatas;
import org.fagu.fmv.im.Image;
import org.fagu.fmv.im.soft.Identify;
import org.fagu.fmv.mymedia.classify.duplicate.DuplicatedFiles;
import org.fagu.fmv.mymedia.classify.duplicate.DuplicatedResult;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.utils.file.DoneFuture;


/**
 * @author f.agu
 */
public class ImageFinder extends AutoSaveLoadFileFinder<Image> implements Serializable {

	private static final long serialVersionUID = - 6555092287035209202L;

	private static final int BUFFER_SIZE = 20;

	private static final Set<String> EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "tiff", "tif"));

	private final ExecutorService executorService;

	private final Soft identifySoft;

	public ImageFinder(Logger logger, File saveFile) {
		this(logger, saveFile, Runtime.getRuntime().availableProcessors());
	}

	public ImageFinder(Logger logger, File saveFile, int nThreads) {
		super(logger, EXTENSIONS, BUFFER_SIZE, saveFile, List.of(
				MediaWithMetadatasInfoFile.image(),
				new MD5InfoFile(),
				HashingInfoFile.perceptionHash()));
		if(nThreads > 1) {
			logger.log("Use " + nThreads + " threads");
			executorService = Executors.newFixedThreadPool(nThreads);
		} else {
			executorService = null;
		}
		identifySoft = Identify.search();
	}

	public DuplicatedResult analyzeDuplicatedFiles(List<DuplicatedFiles<?>> duplicatedFilesList) {
		getAllMap().forEach((fileFound, infosFile) -> duplicatedFilesList.forEach(df -> df.populate(fileFound, infosFile)));

		showAndLog("");
		boolean previousDup = false;
		for(DuplicatedFiles<?> duplicatedFiles : duplicatedFilesList) {
			if(previousDup) {
				showAndLog("");
				previousDup = false;
			}
			previousDup = duplicatedFiles.analyze();
		}
		if(previousDup) {
			showAndLog("");
		}
		return new DuplicatedResult(duplicatedFilesList);
	}

	@Override
	public void close() throws IOException {
		if(executorService != null) {
			executorService.shutdown();
			try {
				executorService.awaitTermination(1, TimeUnit.HOURS);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.close();
	}

	// *****************************************

	@Override
	protected Future<Map<FileFound, InfosFile>> flushToMap(List<FileFound> buffer, Consumer<List<FileFound>> consumer) {
		Callable<Map<FileFound, InfosFile>> callable = create(buffer, consumer);
		if(executorService == null) {
			try {
				return new DoneFuture<>(callable.call());
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return executorService.submit(callable);
	}

	// *****************************************

	// private Callable<Map<File, Image>> createMock(List<File> buffer, Consumer<List<File>> consumer) {
	// final Map<File, Image> outMap = buffer.stream().collect(Collectors.toMap(f -> f, f -> new Image(f)));
	// return () -> {
	// showAndLog(outMap.size());
	// Thread.sleep(2000);
	// return outMap;
	// };
	// }

	private Callable<Map<FileFound, InfosFile>> create(List<FileFound> buffer, Consumer<List<FileFound>> consumer) {
		List<File> files = buffer.stream()
				.map(FileFound::getFileFound)
				.filter(f -> f.length() > 1000L)
				.collect(Collectors.toList());
		return () -> {
			Map<File, IMIdentifyImageMetadatas> map = null;
			try {
				map = IMIdentifyImageMetadatas.with(files)
						.soft(identifySoft)
						.logger(cl -> logger.log(CommandLineUtils.toLine(cl)))
						.extractAll();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			Map<File, FileFound> reverseMap = buffer.stream().collect(Collectors.toMap(FileFound::getFileFound, ff -> ff));
			Map<FileFound, InfosFile> outMap = new LinkedHashMap<>(map.size());

			for(Entry<File, IMIdentifyImageMetadatas> entry : map.entrySet()) {
				Image image = new Image(entry.getKey(), entry.getValue());
				outMap.put(reverseMap.get(entry.getKey()), new InfosFile(image));
			}
			afterFlush(outMap);
			addAll(outMap);
			if(consumer != null) {
				consumer.accept(buffer);
			}
			return outMap;
		};
	}

}
