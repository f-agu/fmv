package org.fagu.fmv.im;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Oodrive
 * @author f.agu
 * @created 5 oct. 2023 14:45:08
 */
public abstract class SelectedFrames {

	private static final SelectedFrames ALL = new SelectedFrames() {

		@Override
		public String toString() {
			return StringUtils.EMPTY;
		}
	};

	private SelectedFrames() {}

	public static SelectedFrames first() {
		return one(0);
	}

	public static SelectedFrames one(int num) {
		if(num < 0) {
			throw new IllegalArgumentException("'num' must be positive: " + num);
		}
		return new SelectedFrames() {

			@Override
			public String toString() {
				return new StringBuilder(6).append('[').append(num).append(']').toString();
			}
		};
	}

	public static SelectedFrames sequence(int start, int end) {
		if(start < 0) {
			throw new IllegalArgumentException("'start' must be positive: " + start);
		}
		if(start > end) {
			throw new IllegalArgumentException("'end'(" + end + ") must be higher than 'start'(" + start + ")");
		}
		if(start == end) {
			return one(start);
		}
		return new SelectedFrames() {

			@Override
			public String toString() {
				return new StringBuilder(6).append('[')
						.append(start).append('-').append(end)
						.append(']').toString();
			}
		};
	}

	public static SelectedFrames of(int... nums) {
		if(nums == null || nums.length == 0) {
			throw new IllegalArgumentException("'nums' undefined");
		}
		Arrays.stream(nums).forEach(num -> {
			if(num < 0) {
				throw new IllegalArgumentException("'num' must be positive: " + num);
			}
		});
		if(nums.length == 1) {
			return one(nums[0]);
		}
		return new SelectedFrames() {

			@Override
			public String toString() {
				return new StringBuilder(6).append('[')
						.append(Arrays.stream(nums).distinct().mapToObj(n -> Integer.toString(n)).collect(Collectors.joining(",")))
						.append(']').toString();
			}
		};
	}

	public static SelectedFrames all() {
		return ALL;
	}

}
