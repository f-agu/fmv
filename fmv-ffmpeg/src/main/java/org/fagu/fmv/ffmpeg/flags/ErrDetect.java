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
 * Set error detection flags (default 1)
 * 
 * @author f.agu
 */
public class ErrDetect extends Flags<ErrDetect> {

	/**
	 * Verify embedded CRCs
	 */
	public static final ErrDetect CRCCHECK = new ErrDetect(0, "crccheck", IO.INPUT);

	/**
	 * Detect bitstream specification deviations
	 */
	public static final ErrDetect BITSTREAM = new ErrDetect(1, "bitstream", IO.INPUT);

	/**
	 * Detect improper bitstream length
	 */
	public static final ErrDetect BUFFER = new ErrDetect(2, "buffer", IO.INPUT);

	/**
	 * Abort decoding on minor error detection
	 */
	public static final ErrDetect EXPLODE = new ErrDetect(3, "explode", IO.INPUT);

	/**
	 * Ignore errors
	 */
	public static final ErrDetect IGNORE_ERR = new ErrDetect(4, "ignore_err", IO.INPUT);

	/**
	 * Consider things that violate the spec, are fast to check and have not been seen in the wild as errors
	 */
	public static final ErrDetect CAREFUL = new ErrDetect(5, "careful", IO.INPUT);

	/**
	 * Consider all spec non compliancies as errors
	 */
	public static final ErrDetect COMPLIANT = new ErrDetect(6, "compliant", IO.INPUT);

	/**
	 * Consider things that a sane encoder shouldn't do as an error
	 */
	public static final ErrDetect AGGRESSIVE = new ErrDetect(7, "aggressive", IO.INPUT);

	protected ErrDetect(int index, String flag, IO io) {
		super(ErrDetect.class, index, flag, io);
	}
}
