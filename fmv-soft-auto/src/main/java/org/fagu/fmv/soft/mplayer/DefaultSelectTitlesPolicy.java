package org.fagu.fmv.soft.mplayer;

/*-
 * #%L
 * fmv-soft-auto
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * @author fagu
 */
public class DefaultSelectTitlesPolicy implements SelectTitlesPolicy {

	private static final int MIN_LENGTH_PERCENT = 10;

	private static final Duration MIN_LENTGH_UNATARY = Duration.ofHours(1);

	@Override
	public Collection<MPlayerTitle> select(Collection<MPlayerTitle> titles) {
		SortedSet<MPlayerTitle> set = new TreeSet<>((t1, t2) -> t2.getLength().compareTo(t1.getLength())); // reverse
		set.addAll(titles);
		if(set.first().getLength().compareTo(org.fagu.fmv.utils.time.Duration.valueOf(MIN_LENTGH_UNATARY.getSeconds())) > 0) {
			return Collections.singletonList(set.first());
		}

		double totalDuration = titles
				.stream()
				.map(t -> t.getLength())
				.reduce((t1, t2) -> t1.add(t2))
				.get()
				.toSeconds();

		List<MPlayerTitle> list = new ArrayList<>();
		titles
				.stream()
				.filter(t -> (100D * t.getLength().toSeconds() / totalDuration) > MIN_LENGTH_PERCENT)
				.forEach(list::add);

		return list;
	}
}
