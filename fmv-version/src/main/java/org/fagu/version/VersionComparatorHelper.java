package org.fagu.version;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * @author f.agu
 */
public class VersionComparatorHelper {

	public static int compare(Version o1, Version o2) {
		for(VersionComparator versionComparator : getVersionComparator()) {
			int compare = versionComparator.compare(o1, o2);
			if(compare != 0) {
				return compare;
			}
		}
		return 0;
	}

	// ********************************************************************

	private static List<VersionComparator> getVersionComparator() {
		SortedMap<Integer, List<VersionComparator>> map = new TreeMap<>();

		// from SPI
		int size = 0;
		for(VersionComparator versionComparator : ServiceLoader.load(VersionComparator.class, VersionComparatorHelper.class.getClassLoader())) {
			int order = versionComparator.getOrder();
			map.computeIfAbsent(order, k -> new ArrayList<>())
					.add(versionComparator);
			++size;
		}

		List<VersionComparator> comparators = new ArrayList<>(size);
		for(Entry<Integer, List<VersionComparator>> entry : map.entrySet()) {
			comparators.addAll(entry.getValue());
		}
		return comparators;
	}
}
