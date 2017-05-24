package org.fagu.fmv.utils.io;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author f.agu
 */
@FunctionalInterface
public interface InputStreamSupplier {

	/**
	 * @return
	 * @throws IOException
	 */
	InputStream getInputStream() throws IOException;

}
