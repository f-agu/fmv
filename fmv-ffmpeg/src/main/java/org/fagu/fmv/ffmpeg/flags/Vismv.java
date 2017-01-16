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
 * Visualize motion vectors (MVs) (default 0)
 * 
 * @author f.agu
 */
public class Vismv extends Flags<Vismv> {

	/**
	 * Forward predicted MVs of P-frames
	 */
	public static final Vismv PF = new Vismv(0, "pf", IO.INPUT);

	/**
	 * Forward predicted MVs of B-frames
	 */
	public static final Vismv BF = new Vismv(1, "bf", IO.INPUT);

	/**
	 * Backward predicted MVs of B-frames
	 */
	public static final Vismv BB = new Vismv(2, "bb", IO.INPUT);

	/**
	 * @param index
	 * @param flag
	 * @param io
	 */
	protected Vismv(int index, String flag, IO io) {
		super(Vismv.class, index, flag, io);
	}
}
