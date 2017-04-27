package org.fagu.fmv.core.project;

/*-
 * #%L
 * fmv-core
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
import org.fagu.fmv.utils.PropertyValues.BooleanPropertyValue;
import org.fagu.fmv.utils.PropertyValues.IntegerPropertyValue;


/**
 * @author f.agu
 */
public class Properties {

	public static final PropertyValue<Boolean> PREPARE_BACKGROUND = new BooleanPropertyValue("prepare.background", true);

	public static final PropertyValue<Boolean> SHOW_COMMAND_LINE = new BooleanPropertyValue("commandline.show", false);

	public static final PropertyValue<Integer> VIEW_LAST_MEDIA = new IntegerPropertyValue("view.last.media", null);

	public static final PropertyValue<Integer> PREVIEW_X264_CRF = new IntegerPropertyValue("preview.x264.crf", 23);

	public static final PropertyValue<Integer> PREPARE_MAKE_X264_CRF = new IntegerPropertyValue("make-prepare.x264.crf", 5);

	public static final PropertyValue<Integer> MAKE_X264_CRF = new IntegerPropertyValue("make.x264.crf", 20);

	/**
	 *
	 */
	private Properties() {}

}
