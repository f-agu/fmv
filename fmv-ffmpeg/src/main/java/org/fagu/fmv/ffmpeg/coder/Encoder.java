package org.fagu.fmv.ffmpeg.coder;

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
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.Fraction;


/**
 * @author f.agu
 */
public abstract class Encoder<M> extends Coder<M> {

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum MeMethod {
		// zero motion estimation (fastest)
		ZERO("zero", IO.OUTPUT),
		// full motion estimation (slowest)
		FULL("full", IO.OUTPUT),
		// EPZS motion estimation (default)
		EPZS("epzs", IO.OUTPUT),
		// esa motion estimation (alias for full)
		ESA("esa", IO.OUTPUT),
		// tesa motion estimation
		TESA("tesa", IO.OUTPUT),
		// diamond motion estimation (alias for EPZS)
		DIA("dia", IO.OUTPUT),
		// log motion estimation
		LOG("log", IO.OUTPUT),
		// phods motion estimation
		PHODS("phods", IO.OUTPUT),
		// X1 motion estimation
		X1("x1", IO.OUTPUT),
		// hex motion estimation
		HEX("hex", IO.OUTPUT),
		// umh motion estimation
		UMH("umh", IO.OUTPUT),
		// iter motion estimation
		ITER("iter", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private MeMethod(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Dct {
		// autoselect a good one (default)
		AUTO("auto", IO.OUTPUT),
		// fast integer
		FASTINT("fastint", IO.OUTPUT),
		// accurate integer
		INT("int", IO.OUTPUT),
		MMX("mmx", IO.OUTPUT),
		ALTIVEC("altivec", IO.OUTPUT),
		// floating point AAN DCT
		FAAN("faan", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Dct(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Pred {
		LEFT("left", IO.OUTPUT), PLANE("plane", IO.OUTPUT), MEDIAN("median", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Pred(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Cmp {
		// sum of absolute differences, fast (default)
		SAD("sad", IO.OUTPUT),
		// sum of squared errors
		SSE("sse", IO.OUTPUT),
		// sum of absolute Hadamard transformed differences
		SATD("satd", IO.OUTPUT),
		// sum of absolute DCT transformed differences
		DCT("dct", IO.OUTPUT),
		// sum of squared quantization errors (avoid, low quality)
		PSNR("psnr", IO.OUTPUT),
		// number of bits needed for the block
		BIT("bit", IO.OUTPUT),
		// rate distortion optimal, slow
		RD("rd", IO.OUTPUT),
		// 0
		ZERO("zero", IO.OUTPUT),
		// sum of absolute vertical differences
		VSAD("vsad", IO.OUTPUT),
		// sum of squared vertical differences
		VSSE("vsse", IO.OUTPUT),
		// noise preserving sum of squared differences
		NSSE("nsse", IO.OUTPUT),
		// 5/3 wavelet, only used in snow
		W53("w53", IO.OUTPUT),
		// 9/7 wavelet, only used in snow
		W97("w97", IO.OUTPUT),
		DCTMAX("dctmax", IO.OUTPUT),
		CHROMA("chroma", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Cmp(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Subcmp {
		// sum of absolute differences, fast (default)
		SAD("sad", IO.OUTPUT),
		// sum of squared errors
		SSE("sse", IO.OUTPUT),
		// sum of absolute Hadamard transformed differences
		SATD("satd", IO.OUTPUT),
		// sum of absolute DCT transformed differences
		DCT("dct", IO.OUTPUT),
		// sum of squared quantization errors (avoid, low quality)
		PSNR("psnr", IO.OUTPUT),
		// number of bits needed for the block
		BIT("bit", IO.OUTPUT),
		// rate distortion optimal, slow
		RD("rd", IO.OUTPUT),
		// 0
		ZERO("zero", IO.OUTPUT),
		// sum of absolute vertical differences
		VSAD("vsad", IO.OUTPUT),
		// sum of squared vertical differences
		VSSE("vsse", IO.OUTPUT),
		// noise preserving sum of squared differences
		NSSE("nsse", IO.OUTPUT),
		// 5/3 wavelet, only used in snow
		W53("w53", IO.OUTPUT),
		// 9/7 wavelet, only used in snow
		W97("w97", IO.OUTPUT),
		DCTMAX("dctmax", IO.OUTPUT),
		CHROMA("chroma", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Subcmp(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Mbcmp {
		// sum of absolute differences, fast (default)
		SAD("sad", IO.OUTPUT),
		// sum of squared errors
		SSE("sse", IO.OUTPUT),
		// sum of absolute Hadamard transformed differences
		SATD("satd", IO.OUTPUT),
		// sum of absolute DCT transformed differences
		DCT("dct", IO.OUTPUT),
		// sum of squared quantization errors (avoid, low quality)
		PSNR("psnr", IO.OUTPUT),
		// number of bits needed for the block
		BIT("bit", IO.OUTPUT),
		// rate distortion optimal, slow
		RD("rd", IO.OUTPUT),
		// 0
		ZERO("zero", IO.OUTPUT),
		// sum of absolute vertical differences
		VSAD("vsad", IO.OUTPUT),
		// sum of squared vertical differences
		VSSE("vsse", IO.OUTPUT),
		// noise preserving sum of squared differences
		NSSE("nsse", IO.OUTPUT),
		// 5/3 wavelet, only used in snow
		W53("w53", IO.OUTPUT),
		// 9/7 wavelet, only used in snow
		W97("w97", IO.OUTPUT),
		DCTMAX("dctmax", IO.OUTPUT),
		CHROMA("chroma", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Mbcmp(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Ildctcmp {
		// sum of absolute differences, fast (default)
		SAD("sad", IO.OUTPUT),
		// sum of squared errors
		SSE("sse", IO.OUTPUT),
		// sum of absolute Hadamard transformed differences
		SATD("satd", IO.OUTPUT),
		// sum of absolute DCT transformed differences
		DCT("dct", IO.OUTPUT),
		// sum of squared quantization errors (avoid, low quality)
		PSNR("psnr", IO.OUTPUT),
		// number of bits needed for the block
		BIT("bit", IO.OUTPUT),
		// rate distortion optimal, slow
		RD("rd", IO.OUTPUT),
		// 0
		ZERO("zero", IO.OUTPUT),
		// sum of absolute vertical differences
		VSAD("vsad", IO.OUTPUT),
		// sum of squared vertical differences
		VSSE("vsse", IO.OUTPUT),
		// noise preserving sum of squared differences
		NSSE("nsse", IO.OUTPUT),
		// 5/3 wavelet, only used in snow
		W53("w53", IO.OUTPUT),
		// 9/7 wavelet, only used in snow
		W97("w97", IO.OUTPUT),
		DCTMAX("dctmax", IO.OUTPUT),
		CHROMA("chroma", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Ildctcmp(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Precmp {
		// sum of absolute differences, fast (default)
		SAD("sad", IO.OUTPUT),
		// sum of squared errors
		SSE("sse", IO.OUTPUT),
		// sum of absolute Hadamard transformed differences
		SATD("satd", IO.OUTPUT),
		// sum of absolute DCT transformed differences
		DCT("dct", IO.OUTPUT),
		// sum of squared quantization errors (avoid, low quality)
		PSNR("psnr", IO.OUTPUT),
		// number of bits needed for the block
		BIT("bit", IO.OUTPUT),
		// rate distortion optimal, slow
		RD("rd", IO.OUTPUT),
		// 0
		ZERO("zero", IO.OUTPUT),
		// sum of absolute vertical differences
		VSAD("vsad", IO.OUTPUT),
		// sum of squared vertical differences
		VSSE("vsse", IO.OUTPUT),
		// noise preserving sum of squared differences
		NSSE("nsse", IO.OUTPUT),
		// 5/3 wavelet, only used in snow
		W53("w53", IO.OUTPUT),
		// 9/7 wavelet, only used in snow
		W97("w97", IO.OUTPUT),
		DCTMAX("dctmax", IO.OUTPUT),
		CHROMA("chroma", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Precmp(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Coder {
		// variable length coder / Huffman coder
		VLC("vlc", IO.OUTPUT),
		// arithmetic coder
		AC("ac", IO.OUTPUT),
		// raw (no encoding)
		RAW("raw", IO.OUTPUT),
		// run-length coder
		RLE("rle", IO.OUTPUT),
		// deflate-based coder
		DEFLATE("deflate", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Coder(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Mbd {
		// use mbcmp (default)
		SIMPLE("simple", IO.OUTPUT),
		// use fewest bits
		BITS("bits", IO.OUTPUT),
		// use best rate distortion
		RD("rd", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Mbd(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Profile {
		UNKNOWN("unknown", IO.OUTPUT),
		AAC_MAIN("aac_main", IO.OUTPUT),
		AAC_LOW("aac_low", IO.OUTPUT),
		AAC_SSR("aac_ssr", IO.OUTPUT),
		AAC_LTP("aac_ltp", IO.OUTPUT),
		AAC_HE("aac_he", IO.OUTPUT),
		AAC_HE_V2("aac_he_v2", IO.OUTPUT),
		AAC_LD("aac_ld", IO.OUTPUT),
		AAC_ELD("aac_eld", IO.OUTPUT),
		MPEG2_AAC_LOW("mpeg2_aac_low", IO.OUTPUT),
		MPEG2_AAC_HE("mpeg2_aac_he", IO.OUTPUT),
		DTS("dts", IO.OUTPUT),
		DTS_ES("dts_es", IO.OUTPUT),
		DTS_96_24("dts_96_24", IO.OUTPUT),
		DTS_HD_HRA("dts_hd_hra", IO.OUTPUT),
		DTS_HD_MA("dts_hd_ma", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Profile(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Level {
		UNKNOWN("unknown", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Level(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Skipcmp {
		// sum of absolute differences, fast (default)
		SAD("sad", IO.OUTPUT),
		// sum of squared errors
		SSE("sse", IO.OUTPUT),
		// sum of absolute Hadamard transformed differences
		SATD("satd", IO.OUTPUT),
		// sum of absolute DCT transformed differences
		DCT("dct", IO.OUTPUT),
		// sum of squared quantization errors (avoid, low quality)
		PSNR("psnr", IO.OUTPUT),
		// number of bits needed for the block
		BIT("bit", IO.OUTPUT),
		// rate distortion optimal, slow
		RD("rd", IO.OUTPUT),
		// 0
		ZERO("zero", IO.OUTPUT),
		// sum of absolute vertical differences
		VSAD("vsad", IO.OUTPUT),
		// sum of squared vertical differences
		VSSE("vsse", IO.OUTPUT),
		// noise preserving sum of squared differences
		NSSE("nsse", IO.OUTPUT),
		// 5/3 wavelet, only used in snow
		W53("w53", IO.OUTPUT),
		// 9/7 wavelet, only used in snow
		W97("w97", IO.OUTPUT),
		DCTMAX("dctmax", IO.OUTPUT),
		CHROMA("chroma", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Skipcmp(String flag, IO io) {
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

	// -----------------------------------------------

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum AudioServiceType {
		// Main Audio Service
		MA("ma", IO.OUTPUT),
		// Effects
		EF("ef", IO.OUTPUT),
		// Visually Impaired
		VI("vi", IO.OUTPUT),
		// Hearing Impaired
		HI("hi", IO.OUTPUT),
		// Dialogue
		DI("di", IO.OUTPUT),
		// Commentary
		CO("co", IO.OUTPUT),
		// Emergency
		EM("em", IO.OUTPUT),
		// Voice Over
		VO("vo", IO.OUTPUT),
		// Karaoke
		KA("ka", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private AudioServiceType(String flag, IO io) {
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

	// -----------------------------------------------

	/**
	 * @param type
	 * @param name
	 */
	public Encoder(Type type, String name) {
		super(type, name, IO.OUTPUT);
	}

	/**
	 * Set bitrate (in bits/s) (from 0 to INT_MAX) (default 200000)
	 * 
	 * @param b
	 * @return
	 */
	public M b(int b) {
		if(b < 0) {
			throw new IllegalArgumentException("b must be at least 0: " + b);
		}
		parameter("-b", Integer.toString(b));
		return getMThis();
	}

	/**
	 * Set bitrate (in bits/s) (from 0 to INT_MAX) (default 128000)
	 * 
	 * @param ab
	 * @return
	 */
	public M ab(int ab) {
		if(ab < 0) {
			throw new IllegalArgumentException("ab must be at least 0: " + ab);
		}
		parameter("-ab", Integer.toString(ab));
		return getMThis();
	}

	/**
	 * Set video bitrate tolerance (in bits/s). In 1-pass mode, bitrate tolerance specifies how far ratecontrol is
	 * willing to deviate from the target average bitrate value. This is not related to minimum/maximum bitrate.
	 * Lowering tolerance too much has an adverse effect on quality. (from 1 to INT_MAX) (default 4e+006)
	 * 
	 * @param bt
	 * @return
	 */
	public M bt(int bt) {
		if(bt < 1) {
			throw new IllegalArgumentException("bt must be at least 1: " + bt);
		}
		parameter("-bt", Integer.toString(bt));
		return getMThis();
	}

	/**
	 * Set motion estimation method (from INT_MIN to INT_MAX) (default 5)
	 * 
	 * @param meMethod
	 * @return
	 */
	public M meMethod(MeMethod meMethod) {
		if( ! meMethod.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + meMethod.io() + ": " + io);
		}
		parameter("-me_method", meMethod.flag());
		return getMThis();
	}

	/**
	 * Set the group of picture (GOP) size (from INT_MIN to INT_MAX) (default 12)
	 * 
	 * @param g
	 * @return
	 */
	public M g(int g) {
		parameter("-g", Integer.toString(g));
		return getMThis();
	}

	/**
	 * Set cutoff bandwidth (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param cutoff
	 * @return
	 */
	public M cutoff(int cutoff) {
		parameter("-cutoff", Integer.toString(cutoff));
		return getMThis();
	}

	/**
	 * (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param frameSize
	 * @return
	 */
	public M frameSize(int frameSize) {
		parameter("-frame_size", Integer.toString(frameSize));
		return getMThis();
	}

	/**
	 * Video quantizer scale compression (VBR). Constant of ratecontrol equation. Recommended range for default rc_eq:
	 * 0.0-1.0 (from -FLT_MAX to FLT_MAX) (default 0.5)
	 * 
	 * @param qcomp
	 * @return
	 */
	public M qcomp(float qcomp) {
		parameter("-qcomp", Float.toString(qcomp));
		return getMThis();
	}

	/**
	 * Video quantizer scale blur (VBR) (from -1 to FLT_MAX) (default 0.5)
	 * 
	 * @param qblur
	 * @return
	 */
	public M qblur(float qblur) {
		if(qblur < - 1.0) {
			throw new IllegalArgumentException("qblur must be at least -1.0: " + qblur);
		}
		parameter("-qblur", Float.toString(qblur));
		return getMThis();
	}

	/**
	 * Minimum video quantizer scale (VBR) (from -1 to 69) (default 2)
	 * 
	 * @param qmin
	 * @return
	 */
	public M qmin(int qmin) {
		if( - 1 > qmin || qmin > 69) {
			throw new IllegalArgumentException("qmin must be between -1 and 69: " + qmin);
		}
		parameter("-qmin", Integer.toString(qmin));
		return getMThis();
	}

	/**
	 * Maximum video quantizer scale (VBR) (from -1 to 1024) (default 31)
	 * 
	 * @param qmax
	 * @return
	 */
	public M qmax(int qmax) {
		if( - 1 > qmax || qmax > 1024) {
			throw new IllegalArgumentException("qmax must be between -1 and 1024: " + qmax);
		}
		parameter("-qmax", Integer.toString(qmax));
		return getMThis();
	}

	/**
	 * Maximum difference between the quantizer scales (VBR) (from INT_MIN to INT_MAX) (default 3)
	 * 
	 * @param qdiff
	 * @return
	 */
	public M qdiff(int qdiff) {
		parameter("-qdiff", Integer.toString(qdiff));
		return getMThis();
	}

	/**
	 * Set maximum number of B frames between non-B-frames (from -1 to INT_MAX) (default 0)
	 * 
	 * @param bf
	 * @return
	 */
	public M bf(int bf) {
		if(bf < - 1) {
			throw new IllegalArgumentException("bf must be at least -1: " + bf);
		}
		parameter("-bf", Integer.toString(bf));
		return getMThis();
	}

	/**
	 * QP factor between P- and B-frames (from -FLT_MAX to FLT_MAX) (default 1.25)
	 * 
	 * @param bQfactor
	 * @return
	 */
	public M bQfactor(float bQfactor) {
		parameter("-b_qfactor", Float.toString(bQfactor));
		return getMThis();
	}

	/**
	 * Ratecontrol method (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param rcStrategy
	 * @return
	 */
	public M rcStrategy(int rcStrategy) {
		parameter("-rc_strategy", Integer.toString(rcStrategy));
		return getMThis();
	}

	/**
	 * Strategy to choose between I/P/B-frames (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param bStrategy
	 * @return
	 */
	public M bStrategy(int bStrategy) {
		parameter("-b_strategy", Integer.toString(bStrategy));
		return getMThis();
	}

	/**
	 * RTP payload size in bytes (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param ps
	 * @return
	 */
	public M ps(int ps) {
		parameter("-ps", Integer.toString(ps));
		return getMThis();
	}

	/**
	 * QP offset between P- and B-frames (from -FLT_MAX to FLT_MAX) (default 1.25)
	 * 
	 * @param bQoffset
	 * @return
	 */
	public M bQoffset(float bQoffset) {
		parameter("-b_qoffset", Float.toString(bQoffset));
		return getMThis();
	}

	/**
	 * Use MPEG quantizers instead of H.263 (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param mpegQuant
	 * @return
	 */
	public M mpegQuant(int mpegQuant) {
		parameter("-mpeg_quant", Integer.toString(mpegQuant));
		return getMThis();
	}

	/**
	 * How to keep quantizer between qmin and qmax (0 = clip, 1 = use differentiable function) (from 0 to 99) (default
	 * 0)
	 * 
	 * @param qsquish
	 * @return
	 */
	public M qsquish(float qsquish) {
		if(0.0 > qsquish || qsquish > 99.0) {
			throw new IllegalArgumentException("qsquish must be between 0.0 and 99.0: " + qsquish);
		}
		parameter("-qsquish", Float.toString(qsquish));
		return getMThis();
	}

	/**
	 * Experimental quantizer modulation (from -FLT_MAX to FLT_MAX) (default 0)
	 * 
	 * @param rcQmodAmp
	 * @return
	 */
	public M rcQmodAmp(float rcQmodAmp) {
		parameter("-rc_qmod_amp", Float.toString(rcQmodAmp));
		return getMThis();
	}

	/**
	 * Experimental quantizer modulation (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param rcQmodFreq
	 * @return
	 */
	public M rcQmodFreq(int rcQmodFreq) {
		parameter("-rc_qmod_freq", Integer.toString(rcQmodFreq));
		return getMThis();
	}

	/**
	 * Set rate control equation. When computing the expression, besides the standard functions defined in the section
	 * 'Expression Evaluation', the following functions are available: bits2qp(bits), qp2bits(qp). Also the following
	 * constants are available: iTex pTex tex mv fCode iCount mcVar var isI isP isB avgQP qComp avgIITex avgPITex
	 * avgPPTex avgBPTex avgTex.
	 * 
	 * @param rcEq
	 * @return
	 */
	public M rcEq(String rcEq) {
		parameter("-rc_eq", rcEq);
		return getMThis();
	}

	/**
	 * Maximum bitrate (in bits/s). Used for VBV together with bufsize. (from 0 to INT_MAX) (default 0)
	 * 
	 * @param maxrate
	 * @return
	 */
	public M maxrate(int maxrate) {
		if(maxrate < 0) {
			throw new IllegalArgumentException("maxrate must be at least 0: " + maxrate);
		}
		parameter("-maxrate", Integer.toString(maxrate));
		return getMThis();
	}

	/**
	 * Minimum bitrate (in bits/s). Most useful in setting up a CBR encode. It is of little use otherwise. (from INT_MIN
	 * to INT_MAX) (default 0)
	 * 
	 * @param minrate
	 * @return
	 */
	public M minrate(int minrate) {
		parameter("-minrate", Integer.toString(minrate));
		return getMThis();
	}

	/**
	 * Set ratecontrol buffer size (in bits) (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param bufsize
	 * @return
	 */
	public M bufsize(int bufsize) {
		parameter("-bufsize", Integer.toString(bufsize));
		return getMThis();
	}

	/**
	 * Currently useless (from -FLT_MAX to FLT_MAX) (default 1)
	 * 
	 * @param rcBufAggressivity
	 * @return
	 */
	public M rcBufAggressivity(float rcBufAggressivity) {
		parameter("-rc_buf_aggressivity", Float.toString(rcBufAggressivity));
		return getMThis();
	}

	/**
	 * QP factor between P- and I-frames (from -FLT_MAX to FLT_MAX) (default -0.8)
	 * 
	 * @param iQfactor
	 * @return
	 */
	public M iQfactor(float iQfactor) {
		parameter("-i_qfactor", Float.toString(iQfactor));
		return getMThis();
	}

	/**
	 * QP offset between P- and I-frames (from -FLT_MAX to FLT_MAX) (default 0)
	 * 
	 * @param iQoffset
	 * @return
	 */
	public M iQoffset(float iQoffset) {
		parameter("-i_qoffset", Float.toString(iQoffset));
		return getMThis();
	}

	/**
	 * Initial complexity for 1-pass encoding (from -FLT_MAX to FLT_MAX) (default 0)
	 * 
	 * @param rcInitCplx
	 * @return
	 */
	public M rcInitCplx(float rcInitCplx) {
		parameter("-rc_init_cplx", Float.toString(rcInitCplx));
		return getMThis();
	}

	/**
	 * DCT algorithm (from 0 to INT_MAX) (default 0)
	 * 
	 * @param dct
	 * @return
	 */
	public M dct(Dct dct) {
		if( ! dct.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + dct.io() + ": " + io);
		}
		parameter("-dct", dct.flag());
		return getMThis();
	}

	/**
	 * Compresses bright areas stronger than medium ones (from -FLT_MAX to FLT_MAX) (default 0)
	 * 
	 * @param lumiMask
	 * @return
	 */
	public M lumiMask(float lumiMask) {
		parameter("-lumi_mask", Float.toString(lumiMask));
		return getMThis();
	}

	/**
	 * Temporal complexity masking (from -FLT_MAX to FLT_MAX) (default 0)
	 * 
	 * @param tcplxMask
	 * @return
	 */
	public M tcplxMask(float tcplxMask) {
		parameter("-tcplx_mask", Float.toString(tcplxMask));
		return getMThis();
	}

	/**
	 * Spatial complexity masking (from -FLT_MAX to FLT_MAX) (default 0)
	 * 
	 * @param scplxMask
	 * @return
	 */
	public M scplxMask(float scplxMask) {
		parameter("-scplx_mask", Float.toString(scplxMask));
		return getMThis();
	}

	/**
	 * Inter masking (from -FLT_MAX to FLT_MAX) (default 0)
	 * 
	 * @param pMask
	 * @return
	 */
	public M pMask(float pMask) {
		parameter("-p_mask", Float.toString(pMask));
		return getMThis();
	}

	/**
	 * Compresses dark areas stronger than medium ones (from -FLT_MAX to FLT_MAX) (default 0)
	 * 
	 * @param darkMask
	 * @return
	 */
	public M darkMask(float darkMask) {
		parameter("-dark_mask", Float.toString(darkMask));
		return getMThis();
	}

	/**
	 * Prediction method (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param pred
	 * @return
	 */
	public M pred(Pred pred) {
		if( ! pred.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + pred.io() + ": " + io);
		}
		parameter("-pred", pred.flag());
		return getMThis();
	}

	/**
	 * Sample aspect ratio (from 0 to 10) (default 0/1)
	 * 
	 * @param aspect
	 * @return
	 */
	public M aspect(Fraction aspect) {
		if(0 > aspect.doubleValue() || aspect.doubleValue() > 10) {
			throw new IllegalArgumentException("aspect must be between 0 and 10: " + aspect);
		}
		parameter("-aspect", aspect.toString());
		return getMThis();
	}

	/**
	 * Full-pel ME compare function (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param cmp
	 * @return
	 */
	public M cmp(Cmp cmp) {
		if( ! cmp.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + cmp.io() + ": " + io);
		}
		parameter("-cmp", cmp.flag());
		return getMThis();
	}

	/**
	 * Sub-pel ME compare function (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param subcmp
	 * @return
	 */
	public M subcmp(Subcmp subcmp) {
		if( ! subcmp.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + subcmp.io() + ": " + io);
		}
		parameter("-subcmp", subcmp.flag());
		return getMThis();
	}

	/**
	 * Macroblock compare function (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param mbcmp
	 * @return
	 */
	public M mbcmp(Mbcmp mbcmp) {
		if( ! mbcmp.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + mbcmp.io() + ": " + io);
		}
		parameter("-mbcmp", mbcmp.flag());
		return getMThis();
	}

	/**
	 * Interlaced DCT compare function (from INT_MIN to INT_MAX) (default 8)
	 * 
	 * @param ildctcmp
	 * @return
	 */
	public M ildctcmp(Ildctcmp ildctcmp) {
		if( ! ildctcmp.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + ildctcmp.io() + ": " + io);
		}
		parameter("-ildctcmp", ildctcmp.flag());
		return getMThis();
	}

	/**
	 * Diamond type & size for motion estimation (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param diaSize
	 * @return
	 */
	public M diaSize(int diaSize) {
		parameter("-dia_size", Integer.toString(diaSize));
		return getMThis();
	}

	/**
	 * Amount of motion predictors from the previous frame (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param lastPred
	 * @return
	 */
	public M lastPred(int lastPred) {
		parameter("-last_pred", Integer.toString(lastPred));
		return getMThis();
	}

	/**
	 * Pre motion estimation (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param preme
	 * @return
	 */
	public M preme(int preme) {
		parameter("-preme", Integer.toString(preme));
		return getMThis();
	}

	/**
	 * Pre motion estimation compare function (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param precmp
	 * @return
	 */
	public M precmp(Precmp precmp) {
		if( ! precmp.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + precmp.io() + ": " + io);
		}
		parameter("-precmp", precmp.flag());
		return getMThis();
	}

	/**
	 * Diamond type & size for motion estimation pre-pass (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param preDiaSize
	 * @return
	 */
	public M preDiaSize(int preDiaSize) {
		parameter("-pre_dia_size", Integer.toString(preDiaSize));
		return getMThis();
	}

	/**
	 * Sub-pel motion estimation quality (from INT_MIN to INT_MAX) (default 8)
	 * 
	 * @param subq
	 * @return
	 */
	public M subq(int subq) {
		parameter("-subq", Integer.toString(subq));
		return getMThis();
	}

	/**
	 * Limit motion vectors range (1023 for DivX player) (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param meRange
	 * @return
	 */
	public M meRange(int meRange) {
		parameter("-me_range", Integer.toString(meRange));
		return getMThis();
	}

	/**
	 * Intra quant bias (from INT_MIN to INT_MAX) (default 999999)
	 * 
	 * @param ibias
	 * @return
	 */
	public M ibias(int ibias) {
		parameter("-ibias", Integer.toString(ibias));
		return getMThis();
	}

	/**
	 * Inter quant bias (from INT_MIN to INT_MAX) (default 999999)
	 * 
	 * @param pbias
	 * @return
	 */
	public M pbias(int pbias) {
		parameter("-pbias", Integer.toString(pbias));
		return getMThis();
	}

	/**
	 * (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param globalQuality
	 * @return
	 */
	public M globalQuality(int globalQuality) {
		parameter("-global_quality", Integer.toString(globalQuality));
		return getMThis();
	}

	/**
	 * (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param coder
	 * @return
	 */
	public M coder(Coder coder) {
		if( ! coder.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + coder.io() + ": " + io);
		}
		parameter("-coder", coder.flag());
		return getMThis();
	}

	/**
	 * Context model (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param context
	 * @return
	 */
	public M context(int context) {
		parameter("-context", Integer.toString(context));
		return getMThis();
	}

	/**
	 * Macroblock decision algorithm (high quality mode) (from 0 to 2) (default 0)
	 * 
	 * @param mbd
	 * @return
	 */
	public M mbd(Mbd mbd) {
		if( ! mbd.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + mbd.io() + ": " + io);
		}
		parameter("-mbd", mbd.flag());
		return getMThis();
	}

	/**
	 * Scene change threshold (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param scThreshold
	 * @return
	 */
	public M scThreshold(int scThreshold) {
		parameter("-sc_threshold", Integer.toString(scThreshold));
		return getMThis();
	}

	/**
	 * Minimum Lagrange factor (VBR) (from 0 to INT_MAX) (default 236)
	 * 
	 * @param lmin
	 * @return
	 */
	public M lmin(int lmin) {
		if(lmin < 0) {
			throw new IllegalArgumentException("lmin must be at least 0: " + lmin);
		}
		parameter("-lmin", Integer.toString(lmin));
		return getMThis();
	}

	/**
	 * Maximum Lagrange factor (VBR) (from 0 to INT_MAX) (default 3658)
	 * 
	 * @param lmax
	 * @return
	 */
	public M lmax(int lmax) {
		if(lmax < 0) {
			throw new IllegalArgumentException("lmax must be at least 0: " + lmax);
		}
		parameter("-lmax", Integer.toString(lmax));
		return getMThis();
	}

	/**
	 * Noise reduction (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param nr
	 * @return
	 */
	public M nr(int nr) {
		parameter("-nr", Integer.toString(nr));
		return getMThis();
	}

	/**
	 * Number of bits which should be loaded into the rc buffer before decoding starts (from INT_MIN to INT_MAX)
	 * (default 0)
	 * 
	 * @param rcInitOccupancy
	 * @return
	 */
	public M rcInitOccupancy(int rcInitOccupancy) {
		parameter("-rc_init_occupancy", Integer.toString(rcInitOccupancy));
		return getMThis();
	}

	/**
	 * (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param error
	 * @return
	 */
	public M error(int error) {
		parameter("-error", Integer.toString(error));
		return getMThis();
	}

	/**
	 * Motion estimation threshold (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param meThreshold
	 * @return
	 */
	public M meThreshold(int meThreshold) {
		parameter("-me_threshold", Integer.toString(meThreshold));
		return getMThis();
	}

	/**
	 * Macroblock threshold (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param mbThreshold
	 * @return
	 */
	public M mbThreshold(int mbThreshold) {
		parameter("-mb_threshold", Integer.toString(mbThreshold));
		return getMThis();
	}

	/**
	 * Intra_dc_precision (from -8 to 16) (default 0)
	 * 
	 * @param dc
	 * @return
	 */
	public M dc(int dc) {
		if( - 8 > dc || dc > 16) {
			throw new IllegalArgumentException("dc must be between -8 and 16: " + dc);
		}
		parameter("-dc", Integer.toString(dc));
		return getMThis();
	}

	/**
	 * Nsse weight (from INT_MIN to INT_MAX) (default 8)
	 * 
	 * @param nssew
	 * @return
	 */
	public M nssew(int nssew) {
		parameter("-nssew", Integer.toString(nssew));
		return getMThis();
	}

	/**
	 * (from INT_MIN to INT_MAX) (default -99)
	 * 
	 * @param profile
	 * @return
	 */
	public M profile(Profile profile) {
		if( ! profile.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + profile.io() + ": " + io);
		}
		parameter("-profile", profile.flag());
		return getMThis();
	}

	/**
	 * (from INT_MIN to INT_MAX) (default -99)
	 * 
	 * @param level
	 * @return
	 */
	public M level(Level level) {
		if( ! level.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + level.io() + ": " + io);
		}
		parameter("-level", level.flag());
		return getMThis();
	}

	/**
	 * Frame skip threshold (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param skipThreshold
	 * @return
	 */
	public M skipThreshold(int skipThreshold) {
		parameter("-skip_threshold", Integer.toString(skipThreshold));
		return getMThis();
	}

	/**
	 * Frame skip factor (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param skipFactor
	 * @return
	 */
	public M skipFactor(int skipFactor) {
		parameter("-skip_factor", Integer.toString(skipFactor));
		return getMThis();
	}

	/**
	 * Frame skip exponent (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param skipExp
	 * @return
	 */
	public M skipExp(int skipExp) {
		parameter("-skip_exp", Integer.toString(skipExp));
		return getMThis();
	}

	/**
	 * Frame skip compare function (from INT_MIN to INT_MAX) (default 13)
	 * 
	 * @param skipcmp
	 * @return
	 */
	public M skipcmp(Skipcmp skipcmp) {
		if( ! skipcmp.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + skipcmp.io() + ": " + io);
		}
		parameter("-skipcmp", skipcmp.flag());
		return getMThis();
	}

	/**
	 * Increase the quantizer for macroblocks close to borders (from -FLT_MAX to FLT_MAX) (default 0)
	 * 
	 * @param borderMask
	 * @return
	 */
	public M borderMask(float borderMask) {
		parameter("-border_mask", Float.toString(borderMask));
		return getMThis();
	}

	/**
	 * Minimum macroblock Lagrange factor (VBR) (from 1 to 32767) (default 236)
	 * 
	 * @param mblmin
	 * @return
	 */
	public M mblmin(int mblmin) {
		if(1 > mblmin || mblmin > 32767) {
			throw new IllegalArgumentException("mblmin must be between 1 and 32767: " + mblmin);
		}
		parameter("-mblmin", Integer.toString(mblmin));
		return getMThis();
	}

	/**
	 * Maximum macroblock Lagrange factor (VBR) (from 1 to 32767) (default 3658)
	 * 
	 * @param mblmax
	 * @return
	 */
	public M mblmax(int mblmax) {
		if(1 > mblmax || mblmax > 32767) {
			throw new IllegalArgumentException("mblmax must be between 1 and 32767: " + mblmax);
		}
		parameter("-mblmax", Integer.toString(mblmax));
		return getMThis();
	}

	/**
	 * Motion estimation bitrate penalty compensation (1.0 = 256) (from INT_MIN to INT_MAX) (default 256)
	 * 
	 * @param mepc
	 * @return
	 */
	public M mepc(int mepc) {
		parameter("-mepc", Integer.toString(mepc));
		return getMThis();
	}

	/**
	 * Refine the two motion vectors used in bidirectional macroblocks (from 0 to 4) (default 1)
	 * 
	 * @param bidirRefine
	 * @return
	 */
	public M bidirRefine(int bidirRefine) {
		if(0 > bidirRefine || bidirRefine > 4) {
			throw new IllegalArgumentException("bidirRefine must be between 0 and 4: " + bidirRefine);
		}
		parameter("-bidir_refine", Integer.toString(bidirRefine));
		return getMThis();
	}

	/**
	 * Downscale frames for dynamic B-frame decision (from 0 to 10) (default 0)
	 * 
	 * @param brdScale
	 * @return
	 */
	public M brdScale(int brdScale) {
		if(0 > brdScale || brdScale > 10) {
			throw new IllegalArgumentException("brdScale must be between 0 and 10: " + brdScale);
		}
		parameter("-brd_scale", Integer.toString(brdScale));
		return getMThis();
	}

	/**
	 * Minimum interval between IDR-frames (from INT_MIN to INT_MAX) (default 25)
	 * 
	 * @param keyintMin
	 * @return
	 */
	public M keyintMin(int keyintMin) {
		parameter("-keyint_min", Integer.toString(keyintMin));
		return getMThis();
	}

	/**
	 * Reference frames to consider for motion compensation (from INT_MIN to INT_MAX) (default 1)
	 * 
	 * @param refs
	 * @return
	 */
	public M refs(int refs) {
		parameter("-refs", Integer.toString(refs));
		return getMThis();
	}

	/**
	 * Chroma QP offset from luma (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param chromaoffset
	 * @return
	 */
	public M chromaoffset(int chromaoffset) {
		parameter("-chromaoffset", Integer.toString(chromaoffset));
		return getMThis();
	}

	/**
	 * Rate-distortion optimal quantization (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param trellis
	 * @return
	 */
	public M trellis(int trellis) {
		parameter("-trellis", Integer.toString(trellis));
		return getMThis();
	}

	/**
	 * Multiplied by qscale for each frame and added to scene_change_score (from 0 to INT_MAX) (default 6)
	 * 
	 * @param scFactor
	 * @return
	 */
	public M scFactor(int scFactor) {
		if(scFactor < 0) {
			throw new IllegalArgumentException("scFactor must be at least 0: " + scFactor);
		}
		parameter("-sc_factor", Integer.toString(scFactor));
		return getMThis();
	}

	/**
	 * (from 0 to INT_MAX) (default 256)
	 * 
	 * @param mv0Threshold
	 * @return
	 */
	public M mv0Threshold(int mv0Threshold) {
		if(mv0Threshold < 0) {
			throw new IllegalArgumentException("mv0Threshold must be at least 0: " + mv0Threshold);
		}
		parameter("-mv0_threshold", Integer.toString(mv0Threshold));
		return getMThis();
	}

	/**
	 * Adjust sensitivity of b_frame_strategy 1 (from 1 to INT_MAX) (default 40)
	 * 
	 * @param bSensitivity
	 * @return
	 */
	public M bSensitivity(int bSensitivity) {
		if(bSensitivity < 1) {
			throw new IllegalArgumentException("bSensitivity must be at least 1: " + bSensitivity);
		}
		parameter("-b_sensitivity", Integer.toString(bSensitivity));
		return getMThis();
	}

	/**
	 * (from INT_MIN to INT_MAX) (default -1)
	 * 
	 * @param compressionLevel
	 * @return
	 */
	public M compressionLevel(int compressionLevel) {
		parameter("-compression_level", Integer.toString(compressionLevel));
		return getMThis();
	}

	/**
	 * (from INT_MIN to INT_MAX) (default -1)
	 * 
	 * @param minPredictionOrder
	 * @return
	 */
	public M minPredictionOrder(int minPredictionOrder) {
		parameter("-min_prediction_order", Integer.toString(minPredictionOrder));
		return getMThis();
	}

	/**
	 * (from INT_MIN to INT_MAX) (default -1)
	 * 
	 * @param maxPredictionOrder
	 * @return
	 */
	public M maxPredictionOrder(int maxPredictionOrder) {
		parameter("-max_prediction_order", Integer.toString(maxPredictionOrder));
		return getMThis();
	}

	/**
	 * GOP timecode frame start number, in non-drop-frame format (from 0 to I64_MAX) (default 0)
	 * 
	 * @param timecodeFrameStart
	 * @return
	 */
	public M timecodeFrameStart(long timecodeFrameStart) {
		if(timecodeFrameStart < 0) {
			throw new IllegalArgumentException("timecodeFrameStart must be at least 0: " + timecodeFrameStart);
		}
		parameter("-timecode_frame_start", Long.toString(timecodeFrameStart));
		return getMThis();
	}

	/**
	 * (from 0 to FLT_MAX) (default 0)
	 * 
	 * @param rcMaxVbvUse
	 * @return
	 */
	public M rcMaxVbvUse(float rcMaxVbvUse) {
		if(rcMaxVbvUse < 0.0) {
			throw new IllegalArgumentException("rcMaxVbvUse must be at least 0.0: " + rcMaxVbvUse);
		}
		parameter("-rc_max_vbv_use", Float.toString(rcMaxVbvUse));
		return getMThis();
	}

	/**
	 * (from 0 to FLT_MAX) (default 3)
	 * 
	 * @param rcMinVbvUse
	 * @return
	 */
	public M rcMinVbvUse(float rcMinVbvUse) {
		if(rcMinVbvUse < 0.0) {
			throw new IllegalArgumentException("rcMinVbvUse must be at least 0.0: " + rcMinVbvUse);
		}
		parameter("-rc_min_vbv_use", Float.toString(rcMinVbvUse));
		return getMThis();
	}

	/**
	 * Number of slices, used in parallelized encoding (from 0 to INT_MAX) (default 0)
	 * 
	 * @param slices
	 * @return
	 */
	public M slices(int slices) {
		if(slices < 0) {
			throw new IllegalArgumentException("slices must be at least 0: " + slices);
		}
		parameter("-slices", Integer.toString(slices));
		return getMThis();
	}

	/**
	 * Audio service type (from 0 to 8) (default 0)
	 * 
	 * @param audioServiceType
	 * @return
	 */
	public M audioServiceType(AudioServiceType audioServiceType) {
		if( ! audioServiceType.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + audioServiceType.io() + ": " + io);
		}
		parameter("-audio_service_type", audioServiceType.flag());
		return getMThis();
	}

	/**
	 * (from 0 to 1) (default 0)
	 * 
	 * @param sideDataOnlyPackets
	 * @return
	 */
	public M sideDataOnlyPackets(boolean sideDataOnlyPackets) {
		parameter("-side_data_only_packets", Integer.toString(sideDataOnlyPackets ? 1 : 0));
		return getMThis();
	}

}
