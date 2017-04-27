package org.fagu.fmv.cli.command;

/*-
 * #%L
 * fmv-cli
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
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
			// getPrinter().println("last view not defined");
			// return;
			lastView = Integer.valueOf(0);
		}
		FileSource source = project.getSource(lastView);
		getPrinter().println("Last view n°" + lastView + ": " + source.getFile().getName());
		lastView = operator.applyAsInt(lastView);
		super.run(new String[] {lastView.toString()});
	}

}
