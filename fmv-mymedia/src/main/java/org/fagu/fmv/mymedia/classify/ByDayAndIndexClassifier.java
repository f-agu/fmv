package org.fagu.fmv.mymedia.classify;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.collection.MapSortedSet;
import org.fagu.fmv.utils.collection.MultiValueMaps;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public class ByDayAndIndexClassifier extends Classifier<FileFinder<Media>, Media> {

	private final static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private final MediaTimeComparator<Media> mediaTimeComparator;

	private final MapSortedSet<String, FileFinder<Media>.InfosFile> map;

	/**
	 * @param fileFinder
	 * @param destFolder
	 * @param mediaTimeComparator
	 */
	public ByDayAndIndexClassifier(FileFinder<Media> fileFinder, File destFolder, MediaTimeComparator<Media> mediaTimeComparator) {
		super(fileFinder, destFolder);
		this.mediaTimeComparator = mediaTimeComparator;
		map = MultiValueMaps.sortedSet(new TreeMap<>(), () -> new TreeSet<>(mediaTimeComparator));
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.Classifier#add(org.fagu.fmv.utils.file.FileFinder.InfosFile)
	 */
	@Override
	public void add(FileFinder<Media>.InfosFile infosFile) {
		long time = mediaTimeComparator.getTime(infosFile);
		String strDate = DAY_FORMAT.format(new Date(time));
		map.add(strDate, infosFile);
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.Classifier#classify(org.fagu.fmv.mymedia.classify.Converter)
	 */
	@Override
	public List<File> classify(Converter<Media> converter, ConverterListener<Media> listener) throws IOException {
		List<File> files = new ArrayList<>(100);
		int idxLen = 1 + (int)Math.log10(map.sizeValues());

		int index = 0;
		for(Entry<String, SortedSet<FileFinder<Media>.InfosFile>> entry : map.entrySet()) {
			File curFolder = new File(destFolder, entry.getKey());
			if( ! curFolder.exists()) {
				curFolder.mkdirs();
			}
			for(FileFinder<Media>.InfosFile infosFile : entry.getValue()) {
				Media media = infosFile.getMain();
				File srcFile = media.getFile();
				StringBuilder buf = new StringBuilder();
				buf.append(StringUtils.leftPad(Integer.toString(++index), idxLen, '0')).append('.');
				buf.append(FilenameUtils.getExtension(srcFile.getName()).toLowerCase());
				File destFile = new File(curFolder, buf.toString());
				converter.convert(media, infosFile, destFile, listener);
				files.add(destFile);
			}
		}
		return files;
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {}
}
