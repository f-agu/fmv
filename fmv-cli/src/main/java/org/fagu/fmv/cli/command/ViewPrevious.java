package org.fagu.fmv.cli.command;

import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Command;


/**
 * @author f.agu
 */
@Command("viewprevious")
@Alias("v-")
public class ViewPrevious extends ViewShift {

	/**
	 * 
	 */
	public ViewPrevious() {
		super(lastView -> lastView - 1);
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "View previous media";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "viewprevious";
	}

}
