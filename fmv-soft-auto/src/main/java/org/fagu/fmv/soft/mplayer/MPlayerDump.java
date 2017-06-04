package org.fagu.fmv.soft.mplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

			// audio stream: 0 format: ac3 (5.1) language: en aid: 128.
			// audio stream: 1 format: ac3 (5.1) language: fr aid: 129.
			// audio stream: 2 format: ac3 (5.1) language: nl aid: 130.
			// audio stream: 3 format: ac3 (stereo) language: nl aid: 131.
			// audio stream: 4 format: ac3 (stereo) language: en aid: 132.
			// number of audio channels on disk: 5.
			// subtitle ( sid ): 0 language: en
			// subtitle ( sid ): 1 language: fr
			// subtitle ( sid ): 2 language: nl
			// subtitle ( sid ): 3 language: en
			// subtitle ( sid ): 4 language: fr
			// subtitle ( sid ): 5 language: nl

			// dump: 35966976 bytes written (~0.8%)

			SoftExecutor softExecutor = MPlayer.search()
					.withParameters(params)
					// .logCommandLine(System.out::println)
					.addOutReadLine(l -> {
						if(l.startsWith("audio stream:")) {
							Map<String, String> parse = parse(l.substring(0, l.length() - 1));
						}
						if(l.startsWith("subtitle (")) {
							Map<String, String> parse = parse(l);
							//
						}
						System.out.println("OUT: " + l);
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

			return new MPlayerDump();
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

	/**
	 * 
	 */
	private MPlayerDump() {}

	/**
	 * @param dvdDrive
	 * @return
	 */
	public static MPlayerDumpBuilder fromDVDDrive(File dvdDrive) {
		return new MPlayerDumpBuilder(dvdDrive);
	}

}
