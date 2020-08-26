package org.fagu.fmv.soft.gs;

import org.fagu.fmv.soft.exec.CommandLineHidePolicy;
import org.fagu.fmv.soft.exec.CommandLineToString;
import org.fagu.fmv.soft.exec.CommandLineToString.CommandLineToStringBuilder;


/**
 * @author f.agu
 * @created 26 août 2020 12:17:02
 */
public class GSPasswordCommandLineHidePolicy implements CommandLineHidePolicy {

	@Override
	public void applyPolicy(CommandLineToStringBuilder builder) {
		builder
				.whenArg().verify(a -> a.startsWith("-sOwnerPassword")).replaceBy("-sOwnerPassword" + CommandLineToString.HIDE)
				.whenArg().verify(a -> a.startsWith("-sUserPassword")).replaceBy("-sUserPassword" + CommandLineToString.HIDE);
	}

}
