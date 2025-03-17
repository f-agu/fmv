package org.fagu.fmv.ffmpeg.metadatas;

/*-
 * #%L
 * fmv-ffmpeg
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.fagu.fmv.ffmpeg.executor.FFExecuted;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.ioe.FileMediaInput;
import org.fagu.fmv.ffmpeg.ioe.PipeMediaInput;
import org.fagu.fmv.ffmpeg.operation.InfoOperation;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.media.MetadatasBuilder;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.utils.collection.MapList;
import org.fagu.fmv.utils.collection.MultiValueMaps;

import com.google.gson.Gson;


/**
 * @author f.agu
 */
public class MovieMetadatas implements Metadatas {

	private static final Map<String, Factory<?>> FACTORY_MAP = new HashMap<>(4);

	static {
		FACTORY_MAP.put("format", Format::new);
		FACTORY_MAP.put("streams", Stream::create);
		FACTORY_MAP.put("chapters", Chapter::new);
	}

	// --------------------------------------------------------

	public static class MovieMetadatasBuilder implements MetadatasBuilder<MovieMetadatas, MovieMetadatasBuilder> {

		private final MediaInput mediaInput;

		private Soft soft;

		private final List<Consumer<SoftExecutor>> customizeSoftExecutors;

		private MovieMetadatasBuilder(MediaInput mediaInput) {
			this.mediaInput = Objects.requireNonNull(mediaInput);
			customizeSoftExecutors = new LinkedList<>();
		}

		@Override
		public MovieMetadatasBuilder soft(Soft soft) {
			this.soft = Objects.requireNonNull(soft);
			return this;
		}

		@Override
		public MovieMetadatasBuilder customizeExecutor(Consumer<SoftExecutor> customizeSoftExecutor) {
			if(customizeSoftExecutor != null) {
				customizeSoftExecutors.add(customizeSoftExecutor);
			}
			return this;
		}

		@Override
		public MovieMetadatas extract() throws IOException {
			InfoOperation infoOperation = new InfoOperation(mediaInput);
			FFExecutor<MovieMetadatas> executor = new FFExecutor<>(infoOperation);
			customizeSoftExecutors.forEach(executor::customizeSoftExecutor);
			executor.setSoft(soft);
			FFExecuted<MovieMetadatas> execute = executor.execute();
			return execute.getResult();
		}

	}

	// --------------------------------------------------------

	private final List<InfoBase> infoBases;

	private final String original;

	protected MovieMetadatas(List<InfoBase> infoBaseList, String original) {
		this.infoBases = Collections.unmodifiableList(infoBaseList);
		this.original = original;
	}

	public static MovieMetadatasBuilder with(MediaInput mediaInput) {
		return new MovieMetadatasBuilder(mediaInput);
	}

	public static MovieMetadatasBuilder with(InputStream inputStream) {
		return new MovieMetadatasBuilder(new PipeMediaInput())
				.customizeExecutor(se -> se.input(inputStream));
	}

	public static MovieMetadatasBuilder with(File file) {
		return new MovieMetadatasBuilder(new FileMediaInput(file));
	}

	public static MovieMetadatas parseJSON(String jsonString) {
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = gson.fromJson(jsonString, Map.class);
		return create(map, jsonString);
	}

	private static MovieMetadatas create(Map<String, Object> map, String jsonString) {
		List<InfoBase> objects = new ArrayList<>(4);
		MovieMetadatas movieMetadatas = new MovieMetadatas(objects, jsonString);
		BiConsumer<Factory<?>, Object> appender = (f, v) -> {
			if(v instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> vmap = (Map<String, Object>)v;
				InfoBase infoBase = f.create(movieMetadatas, vmap);
				if(infoBase != null) {
					objects.add(infoBase);
				}
			}
		};

		for(Entry<String, Object> entry : map.entrySet()) {
			String name = entry.getKey();
			Factory<?> factory = FACTORY_MAP.get(name);
			if(factory == null) {
				continue;
			}
			Object value = entry.getValue();
			if(value instanceof Collection) {
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>)value;
				for(Object obj : collection) {
					appender.accept(factory, obj);
				}
			} else {
				appender.accept(factory, value);
			}

		}
		return movieMetadatas;
	}

	public static void setFactory(String type, Factory<?> factory) {
		FACTORY_MAP.put(type, factory);
	}

	@Override
	public Map<String, Object> getData() {
		Map<String, Object> main = new LinkedHashMap<>();
		for(InfoBase infoBase : infoBases) {
			main.put(infoBase.getName(), infoBase.getData());
		}
		return main;
	}

	@Override
	public String toJSON() {
		return original;
	}

	public List<InfoBase> getInfoBaseList() {
		return infoBases;
	}

	public List<Stream> getStreams() {
		return filterBy(Stream.class);
	}

	public List<InfoBase> getStreams(Type type) {
		return filterBy(getClassByType(type));
	}

	@SuppressWarnings("unchecked")
	public <S extends InfoBase> List<S> getStreams(Predicate<S> predicate) {
		List<S> list = new ArrayList<>();
		for(InfoBase infoBase : infoBases) {
			if(predicate.test((S)infoBase)) {
				list.add((S)infoBase);
			}
		}
		return list;
	}

	public MapList<Type, Stream> toTypeMap() {
		MapList<Type, Stream> map = MultiValueMaps.hashMapArrayList();
		for(Stream stream : filterBy(Stream.class)) {
			map.add(stream.type(), stream);
		}
		return map;
	}

	public List<Chapter> getChapters() {
		return filterBy(Chapter.class);
	}

	public List<AudioStream> getAudioStreams() {
		return filterBy(AudioStream.class);
	}

	public List<VideoStream> getVideoStreams() {
		return filterBy(VideoStream.class);
	}

	public List<SubtitleStream> getSubtitleStreams() {
		return filterBy(SubtitleStream.class);
	}

	public AudioStream getAudioStream() {
		return filterFirstBy(AudioStream.class);
	}

	public VideoStream getVideoStream() {
		return filterFirstBy(VideoStream.class);
	}

	public SubtitleStream getSubtitleStream() {
		return filterFirstBy(SubtitleStream.class);
	}

	public Stream getFirstStreamBy(Type type) {
		for(Stream stream : filterBy(Stream.class)) {
			if(stream.type() == type) {
				return stream;
			}
		}
		return null;
	}

	public Format getFormat() {
		List<Format> list = filterBy(Format.class);
		return ! list.isEmpty() ? list.get(0) : null;
	}

	public Map<InfoBase, Object> searchTags(String name) {
		Map<InfoBase, Object> map = new HashMap<>();
		for(InfoBase infoBase : infoBases) {
			infoBase.tag(name).ifPresent(value -> map.put(infoBase, value));
		}
		return map;
	}

	public boolean isTreatedByFMV() {
		for(InfoBase infoBase : infoBases) {
			if(infoBase.isTreatedByFMV()) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(Type type) {
		return ! filterBy(getClassByType(type)).isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(1000);
		String ls = System.getProperty("line.separator");
		for(Format format : filterBy(Format.class)) {
			buf.append("Format:").append(ls);
			for(String name : new TreeSet<String>(format.getNames())) {
				buf.append("  ").append(name).append(": ").append(format.get(name)).append(ls);
			}
		}
		for(Stream stream : filterBy(Stream.class)) {
			buf.append("Stream: ").append(stream.index()).append(" / ").append(stream.codecType().orElse("?")).append(ls);
			for(String name : new TreeSet<String>(stream.getNames())) {
				buf.append("  ").append(name).append(": ").append(stream.get(name)).append(ls);
			}
		}
		return buf.toString();
	}

	// ---------------------------------------------------------------

	/**
	 * @author f.agu
	 *
	 * @param <O>
	 */
	@FunctionalInterface
	public interface Factory<O extends InfoBase> {

		O create(MovieMetadatas movieMetadatas, Map<String, Object> map);
	}

	// *****************************************************

	private <T extends InfoBase> T filterFirstBy(Class<T> cls) {
		List<T> list = filterBy(cls);
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	private <T extends InfoBase> List<T> filterBy(Class<T> cls) {
		return getStreams(ib -> cls.isAssignableFrom(ib.getClass()));
	}

	@SuppressWarnings("unchecked")
	private <T> Class<T> getClassByType(Type type) {
		switch(type) {
			case VIDEO:
				return (Class<T>)VideoStream.class;
			case AUDIO:
				return (Class<T>)AudioStream.class;
			case ATTACHEMENTS:
				return (Class<T>)AttachmentsStream.class;
			case DATA:
				return (Class<T>)DataStream.class;
			case SUBTITLE:
				return (Class<T>)SubtitleStream.class;
			case UNKNOWN:
			default:
				return (Class<T>)UnknownStream.class;
		}
	}

}
