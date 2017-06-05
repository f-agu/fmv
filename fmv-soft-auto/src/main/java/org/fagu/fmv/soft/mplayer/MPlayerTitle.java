package org.fagu.fmv.soft.mplayer;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author fagu
 */
public class MPlayerTitle {

	private final int num;

	private Duration length;

	private NavigableSet<Time> chapters;

	/**
	 * @param num
	 */
	public MPlayerTitle(int num) {
		this.num = num;
	}

	/**
	 * @return
	 */
	public int getNum() {
		return num;
	}

	/**
	 * @return
	 */
	public Duration getLength() {
		return length;
	}

	/**
	 * @return
	 */
	public NavigableSet<Time> getChapters() {
		return Collections.unmodifiableNavigableSet(chapters);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Title(" + num + "," + length + "," + (chapters != null ? chapters.size() : 0) + " chapters)";
	}

	// ************************

	/**
	 * @param s
	 */
	void setLength(String s) {
		this.length = Duration.valueOf(Double.parseDouble(s));
	}

	/**
	 * @param s
	 */
	void setChapters(String s) {
		StringTokenizer tokenizer = new StringTokenizer(s, ",");
		NavigableSet<Time> times = new TreeSet<>();
		String token;
		while(tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken().trim();
			if("".equals(token)) {
				continue;
			}
			times.add(Time.parse(token));
		}
		chapters = times;
	}

}
