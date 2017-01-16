package org.fagu.fmv.mymedia.img2mv;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import java.util.Scanner;
import java.util.function.Function;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.fagu.fmv.ffmpeg.utils.FPS;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.utils.io.UnclosedInputStream;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class Bootstrap {

	private Size size;

	private FrameRate imageFrameRate;

	private Integer videoFrameRate;

	/**
	 *
	 */
	public Bootstrap() {}

	/**
	 * @param args
	 * @throws Exception
	 */
	private String[] parse(String[] args) throws Exception {
		DefaultParser parser = new DefaultParser();

		CommandLine commandLine = parser.parse(getOptions(), args);
		if(commandLine.hasOption('s')) {
			size = Size.parse(commandLine.getOptionValue('s'));
		}
		if(commandLine.hasOption('i')) {
			imageFrameRate = FrameRate.parse(commandLine.getOptionValue('i'));
		}
		if(commandLine.hasOption('c')) {
			videoFrameRate = Integer.parseInt(commandLine.getOptionValue('c'));
		} else {
			videoFrameRate = 25;
		}

		return commandLine.getArgs();
	}

	/**
	 * 
	 */
	private void askMissing() {
		if(size == null) {
			size = askSomethings("Size [hd720]?", Size.HD720, Size::parse);
		}
		if(imageFrameRate == null) {
			imageFrameRate = askSomethings("Image frame rate ?", null, FrameRate::parse);
		}
		if(videoFrameRate == null) {
			videoFrameRate = askSomethings("Video frame rate ?", null, Integer::parseInt);
		}
	}

	/**
	 * @param title
	 * @param defaultValue
	 * @param parser
	 * @return
	 */
	private <T> T askSomethings(String title, T defaultValue, Function<String, T> parser) {
		System.out.print(title);
		while(true) {
			try {
				String line = nextLine();
				if(defaultValue == null && "".equals(line)) {
					return defaultValue;
				}
				return parser.apply(line);
			} catch(Exception e) {
				System.out.println("error: " + e.getMessage());
			}
		}
	}

	/**
	 * @return
	 */
	private String nextLine() {
		try (Scanner scanner = new Scanner(new UnclosedInputStream(System.in))) {
			return scanner.nextLine();
		}
	}

	/**
	 * @param folder
	 * @throws Exception
	 */
	private void start(File folder) throws Exception {
		if( ! folder.exists()) {
			System.out.println("Folder not found: " + folder);
			return;
		}
		if( ! folder.isDirectory()) {
			System.out.println("It's not a folder: " + folder);
			return;
		}

		File destVideo = new File(folder, "movie.mp4");
		try (PreparedImages prepare = Images.find(folder).prepare(size)) {
			prepare.makeVideo(imageFrameRate, destVideo, FPS.inOneSecond(videoFrameRate));
		}
	}

	/**
	 * @return
	 */
	private static Options getOptions() {
		Options options = new Options();
		options.addOption("s", "size", true, "Size of video (example: 1920x1080, 1280x720, hd480, vga)");
		options.addOption("i", "img-frame-rate", true, "Image frame rate (example: 1/5 = 1 image for 5 seconds, 6 = 6 images per seconds)");
		options.addOption("v", "video-frame-rate", true, "Video frame rate (default: 25)");
		return options;
	}

	/**
	 * 
	 */
	private static void displayUsage() {
		String usage = "java -cp . " + Bootstrap.class + " [--size <WxH>] [--img-frame-rate <fps>] [--video-frame-rate <fps>] <folder>";
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(usage, getOptions());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if(args.length == 0) {
			displayUsage();
			return;
		}
		Bootstrap bootstrap = new Bootstrap();
		String[] files = bootstrap.parse(args);
		bootstrap.askMissing();
		if(files.length == 0) {
			System.out.println("Folder not defined");
			return;
		}
		if(files.length > 1) {
			System.out.println("Too many folders defined");
			return;
		}

		File folder = new File(files[0]);

		bootstrap.start(folder);
	}
}
