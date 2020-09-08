package org.fagu.fmv.soft.mplayer;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.soft.exec.CommandLineUtils;


/**
 * @author fagu
 */
public class MPlayerDump {

	public static class MPlayerDumpBuilder {

		private final File dvdDrive;

		private IntConsumer progress;

		private Consumer<String> logger = l -> {};

		private MPlayerDumpBuilder(File dvdDrive) {
			this.dvdDrive = Objects.requireNonNull(dvdDrive);
		}

		public MPlayerDumpBuilder progress(IntConsumer progress) {
			this.progress = progress;
			return this;
		}

		public MPlayerDumpBuilder logger(Consumer<String> logger) {
			if(logger != null) {
				this.logger = logger;
			}
			return this;
		}

		public MPlayerDump dump(int titleNum, File outFile) throws IOException {
			List<String> params = new ArrayList<>();
			params.add("-dumpstream");
			params.add("-dvd-device");
			params.add(dvdDrive.toString());
			params.add("dvd://" + titleNum);
			params.add("-dumpfile");
			params.add(outFile.getAbsolutePath());

			List<Stream> streams = new ArrayList<>();
			SoftExecutor softExecutor = MPlayer.search()
					.withParameters(params)
					.logCommandLine(cl -> logger.accept(CommandLineUtils.toLine(cl)))
					.addOutReadLine(l -> {
						if(l.startsWith("audio stream:")) {
							Map<String, String> parse = parse(l.substring(0, l.length() - 1));
							streams.add(new AudioStream(Integer.parseInt(parse.get("audio stream")), parse));
							logger.accept("Add " + l);
						}
						if(l.startsWith("subtitle (")) {
							Map<String, String> parse = parse(l);
							streams.add(new Subtitle(Integer.parseInt(parse.get("subtitle ( sid )")), parse));
							logger.accept("Add " + l);
						}
					});

			if(progress != null) {
				Pattern pattern = Pattern.compile("dump: \\d+ bytes written \\(~(\\d+).\\d+%\\)");
				AtomicInteger lastProgress = new AtomicInteger( - 1);
				softExecutor.addOutReadLine(l -> {
					Matcher matcher = pattern.matcher(l);
					if(matcher.matches()) {
						int value = Integer.parseInt(matcher.group(1));
						lastProgress.updateAndGet(prev -> {
							if(value != prev) {
								progress.accept(value);
							}
							return value;
						});
					}
				});
			}

			softExecutor.execute();

			return new MPlayerDump(streams);
		}

		// --------------------------------------------

		static Map<String, String> parse(String line) {
			Map<String, String> map = new HashMap<>();
			String key = null;
			for(String s : line.split(": ")) {
				if(key == null) {
					key = s;
					continue;
				}
				int pos = s.lastIndexOf(' ');
				if(pos < 0) {
					map.put(key, s);
				} else {
					String starts = s.substring(0, pos);
					String last = s.substring(pos + 1);
					map.put(key, starts);
					key = last;
				}
			}
			return map;
		}

	}

	// ------------------------------------------

	private final List<Stream> streams;

	public MPlayerDump(List<Stream> streams) {
		this.streams = streams;
	}

	public static MPlayerDumpBuilder fromDVDDrive(File dvdDrive) {
		return new MPlayerDumpBuilder(dvdDrive);
	}

	public List<AudioStream> getAudioStreams() {
		return streams.stream()
				.filter(s -> s instanceof AudioStream)
				.map(s -> (AudioStream)s)
				.collect(Collectors.toList());
	}

	public List<Subtitle> getSubtitles() {
		return streams.stream()
				.filter(s -> s instanceof Subtitle)
				.map(s -> (Subtitle)s)
				.collect(Collectors.toList());
	}

}
