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

import java.util.function.Predicate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.fagu.fmv.cli.ConsoleOutput;
import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Aliases;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.core.project.FileSource;
import org.fagu.fmv.media.FileType;


/**
 * @author f.agu
 */
@Command("list")
@Aliases({@Alias("l"), @Alias("ls")})
public class List extends AbstractCommand {

	/**
	 * 
	 */
	public List() {}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		Predicate<FileSource> filter = getFilter(args);
		for(FileSource fileSource : project.getSources()) {
			if(filter.test(fileSource)) {
				println(ConsoleOutput.forOutput(fileSource));
			}
		}
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "List the media sources";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "list [filter-media-type]";
	}

	/**
	 * @see org.fagu.fmv.cli.command.AbstractCommand#getOptions()
	 */
	public Options getOptions() {
		Options options = super.getOptions();
		for(FileType fileType : FileType.values()) {
			String name = fileType.name().toLowerCase();
			options.addOption(Character.toString(name.charAt(0)), name, false, "");
		}
		return options;
	}

	// *************************************************

	/**
	 * @param args
	 * @return
	 */
	private Predicate<FileSource> getFilter(String[] args) {
		CommandLine cmdLine = parse(args);
		Predicate<FileSource> predicate = null;
		for(final FileType fileType : FileType.values()) {
			String name = fileType.name().toLowerCase();
			if(cmdLine.hasOption(name)) {
				Predicate<FileSource> tmp = fs -> fileType == fs.getFileType();
				predicate = predicate == null ? tmp : predicate.or(tmp);
			}
		}
		if(predicate == null) {
			return s -> true;
		}
		return predicate;
	}
}
