package org.fagu.fmv.soft.mediainfo.raw;

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
import org.fagu.fmv.soft.mediainfo.AudioInfo;
import org.fagu.fmv.soft.mediainfo.GeneralInfo;
import org.fagu.fmv.soft.mediainfo.ImageInfo;
import org.fagu.fmv.soft.mediainfo.InfoBase;
import org.fagu.fmv.soft.mediainfo.MenuInfo;
import org.fagu.fmv.soft.mediainfo.OtherInfo;
import org.fagu.fmv.soft.mediainfo.TextInfo;
import org.fagu.fmv.soft.mediainfo.VideoInfo;


/**
 * @author f.agu
 * @created 3 juil. 2019 10:49:31
 */
public class RawDetails0ReadLine implements ReadLine, Closeable {

	private static final Pattern TITLE_INDEX = Pattern.compile("\\w+ #(\\d+)");

	private final Consumer<InfoBase> infoConsumer;

	private final Map<String, Object> infoMap;

	private Function<Map<String, Object>, InfoBase> currentInfoBaseFactory;

	public RawDetails0ReadLine(Consumer<InfoBase> infoConsumer) {
		this.infoConsumer = Objects.requireNonNull(infoConsumer);
		infoMap = new HashMap<>();
	}

	@Override
	public void read(String line) {
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

	private Function<Map<String, Object>, InfoBase> parseType(String line) {
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
