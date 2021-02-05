package org.fagu.fmv.soft;

import org.fagu.fmv.soft.find.FoundReason;


/**
 * @author Oodrive
 * @author f.agu
 * @created 5 févr. 2021 11:28:28
 */
@FunctionalInterface
public interface SoftLoggerColorizer {

	Integer getColor(FoundReason foundReason);
}
