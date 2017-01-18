package org.fagu.fmv.soft.exec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * @author f.agu
 */
public class MultiReadLine implements ReadLine {

	private final List<ReadLine> readLines;

	/**
	 * @param readLines
	 */
	private MultiReadLine(Collection<ReadLine> readLines) {
		this.readLines = Collections.unmodifiableList(new ArrayList<>(readLines));
	}

	/**
	 * @param readLines
	 * @return
	 */
	public static ReadLine createWith(ReadLine... readLines) {
		return createWith(readLines);
	}

	/**
	 * @param readLines
	 * @return
	 */
	public static ReadLine createWith(Collection<ReadLine> readLines) {
		if(readLines.isEmpty()) {
			return EmptyReadLine.INSTANCE;
		}
		if(readLines.size() == 1) {
			return readLines.iterator().next();
		}
		return new MultiReadLine(readLines);
	}

	/**
	 * @see org.fagu.fmv.utils.exec.ReadLine#read(java.lang.String)
	 */
	@Override
	public void read(String line) {
		for(ReadLine readLine : readLines) {
			readLine.read(line);
		}
	}

	/**
	 * @return
	 */
	public List<ReadLine> getReadLines() {
		return readLines;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return readLines.toString();
	}
}
