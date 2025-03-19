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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.PlaceHolder;
import org.fagu.fmv.utils.Replacer;
import org.fagu.fmv.utils.Replacers;
import org.fagu.fmv.utils.collection.MapSortedSet;
import org.fagu.fmv.utils.collection.MultiValueMaps;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public class ByPatternClassifier<F extends FileFinder<M>, M extends Media> extends Classifier<F, M> {

	// -------------------------------------------------

	/**
	 * @author f.agu
	 *
	 * @param <M>
	 */
	public interface ReplacerMap<M extends Media> {

		void analyze(M media, ByPatternClassifier<?, M> byPatternClassifier);

		Replacer getReplacer(M media, String destPath, ByPatternClassifier<?, M> byPatternClassifier);
	}

	// -------------------------------------------------

	private final MediaTimeComparator<M> mediaTimeComparator;

	private final MapSortedSet<String, FileFinder<M>.InfosFile> map;

	private final String pattern;

	private final ReplacerMap<M> replacerMap;

	public ByPatternClassifier(F fileFinder, File destFolder, MediaTimeComparator<M> mediaTimeComparator, String pattern,
			ReplacerMap<M> replacerMap) {
		super(fileFinder, destFolder);
		this.mediaTimeComparator = Objects.requireNonNull(mediaTimeComparator);
		this.pattern = Objects.requireNonNull(pattern);
		this.replacerMap = Objects.requireNonNull(replacerMap);
		map = MultiValueMaps.sortedSet(new TreeMap<>(), () -> new TreeSet<>(mediaTimeComparator));
	}

	public MediaTimeComparator<M> getMediaTimeComparator() {
		return mediaTimeComparator;
	}

	public String getPattern() {
		return pattern;
	}

	@Override
	public void add(FileFinder<M>.InfosFile infosFile) {}

	@Override
	public List<File> classify(Converter<M> converter, ConverterListener<M> listener) throws IOException {
		List<File> files = new ArrayList<>(100);
		for(FileFinder<M>.InfosFile infosFile : finder.getAll()) {
			M media = infosFile.getMain();
			String extension = FilenameUtils.getExtension(media.getFile().getName());
			Map<String, String> rmap = replacerMap(media, converter.getFormat(extension));
			String key = PlaceHolder.format(pattern, Replacers.chain().map(rmap).unresolvableCopy());
			map.add(key, infosFile);
			replacerMap.analyze(media, this);
		}

		for(Entry<String, SortedSet<FileFinder<M>.InfosFile>> entry : map.entrySet()) {
			for(FileFinder<M>.InfosFile infosFile : entry.getValue()) {
				M media = infosFile.getMain();
				String extension = FilenameUtils.getExtension(media.getFile().getName());
				Map<String, String> map = replacerMap(media, converter.getFormat(extension));
				String path = PlaceHolder.format(pattern, Replacers.chain().map(map).unresolvableCopy());
				path = PlaceHolder.format(path, replacerMap.getReplacer(media, path, this));
				File destFile = new File(destFolder, path);
				if( ! destFile.getParentFile().exists()) {
					destFile.getParentFile().mkdirs();
				}
				converter.convert(media, infosFile, destFile, listener);
				files.add(destFile);
			}
		}
		return files;
	}

	@Override
	public void close() throws IOException {}

	// ***************************************

	private Map<String, String> replacerMap(M media, String format) {
		Map<String, String> map = new HashMap<>();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mediaTimeComparator.getTime(media));
		calendar.get(Calendar.YEAR);
		map.put("yyyy", StringUtils.leftPad(Integer.toString(calendar.get(Calendar.YEAR)), 4, '0'));
		map.put("MM", StringUtils.leftPad(Integer.toString(calendar.get(Calendar.MONTH) + 1), 2, '0'));
		map.put("dd", StringUtils.leftPad(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)), 2, '0'));

		map.put("HH", StringUtils.leftPad(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)), 2, '0'));
		map.put("mm", StringUtils.leftPad(Integer.toString(calendar.get(Calendar.MINUTE)), 2, '0'));
		map.put("SS", StringUtils.leftPad(Integer.toString(calendar.get(Calendar.SECOND)), 2, '0'));

		File srcFile = media.getFile();
		map.put("basename", FilenameUtils.getBaseName(srcFile.getName()).toLowerCase());
		map.put("extension", FilenameUtils.getExtension(srcFile.getName()).toLowerCase());
		map.put("filename", srcFile.getName());

		return map;
	}

}
