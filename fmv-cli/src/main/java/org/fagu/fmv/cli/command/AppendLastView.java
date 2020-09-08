package org.fagu.fmv.cli.command;

/*-
 * #%L
 * fmv-cli
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.core.project.FileSource;
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
		FileSource source = project.getSource(lastView);
		getPrinter().println("Last view n°" + lastView + ": " + source.getFile().getName());
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
