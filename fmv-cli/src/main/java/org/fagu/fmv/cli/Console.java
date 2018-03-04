package org.fagu.fmv.cli;

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
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.cli.exception.ExitException;
import org.fagu.fmv.cli.exception.LineParseException;
import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.exec.executable.ConcatExecutable;
import org.fagu.fmv.core.exec.executable.GenericExecutable;
import org.fagu.fmv.core.exec.filter.FadeAudioVideoFilterExec;
import org.fagu.fmv.core.exec.filter.GenericFilterExec;
import org.fagu.fmv.core.project.FileSource;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.core.project.ProjectListener;
import org.fagu.fmv.ffmpeg.filter.impl.FadeType;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;

import jline.console.ConsoleReader;

/**
 * @author f.agu
 */
public class Console {

	private Environnement environnement;

	private Prompt prompt;

	private CommandBuilder commandBuilder;

	private Project project;

	private ConsoleReader consoleReader;

	/**
	 * @param environnement
	 */
	public Console(Environnement environnement) {
		this.environnement = environnement;
		prompt = environnement.getPrompt();
		project = environnement.getProject();
		consoleReader = environnement.getConsoleReader();
		commandBuilder = environnement.getCommandBuilder();
	}

	/**
	 * @throws IOException
	 */
	public void run() throws IOException {

		addCommands();
		initListener(consoleReader);
		startConsole(consoleReader);

		while (true) { // NOSONAR
			consoleReader.setPrompt(prompt.get(environnement));
			String line = consoleReader.readLine();
			if (StringUtils.isBlank(line)) {
				continue;
			}
			try {
				commandBuilder.createAndExec(line);
			} catch (LineParseException e) {
				consoleReader.println("Error: " + e.getMessage());
				e.printStackTrace();
			} catch (ExitException e) {
				// TODO without saving ?
				break;
			}
		}
	}

	// *****************************************

	/**
	 *
	 */
	private void addCommands() {
		commandBuilder.add(Command.class.getPackage());
	}

	/**
	 * @param consoleReader
	 */
	private void initListener(final ConsoleReader consoleReader) {
		project.addListener(new ProjectListener() {

			/**
			 * @see org.fagu.fmv.core.project.ProjectListenerAdaptor#eventAddSourcePost(java.io.File,
			 *      org.fagu.fmv.core.project.FileSource, int)
			 */
			@Override
			public void eventAddSourcePost(File fromFile, FileSource fileSource, int index) {
				println("1 " + ConsoleOutput.forOutput(fileSource, fromFile));
			}

			/**
			 * @see org.fagu.fmv.core.project.ProjectListenerAdaptor#eventExecPrePreviewViaMake(org.fagu.fmv.core.exec.Executable)
			 */
			@Override
			public void eventExecPrePreviewViaMake(Executable executable) {
				println("2 " + getPadding(executable) + executable);
			}

			/**
			 * @see org.fagu.fmv.core.project.ProjectListenerAdaptor#eventExecPrePreviewScale(org.fagu.fmv.core.exec.Executable,
			 *      java.io.File)
			 */
			@Override
			public void eventExecPrePreviewScale(Executable executable, File destinationFile) {
				println("3 " + getPadding(executable) + "[PREVIEW] " + executable);
			}

			// *******************************

			private String getPadding(Executable executable) {
				return StringUtils.leftPad("", 4 * (1 + executable.getDepth(i -> i instanceof Executable)));
			}

			/**
			 * @param msg
			 */
			private void println(String msg) {
				try {
					consoleReader.println(msg);
					consoleReader.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @param consoleReader
	 * @throws IOException
	 */
	private void startConsole(ConsoleReader consoleReader) throws IOException {
		List<Executable> executables = project.getExecutables();
		if (!executables.isEmpty()) {
			return;
		}
		consoleReader.println("Create a default structure");
		GenericExecutable rootExec = new GenericExecutable(project);

		FadeAudioVideoFilterExec fadeOut = new FadeAudioVideoFilterExec(project, FadeType.OUT, Time.valueOf(200),
				Duration.valueOf(3));
		rootExec.add(fadeOut);

		FadeAudioVideoFilterExec fadeIn = new FadeAudioVideoFilterExec(project, FadeType.IN, Time.valueOf(0),
				Duration.valueOf(2));
		fadeOut.add(fadeIn);

		GenericFilterExec audioMerge = new GenericFilterExec(project, "amerge");
		fadeIn.add(audioMerge);

		ConcatExecutable concat = new ConcatExecutable(project);
		audioMerge.add(concat);

		project.modified();
	}
}
