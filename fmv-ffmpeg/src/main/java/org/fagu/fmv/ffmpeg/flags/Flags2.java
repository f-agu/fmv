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
 * (default 0)
 * 
 * @author f.agu
 */
public class Flags2 extends Flags<Flags2> {
	/**
	 * Allow non-spec-compliant speedup tricks
	 */
	public static final Flags2 FAST = new Flags2(0, "fast", IO.OUTPUT);
	/**
	 * Skip bitstream encoding
	 */
	public static final Flags2 NOOUT = new Flags2(1, "noout", IO.OUTPUT);
	/**
	 * Ignore cropping information from sps
	 */
	public static final Flags2 IGNORECROP = new Flags2(2, "ignorecrop", IO.INPUT);
	/**
	 * Place global headers at every keyframe instead of in extradata
	 */
	public static final Flags2 LOCAL_HEADER = new Flags2(3, "local_header", IO.OUTPUT);
	/**
	 * Frame data might be split into multiple chunks
	 */
	public static final Flags2 CHUNKS = new Flags2(4, "chunks", IO.INPUT);
	/**
	 * Show all frames before the first keyframe
	 */
	public static final Flags2 SHOWALL = new Flags2(5, "showall", IO.INPUT);

	/**
	 * @param index
	 * @param flag
	 * @param io
	 */
	protected Flags2(int index, String flag, IO io) {
		super(Flags2.class, index, flag, io);
	}
}
