package org.fagu.fmv.soft;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.exec.CommandLine;


/**
 * @author Oodrive
 * @author f.agu
 * @created 13 nov. 2020 17:27:49
 */
public interface AroundExecuteSupplier {

	AroundExecute get(CommandLine command, Map<String, String> environment) throws IOException;
}
