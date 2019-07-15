package org.fagu.fmv.soft.mediainfo;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.soft.exec.ReadLine;


/**
 * @author Oodrive
 * @author f.agu
 * @created 3 juil. 2019 10:49:31
 */
public class ReadLineDetails0 implements ReadLine, Closeable {

	private static final Pattern TITLE_INDEX = Pattern.compile("\\w+ #(\\d+)");

	private final Consumer<InfoBase> infoConsumer;

	private final Map<String, String> infoMap;

	private Function<Map<String, String>, InfoBase> currentInfoBaseFactory;

	public ReadLineDetails0(Consumer<InfoBase> infoConsumer) {
		this.infoConsumer = Objects.requireNonNull(infoConsumer);
		infoMap = new HashMap<>();
	}

	@Override
	public void read(String line) {
		System.out.println(line);
		if("".equals(line.trim())) {
			if(currentInfoBaseFactory != null) {
				infoConsumer.accept(currentInfoBaseFactory.apply(infoMap));
			}
			currentInfoBaseFactory = null;
			infoMap.clear();
			return;
		}
		if(currentInfoBaseFactory == null) {
			currentInfoBaseFactory = parseType(line);
			return;
		}
		int posDDot = line.indexOf(':', 20);
		if(posDDot > 0) {
			String key = line.substring(0, posDDot).trim();
			String value = line.substring(posDDot + 1).trim();
			infoMap.put(key, value);
		} else {
			infoMap.put(line, null);
		}
	}

	@Override
	public void close() throws IOException {
		if(currentInfoBaseFactory != null && ! infoMap.isEmpty()) {
			infoConsumer.accept(currentInfoBaseFactory.apply(infoMap));
		}
	}

	// *******************************************

	private Function<Map<String, String>, InfoBase> parseType(String line) {
		int indexByType = parseIndex(line);

		if(line.startsWith("General")) {
			return infos -> new GeneralInfo(indexByType, infos);
		} else if(line.startsWith("Audio")) {
			return infos -> new AudioInfo(indexByType, infos);
		} else if(line.startsWith("Video")) {
			return infos -> new VideoInfo(indexByType, infos);
		} else if(line.startsWith("Text")) {
			return infos -> new TextInfo(indexByType, infos);
		} else if(line.startsWith("Menu")) {
			return infos -> new MenuInfo(indexByType, infos);
		} else if(line.startsWith("Image")) {
			return infos -> new ImageInfo(indexByType, infos);
		} else if(line.startsWith("Other")) {
			return infos -> new OtherInfo(indexByType, infos);
		}
		throw new RuntimeException("Undefined type: " + line);
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