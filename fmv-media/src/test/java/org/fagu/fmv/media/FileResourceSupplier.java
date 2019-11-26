package org.fagu.fmv.media;

import java.io.File;
import java.io.IOException;


/**
 * @author Oodrive
 * @author f.agu
 * @created 26 nov. 2019 15:29:00
 */
@FunctionalInterface
public interface FileResourceSupplier {

	File supply(String resourceName) throws IOException;

}
