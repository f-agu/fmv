package org.fagu.fmv.cli.command;

import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.core.project.FileSource;
import org.fagu.fmv.core.project.Properties;


/**
 * @author f.agu
 */
@Command("viewnext")
@Alias("vv")
public class ViewNext extends View {

	/**
	 * 
	 */
	public ViewNext() {}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length != 0) {
			println(getSyntax());
			return;
		}
		Integer lastView = project.getProperty(Properties.VIEW_LAST_MEDIA);
		if(lastView == null) {
			getPrinter().println("last view not defined");
			return;
		}
		FileSource source = project.getSource(lastView);
		getPrinter().println("Last view n°" + lastView + ": " + source.getFile().getName());
		lastView = Integer.valueOf(lastView + 1);
		super.run(new String[] {lastView.toString()});
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
