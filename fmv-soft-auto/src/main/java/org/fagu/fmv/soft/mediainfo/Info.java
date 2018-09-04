package org.fagu.fmv.soft.mediainfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author f.agu
 * @created 7 avr. 2018 14:15:55
 */
public class Info {

	private final List<InfoBase> infoBases;

	/**
	 * @param infoBases
	 */
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
}
