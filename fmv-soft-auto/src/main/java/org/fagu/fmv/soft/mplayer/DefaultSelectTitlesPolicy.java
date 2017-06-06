package org.fagu.fmv.soft.mplayer;

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

	/**
	 * @see org.fagu.fmv.soft.mplayer.SelectTitlesPolicy#select(java.util.Collection)
	 */
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
