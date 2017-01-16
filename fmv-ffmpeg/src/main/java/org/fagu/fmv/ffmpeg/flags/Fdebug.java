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
 * @author f.agu
 */
public class Fdebug extends Flags<Fdebug> {

	public static final Fdebug TS = new Fdebug(0, "ts", IO.INPUT_OUTPUT);

	/**
	 * @param index
	 * @param flag
	 * @param io
	 */
	protected Fdebug(int index, String flag, IO io) {
		super(Fdebug.class, index, flag, io);
	}
}
