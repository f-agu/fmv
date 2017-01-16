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
 * Scaler flags (default 4)
 * 
 * @author f.agu
 */
public class SwsFlags extends Flags<SwsFlags> {
	/**
	 * Fast bilinear
	 */
	public static final SwsFlags FAST_BILINEAR = new SwsFlags(0, "fast_bilinear", IO.OUTPUT);
	/**
	 * Bilinear
	 */
	public static final SwsFlags BILINEAR = new SwsFlags(1, "bilinear", IO.OUTPUT);
	/**
	 * Bicubic
	 */
	public static final SwsFlags BICUBIC = new SwsFlags(2, "bicubic", IO.OUTPUT);
	/**
	 * Experimental
	 */
	public static final SwsFlags EXPERIMENTAL = new SwsFlags(3, "experimental", IO.OUTPUT);
	/**
	 * Nearest neighbor
	 */
	public static final SwsFlags NEIGHBOR = new SwsFlags(4, "neighbor", IO.OUTPUT);
	/**
	 * Averaging area
	 */
	public static final SwsFlags AREA = new SwsFlags(5, "area", IO.OUTPUT);
	/**
	 * Luma bicubic, chroma bilinear
	 */
	public static final SwsFlags BICUBLIN = new SwsFlags(6, "bicublin", IO.OUTPUT);
	/**
	 * Gaussian
	 */
	public static final SwsFlags GAUSS = new SwsFlags(7, "gauss", IO.OUTPUT);
	/**
	 * Sinc
	 */
	public static final SwsFlags SINC = new SwsFlags(8, "sinc", IO.OUTPUT);
	/**
	 * Lanczos
	 */
	public static final SwsFlags LANCZOS = new SwsFlags(9, "lanczos", IO.OUTPUT);
	/**
	 * Natural bicubic spline
	 */
	public static final SwsFlags SPLINE = new SwsFlags(10, "spline", IO.OUTPUT);
	/**
	 * Print info
	 */
	public static final SwsFlags PRINT_INFO = new SwsFlags(11, "print_info", IO.OUTPUT);
	/**
	 * Accurate rounding
	 */
	public static final SwsFlags ACCURATE_RND = new SwsFlags(12, "accurate_rnd", IO.OUTPUT);
	/**
	 * Full chroma interpolation
	 */
	public static final SwsFlags FULL_CHROMA_INT = new SwsFlags(13, "full_chroma_int", IO.OUTPUT);
	/**
	 * Full chroma input
	 */
	public static final SwsFlags FULL_CHROMA_INP = new SwsFlags(14, "full_chroma_inp", IO.OUTPUT);
	/**
	 * 
	 */
	public static final SwsFlags BITEXACT = new SwsFlags(15, "bitexact", IO.OUTPUT);
	/**
	 * Error diffusion dither
	 */
	public static final SwsFlags ERROR_DIFFUSION = new SwsFlags(16, "error_diffusion", IO.OUTPUT);

	/**
	 * @param index
	 * @param flag
	 * @param io
	 */
	protected SwsFlags(int index, String flag, IO io) {
		super(SwsFlags.class, index, flag, io);
	}
}
