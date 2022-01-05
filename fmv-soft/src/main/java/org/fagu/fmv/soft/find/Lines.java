package org.fagu.fmv.soft.find;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.fagu.fmv.utils.collection.LimitedLastQueue;


/**
 * @author Oodrive
 * @author f.agu
 * @created 5 janv. 2022 13:55:43
 */
public class Lines {

	// --------------------------------------------

	public enum Standard {
		OUT, ERR
	}

	// --------------------------------------------

	public static final class Line {

		private final Standard standard;

		private final String value;

		public Line(Standard standard, String value) {
			this.standard = Objects.requireNonNull(standard);
			this.value = Objects.requireNonNull(value);
		}

		public boolean isOut() {
			return is(Standard.OUT);
		}

		public boolean isErr() {
			return is(Standard.ERR);
		}

		public boolean is(Standard standard) {
			return this.standard == standard;
		}

		public Standard getStandard() {
			return standard;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return standard + ": " + value;
		}
	}

	// --------------------------------------------

	private final List<Line> list;

	public Lines() {
		this(new LimitedLastQueue<>(500));
	}

	public Lines(List<Line> list) {
		this.list = Objects.requireNonNull(list);
	}

	public Lines addOut(String value) {
		return add(new Line(Standard.OUT, value));
	}

	public Lines addErr(String value) {
		return add(new Line(Standard.ERR, value));
	}

	public Lines add(Line line) {
		list.add(Objects.requireNonNull(line));
		return this;
	}

	public List<Line> getLines() {
		return list;
	}

	public Stream<Line> lines() {
		return list.stream();
	}

	public Stream<String> values() {
		return list.stream().map(Line::getValue);
	}

	public Stream<Line> outLines() {
		return lines().filter(Line::isOut);
	}

	public Stream<Line> errLines() {
		return lines().filter(Line::isErr);
	}
}
