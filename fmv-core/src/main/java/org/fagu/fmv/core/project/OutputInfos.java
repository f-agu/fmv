package org.fagu.fmv.core.project;

/*
 * #%L
 * fmv-core
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

import org.dom4j.Element;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class OutputInfos implements Cloneable {

	private String format;

	private FrameRate frameRate;

	private Size size;

	private int audioSampling;

	/**
	 * @return
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return
	 */
	public FrameRate getFrameRate() {
		return frameRate;
	}

	/**
	 * @param frameRate
	 */
	public void setFrameRate(FrameRate frameRate) {
		this.frameRate = frameRate;
	}

	/**
	 * @return
	 */
	public Size getSize() {
		return size;
	}

	/**
	 * @param size
	 */
	public void setSize(Size size) {
		this.size = size;
	}

	/**
	 * @return
	 */
	public int getAudioSampling() {
		return audioSampling;
	}

	/**
	 * @param audioSampling
	 */
	public void setAudioSampling(int audioSampling) {
		this.audioSampling = audioSampling;
	}

	/**
	 * @param toElement
	 */
	public void save(Element toElement) {
		toElement.addAttribute("format", format);

		Element videoElement = toElement.addElement("video");
		videoElement.addAttribute("height", Integer.toString(size.getHeight()));
		videoElement.addAttribute("width", Integer.toString(size.getWidth()));
		videoElement.addAttribute("rate", frameRate.toString());

		Element audioElement = toElement.addElement("audio");
		audioElement.addAttribute("sampling", Integer.toString(audioSampling));
	}

	/**
	 * @param fromElement
	 * @throws LoadException
	 */
	public void load(Element fromElement) throws LoadException {
		format = LoadUtils.attributeRequire(fromElement, "format");

		Element videoElement = LoadUtils.elementRequire(fromElement, "video");
		int height = LoadUtils.attributeRequireInt(videoElement, "height");
		int width = LoadUtils.attributeRequireInt(videoElement, "width");
		size = Size.valueOf(width, height);

		try {
			frameRate = FrameRate.parse(LoadUtils.attributeRequire(videoElement, "rate"));
		} catch(IllegalArgumentException e) {
			throw new LoadException(e);
		}

		Element audioElement = LoadUtils.elementRequire(fromElement, "audio");
		audioSampling = LoadUtils.attributeRequireInt(audioElement, "sampling");
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		OutputInfos outputInfos = null;
		try {
			outputInfos = (OutputInfos)super.clone();
		} catch(CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return outputInfos;

	}
}
