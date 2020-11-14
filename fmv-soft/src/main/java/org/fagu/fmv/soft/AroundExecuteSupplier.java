package org.fagu.fmv.soft;

import java.io.IOException;


/**
 * @author Oodrive
 * @author f.agu
 * @created 13 nov. 2020 17:27:49
 */
public interface AroundExecuteSupplier {

	AroundExecute get() throws IOException;
}
