package org.fagu.version.range;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class VersionRange implements VersionFilter {

	public enum Combinatoric {
		INCLUDE, EXCLUDE
	}

	// ------------------------------------------------

	public static class Builder {

		private final Version minVersion;

		private final Combinatoric minCombinatoric;

		private Builder(Version minVersion, Combinatoric minCombinatoric) {
			this.minVersion = Objects.requireNonNull(minVersion);
			this.minCombinatoric = Objects.requireNonNull(minCombinatoric);
		}

		public VersionRange and(Version maxVersion, Combinatoric combinatoric) {
			Objects.requireNonNull(maxVersion);
			Objects.requireNonNull(combinatoric);
			return new VersionRange(minVersion, minCombinatoric, maxVersion, combinatoric);

		}
	}

	// ------------------------------------------------

	private final Version minVersion;

	private final Combinatoric minCombinatoric;

	private final Version maxVersion;

	private final Combinatoric maxCombinatoric;

	private final VersionFilter minFilter;

	private final VersionFilter maxFilter;

	private VersionRange(Version minVersion, Combinatoric minCombinatoric, Version maxVersion, Combinatoric maxCombinatoric) {
		this.minVersion = Objects.requireNonNull(minVersion);
		this.minCombinatoric = Objects.requireNonNull(minCombinatoric);
		this.maxVersion = Objects.requireNonNull(maxVersion);
		this.maxCombinatoric = Objects.requireNonNull(maxCombinatoric);
		this.minFilter = minCombinatoric == Combinatoric.INCLUDE ? VersionFilter.upperOrEqualsThan(minVersion)
				: VersionFilter.upperThan(minVersion);
		this.maxFilter = maxCombinatoric == Combinatoric.INCLUDE ? VersionFilter.lowerOrEqualsThan(maxVersion)
				: VersionFilter.lowerThan(maxVersion);
	}

	public static Builder between(Version minVersion, Combinatoric combinatoric) {
		return new Builder(minVersion, combinatoric);
	}

	public static VersionFilter parse(String inText) {
		String text = inText.replace(" ", "");
		List<VersionFilter> filters = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		Iterator<String> iterator = text.chars().mapToObj(i -> Character.toString((char)i)).iterator();
		while(iterator.hasNext()) {
			String c = iterator.next();
			switch(c) {
				case "(":
					filters.addAll(basics(current.toString()));
					current = new StringBuilder();
					filters.add(parseBetween(Combinatoric.EXCLUDE, iterator));
					break;
				case "[":
					filters.addAll(basics(current.toString()));
					current = new StringBuilder();
					filters.add(parseBetween(Combinatoric.INCLUDE, iterator));
					break;
				case ",":
					filters.addAll(basics(current.toString()));
					current = new StringBuilder();
					break;
				default:
					current.append(c);
			}
		}
		filters.addAll(basics(current.toString()));
		return VersionFilter.or(filters);
	}

	public Version getMinVersion() {
		return minVersion;
	}

	public Combinatoric getMinCombinatoric() {
		return minCombinatoric;
	}

	public Version getMaxVersion() {
		return maxVersion;
	}

	public Combinatoric getMaxCombinatoric() {
		return maxCombinatoric;
	}

	public VersionFilter getMinFilter() {
		return minFilter;
	}

	public VersionFilter getMaxFilter() {
		return maxFilter;
	}

	@Override
	public boolean test(Version version) {
		return minFilter.test(version) && maxFilter.test(version);
	}

	@Override
	public String toString() {
		String start = minFilter.toString();
		String end = maxFilter.toString();
		return new StringBuilder()
				.append(start.substring(0, start.indexOf(',')))
				.append(',')
				.append(end.substring(end.indexOf(',') + 1))
				.toString();
	}

	// *******************************************************

	private static List<VersionFilter> basics(String text) {
		StringTokenizer tokenizer = new StringTokenizer(text, ",");
		List<VersionFilter> filters = new ArrayList<>();
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			if( ! token.isEmpty()) {
				filters.add(VersionFilter.of(Version.parse(token)));
			}
		}
		return filters;
	}

	private static VersionFilter parseBetween(Combinatoric start, Iterator<String> iterator) {
		StringBuilder sb = new StringBuilder();
		while(iterator.hasNext()) {
			String c = iterator.next();
			if(")".equals(c)) {
				return parseBetween(start, sb.toString(), Combinatoric.EXCLUDE);
			} else if("]".equals(c)) {
				return parseBetween(start, sb.toString(), Combinatoric.INCLUDE);
			}
			sb.append(c);
		}
		throw new IllegalStateException();
	}

	private static VersionFilter parseBetween(Combinatoric start, String value, Combinatoric end) {
		int comma = value.indexOf(',');
		if(comma < 0) {
			throw new IllegalArgumentException("Unable to parse: " + value + ". ',' is missing");
		}
		String minVerStr = value.substring(0, comma).trim();
		String maxVerStr = value.substring(comma + 1).trim();
		if(minVerStr.isEmpty() && maxVerStr.isEmpty()) {
			throw new IllegalArgumentException("Unable to parse: " + value + ". Value(s) missing in " + value);
		} else if(minVerStr.isEmpty()) {
			Version maxVersion = Version.parse(maxVerStr);
			return end == Combinatoric.INCLUDE ? VersionFilter.lowerOrEqualsThan(maxVersion) : VersionFilter.lowerThan(maxVersion);
		} else if(maxVerStr.isEmpty()) {
			Version minVersion = Version.parse(minVerStr);
			return start == Combinatoric.INCLUDE ? VersionFilter.upperOrEqualsThan(minVersion) : VersionFilter.upperThan(minVersion);
		} else {
			Version minVersion = Version.parse(minVerStr);
			Version maxVersion = Version.parse(maxVerStr);
			return new VersionRange(minVersion, start, maxVersion, end);
		}
	}

}
