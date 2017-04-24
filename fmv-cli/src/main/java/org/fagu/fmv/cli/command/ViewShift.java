package org.fagu.fmv.cli.command;

import java.util.Objects;
import java.util.function.IntUnaryOperator;

import org.fagu.fmv.core.project.FileSource;
import org.fagu.fmv.core.project.Properties;


/**
 * @author f.agu
 */
public abstract class ViewShift extends View {

	private final IntUnaryOperator operator;

	/**
	 * @param operator
	 */
	public ViewShift(IntUnaryOperator operator) {
		this.operator = Objects.requireNonNull(operator);
	}

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
		lastView = operator.applyAsInt(lastView);
		super.run(new String[] {lastView.toString()});
	}

}
