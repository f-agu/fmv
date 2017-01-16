package org.fagu.fmv.ffmpeg.flags;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2016 fagu
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
 * Set peak mode (default 0)
 *
 * @author f.agu
 */
public class Peak extends Flags<Peak> {

	/**
	 * Disable any peak mode
	 */
	public static final Peak NONE = new Peak(0, "none", IO.UNDEFINED);

	/**
	 * Enable peak-sample mode
	 */
	public static final Peak SAMPLE = new Peak(1, "sample", IO.UNDEFINED);

	/**
	 * Enable true-peak mode
	 */
	public static final Peak TRUE = new Peak(2, "true", IO.UNDEFINED);

	/**
	 * @param index
	 * @param flag
	 * @param io
	 */
	protected Peak(int index, String flag, IO io) {
		super(Peak.class, index, flag, io);
	}
}
