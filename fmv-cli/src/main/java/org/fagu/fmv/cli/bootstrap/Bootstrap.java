package org.fagu.fmv.cli.bootstrap;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.fagu.fmv.cli.Console;
import org.fagu.fmv.cli.Environnement;
import org.fagu.fmv.cli.FMVCLIConfig;
import org.fagu.fmv.core.FMV;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.OutputInfos;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.soft.FFMpeg;
import org.fagu.fmv.ffmpeg.soft.FFProbe;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.utils.io.UnclosedInputStream;
import org.fagu.fmv.utils.media.Size;


/**
 * VM arguments with eclipse :<br>
 * -Djline.terminal=off
 * 
 * @author f.agu
 */
public class Bootstrap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if( ! FFMpeg.search().isFound()) {
			System.out.println("FFMpeg not found !");
			return;
		}
		if( ! FFProbe.search().isFound()) {
			System.out.println("FFProbe not found !");
			return;
		}

		CommandLineParser parser = new GnuParser();

		Options options = new Options();
		options.addOption("c", "conf", true, "Conf properties");

		CommandLine commandLine = parser.parse(options, args);
		FMVCLIConfig fmvcliConfig = null;
		if(commandLine.hasOption('c')) {
			String conf = commandLine.getOptionValue('c');
			fmvcliConfig = openFMVCLIConfig(conf);
		} else {
			fmvcliConfig = new FMVCLIConfig();
		}
		Project project = null;
		String[] args2 = commandLine.getArgs();
		if(args2.length > 0) {
			project = loadProject(new File(args2[0]));
		} else {
			project = menu(System.out);
		}

		Console console = new Console(new Environnement(project, fmvcliConfig));
		console.run();
	}

	// *******************************************************

	/**
	 * @param printStream
	 * @return
	 */
	private static Project menu(PrintStream printStream) {
		printStream.println("FMV " + FMV.getVersion());
		printStream.println();
		printStream.print("New project ? ");
		try (Scanner scanner = new Scanner(new UnclosedInputStream(System.in))) {
			String answer = scanner.nextLine();
			if(answer.toLowerCase().startsWith("y")) {
				return newProject(printStream);
			}
			return loadProject(printStream);
		}
	}

	/**
	 * @param printStream
	 * @return
	 */
	private static Project newProject(PrintStream printStream) {
		try (Scanner scanner = new Scanner(new UnclosedInputStream(System.in))) {

			// name
			printStream.print("Name: ");
			String name = scanner.nextLine();

			// save file
			final File DEFAULT_SAVE_FILE = new File(name);
			printStream.print("SaveFolder: [" + DEFAULT_SAVE_FILE.getAbsolutePath() + "] ");
			File saveFile = null;
			String ssavefile = scanner.nextLine();
			if(StringUtils.isBlank(ssavefile)) {
				saveFile = DEFAULT_SAVE_FILE;
			} else {
				saveFile = new File(ssavefile);
			}
			saveFile = new File(saveFile, name + ".fmv");
			saveFile.getParentFile().mkdirs();

			// format
			final String DEFAULT_FORMAT = "mp4";
			printStream.print("Format: [" + DEFAULT_FORMAT + "] ");
			String format = scanner.nextLine();
			if(StringUtils.isBlank(format)) {
				format = DEFAULT_FORMAT;
			}

			// size
			final Size DEFAULT_SIZE = Size.HD720;
			printStream.print("Size: [" + DEFAULT_SIZE + "] ");
			Size size = null;
			while(size == null) {
				String ssize = scanner.nextLine();
				if(StringUtils.isBlank(ssize)) {
					size = DEFAULT_SIZE;
				} else {
					try {
						size = Size.parse(ssize);
					} catch(IllegalArgumentException e) {
						printStream.println("Don't understand: " + ssize);
					}
				}
			}

			// rate
			final FrameRate DEFAULT_RATE = FrameRate.valueOf(30, 1);
			printStream.print("Rate: [" + DEFAULT_RATE + "] ");
			FrameRate frameRate = null;
			while(frameRate == null) {
				String srate = scanner.nextLine();
				if(StringUtils.isBlank(srate)) {
					frameRate = DEFAULT_RATE;
				} else {
					try {
						frameRate = FrameRate.parse(srate);
					} catch(IllegalArgumentException e) {
						printStream.println("Don't understand: " + srate);
					}
				}
			}
			OutputInfos outputInfos = new OutputInfos();
			outputInfos.setSize(size);
			outputInfos.setFrameRate(frameRate);
			outputInfos.setAudioSampling(44100); // TODO
			outputInfos.setFormat(format);

			Project project = new Project(saveFile, outputInfos);
			try {
				project.save();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			return project;

		}
	}

	/**
	 * @param printStream
	 * @return
	 */
	private static Project loadProject(PrintStream printStream) {
		try (Scanner scanner = new Scanner(new UnclosedInputStream(System.in))) {
			printStream.println("SaveFile: ");
			String ssavefile = scanner.nextLine();
			File saveFile = new File(ssavefile);
			return loadProject(saveFile);
		}
	}

	/**
	 * @param saveFile
	 * @return
	 */
	private static Project loadProject(File saveFile) {
		Project project = new Project(saveFile);
		try {
			project.load();
		} catch(LoadException e) {
			throw new RuntimeException(e);
		}
		return project;
	}

	/**
	 * @param conf
	 * @return
	 * @throws IOException
	 */
	private static FMVCLIConfig openFMVCLIConfig(String conf) throws IOException {
		File file = new File(conf);
		if( ! file.exists()) {
			System.out.println("File not found: " + conf);
			return new FMVCLIConfig();
		}
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			properties.load(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		return new FMVCLIConfig(properties);
	}
}
