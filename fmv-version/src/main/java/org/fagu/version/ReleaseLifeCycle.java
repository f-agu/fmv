package org.fagu.version;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * @author f.agu
 */
public class ReleaseLifeCycle implements Comparable<ReleaseLifeCycle>, Serializable {

	private static final long serialVersionUID = - 8396216289059072416L;

	private static final List<List<String>> NAMES = new ArrayList<>();

	public static final ReleaseLifeCycle EMPTY = new ReleaseLifeCycle("", 8, null);

	private static final ReleaseLifeCycle SNAPSHOT = new ReleaseLifeCycle("SNAPSHOT", 0, null);

	static {
		// keep order
		NAMES.add(Arrays.asList("snapshot")); // 0
		NAMES.add(Arrays.asList("pre-alpha", "prealpha")); // 1
		NAMES.add(Arrays.asList("alpha")); // 2
		NAMES.add(Arrays.asList("beta")); // 3
		NAMES.add(Arrays.asList("rc", "releasecandidate", "release-candidate")); // 4
		NAMES.add(Arrays.asList("rtm", "rtw")); // 5
		NAMES.add(Arrays.asList("ga")); // 6
		NAMES.add(Arrays.asList("final", "gold")); // 7
	}

	private final String name;

	private final int namePrecedence;

	private final Integer value;

	private ReleaseLifeCycle(String name, int namePrecedence, Integer value) {
		this.name = Objects.requireNonNull(name);
		this.namePrecedence = namePrecedence;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

	@Override
	public int compareTo(ReleaseLifeCycle o) {
		if(namePrecedence == o.namePrecedence) {
			int val = value == null ? 0 : value;
			int oval = o.value == null ? 0 : o.value;
			if(val == oval) {
				return 0;
			}
			return val < oval ? - 1 : 1;
		}
		return namePrecedence < o.namePrecedence ? - 1 : 1;
	}

	@Override
	public String toString() {
		if(value == null) {
			return name.toUpperCase();
		}
		return name.toUpperCase() + value;
	}

	public static ReleaseLifeCycle parse(String value) {
		if("".equals(value)) {
			return EMPTY;
		}
		String valuelc = value.toLowerCase();
		int index = 0;
		for(List<String> names : NAMES) {
			++index;
			for(String name : names) {
				if(valuelc.startsWith(name)) {
					String after = value.substring(name.length());
					int prece = 0;
					if( ! "".equals(after)) {
						try {
							prece = Integer.parseInt(after);
						} catch(Exception e) {
							return null;
						}
					}
					return new ReleaseLifeCycle(name, index, prece);
				}
			}
		}
		return null;
	}

	public static ReleaseLifeCycle snapshot() {
		return SNAPSHOT;
	}

	public static ReleaseLifeCycle rc(int value) {
		return new ReleaseLifeCycle("rc", 4, value);
	}
}
