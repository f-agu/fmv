package org.fagu.version.range;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fagu.version.Version;


/**
 * @author f.agu
 */
public interface VersionFilter extends Predicate<Version> {

	default boolean anyMatch(Version... versions) {
		return anyMatch(Arrays.asList(versions));
	}

	default boolean anyMatch(Collection<Version> versions) {
		return versions.stream().anyMatch(this::test);
	}

	// -----------------------------

	static VersionFilter accept() {
		return VersionFilters.ACCEPT;
	}

	static VersionFilter reject() {
		return VersionFilters.REJECT;
	}

	static VersionFilter constant(boolean accepted) {
		return accepted ? VersionFilters.ACCEPT : VersionFilters.REJECT;
	}

	static VersionFilter of(Version... versions) {
		return of(versions != null ? Arrays.asList(versions) : Collections.emptyList());
	}

	static VersionFilter of(Collection<Version> versions) {
		if(versions == null || versions.isEmpty()) {
			return reject();
		}
		Set<Version> set = new HashSet<>(versions);
		return new VersionFilter() {

			@Override
			public boolean test(Version v) {
				return set.contains(v);
			}

			@Override
			public String toString() {
				return set.stream().map(Version::toString).collect(Collectors.joining(","));
			}
		};
	}

	static VersionFilter upperThan(Version version) {
		return new VersionFilter() {

			@Override
			public boolean test(Version v) {
				return v.isUpperThan(version);
			}

			@Override
			public String toString() {
				return "(" + version + ",)";
			}
		};
	}

	static VersionFilter upperOrEqualsThan(Version version) {
		return new VersionFilter() {

			@Override
			public boolean test(Version v) {
				return v.isUpperOrEqualsThan(version);
			}

			@Override
			public String toString() {
				return "[" + version + ",)";
			}
		};
	}

	static VersionFilter lowerThan(Version version) {
		return new VersionFilter() {

			@Override
			public boolean test(Version v) {
				return v.isLowerThan(version);
			}

			@Override
			public String toString() {
				return "(," + version + ')';
			}
		};
	}

	static VersionFilter lowerOrEqualsThan(Version version) {
		return new VersionFilter() {

			@Override
			public boolean test(Version v) {
				return v.isLowerOrEqualsThan(version);
			}

			@Override
			public String toString() {
				return "(," + version + ']';
			}
		};
	}

	static VersionFilter or(VersionFilter... versionFilters) {
		return or(versionFilters != null ? Arrays.asList(versionFilters) : Collections.emptyList());
	}

	static VersionFilter or(Collection<VersionFilter> versionFilters) {
		if(versionFilters == null || versionFilters.isEmpty()) {
			return reject();
		}
		return new VersionFilter() {

			@Override
			public boolean test(Version v) {
				return versionFilters.stream().anyMatch(vf -> vf.test(v));
			}

			@Override
			public String toString() {
				return versionFilters.stream().map(VersionFilter::toString).collect(Collectors.joining(","));
			}
		};
	}

	static VersionFilter parse(String text) {
		return VersionRange.parse(text);
	}

}
