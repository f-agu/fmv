package org.fagu.fmv.soft.mplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.fagu.fmv.soft.exec.CommandLineUtils;


/**
 * @author f.agu
 */
public class MPlayerTitles {

	// --------------------------------------

	public static class MPlayerTitlesBuilder {

		private final File dvdDrive;

		private Consumer<String> logger = m -> {};

		private MPlayerTitlesBuilder(File dvdDrive) {
			this.dvdDrive = Objects.requireNonNull(dvdDrive);
		}

		public MPlayerTitlesBuilder logger(Consumer<String> logger) {
			if(logger != null) {
				this.logger = logger;
			}
			return this;
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
					.logCommandLine(cl -> logger.accept(CommandLineUtils.toLine(cl)))
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

			logger.accept("Properties:");
			properties.entrySet().stream()
					.filter(e -> StringUtils.isNotEmpty(e.getKey()))
					.forEach(e -> logger.accept("   " + e.getKey() + ": " + e.getValue()));

			logger.accept("Titles:");
			mPlayerTitlesMap.values().forEach(t -> logger.accept("   " + t));

			return new MPlayerTitles(properties, mPlayerTitlesMap);
		}
	}

	private final Map<String, String> properties;

	private final NavigableMap<Integer, MPlayerTitle> mPlayerTitlesMap;

	public MPlayerTitles(Map<String, String> properties, NavigableMap<Integer, MPlayerTitle> mPlayerTitleMap) {
		this.properties = Collections.unmodifiableMap(properties);
		this.mPlayerTitlesMap = Collections.unmodifiableNavigableMap(mPlayerTitleMap);
	}

	public static MPlayerTitlesBuilder fromDVDDrive(File dvdDrive) {
		return new MPlayerTitlesBuilder(dvdDrive);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public Collection<MPlayerTitle> getTitles() {
		return mPlayerTitlesMap.values();
	}

	public NavigableMap<Integer, MPlayerTitle> getTitlesMap() {
		return mPlayerTitlesMap;
	}

}
