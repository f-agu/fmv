package org.fagu.fmv.soft.mplayer;

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


/**
 * @author fagu
 */
public class MPlayerDump {

	/**
	 * @author fagu
	 */
	public static class MPlayerDumpBuilder {

		private final File dvdDrive;

		private IntConsumer progress;

		private Consumer<String> logger = l -> {};

		private MPlayerDumpBuilder(File dvdDrive) {
			this.dvdDrive = Objects.requireNonNull(dvdDrive);
		}

		/**
		 * @param progress
		 * @return
		 */
		public MPlayerDumpBuilder progress(IntConsumer progress) {
			this.progress = progress;
			return this;
		}

		/**
		 * @param logger
		 * @return
		 */
		public MPlayerDumpBuilder logger(Consumer<String> logger) {
			if(logger != null) {
				this.logger = logger;
			}
			return this;
		}

		/**
		 * @param titleNum
		 * @param outFile
		 * @return
		 * @throws IOException
		 */
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
					.logCommandLine(logger)
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

		/**
		 * @param line
		 * @return
		 */
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

	/**
	 * @param streams
	 */
	public MPlayerDump(List<Stream> streams) {
		this.streams = streams;
	}

	/**
	 * @param dvdDrive
	 * @return
	 */
	public static MPlayerDumpBuilder fromDVDDrive(File dvdDrive) {
		return new MPlayerDumpBuilder(dvdDrive);
	}

	/**
	 * @return
	 */
	public List<AudioStream> getAudioStreams() {
		return streams.stream()
				.filter(s -> s instanceof AudioStream)
				.map(s -> (AudioStream)s)
				.collect(Collectors.toList());
	}

	/**
	 * @return
	 */
	public List<Subtitle> getSubtitles() {
		return streams.stream()
				.filter(s -> s instanceof Subtitle)
				.map(s -> (Subtitle)s)
				.collect(Collectors.toList());
	}

}
