package org.fagu.fmv.ffmpeg.metadatas;

import java.io.File;
import java.io.IOException;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 fagu
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.fagu.fmv.ffmpeg.executor.Executed;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.ioe.FileMediaInput;
import org.fagu.fmv.ffmpeg.operation.InfoOperation;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.media.MetadatasBuilder;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.utils.collection.MapList;
import org.fagu.fmv.utils.collection.MultiValueMaps;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * @author f.agu
 */
public class MovieMetadatas implements Metadatas, Serializable {

	private static final long serialVersionUID = 943696962662143599L;

	private static final Map<String, Factory<?>> FACTORY_MAP = new HashMap<>(4);

	static {
		FACTORY_MAP.put("format", Format::create);
		FACTORY_MAP.put("streams", Stream::create);
		FACTORY_MAP.put("chapters", Chapter::create);
	}

	// --------------------------------------------------------

	public static class MovieMetadatasBuilder implements MetadatasBuilder<MovieMetadatas, MovieMetadatasBuilder> {

		private final MediaInput mediaInput;

		private Soft soft;

		private Consumer<SoftExecutor> customizeSoftExecutor;

		private MovieMetadatasBuilder(MediaInput mediaInput) {
			this.mediaInput = Objects.requireNonNull(mediaInput);
		}

		@Override
		public MovieMetadatasBuilder soft(Soft soft) {
			this.soft = Objects.requireNonNull(soft);
			return this;
		}

		@Override
		public MovieMetadatasBuilder customizeExecutor(Consumer<SoftExecutor> customizeSoftExecutor) {
			this.customizeSoftExecutor = customizeSoftExecutor;
			return this;
		}

		@Override
		public MovieMetadatas extract() throws IOException {
			InfoOperation infoOperation = new InfoOperation(mediaInput);
			FFExecutor<MovieMetadatas> executor = new FFExecutor<>(infoOperation);
			executor.customizeSoftExecutor(customizeSoftExecutor);
			executor.setSoft(soft);
			Executed<MovieMetadatas> execute = executor.execute();
			return execute.getResult();
		}

	}

	// --------------------------------------------------------

	private final List<InfoBase> infoBaseList;

	private final String original;

	/**
	 * @param infoBaseList
	 * @param original
	 */
	protected MovieMetadatas(List<InfoBase> infoBaseList, String original) {
		this.infoBaseList = Collections.unmodifiableList(infoBaseList);
		this.original = original;
	}

	/**
	 * @param mediaInput
	 * @return
	 */
	public static MovieMetadatasBuilder with(MediaInput mediaInput) {
		return new MovieMetadatasBuilder(mediaInput);
	}

	/**
	 * @param file
	 * @return
	 */
	public static MovieMetadatasBuilder with(File file) {
		return new MovieMetadatasBuilder(new FileMediaInput(file));
	}

	/**
	 * @param str
	 * @return
	 */
	public static MovieMetadatas parseJSON(String str) {
		JSONObject jsonObject = JSONObject.fromObject(str);
		return create(jsonObject);
	}

	/**
	 * @param jsonObject
	 * @return
	 */
	public static MovieMetadatas create(JSONObject jsonObject) {
		List<InfoBase> objects = new ArrayList<>(4);
		JSONArray names = jsonObject.names();
		Iterator<?> namesIterator = names.iterator();
		MovieMetadatas movieMetadatas = new MovieMetadatas(objects, jsonObject.toString());
		while(namesIterator.hasNext()) {
			String name = (String)namesIterator.next();
			Factory<?> factory = FACTORY_MAP.get(name);
			if(factory == null) {
				continue;
			}
			Object object = jsonObject.get(name);
			if(object instanceof JSONArray) {
				JSONArray array = (JSONArray)object;
				ListIterator<?> listIterator = array.listIterator();
				while(listIterator.hasNext()) {
					Object next = listIterator.next();
					if(next instanceof JSONObject) {
						JSONObject nextJson = (JSONObject)next;
						InfoBase obj = factory.create(nextJson, movieMetadatas);
						if(obj != null) {
							objects.add(obj);
						}
					} else {
						// System.out.println(next);
					}
				}
			} else if(object instanceof JSONObject) {
				InfoBase obj = factory.create((JSONObject)object, movieMetadatas);
				if(obj != null) {
					objects.add(obj);
				}
			}
		}
		return movieMetadatas;
	}

	/**
	 * @param type
	 * @param factory
	 */
	public static void setFactory(String type, Factory<?> factory) {
		FACTORY_MAP.put(type, factory);
	}

	/**
	 * @see org.fagu.fmv.media.Metadatas#toJSON()
	 */
	@Override
	public String toJSON() {
		return original;
	}

	/**
	 * @return the infoBaseList
	 */
	public List<InfoBase> getInfoBaseList() {
		return infoBaseList;
	}

	/**
	 * @return
	 */
	public List<Stream> getStreams() {
		return filterBy(Stream.class);
	}

	/**
	 * @param type
	 * @return
	 */
	public List<InfoBase> getStreams(Type type) {
		return filterBy(getClassByType(type));
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <S extends InfoBase> List<S> getStreams(Predicate<S> predicate) {
		List<S> list = new ArrayList<>();
		for(InfoBase infoBase : infoBaseList) {
			if(predicate.test((S)infoBase)) {
				list.add((S)infoBase);
			}
		}
		return list;
	}

	/**
	 * @return
	 */
	public MapList<Type, Stream> toTypeMap() {
		MapList<Type, Stream> map = MultiValueMaps.hashMapArrayList();
		for(Stream stream : filterBy(Stream.class)) {
			map.add(stream.type(), stream);
		}
		return map;
	}

	/**
	 * @return
	 */
	public List<Chapter> getChapters() {
		return filterBy(Chapter.class);
	}

	/**
	 * @return
	 */
	public List<AudioStream> getAudioStreams() {
		return filterBy(AudioStream.class);
	}

	/**
	 * @return
	 */
	public List<VideoStream> getVideoStreams() {
		return filterBy(VideoStream.class);
	}

	/**
	 * @return
	 */
	public List<SubtitleStream> getSubtitleStreams() {
		return filterBy(SubtitleStream.class);
	}

	/**
	 * @return
	 */
	public AudioStream getAudioStream() {
		return filterFirstBy(AudioStream.class);
	}

	/**
	 * @return
	 */
	public VideoStream getVideoStream() {
		return filterFirstBy(VideoStream.class);
	}

	/**
	 * @return
	 */
	public SubtitleStream getSubtitleStream() {
		return filterFirstBy(SubtitleStream.class);
	}

	/**
	 * @param type
	 * @return
	 */
	public Stream getFirstStreamBy(Type type) {
		for(InfoBase obj : filterBy(Stream.class)) {
			Stream stream = (Stream)obj;
			if(stream.type() == type) {
				return stream;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public Format getFormat() {
		List<Format> list = filterBy(Format.class);
		return ! list.isEmpty() ? list.get(0) : null;
	}

	/**
	 * @param name
	 * @return
	 */
	public Map<InfoBase, Object> searchTags(String name) {
		Map<InfoBase, Object> map = new HashMap<>();
		for(InfoBase infoBase : infoBaseList) {
			Object value = infoBase.tag(name);
			if(value != null) {
				map.put(infoBase, value);
			}
		}
		return map;
	}

	/**
	 * @return
	 */
	public boolean isTreatedByFMV() {
		for(InfoBase infoBase : infoBaseList) {
			if(infoBase.isTreatedByFMV()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param type
	 * @return
	 */
	public boolean contains(Type type) {
		return ! filterBy(getClassByType(type)).isEmpty();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
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
			buf.append("Stream: ").append(stream.index()).append(" / ").append(stream.codecType()).append(ls);
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

		/**
		 * @param jsonObject
		 * @param movieMetadatas
		 * @return
		 */
		O create(JSONObject jsonObject, MovieMetadatas movieMetadatas);
	}

	// ---------------------------------------------------------------

	/**
	 * @param jsonObject
	 * @return
	 */
	static NavigableMap<String, Object> createMap(JSONObject jsonObject) {
		JSONArray names = jsonObject.names();
		Iterator<?> iterator = names.iterator();
		NavigableMap<String, Object> map = new TreeMap<>();
		while(iterator.hasNext()) {
			String name = (String)iterator.next();
			String nameLC = name.toLowerCase();
			Object object = jsonObject.get(name);
			if(object instanceof JSONObject) {
				NavigableMap<String, Object> createMap = createMap((JSONObject)object);
				map.put(name, createMap);
				map.put(nameLC, createMap);
			} else if(object instanceof List) {
				@SuppressWarnings("unchecked")
				List<Object> jsonArray = (List<Object>)object;
				map.put(name, jsonArray);
			} else {
				map.put(name, object);
				map.put(nameLC, object);
			}
		}
		return map;
	}

	// *****************************************************

	/**
	 * @param cls
	 * @return
	 */
	private <T extends InfoBase> T filterFirstBy(Class<T> cls) {
		List<T> list = filterBy(cls);
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * @param cls
	 * @return
	 */
	private <T extends InfoBase> List<T> filterBy(Class<T> cls) {
		return getStreams(ib -> cls.isAssignableFrom(ib.getClass()));
	}

	/**
	 * @param type
	 * @return
	 */
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
