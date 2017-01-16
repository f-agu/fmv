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

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public class TimeOffsetComparator implements Comparator<FileFinder<Movie>.InfosFile> {

	private final Map<Predicate<Movie>, Long> filterMap = new LinkedHashMap<>();

	/**
	 *
	 */
	public TimeOffsetComparator() {}

	/**
	 * @param filter
	 * @param timeDiff
	 */
	public void addFilter(Predicate<Movie> filter, long timeDiff) {
		filterMap.put(filter, timeDiff);
	}

	/**
	 * @param image
	 * @return
	 */
	public long getTime(Movie movie) {
		long time = 0;
		for(Entry<Predicate<Movie>, Long> entry : filterMap.entrySet()) {
			if(entry.getKey().test(movie)) {
				time += entry.getValue();
			}
		}
		return movie.getTime() + time;
	}

	/**
	 * @param infosFile
	 * @return
	 */
	public long getTime(FileFinder<Movie>.InfosFile infosFile) {
		return getTime(infosFile.getMain());
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(FileFinder<Movie>.InfosFile o1, FileFinder<Movie>.InfosFile o2) {
		long t1 = getTime(o1);
		long t2 = getTime(o2);
		return t1 < t2 ? - 1 : t1 > t2 ? 1 : 0;
	}

}
