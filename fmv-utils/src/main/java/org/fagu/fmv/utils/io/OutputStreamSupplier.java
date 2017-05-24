package org.fagu.fmv.utils.io;

import java.io.IOException;
import java.io.OutputStream;


/**
 * @author f.agu
 */
@FunctionalInterface
public interface OutputStreamSupplier {

	/**
	 * @return
	 * @throws IOException
	 */
	OutputStream getOutputStream() throws IOException;

}
