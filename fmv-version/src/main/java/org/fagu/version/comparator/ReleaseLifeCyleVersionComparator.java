package org.fagu.version.comparator;

import org.fagu.version.ReleaseLifeCycle;
import org.fagu.version.Version;
import org.fagu.version.VersionComparator;


/**
 * @author f.agu
 */
public class ReleaseLifeCyleVersionComparator implements VersionComparator {

	@Override
	public int compare(Version o1, Version o2) {
		ReleaseLifeCycle myReleaseLifeCycle = o1.getReleaseLifeCycle();
		ReleaseLifeCycle otherReleaseLifeCycle = o2.getReleaseLifeCycle();
		if((myReleaseLifeCycle == null || "".equals(myReleaseLifeCycle.toString()))
				&& (otherReleaseLifeCycle == null || "".equals(otherReleaseLifeCycle.toString()))) {
			return 0;
		}
		if(otherReleaseLifeCycle == null) {
			return - 1;
		}
		if(myReleaseLifeCycle == null) {
			return 1;
		}
		return myReleaseLifeCycle.compareTo(otherReleaseLifeCycle);
	}

	@Override
	public int getOrder() {
		return 2000;
	}

}
