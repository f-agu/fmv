package org.fagu.fmv.media;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author Oodrive
 * @author f.agu
 * @created 26 nov. 2019 15:29:00
 */
@FunctionalInterface
public interface InputStreamResourceSupplier {

	InputStream supply(String resourceName) throws IOException;

}
