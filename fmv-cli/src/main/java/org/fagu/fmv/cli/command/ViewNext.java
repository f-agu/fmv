package org.fagu.fmv.cli.command;

import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Command;


/**
 * @author f.agu
 */
@Command("viewnext")
@Alias("v+")
public class ViewNext extends ViewShift {

	/**
	 * 
	 */
	public ViewNext() {
		super(lastView -> lastView + 1);
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "View next media";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "viewnext";
	}

}
