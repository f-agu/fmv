package org.fagu.fmv.soft.mplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class MPlayerTitles {

	private static final int MIN_LENGTH_PERCENT = 10;

	// --------------------------------------

	public static class MPlayerTitlesBuilder {

		private final File dvdDrive;

		private MPlayerTitlesBuilder(File dvdDrive) {
			this.dvdDrive = Objects.requireNonNull(dvdDrive);
		}

		public MPlayerTitles find() throws IOException {
			List<String> params = new ArrayList<>();
			params.add("-noquiet");
			params.add("-slave");
			params.add("-identify");
			params.add("-dvd-device");
			params.add(dvdDrive.toString());
			params.add("-frames");
			params.add("0");
			params.add("-nocache");
			params.add("dvdnav://");

			Map<String, String> properties = new HashMap<>();
			NavigableMap<Integer, MPlayerTitle> mPlayerTitlesMap = new TreeMap<>();
			Pattern titleLengthPattern = Pattern.compile("ID_DVD_TITLE_(\\d+)_LENGTH");
			Pattern titleChaptersPattern = Pattern.compile("ID_DVD_TITLE_(\\d+)_CHAPTERS");
			Pattern chaptersPattern = Pattern.compile("TITLE (\\d+), CHAPTERS: ");

			BiConsumer<Matcher, Consumer<MPlayerTitle>> idDVDTitleConsumer = (matcher, func) -> {
				int num = Integer.parseInt(matcher.group(1));
				MPlayerTitle mPlayerTitle = mPlayerTitlesMap.computeIfAbsent(num, MPlayerTitle::new);
				func.accept(mPlayerTitle);
			};

			MPlayer.search()
					.withParameters(params)
					// .logCommandLine(System.out::println)
					.addOutReadLine(l -> {
						if(l.contains("=")) {
							String key = StringUtils.substringBefore(l, "=");
							String value = StringUtils.substringAfter(l, "=");
							Matcher matcher = titleLengthPattern.matcher(key);
							if(matcher.matches()) {
								idDVDTitleConsumer.accept(matcher, mPlayerTitle -> mPlayerTitle.setLength(value));
								return;
							}
							matcher = titleChaptersPattern.matcher(key);
							if(matcher.matches()) {
								// ignore
								return;
							}
							properties.put(key, value);
						} else if(l.contains(":")) {
							String key = StringUtils.substringBefore(l, ":");
							String value = StringUtils.substringAfter(l, ":").trim();
							Matcher matcher = chaptersPattern.matcher(key);
							if(matcher.matches()) {
								idDVDTitleConsumer.accept(matcher, mPlayerTitle -> mPlayerTitle.setChapters(value));
							}
						}
					})
					.execute();
			return new MPlayerTitles(properties, mPlayerTitlesMap);
		}
	}

	private final Map<String, String> properties;

	private final NavigableMap<Integer, MPlayerTitle> mPlayerTitlesMap;

	/**
	 * @param properties
	 * @param mPlayerTitleMap
	 */
	private MPlayerTitles(Map<String, String> properties, NavigableMap<Integer, MPlayerTitle> mPlayerTitleMap) {
		this.properties = Collections.unmodifiableMap(properties);
		this.mPlayerTitlesMap = Collections.unmodifiableNavigableMap(mPlayerTitleMap);
	}

	/**
	 * @param dvdDrive
	 * @return
	 */
	public static MPlayerTitlesBuilder fromDVDDrive(File dvdDrive) {
		return new MPlayerTitlesBuilder(dvdDrive);
	}

	/**
	 * @return
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * @return
	 */
	public NavigableMap<Integer, MPlayerTitle> getTitlesMap() {
		return mPlayerTitlesMap;
	}

	/**
	 * @return
	 */
	public List<MPlayerTitle> getTitlesLongest() {
		double totalDuration = mPlayerTitlesMap.values().stream()
				.map(t -> t.getLength())
				.reduce((t1, t2) -> t1.add(t2))
				.get()
				.toSeconds();

		List<MPlayerTitle> list = new ArrayList<>();
		mPlayerTitlesMap.values().stream()
				.filter(t -> (100D * t.getLength().toSeconds() / totalDuration) > MIN_LENGTH_PERCENT)
				.forEach(list::add);

		return list;
	}

}
