package org.fagu.fmv.soft.mplayer;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2020 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collections;
import java.util.NavigableSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.fagu.fmv.utils.time.Duration;


/**
 * @author fagu
 */
public class MPlayerTitle {

	private final int num;

	private Duration length;

	private NavigableSet<Duration> chapters;

	public MPlayerTitle(int num) {
		this.num = num;
	}

	public MPlayerTitle(int num, Duration length, NavigableSet<Duration> chapters) {
		this(num);
		this.length = length;
		this.chapters = chapters;
	}

	public int getNum() {
		return num;
	}

	public Duration getLength() {
		return length;
	}

	public NavigableSet<Duration> getChapters() {
		return Collections.unmodifiableNavigableSet(chapters);
	}

	@Override
	public String toString() {
		return "Title(" + num + "," + length + (chapters != null ? ", " + chapters.size() + " chapter" + (chapters.size() > 1 ? "s" : "") : "") + ")";
	}

	// ************************

	void setLength(String s) {
		this.length = Duration.valueOf(Double.parseDouble(s));
	}

	void setChapters(String s) {
		StringTokenizer tokenizer = new StringTokenizer(s, ",");
		NavigableSet<Duration> times = new TreeSet<>();
		String token;
		while(tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken().trim();
			if("".equals(token)) {
				continue;
			}
			times.add(Duration.parse(token));
		}
		chapters = times;
	}

}
