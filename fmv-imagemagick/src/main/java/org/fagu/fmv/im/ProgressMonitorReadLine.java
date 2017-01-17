package org.fagu.fmv.im;

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

	/**
	 * 
	 */
	public ProgressMonitorReadLine() {}

	/**
	 * @see org.fagu.fmv.soft.exec.ReadLine#read(java.lang.String)
	 */
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

	/**
	 * @return
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return
	 */
	public int getPercent() {
		return percent;
	}

	/**
	 * @return
	 */
	public long getMax() {
		return max;
	}

	/**
	 * @return
	 */
	public long getValue() {
		return value;
	}

}
