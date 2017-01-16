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
 * Set error concealment strategy (default 3)
 * 
 * @author f.agu
 */
public class Ec extends Flags<Ec> {

	/**
	 * Iterative motion vector (MV) search (slow)
	 */
	public static final Ec GUESS_MVS = new Ec(0, "guess_mvs", IO.INPUT);

	/**
	 * Use strong deblock filter for damaged MBs
	 */
	public static final Ec DEBLOCK = new Ec(1, "deblock", IO.INPUT);

	/**
	 * Favor predicting from the previous frame
	 */
	public static final Ec FAVOR_INTER = new Ec(2, "favor_inter", IO.INPUT);

	/**
	 * @param index
	 * @param flag
	 * @param io
	 */
	protected Ec(int index, String flag, IO io) {
		super(Ec.class, index, flag, io);
	}
}
