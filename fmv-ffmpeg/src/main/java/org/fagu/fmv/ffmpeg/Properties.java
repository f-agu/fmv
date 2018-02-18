package org.fagu.fmv.ffmpeg;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import org.fagu.fmv.utils.PropertyValue;
import org.fagu.fmv.utils.PropertyValues.IntegerPropertyValue;
import org.fagu.fmv.utils.PropertyValues.StringPropertyValue;

/**
 * @author Oodrive
 * @author f.agu
 * @created 24 avr. 2017 10:32:04
 */
public class Properties {

	public static final String PROPERTY_PREFIX = "fmv.ffmpeg.";

	public static final PropertyValue<String> H264_PRESET = new StringPropertyValue(PROPERTY_PREFIX + "h264.preset",
			"medium");

	public static final PropertyValue<Integer> H264_QUALITY = new IntegerPropertyValue(PROPERTY_PREFIX + "h264.quality",
			22);

	private Properties() {
	}

}
