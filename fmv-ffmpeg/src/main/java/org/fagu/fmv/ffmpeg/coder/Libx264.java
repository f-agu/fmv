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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.fagu.fmv.ffmpeg.format.IO;
import org.fagu.fmv.ffmpeg.operation.LibLog;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * ffmpeg -h encoder=libx264
 *
 * {@link https://trac.ffmpeg.org/wiki/Encode/H.264}<br>
 * {@link http://x264.janhum.alfahosting.org/fullhelp.txt}<br>
 * {@link http://mewiki.project357.com/wiki/X264_Settings#level}
 *
 * @author f.agu
 */
public class Libx264 extends Encoder<Libx264> implements LibLog {

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum AqMode {
		NONE("none", IO.OUTPUT),
		// Variance AQ (complexity mask)
		VARIANCE("variance", IO.OUTPUT),
		// Auto-variance AQ (experimental)
		AUTOVARIANCE("autovariance", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private AqMode(String flag, IO io) {
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
	public enum Weightp {
		NONE("none", IO.OUTPUT), SIMPLE("simple", IO.OUTPUT), SMART("smart", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private Weightp(String flag, IO io) {
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
	public enum BPyramid {
		NONE("none", IO.OUTPUT),
		// Strictly hierarchical pyramid
		STRICT("strict", IO.OUTPUT),
		// Non-strict (not Blu-ray compatible)
		NORMAL("normal", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private BPyramid(String flag, IO io) {
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
	public enum DirectPred {
		NONE("none", IO.OUTPUT), SPATIAL("spatial", IO.OUTPUT), TEMPORAL("temporal", IO.OUTPUT), AUTO("auto", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private DirectPred(String flag, IO io) {
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

		L_1("1"), //
		L_1b("1"), //
		L_1_1("1.1"), //
		L_1_2("1.2"), //
		L_1_3("1.3"), //
		L_2("2"), //
		L_2_1("2.1"), //
		L_2_2("2.2"), //
		L_3_0("3.0"), //
		L_3_1("3.1"), //
		L_3_2("3.2"), //
		L_4_0("4.0"), //
		L_4_1("4.1"), //
		L_4_2("4.2"), //
		L_5("5"), //
		L_5_1("5.1");

		private final String value;

		/**
		 * @param value
		 */
		private Level(String value) {
			this.value = value;
		}

		/**
		 * @return
		 */
		public String flag() {
			return value;
		}

	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum NalHrd {
		NONE("none", IO.OUTPUT), VBR("vbr", IO.OUTPUT), CBR("cbr", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private NalHrd(String flag, IO io) {
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
	 * {@link https://trac.ffmpeg.org/wiki/Encode/H.264}
	 *
	 * @author f.agu
	 */
	public enum Preset {

		// presets in descending order of speed are :

		ULTRAFAST("ultrafast"), //
		SUPERFAST("superfast"), //
		VERYFAST("veryfast"), //
		FASTER("faster"), //
		FAST("fast"), //
		MEDIUM("medium"), //
		SLOW("slow"), //
		SLOWER("slower"), //
		VERYSLOW("veryslow"), //
		PLACEBO("placebo");

		private final String value;

		/**
		 * @param value
		 */
		private Preset(String value) {
			this.value = value;
		}

		/**
		 * @return
		 */
		public String flag() {
			return value;
		}
	}

	// -----------------------------------------------
	/**
	 * @author f.agu
	 */
	public enum Profile {

		BASELINE("baseline"), //
		MAIN("main"), //
		HIGH("high"), //
		HIGH10("high10"), //
		HIGH422("high422"), //
		HIGH444("high444");

		private final String value;

		/**
		 * @param value
		 */
		private Profile(String value) {
			this.value = value;
		}

		/**
		 * @return
		 */
		public String flag() {
			return value;
		}

	}

	// -----------------------------------------------
	/**
	 * @author f.agu
	 */
	public enum Tune {

		FILM("film"), //
		AIMATION("animation"), //
		GRAIN("grain"), //
		STILLIMAGE("stillimage"), //
		PSNR("psnr"), //
		SSIM("ssim"), //
		FASTDECODE("fastdecode"), //
		ZEROLATENCY("zerolatency");

		private final String value;

		/**
		 * @param value
		 */
		private Tune(String value) {
			this.value = value;
		}

		/**
		 * @return
		 */
		public String flag() {
			return value;
		}

	}

	// -----------------------------------------------

	/**
	 * {@link http://trac.ffmpeg.org/wiki/Encode/H.264}<br>
	 * {@link https
	 * ://developer.apple .com/library/mac/documentation/NetworkingInternet/Conceptual /StreamingMediaGuide
	 * /UsingHTTPLiveStreaming/UsingHTTPLiveStreaming.html#//apple_ref /doc/uid/TP40008332-CH102-SW8}
	 *
	 * @author f.agu
	 */
	public enum Compression {

		/**
		 * H.264 Baseline 3.0: All devices
		 */
		ALL_DEVICES(Profile.BASELINE, Level.L_3_0),
		/**
		 * H.264 Baseline 3.1: iPhone 3G and later, and iPod touch 2nd generation and later.
		 */
		IPHONE_3G_OR_MORE(Profile.BASELINE, Level.L_3_1),
		/**
		 * iPad (all versions), Apple TV 2 and later, iPhone 4 and later
		 */
		IPAD_ALL(Profile.MAIN, Level.L_3_1),
		/**
		 * Apple TV 3 and later, iPad 2 and later, iPhone 4S and later
		 */
		APPLE_TV3(Profile.MAIN, Level.L_4_0),
		/**
		 * Apple TV 3 and later, iPad 2 and later, iPhone 4S and later
		 */
		APPLE_TV3_BIS(Profile.HIGH, Level.L_4_0),
		/**
		 * iPad 2 and later and iPhone 4S and later
		 */
		IPAD2(Profile.HIGH, Level.L_4_1);

		private final Profile profile;

		private final Level level;

		/**
		 * @param profile
		 * @param level
		 */
		private Compression(Profile profile, Level level) {
			this.profile = profile;
			this.level = level;
		}

		/**
		 * @return
		 */
		public Profile getProfile() {
			return profile;
		}

		/**
		 * @return
		 */
		public Level getLevel() {
			return level;
		}

	}

	// -----------------------------------------------

	private final List<String> logs = new ArrayList<>();

	/**
	 *
	 */
	protected Libx264() {
		super(Type.VIDEO, "libx264");
	}

	/**
	 * @return
	 */
	public static Libx264 build() {
		return new Libx264();
	}

	// ****************************************

	/**
	 * @return
	 */
	public Libx264 mostCompatible() {
		preset(Preset.MEDIUM);
		compression(Compression.ALL_DEVICES);
		// http://slhck.info/articles/crf
		return crf(23);
	}

	/**
	 * @param compression
	 * @return
	 */
	public Libx264 compression(Compression compression) {
		return profile(compression.getProfile()).level(compression.getLevel());
	}

	// **********************************************

	/**
	 * Set the encoding preset (cf. x264 --fullhelp) (default "medium")
	 *
	 * @param preset
	 * @return
	 */
	public Libx264 preset(Preset preset) {
		parameter("-preset", preset.flag());
		return this;
	}

	/**
	 * Tune the encoding params (cf. x264 --fullhelp)
	 *
	 * @param tune
	 * @return
	 */
	public Libx264 tune(Tune tune) {
		parameter("-tune", tune.flag());
		return this;
	}

	/**
	 * Set profile restrictions (cf. x264 --fullhelp)
	 *
	 * @param profile
	 * @return
	 */
	public Libx264 profile(Profile profile) {
		parameter("-profile:v", profile.flag());
		return this;
	}

	/**
	 * Use fast settings when encoding first pass (from 0 to 1) (default 1)
	 *
	 * @param fastfirstpass
	 * @return
	 */
	public Libx264 fastfirstpass(boolean fastfirstpass) {
		parameter("-fastfirstpass", Integer.toString(fastfirstpass ? 1 : 0));
		return this;
	}

	/**
	 * Specify level (as defined by Annex A)
	 *
	 * @param level
	 * @return
	 */
	public Libx264 level(Level level) {
		parameter("-level", level.flag());
		return this;
	}

	/**
	 * Filename for 2 pass stats
	 *
	 * @param passlogfile
	 * @return
	 */
	public Libx264 passlogfile(String passlogfile) {
		parameter("-passlogfile", passlogfile);
		return this;
	}

	/**
	 * Weighted prediction for P-frames
	 *
	 * @param wpredp
	 * @return
	 */
	public Libx264 wpredp(String wpredp) {
		parameter("-wpredp", wpredp);
		return this;
	}

	/**
	 * X264 options
	 *
	 * @param x264opts
	 * @return
	 */
	public Libx264 x264opts(String x264opts) {
		parameter("-x264opts", x264opts);
		return this;
	}

	/**
	 * Select the quality for constant quality mode (from -1 to 51) (default 23)
	 *
	 * 0 : lossless<br>
	 * <23 : better<br>
	 * >23 : worse<br>
	 * 51 : worst<br>
	 *
	 * @param crf
	 * @return
	 */
	public Libx264 crf(int crf) {
		if( - 1 > crf || crf > 51) {
			throw new IllegalArgumentException("crf must be between -1 and 51: " + name);
		}
		parameter("-crf", Integer.toString(crf));
		return this;
	}

	/**
	 * In CRF mode, prevents VBV from lowering quality beyond this point. (from -1 to FLT_MAX) (default -1)
	 *
	 * @param crfMax
	 * @return
	 */
	public Libx264 crfMax(float crfMax) {
		if( - 1.0 > crfMax || crfMax > 3.4028235E38) {
			throw new IllegalArgumentException("crfMax must be between -1.0 and 3.4028235E38: " + name);
		}
		parameter("-crf_max", Float.toString(crfMax));
		return this;
	}

	/**
	 * Constant quantization parameter rate control method (from -1 to INT_MAX) (default -1)
	 *
	 * @param qp
	 * @return
	 */
	public Libx264 qp(int qp) {
		if( - 1 > qp || qp > 2147483647) {
			throw new IllegalArgumentException("qp must be between -1 and 2147483647: " + name);
		}
		parameter("-qp", Integer.toString(qp));
		return this;
	}

	/**
	 * AQ method (from -1 to INT_MAX) (default -1)
	 *
	 * @param aqMode
	 * @return
	 */
	public Libx264 aqMode(AqMode aqMode) {
		if( ! aqMode.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + aqMode.io() + ": " + io);
		}
		parameter("-aq-mode", aqMode.name());
		return this;
	}

	/**
	 * AQ strength. Reduces blocking and blurring in flat and textured areas. (from -1 to FLT_MAX) (default -1)
	 *
	 * @param aqStrength
	 * @return
	 */
	public Libx264 aqStrength(float aqStrength) {
		if( - 1.0 > aqStrength || aqStrength > 3.4028235E38) {
			throw new IllegalArgumentException("aqStrength must be between -1.0 and 3.4028235E38: " + name);
		}
		parameter("-aq-strength", Float.toString(aqStrength));
		return this;
	}

	/**
	 * Use psychovisual optimizations. (from -1 to 1) (default -1)
	 *
	 * @param psy
	 * @return
	 */
	public Libx264 psy(int psy) {
		if( - 1 > psy || psy > 1) {
			throw new IllegalArgumentException("psy must be between -1 and 1: " + name);
		}
		parameter("-psy", Integer.toString(psy));
		return this;
	}

	/**
	 * Strength of psychovisual optimization, in <psy-rd>:<psy-trellis> format.
	 *
	 * @param psyRd
	 * @return
	 */
	public Libx264 psyRd(String psyRd) {
		parameter("-psy-rd", psyRd);
		return this;
	}

	/**
	 * Number of frames to look ahead for frametype and ratecontrol (from -1 to INT_MAX) (default -1)
	 *
	 * @param rcLookahead
	 * @return
	 */
	public Libx264 rcLookahead(int rcLookahead) {
		if( - 1 > rcLookahead || rcLookahead > 2147483647) {
			throw new IllegalArgumentException("rcLookahead must be between -1 and 2147483647: " + name);
		}
		parameter("-rc-lookahead", Integer.toString(rcLookahead));
		return this;
	}

	/**
	 * Weighted prediction for B-frames. (from -1 to 1) (default -1)
	 *
	 * @param weightb
	 * @return
	 */
	public Libx264 weightb(int weightb) {
		if( - 1 > weightb || weightb > 1) {
			throw new IllegalArgumentException("weightb must be between -1 and 1: " + name);
		}
		parameter("-weightb", Integer.toString(weightb));
		return this;
	}

	/**
	 * Weighted prediction analysis method. (from -1 to INT_MAX) (default -1)
	 *
	 * @param weightp
	 * @return
	 */
	public Libx264 weightp(Weightp weightp) {
		if( ! weightp.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + weightp.io() + ": " + io);
		}
		parameter("-weightp", weightp.name());
		return this;
	}

	/**
	 * Calculate and print SSIM stats. (from -1 to 1) (default -1)
	 *
	 * @param ssim
	 * @return
	 */
	public Libx264 ssim(int ssim) {
		if( - 1 > ssim || ssim > 1) {
			throw new IllegalArgumentException("ssim must be between -1 and 1: " + name);
		}
		parameter("-ssim", Integer.toString(ssim));
		return this;
	}

	/**
	 * Use Periodic Intra Refresh instead of IDR frames. (from -1 to 1) (default -1)
	 *
	 * @param intraRefresh
	 * @return
	 */
	public Libx264 intraRefresh(int intraRefresh) {
		if( - 1 > intraRefresh || intraRefresh > 1) {
			throw new IllegalArgumentException("intraRefresh must be between -1 and 1: " + name);
		}
		parameter("-intra-refresh", Integer.toString(intraRefresh));
		return this;
	}

	/**
	 * Bluray compatibility workarounds. (from -1 to 1) (default -1)
	 *
	 * @param blurayCompat
	 * @return
	 */
	public Libx264 blurayCompat(int blurayCompat) {
		if( - 1 > blurayCompat || blurayCompat > 1) {
			throw new IllegalArgumentException("blurayCompat must be between -1 and 1: " + name);
		}
		parameter("-bluray-compat", Integer.toString(blurayCompat));
		return this;
	}

	/**
	 * Influences how often B-frames are used (from INT_MIN to INT_MAX) (default INT_MIN)
	 *
	 * @param bBias
	 * @return
	 */
	public Libx264 bBias(int bBias) {
		if(bBias > 2147483647) {
			throw new IllegalArgumentException("bBias must be at most 2147483647: " + name);
		}
		parameter("-b-bias", Integer.toString(bBias));
		return this;
	}

	/**
	 * Keep some B-frames as references. (from -1 to INT_MAX) (default -1)
	 *
	 * @param bPyramid
	 * @return
	 */
	public Libx264 bPyramid(BPyramid bPyramid) {
		if( ! bPyramid.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + bPyramid.io() + ": " + io);
		}
		parameter("-b-pyramid", bPyramid.name());
		return this;
	}

	/**
	 * One reference per partition, as opposed to one reference per macroblock (from -1 to 1) (default -1)
	 *
	 * @param mixedRefs
	 * @return
	 */
	public Libx264 mixedRefs(int mixedRefs) {
		if( - 1 > mixedRefs || mixedRefs > 1) {
			throw new IllegalArgumentException("mixedRefs must be between -1 and 1: " + name);
		}
		parameter("-mixed-refs", Integer.toString(mixedRefs));
		return this;
	}

	/**
	 * High profile 8x8 transform. (from -1 to 1) (default -1)
	 *
	 * @param _8x8dct
	 * @return
	 */
	public Libx264 _8x8dct(int _8x8dct) {
		if( - 1 > _8x8dct || _8x8dct > 1) {
			throw new IllegalArgumentException("_8x8dct must be between -1 and 1: " + name);
		}
		parameter("-8x8dct", Integer.toString(_8x8dct));
		return this;
	}

	/**
	 * (from -1 to 1) (default -1)
	 *
	 * @param fastPskip
	 * @return
	 */
	public Libx264 fastPskip(int fastPskip) {
		if( - 1 > fastPskip || fastPskip > 1) {
			throw new IllegalArgumentException("fastPskip must be between -1 and 1: " + name);
		}
		parameter("-fast-pskip", Integer.toString(fastPskip));
		return this;
	}

	/**
	 * Use access unit delimiters. (from -1 to 1) (default -1)
	 *
	 * @param aud
	 * @return
	 */
	public Libx264 aud(int aud) {
		if( - 1 > aud || aud > 1) {
			throw new IllegalArgumentException("aud must be between -1 and 1: " + name);
		}
		parameter("-aud", Integer.toString(aud));
		return this;
	}

	/**
	 * Use macroblock tree ratecontrol. (from -1 to 1) (default -1)
	 *
	 * @param mbtree
	 * @return
	 */
	public Libx264 mbtree(int mbtree) {
		if( - 1 > mbtree || mbtree > 1) {
			throw new IllegalArgumentException("mbtree must be between -1 and 1: " + name);
		}
		parameter("-mbtree", Integer.toString(mbtree));
		return this;
	}

	/**
	 * Loop filter parameters, in <alpha:beta> form.
	 *
	 * @param deblock
	 * @return
	 */
	public Libx264 deblock(String deblock) {
		parameter("-deblock", deblock);
		return this;
	}

	/**
	 * Reduce fluctuations in QP (before curve compression) (from -1 to FLT_MAX) (default -1)
	 *
	 * @param cplxblur
	 * @return
	 */
	public Libx264 cplxblur(float cplxblur) {
		if( - 1.0 > cplxblur || cplxblur > 3.4028235E38) {
			throw new IllegalArgumentException("cplxblur must be between -1.0 and 3.4028235E38: " + name);
		}
		parameter("-cplxblur", Float.toString(cplxblur));
		return this;
	}

	/**
	 * A comma-separated list of partitions to consider. Possible values: p8x8, p4x4, b8x8, i8x8, i4x4, none, all
	 *
	 * @param partitions
	 * @return
	 */
	public Libx264 partitions(String partitions) {
		parameter("-partitions", partitions);
		return this;
	}

	/**
	 * Direct MV prediction mode (from -1 to INT_MAX) (default -1)
	 *
	 * @param directPred
	 * @return
	 */
	public Libx264 directPred(DirectPred directPred) {
		if( ! directPred.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + directPred.io() + ": " + io);
		}
		parameter("-direct-pred", directPred.name());
		return this;
	}

	/**
	 * Limit the size of each slice in bytes (from -1 to INT_MAX) (default -1)
	 *
	 * @param sliceMaxSize
	 * @return
	 */
	public Libx264 sliceMaxSize(int sliceMaxSize) {
		if( - 1 > sliceMaxSize || sliceMaxSize > 2147483647) {
			throw new IllegalArgumentException("sliceMaxSize must be between -1 and 2147483647: " + name);
		}
		parameter("-slice-max-size", Integer.toString(sliceMaxSize));
		return this;
	}

	/**
	 * Filename for 2 pass stats
	 *
	 * @param stats
	 * @return
	 */
	public Libx264 stats(String stats) {
		parameter("-stats", stats);
		return this;
	}

	/**
	 * Signal HRD information (requires vbv-bufsize; cbr not allowed in .mp4) (from -1 to INT_MAX) (default -1)
	 *
	 * @param nalHrd
	 * @return
	 */
	public Libx264 nalHrd(NalHrd nalHrd) {
		if( ! nalHrd.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + nalHrd.io() + ": " + io);
		}
		parameter("-nal-hrd", nalHrd.name());
		return this;
	}

	/**
	 * AVC-Intra class 50/100/200 (from -1 to 200) (default -1)
	 *
	 * @param avcintraClass
	 * @return
	 */
	public Libx264 avcintraClass(Integer avcintraClass) {
		if( - 1 > avcintraClass || avcintraClass > 200) {
			throw new IllegalArgumentException("avcintraClass must be between -1 and 200: " + name);
		}
		parameter("-avcintra-class", Integer.toString(avcintraClass));
		return this;
	}

	/**
	 * Override the x264 configuration using a :-separated list of key=value parameters
	 *
	 * @param x264Params
	 * @return
	 */
	public Libx264 x264Params(String x264Params) {
		parameter("-x264-params", x264Params);
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.LibLog#log(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void log(String title, String somethings, String log) {
		logs.add(log);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.LibLog#getLibLogFilter()
	 */
	@Override
	public Predicate<String> getLibLogFilter() {
		return s -> s.startsWith("libx264");
	}

	/**
	 * @return
	 */
	public List<String> getLogs() {
		return logs;
	}

}
