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

import java.util.List;
import java.util.stream.Collectors;

import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.cli.utils.Printer;
import org.fagu.fmv.core.exec.BaseIdentifiable;
import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.exec.executable.ConcatExecutable;
import org.fagu.fmv.core.exec.executable.CutExecutable;
import org.fagu.fmv.core.exec.executable.GenericExecutable;
import org.fagu.fmv.core.exec.source.SourceSource;
import org.fagu.fmv.core.project.FileSource;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
@Command("append")
@Alias("+")
public class Append extends AbstractCommand {

	/**
	 * 
	 */
	public Append() {
		super();
	}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length == 0) {
			getPrinter().println("usage: append <num-source> ...");
			return;
		}

		int sourceNum = Integer.parseInt(args[0]);
		FileSource source = project.getSource(sourceNum);
		if(source.isAudioOrVideo()) {
			appendAudioOrVideo(sourceNum, args);
		} else if(source.isImage()) {
			appendImage(sourceNum, args);
		} else {
			throw new IllegalStateException("Unknown file type: " + source.getFileType());
		}
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "Append a media in the timeline";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "append <media-num> <time start> <duration>";
	}

	// ********************************************

	/**
	 * @return
	 */
	private ConcatExecutable getConcatExecutable() {
		Executable currentExecutable = environnement.getCurrentExecutable();
		if(currentExecutable != null && currentExecutable instanceof ConcatExecutable) {
			return (ConcatExecutable)currentExecutable;
		}

		List<ConcatExecutable> execs = BaseIdentifiable.stream(project).
				filter(id -> id instanceof ConcatExecutable).
				map(id -> (ConcatExecutable)id).
				collect(Collectors.toList());

		int size = execs.size();
		if(size == 0) {
			return new ConcatExecutable(project);
		}
		if(size == 1) {
			return execs.get(0);
		}

		Printer printer = getPrinter();
		printer.println("Too many executable.");
		printer.println("List all executables: execlist");
		printer.println("And select one: toexecutable <id>");
		return null;
	}

	/**
	 * @param sourceNum
	 * @param args
	 */
	private void appendAudioOrVideo(int sourceNum, String[] args) {
		if(args.length != 3) {
			getPrinter().println("usage: append <num-source> <start-time> <duration>");
			return;
		}

		try {
			Time startTime = Time.parse(args[1]);
			Duration duration = Duration.parse(args[2]);

			ConcatExecutable concatExecutable = getConcatExecutable();
			if(concatExecutable == null) {
				return;
			}
			CutExecutable cutExecutable = new CutExecutable(project, startTime, duration);
			cutExecutable.setSource(new SourceSource(project, sourceNum));
			concatExecutable.add(cutExecutable);

		} catch(IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param sourceNum
	 * @param args
	 */
	private void appendImage(int sourceNum, String[] args) {
		if(args.length != 3) {
			getPrinter().println("usage: append <num-source> <duration>");
			return;
		}
		Duration duration = Duration.parse(args[1]);

		ConcatExecutable concatExecutable = getConcatExecutable();
		if(concatExecutable == null) {
			return;
		}
		GenericExecutable genericExecutable = new GenericExecutable(project);
		genericExecutable.add(new SourceSource(project, sourceNum));

	}
}
