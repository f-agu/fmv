package org.fagu.fmv.mymedia.reduce.neocut;

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
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.ffmpeg.FFHelper;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 * @created 4 avr. 2018 09:48:32
 */
public class Images {

	private final File imageFolder;

	private final Set<Time> times;

	/**
	 * @param imageFolder
	 * @param times
	 */
	public Images(File imageFolder, Set<Time> times) {
		this.imageFolder = Objects.requireNonNull(imageFolder);
		this.times = new HashSet<>(times);
		try {
			FileUtils.forceMkdir(imageFolder);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * @param movieFile
	 * @return
	 * @throws IOException
	 */
	public Map<Time, File> getImages(File movieFile) throws IOException {
		Map<Time, File> map = new HashMap<>();
		for(Time time : times) {
			map.put(time, getOrExtractImage(imageFolder, movieFile, time));
		}
		return map;
	}

	/**
	 * @param movieFile
	 * @param time
	 * @return
	 * @throws IOException
	 */
	public static File extractImage(File movieFile, Time time) throws IOException {
		return getOrExtractImage(movieFile.getParentFile(), movieFile, time);
	}

	// ****************************************************

	/**
	 * @param parentFile
	 * @param movieFile
	 * @param time
	 * @return
	 * @throws IOException
	 */
	private static File getOrExtractImage(File parentFile, File movieFile, Time time) throws IOException {
		File imgFile = new File(parentFile,
				FilenameUtils.getBaseName(movieFile.getName()) + '-' + time.toString().replace(':', '_') + ".png");
		if(imgFile.exists() && imgFile.length() > 1) {
			return imgFile;
		}
		System.out.println("  Generating images for " + movieFile.getName() + "  [" + time + "]...");
		FFHelper.extractOneThumbnail(movieFile, imgFile, time);
		return imgFile;
	}

}
