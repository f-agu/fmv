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
public enum AvoidNegativeTs {

	// enabled when required by target format
	AUTO("auto", IO.OUTPUT),
	// do not change timestamps
	DISABLED("disabled", IO.OUTPUT),
	// shift timestamps so they start at 0
	MAKE_ZERO("make_zero", IO.OUTPUT),
	// shift timestamps so they are non negative
	MAKE_NON_NEGATIVE("make_non_negative", IO.OUTPUT);

	private final String flag;

	private final IO io;

	/**
	 * @param flag
	 * @param io
	 */
	private AvoidNegativeTs(String flag, IO io) {
		this.flag = flag;
		this.io = io;
	}

	/**
	 * @return
	 */
	public String flag() {
		return flag;
	}

	/**
	 * @return
	 */
	public IO io() {
		return io;
	}
}
