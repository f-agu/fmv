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
 * Select multithreading type (default 3)
 * 
 * @author f.agu
 */
public class ThreadType extends Flags<ThreadType> {

	public static final ThreadType SLICE = new ThreadType(0, "slice", IO.INPUT_OUTPUT);

	public static final ThreadType FRAME = new ThreadType(1, "frame", IO.INPUT_OUTPUT);

	/**
	 * @param index
	 * @param flag
	 * @param io
	 */
	protected ThreadType(int index, String flag, IO io) {
		super(ThreadType.class, index, flag, io);
	}
}
