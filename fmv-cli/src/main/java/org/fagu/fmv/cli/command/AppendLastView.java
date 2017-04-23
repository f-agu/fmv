package org.fagu.fmv.cli.command;

import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.core.project.Properties;


/**
 * @author f.agu
 */
@Command("appendlastview")
@Alias("++")
public class AppendLastView extends Append {

	/**
	 * 
	 */
	public AppendLastView() {
		super();
	}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length == 0) {
			getPrinter().println("usage: appendlastview ...");
			return;
		}
		Integer lastView = project.getProperty(Properties.VIEW_LAST_MEDIA);
		if(lastView == null) {
			getPrinter().println("last view not defined");
			return;
		}
		String[] strs = new String[args.length + 1];
		strs[0] = lastView.toString();
		System.arraycopy(args, 0, strs, 1, args.length);
		super.run(strs);
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	// @Override
	// public String getShortDescription() {
	// return "Append a media in the timeline";
	// }

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "appendlastview <time start> <duration>";
	}

}
