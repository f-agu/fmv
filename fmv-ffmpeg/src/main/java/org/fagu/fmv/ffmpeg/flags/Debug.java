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
 * Print specific debug info (default 0)
 * 
 * @author f.agu
 */
public class Debug extends Flags<Debug> {
	/**
	 * Picture info
	 */
	public static final Debug PICT = new Debug(0, "pict", IO.INPUT);
	/**
	 * Rate control
	 */
	public static final Debug RC = new Debug(1, "rc", IO.OUTPUT);
	/**
	 * 
	 */
	public static final Debug BITSTREAM = new Debug(2, "bitstream", IO.INPUT);
	/**
	 * Macroblock (MB) type
	 */
	public static final Debug MB_TYPE = new Debug(3, "mb_type", IO.INPUT);
	/**
	 * Per-block quantization parameter (QP)
	 */
	public static final Debug QP = new Debug(4, "qp", IO.INPUT);
	/**
	 * Motion vector
	 */
	public static final Debug MV = new Debug(5, "mv", IO.INPUT);
	/**
	 * 
	 */
	public static final Debug DCT_COEFF = new Debug(6, "dct_coeff", IO.INPUT);
	/**
	 * 
	 */
	public static final Debug SKIP = new Debug(7, "skip", IO.INPUT);
	/**
	 * 
	 */
	public static final Debug STARTCODE = new Debug(8, "startcode", IO.INPUT);
	/**
	 * 
	 */
	public static final Debug PTS = new Debug(9, "pts", IO.INPUT);
	/**
	 * Error recognition
	 */
	public static final Debug ER = new Debug(10, "er", IO.INPUT);
	/**
	 * Memory management control operations (H.264)
	 */
	public static final Debug MMCO = new Debug(11, "mmco", IO.INPUT);
	/**
	 * 
	 */
	public static final Debug BUGS = new Debug(12, "bugs", IO.INPUT);
	/**
	 * Visualize quantization parameter (QP), lower QP are tinted greener
	 */
	public static final Debug VIS_QP = new Debug(13, "vis_qp", IO.INPUT);
	/**
	 * Visualize block types
	 */
	public static final Debug VIS_MB_TYPE = new Debug(14, "vis_mb_type", IO.INPUT);
	/**
	 * Picture buffer allocations
	 */
	public static final Debug BUFFERS = new Debug(15, "buffers", IO.INPUT);
	/**
	 * Threading operations
	 */
	public static final Debug THREAD_OPS = new Debug(16, "thread_ops", IO.INPUT);
	/**
	 * Skip motion compensation
	 */
	public static final Debug NOMC = new Debug(17, "nomc", IO.INPUT);

	/**
	 * @param index
	 * @param flag
	 * @param io
	 */
	protected Debug(int index, String flag, IO io) {
		super(Debug.class, index, flag, io);
	}
}
