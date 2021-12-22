package org.fagu.fmv.mymedia.reduce.cb;

import java.io.Closeable;
import java.io.IOException;


/**
 * @author f.agu
 * @created 23 nov. 2021 18:21:48
 */
public interface ComicBook extends Closeable {

	String getType();

	int countEntry() throws IOException;

	void reduce(Appender appender) throws IOException;
}
