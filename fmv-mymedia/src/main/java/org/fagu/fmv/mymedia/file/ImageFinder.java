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

import org.fagu.fmv.im.Image;
import org.fagu.fmv.im.ImageMetadatas;
import org.fagu.fmv.utils.file.DoneFuture;


/**
 * @author f.agu
 */
public class ImageFinder extends AutoSaveLoadFileFinder<Image> implements Serializable {

	private static final long serialVersionUID = - 6555092287035209202L;

	private static final int BUFFER_SIZE = 20;

	private static final Set<String> EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "tiff", "tif"));

	private final ExecutorService executorService;

	/**
	 * @param saveFile
	 */
	public ImageFinder(File saveFile) {
		this(saveFile, Runtime.getRuntime().availableProcessors());
	}

	/**
	 * @param saveFile
	 * @param nThreads
	 */
	public ImageFinder(File saveFile, int nThreads) {
		super(EXTENSIONS, BUFFER_SIZE, saveFile, MediaWithMetadatasInfoFile.image());
		if(nThreads > 1) {
			executorService = Executors.newFixedThreadPool(nThreads);
		} else {
			executorService = null;
		}
	}

	/**
	 * @see org.fagu.fmv.mymedia.file.AutoSaveLoadFileFinder#close()
	 */
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

	/**
	 * @see org.fagu.fmv.utils.file.FileFinder#flushToMap(java.util.List, java.util.function.Consumer)
	 */
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

	/**
	 * @param buffer
	 * @param consumer
	 * @return
	 */
	// private Callable<Map<File, Image>> createMock(List<File> buffer, Consumer<List<File>> consumer) {
	// final Map<File, Image> outMap = buffer.stream().collect(Collectors.toMap(f -> f, f -> new Image(f)));
	// return () -> {
	// System.out.println(outMap.size());
	// Thread.sleep(2000);
	// return outMap;
	// };
	// }

	/**
	 * @param buffer
	 * @param consumer
	 * @return
	 */
	private Callable<Map<FileFound, InfosFile>> create(List<FileFound> buffer, Consumer<List<FileFound>> consumer) {
		List<File> files = buffer.stream().map(FileFound::getFileFound).collect(Collectors.toList());
		return () -> {
			Map<File, ImageMetadatas> map = null;
			try {
				map = ImageMetadatas.with(files).extractAll();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			Map<File, FileFound> reverseMap = buffer.stream().collect(Collectors.toMap(FileFound::getFileFound, ff -> ff));
			Map<FileFound, InfosFile> outMap = new LinkedHashMap<>(map.size());

			for(Entry<File, ImageMetadatas> entry : map.entrySet()) {
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
