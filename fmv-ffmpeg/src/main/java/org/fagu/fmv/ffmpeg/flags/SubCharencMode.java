package org.fagu.fmv.ffmpeg.flags;

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

import org.fagu.fmv.ffmpeg.format.IO;


/**
 * Set input text subtitles character encoding mode (default 0)
 * 
 * @author f.agu
 */
public class SubCharencMode extends Flags<SubCharencMode> {

	public static final SubCharencMode DO_NOTHING = new SubCharencMode(0, "do_nothing", IO.INPUT);

	public static final SubCharencMode AUTO = new SubCharencMode(1, "auto", IO.INPUT);

	public static final SubCharencMode PRE_DECODER = new SubCharencMode(2, "pre_decoder", IO.INPUT);

	/**
	 * @param index
	 * @param flag
	 * @param io
	 */
	protected SubCharencMode(int index, String flag, IO io) {
		super(SubCharencMode.class, index, flag, io);
	}
}
