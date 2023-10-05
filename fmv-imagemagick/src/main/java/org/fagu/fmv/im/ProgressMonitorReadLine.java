package org.fagu.fmv.im;

/*-
 * #%L
 * fmv-imagemagick
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.soft.exec.ReadLine;


/**
 * @author f.agu
 * @created 17 janv. 2017 09:57:16
 */
public class ProgressMonitorReadLine implements ReadLine {

	private static final Pattern PATTERN = Pattern.compile("(.*)\\[(.*)\\]: (\\d+) of (\\d+), (\\d+)% complete");

	private String taskName;

	private String fileName;

	private int percent;

	private long value;

	private long max;

	public ProgressMonitorReadLine() {}

	@Override
	public void read(String line) {
		Matcher matcher = PATTERN.matcher(line);
		if(matcher.matches()) {
			taskName = matcher.group(1);
			fileName = matcher.group(2);
			value = Long.parseLong(matcher.group(3));
			max = Long.parseLong(matcher.group(4));
			percent = Integer.parseInt(matcher.group(5));
		}
	}

	public String getTaskName() {
		return taskName;
	}

	public String getFileName() {
		return fileName;
	}

	public int getPercent() {
		return percent;
	}

	public long getMax() {
		return max;
	}

	public long getValue() {
		return value;
	}

}
