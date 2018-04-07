package org.fagu.fmv.soft.mediainfo;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.exec.ReadLine;


/**
 * @author Utilisateur
 * @created 7 avr. 2018 14:06:28
 */
public class MediaInfoExtractor {

	private static final Pattern TITLE_INDEX = Pattern.compile("\\w+ #(\\d+)");

	private final Soft mediaInfoSoft;

	/**
	 * 
	 */
	public MediaInfoExtractor() {
		this(MediaInfo.search());
	}

	/**
	 * @param mediaInfoSoft
	 */
	public MediaInfoExtractor(Soft mediaInfoSoft) {
		this.mediaInfoSoft = Objects.requireNonNull(mediaInfoSoft);
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Info extract(File file) throws IOException {
		return extractAll(Collections.singletonList(file)).get(file);
	}

	/**
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public Map<File, Info> extractAll(File... files) throws IOException {
		return extractAll(Arrays.asList(files));
	}

	/**
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public Map<File, Info> extractAll(Collection<File> files) throws IOException {
		List<String> parameters = new ArrayList<>(files.size() + 1);
		files.stream()
				.map(File::toString)
				.forEach(parameters::add);

		Map<File, Info> fileInfoMap = new HashMap<>();

		try (ReadLineCloseable createReadLine = createReadLine(files, fileInfoMap)) {
			mediaInfoSoft.withParameters(parameters)
					.addOutReadLine(createReadLine)
					.execute();
		}
		return fileInfoMap;
	}

	// **************************************************************

	private static interface ReadLineCloseable extends ReadLine, Closeable {

	}

	/**
	 * @param files
	 * @param infoMap
	 * @return
	 */
	private ReadLineCloseable createReadLine(Collection<File> files, Map<File, Info> fileInfoMap) {
		final Iterator<File> fileIterator = files.iterator();
		final List<InfoBase> infoBases = new ArrayList<>();
		final AtomicReference<Function<Map<String, String>, InfoBase>> infoBaseFactory = new AtomicReference<>();
		final Map<String, String> infoMap = new HashMap<>();
		return new ReadLineCloseable() {

			@Override
			public void read(String line) {
				if(line.isEmpty()) {
					return;
				}
				int posDDot = line.indexOf(':', 20);
				if(posDDot > 0) {
					String key = line.substring(0, posDDot).trim();
					String value = line.substring(posDDot + 1).trim();
					infoMap.put(key, value);

				} else {
					Function<Map<String, String>, InfoBase> currentInfoBaseFactory = infoBaseFactory.getAndSet(null);
					if(currentInfoBaseFactory != null) {
						infoBases.add(currentInfoBaseFactory.apply(infoMap));
					}
					if(parseType(line, infoBaseFactory) && ! infoBases.isEmpty()) {
						fileInfoMap.put(fileIterator.next(), new Info(infoBases));
						infoBases.clear();
					}
					infoMap.clear();
				}
			}

			@Override
			public void close() throws IOException {
				fileInfoMap.put(fileIterator.next(), new Info(infoBases));
			}
		};
	}

	private boolean parseType(String line, AtomicReference<Function<Map<String, String>, InfoBase>> infoBaseFactory) {
		int indexByType = parseIndex(line);

		if(line.startsWith("General")) {
			infoBaseFactory.set(infos -> new GeneralInfo(indexByType, infos));
			return true;
		}

		if(line.startsWith("Audio")) {
			infoBaseFactory.set(infos -> new AudioInfo(indexByType, infos));
		} else if(line.startsWith("Video")) {
			infoBaseFactory.set(infos -> new VideoInfo(indexByType, infos));
		} else if(line.startsWith("Text")) {
			infoBaseFactory.set(infos -> new TextInfo(indexByType, infos));
		} else if(line.startsWith("Menu")) {
			infoBaseFactory.set(infos -> new MenuInfo(indexByType, infos));
		}
		return false;
	}

	private int parseIndex(String line) {
		int index = 1;
		Matcher matcher = TITLE_INDEX.matcher(line);
		if(matcher.matches()) {
			index = Integer.parseInt(matcher.group(1));
		}
		return index;
	}

}
