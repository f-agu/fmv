package org.fagu.fmv.soft.exec;

import org.fagu.fmv.soft.exec.CommandLineToString.CommandLineToStringBuilder;


/**
 * @author f.agu
 * @created 25 août 2020 16:11:56
 */
public interface CommandLineHidePolicy {

	void applyPolicy(CommandLineToStringBuilder builder);
}
