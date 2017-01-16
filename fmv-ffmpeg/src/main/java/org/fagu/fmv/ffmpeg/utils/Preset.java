package org.fagu.fmv.ffmpeg.utils;

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


/**
 * Configuration preset. This does some automatic settings based on the general type of the image.
 * 
 * @author f.agu
 */
public enum Preset {

	/**
	 * Do not use a preset
	 */
	NONE("none"),
	/**
	 * Use the encoder default.
	 */
	DEFAULT("default"),
	/**
	 * Digital picture, like portrait, inner shot
	 */
	PICTURE("picture"),
	/**
	 * Outdoor photograph, with natural lighting
	 */
	PHOTO("photo"),
	/**
	 * Hand or line drawing, with high-contrast details
	 */
	DRAWING("drawing"),
	/**
	 * Small-sized colorful images
	 */
	ICON("icon"),
	/**
	 * Text-like
	 */
	TEXT("text");

	/**
	 * 
	 */
	private String value;

	/**
	 * @param value
	 */
	private Preset(String value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}
}
