package org.fagu.fmv.soft;

import java.io.IOException;


/**
 * @author Oodrive
 * @author f.agu
 * @created 13 nov. 2020 17:27:49
 */
public interface TemporaryFolderSupplier {

	TemporaryFolder get() throws IOException;
}
