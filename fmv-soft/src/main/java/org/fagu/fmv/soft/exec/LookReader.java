package org.fagu.fmv.soft.exec;

import java.io.IOException;


/**
 * @author fagu
 */
@FunctionalInterface
public interface LookReader {

	/**
	 * @param line
	 * @return
	 * @throws IOException
	 */
	boolean look(String line) throws IOException;
}
