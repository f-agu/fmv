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

import java.io.File;
import java.util.List;

import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Aliases;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.cli.utils.OpenFile;
import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.exec.FileCache.Cache;
import org.fagu.fmv.media.FileType;


/**
 * @author f.agu
 */
@Command("preview")
@Aliases({@Alias("p"), @Alias(value = "pl", command = "preview last")})
public class Preview extends AbstractCommand {

	/**
	 * 
	 */
	public Preview() {}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		List<Executable> executables = project.getExecutables();
		if(executables.isEmpty()) {
			return;
		}
		File file = project.getFileCache().getFile(executables.get(0), Cache.PREVIEW);
		System.out.println(file);

		OpenFile openFile = new OpenFile(fmvCliConfig, getPrinter());
		openFile.open(file, FileType.VIDEO);
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "Preview the movie";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "preview";
	}

}
