package org.fagu.fmv.soft;

import java.util.function.Consumer;

import org.fagu.fmv.soft.find.SoftFound;


/**
 * @author Oodrive
 * @author f.agu
 * @created 4 mars 2020 11:05:08
 */
public interface SoftLogListener {

	default void eventFound(Soft soft, SoftFound softFound, Consumer<String> logger) {}

	default void eventNotFoundEmpty(Soft soft, SoftFound softFound, Consumer<String> logger) {}

	default void eventNotFoundNotEmpty(Soft soft, SoftFound softFound, Consumer<String> logger) {}

}
