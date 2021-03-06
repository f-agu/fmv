package org.fagu.fmv.soft.mediainfo;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fagu.fmv.media.MetadatasContainer;


/**
 * @author f.agu
 * @created 7 avr. 2018 14:15:55
 */
public class Info implements MetadatasContainer {

	private final List<InfoBase> infoBases;

	public Info(List<InfoBase> infoBases) {
		this.infoBases = Collections.unmodifiableList(new ArrayList<>(infoBases));
	}

	@SuppressWarnings("unchecked")
	public <T extends InfoBase> List<T> getInfosByType(Class<T> cls) {
		return infoBases.stream()
				.filter(ib -> cls.isAssignableFrom(ib.getClass()))
				.map(ib -> (T)ib)
				.collect(Collectors.toList());
	}

	public List<VideoInfo> getVideos() {
		return getInfosByType(VideoInfo.class);
	}

	public List<AudioInfo> getAudios() {
		return getInfosByType(AudioInfo.class);
	}

	public List<TextInfo> getTexts() {
		return getInfosByType(TextInfo.class);
	}

	public List<MenuInfo> getMenus() {
		return getInfosByType(MenuInfo.class);
	}

	public List<GeneralInfo> getGenerals() {
		return getInfosByType(GeneralInfo.class);
	}

	@SuppressWarnings("unchecked")
	public <T extends InfoBase> Optional<T> getFirstInfoByType(Class<T> cls) {
		return infoBases.stream()
				.filter(ib -> cls.isAssignableFrom(ib.getClass()))
				.map(ib -> (T)ib)
				.findFirst();
	}

	public Optional<VideoInfo> getFirstVideo() {
		return getFirstInfoByType(VideoInfo.class);
	}

	public Optional<AudioInfo> getFirstAudio() {
		return getFirstInfoByType(AudioInfo.class);
	}

	public Optional<ImageInfo> getFirstImage() {
		return getFirstInfoByType(ImageInfo.class);
	}

	public Optional<TextInfo> getFirstText() {
		return getFirstInfoByType(TextInfo.class);
	}

	public Optional<MenuInfo> getFirstMenu() {
		return getFirstInfoByType(MenuInfo.class);
	}

	public Optional<GeneralInfo> getFirstGeneral() {
		return getFirstInfoByType(GeneralInfo.class);
	}

	public List<InfoBase> getInfos() {
		return infoBases;
	}

	@Override
	public Map<String, Object> getData() {
		Map<String, Object> data = new HashMap<>();
		infoBases.forEach(ib -> {
			String name = ib.getType().name().toLowerCase() + ib.getIndexByType();
			data.put(name, ib.getData());
		});
		return data;
	}
}
