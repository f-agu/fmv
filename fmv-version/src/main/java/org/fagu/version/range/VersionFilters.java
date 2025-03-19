package org.fagu.version.range;

import org.fagu.version.Version;


/**
 * @author f.agu
 */
class VersionFilters {

	static final VersionFilter ACCEPT = new VersionFilter() {

		@Override
		public boolean test(Version version) {
			return true;
		}

		@Override
		public String toString() {
			return "*";
		}
	};

	static final VersionFilter REJECT = new VersionFilter() {

		@Override
		public boolean test(Version version) {
			return false;
		}

		@Override
		public String toString() {
			return "";
		}
	};

	private VersionFilters() {}

}
