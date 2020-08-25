package org.fagu.fmv.soft.mediainfo.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.soft.SoftExecutorHelper;
import org.fagu.fmv.soft.mediainfo.AudioInfo;
import org.fagu.fmv.soft.mediainfo.GeneralInfo;
import org.fagu.fmv.soft.mediainfo.ImageInfo;
import org.fagu.fmv.soft.mediainfo.Info;
import org.fagu.fmv.soft.mediainfo.InfoBase;
import org.fagu.fmv.soft.mediainfo.InfoType;
import org.fagu.fmv.soft.mediainfo.MediaInfo;
import org.fagu.fmv.soft.mediainfo.MenuInfo;
import org.fagu.fmv.soft.mediainfo.OtherInfo;
import org.fagu.fmv.soft.mediainfo.TextInfo;
import org.fagu.fmv.soft.mediainfo.VideoInfo;

import com.google.gson.Gson;


/**
 * @author f.agu
 * @created 24 août 2020 15:18:50
 */
public class JsonMediaInfoExtractor extends SoftExecutorHelper<JsonMediaInfoExtractor> {

	private final Soft mediaInfoSoft;

	public JsonMediaInfoExtractor() {
		this(MediaInfo.search());
	}

	public JsonMediaInfoExtractor(Soft mediaInfoSoft) {
		this.mediaInfoSoft = Objects.requireNonNull(mediaInfoSoft);
	}

	public Info extract(File file) throws IOException {
		return extractAll(Collections.singletonList(file)).get(file);
	}

	public Map<File, Info> extractAll(File... files) throws IOException {
		return extractAll(Arrays.asList(files));
	}

	public Map<File, Info> extractAll(Collection<File> files) throws IOException {
		if(files.isEmpty()) {
			return Collections.emptyMap();
		}
		List<String> parameters = new ArrayList<>(files.size() + 1);
		parameters.add("--Output=JSON");

		files.stream()
				.map(File::toString)
				.forEach(parameters::add);

		final EnumMap<InfoType, AtomicInteger> indexByTypeMap = new EnumMap<>(InfoType.class);
		StringBuilder output = new StringBuilder();
		SoftExecutor executor = mediaInfoSoft.withParameters(parameters)
				.addOutReadLine(output::append);
		populate(executor);
		executor.execute();
		return files.size() == 1 ? parseOne(output.toString(), indexByTypeMap) : parseMultiple(output.toString(), indexByTypeMap);
	}

	// *****************************************************

	@SuppressWarnings("unchecked")
	private Map<File, Info> parseOne(String output, Map<InfoType, AtomicInteger> indexByTypeMap) {
		Gson gson = new Gson();
		Map<String, Object> map = gson.fromJson(output, Map.class);
		return Collections.singletonMap(extractFile(map), mediaToInfo(map, indexByTypeMap));
	}

	@SuppressWarnings("unchecked")
	private Map<File, Info> parseMultiple(String output, Map<InfoType, AtomicInteger> indexByTypeMap) {
		Gson gson = new Gson();
		Map<File, Info> map = new HashMap<>();
		List<Map<String, Object>> list = gson.fromJson(output, List.class);
		list.forEach(m -> map.put(extractFile(m), mediaToInfo(m, indexByTypeMap)));
		return map;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private Info mediaToInfo(Map<String, Object> map, Map<InfoType, AtomicInteger> indexByTypeMap) {
		Map<String, Object> media = (Map)map.get("media");
		List<Map<String, Object>> tracks = (List)media.get("track");
		return tracksToInfo(tracks, indexByTypeMap);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private File extractFile(Map<String, Object> map) {
		Map<String, Object> media = (Map)map.get("media");
		return new File((String)media.get("@ref"));
	}

	private Info tracksToInfo(List<Map<String, Object>> tracks, Map<InfoType, AtomicInteger> indexByTypeMap) {
		return new Info(tracks.stream()
				.map(m -> create(m, indexByTypeMap, trackToMap(m)))
				.collect(Collectors.toList()));
	}

	private Map<String, Object> trackToMap(Map<String, Object> trackMap) {
		Map<String, Object> map = new HashMap<>();
		trackMap.forEach((k, v) -> {
			if( ! "@type".equals(k)) {
				map.put(k, v);
			}
		});
		return map;
	}

	private static InfoBase create(Map<String, Object> map, Map<InfoType, AtomicInteger> indexByTypeMap, Map<String, Object> infos) {
		String stype = (String)map.get("@type");
		InfoType type = InfoType.valueOf(stype.toUpperCase());
		int indexByType = indexByTypeMap.computeIfAbsent(type, k -> new AtomicInteger())
				.getAndIncrement();

		switch(type) {
			case GENERAL:
				return new GeneralInfo(indexByType, infos);
			case AUDIO:
				return new AudioInfo(indexByType, infos);
			case VIDEO:
				return new VideoInfo(indexByType, infos);
			case TEXT:
				return new TextInfo(indexByType, infos);
			case MENU:
				return new MenuInfo(indexByType, infos);
			case IMAGE:
				return new ImageInfo(indexByType, infos);
			case OTHER:
				return new OtherInfo(indexByType, infos);
			default:
				throw new RuntimeException("Undefined type: " + stype);
		}

	}

}
