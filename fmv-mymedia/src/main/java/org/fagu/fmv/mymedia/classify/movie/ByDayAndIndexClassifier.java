package org.fagu.fmv.mymedia.classify.movie;

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
import org.fagu.fmv.mymedia.classify.Converter;
import org.fagu.fmv.mymedia.classify.ConverterListener;
import org.fagu.fmv.mymedia.file.MovieFinder;
import org.fagu.fmv.utils.collection.MapSortedSet;
import org.fagu.fmv.utils.collection.MultiValueMaps;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public class ByDayAndIndexClassifier extends MovieClassifier {

	private final TimeOffsetComparator timeOffsetComparator;

	private final MapSortedSet<String, FileFinder<Movie>.InfosFile> map;

	public ByDayAndIndexClassifier(MovieFinder movieFinder, File destFolder, TimeOffsetComparator timeOffsetComparator) {
		super(movieFinder, destFolder);
		this.timeOffsetComparator = timeOffsetComparator;
		map = MultiValueMaps.sortedSet(new TreeMap<>(), () -> new TreeSet<>(timeOffsetComparator));
	}

	@Override
	public void add(FileFinder<Movie>.InfosFile infosFile) {
		long time = timeOffsetComparator.getTime(infosFile);
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = dayFormat.format(new Date(time));
		map.add(strDate, infosFile);
	}

	@Override
	public List<File> classify(Converter<Movie> converter, ConverterListener<Movie> listener) throws IOException {
		List<File> files = new ArrayList<>(100);
		for(Entry<String, SortedSet<FileFinder<Movie>.InfosFile>> entry : map.entrySet()) {
			int index = 0;
			for(FileFinder<Movie>.InfosFile infosFile : entry.getValue()) {
				Movie movie = infosFile.getMain();
				String extension = FilenameUtils.getExtension(movie.getFile().getName());
				StringBuilder buf = new StringBuilder();
				buf.append(entry.getKey()).append(" (");
				buf.append(StringUtils.leftPad(Integer.toString(++index), 2, '0')).append(") .");
				buf.append(converter.getFormat(extension));
				File destFile = new File(destFolder, buf.toString());
				converter.convert(movie, infosFile, destFile, listener);
				files.add(destFile);
			}
		}
		return files;
	}

	@Override
	public void close() throws IOException {}
}
