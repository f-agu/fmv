package org.fagu.fmv.cli.command;

/*
 * #%L
 * fmv-cli
 * %%
 * Copyright (C) 2014 fagu
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

import org.apache.commons.lang.math.NumberUtils;
import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.cli.utils.OpenFile;
import org.fagu.fmv.core.project.FileSource;


/**
 * @author f.agu
 */
@Command("view")
@Alias("v")
public class View extends AbstractCommand {

	/**
	 * 
	 */
	public View() {}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length == 0) {
			println(getSyntax());
			return;
		}
		for(String arg : args) {
			int num = NumberUtils.toInt(arg, - 1);
			if(num < 0) {
				println("Media number error: " + arg);
				continue;
			}
			FileSource source = project.getSource(num);
			if(source == null) {
				println("Media number not found: " + num);
				continue;
			}
			open(source);
		}
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "View a media: image, video, audio";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "view <media num> [media num] ...";
	}

	// **********************************************

	/**
	 * @param source
	 */
	private void open(FileSource source) {
		OpenFile openFile = new OpenFile(fmvCliConfig, getPrinter());
		openFile.open(source);
	}
}