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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.fagu.fmv.ffmpeg.FFHelper;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.mymedia.classify.movie.Movie;
import org.fagu.fmv.utils.file.DoneFuture;


/**
 * @author f.agu
 */
public class MovieFinder extends AutoSaveLoadFileFinder<Movie> {

	private static final long serialVersionUID = 104880189236711184L;

	private static final int BUFFER_SIZE = 1;

	private static final Set<String> EXTENSIONS = new HashSet<>(Arrays.asList("mp4", "mov", "avi", "3gp"));

	public MovieFinder(File saveFile) {
		super(EXTENSIONS, BUFFER_SIZE, saveFile, MediaWithMetadatasInfoFile.movie());
	}

	// *****************************************

	@Override
	protected Future<Map<FileFound, InfosFile>> flushToMap(List<FileFound> buffer, Consumer<List<FileFound>> consumer) {
		Map<FileFound, InfosFile> outMap = new LinkedHashMap<>(buffer.size());
		for(FileFound fileFound : buffer) {
			File file = fileFound.getFileFound();
			try {
				MovieMetadatas movieMetadatas = FFHelper.videoMetadatas(file);
				Map<FileFound, InfosFile> flushMap = Collections.singletonMap(fileFound, new InfosFile(new Movie(file, movieMetadatas)));
				outMap.putAll(flushMap);
				if(consumer != null) {
					consumer.accept(Collections.singletonList(fileFound));
				}
				afterFlush(flushMap);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return new DoneFuture<>(outMap);
	}

}
