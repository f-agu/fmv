package org.fagu.fmv.mymedia.classify;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public interface MediaTimeComparator<M extends Media> extends Comparator<FileFinder<M>.InfosFile> {

	default long getTime(M media) {
		return media.getTime();
	}

	default long getTime(FileFinder<M>.InfosFile infosFile) {
		return getTime(infosFile.getMain());
	}

	@Override
	default public int compare(FileFinder<M>.InfosFile o1, FileFinder<M>.InfosFile o2) {
		long t1 = getTime(o1);
		long t2 = getTime(o2);
		if(t1 < t2) {
			return - 1;
		}
		if(t1 > t2) {
			return 1;
		}
		String file1 = o1.getMain().getFile().getName();
		String file2 = o2.getMain().getFile().getName();
		int compare = file1.compareToIgnoreCase(file2);
		if(compare != 0) {
			return compare;
		}
		return Integer.compare(o1.hashCode(), o2.hashCode());
	}

}
