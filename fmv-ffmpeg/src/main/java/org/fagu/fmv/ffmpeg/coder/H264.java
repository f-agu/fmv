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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
public abstract class H264<M extends H264<?>> extends Encoder<M> implements LibLog {

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class AqMode {

		public static final AqMode NONE = new AqMode("none", IO.OUTPUT);

		// Variance AQ (complexity mask)
		public static final AqMode VARIANCE = new AqMode("variance", IO.OUTPUT);

		// Auto-variance AQ (experimental)
		public static final AqMode AUTOVARIANCE = new AqMode("autovariance", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		public AqMode(String flag, IO io) {
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
	public static class Weightp {

		public static final Weightp NONE = new Weightp("none", IO.OUTPUT);

		public static final Weightp SIMPLE = new Weightp("simple", IO.OUTPUT);

		public static final Weightp SMART = new Weightp("smart", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		public Weightp(String flag, IO io) {
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
	public static class BPyramid {

		public static final BPyramid NONE = new BPyramid("none", IO.OUTPUT);

		// Strictly hierarchical pyramid
		public static final BPyramid STRICT = new BPyramid("strict", IO.OUTPUT);

		// Non-strict (not Blu-ray compatible)
		public static final BPyramid NORMAL = new BPyramid("normal", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		public BPyramid(String flag, IO io) {
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
	public static class DirectPred {

		public static final DirectPred NONE = new DirectPred("none", IO.OUTPUT);

		public static final DirectPred SPATIAL = new DirectPred("spatial", IO.OUTPUT);

		public static final DirectPred TEMPORAL = new DirectPred("temporal", IO.OUTPUT);

		public static final DirectPred AUTO = new DirectPred("auto", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		public DirectPred(String flag, IO io) {
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
	public static class Level {

		public static final Level L_1 = new Level("1");

		public static final Level L_1b = new Level("1");

		public static final Level L_1_1 = new Level("1.1");

		public static final Level L_1_2 = new Level("1.2");

		public static final Level L_1_3 = new Level("1.3");

		public static final Level L_2 = new Level("2");

		public static final Level L_2_1 = new Level("2.1");

		public static final Level L_2_2 = new Level("2.2");

		public static final Level L_3_0 = new Level("3.0");

		public static final Level L_3_1 = new Level("3.1");

		public static final Level L_3_2 = new Level("3.2");

		public static final Level L_4_0 = new Level("4.0");

		public static final Level L_4_1 = new Level("4.1");

		public static final Level L_4_2 = new Level("4.2");

		public static final Level L_5 = new Level("5");

		public static final Level L_5_1 = new Level("5.1");

		private final String value;

		/**
		 * @param value
		 */
		public Level(String value) {
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
	public static class NalHrd {

		public static final NalHrd NONE = new NalHrd("none", IO.OUTPUT);

		public static final NalHrd VBR = new NalHrd("vbr", IO.OUTPUT);

		public static final NalHrd CBR = new NalHrd("cbr", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		public NalHrd(String flag, IO io) {
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
	public static class Preset {

		public static final Preset ULTRAFAST = new Preset("ultrafast");

		public static final Preset SUPERFAST = new Preset("superfast");

		public static final Preset VERYFAST = new Preset("veryfast");

		public static final Preset FASTER = new Preset("faster");

		public static final Preset FAST = new Preset("fast");

		public static final Preset MEDIUM = new Preset("medium");

		public static final Preset SLOW = new Preset("slow");

		public static final Preset SLOWER = new Preset("slower");

		public static final Preset VERYSLOW = new Preset("veryslow");

		public static final Preset PLACEBO = new Preset("placebo");

		private final String value;

		/**
		 * @param value
		 */
		public Preset(String value) {
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
	public static class Profile {

		public static final Profile BASELINE = new Profile("baseline");

		public static final Profile MAIN = new Profile("main");

		public static final Profile HIGH = new Profile("high");

		public static final Profile HIGH10 = new Profile("high10");

		public static final Profile HIGH422 = new Profile("high422");

		public static final Profile HIGH444 = new Profile("high444");

		private final String value;

		/**
		 * @param value
		 */
		public Profile(String value) {
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
	public static class Tune {

		public static final Tune FILM = new Tune("film");

		public static final Tune AIMATION = new Tune("animation");

		public static final Tune GRAIN = new Tune("grain");

		public static final Tune STILLIMAGE = new Tune("stillimage");

		public static final Tune PSNR = new Tune("psnr");

		public static final Tune SSIM = new Tune("ssim");

		public static final Tune FASTDECODE = new Tune("fastdecode");

		public static final Tune ZEROLATENCY = new Tune("zerolatency");

		private final String value;

		/**
		 * @param value
		 */
		public Tune(String value) {
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
	 * {@link https ://developer.apple .com/library/mac/documentation/NetworkingInternet/Conceptual /StreamingMediaGuide
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
	 * @param codecName
	 */
	protected H264(String codecName) {
		super(Type.VIDEO, codecName);
	}

	/**
	 * @return
	 */
	public static Optional<H264<?>> findRecommanded() {
		return Arrays.<H264<?>>asList(/* H264NVEnc.build(), */ Libx264.build()) //
				.stream() //
				.filter(c -> Encoders.exists(c.name())) //
				.findFirst();
	}

	// ****************************************

	/**
	 * @return
	 */
	public abstract H264<M> mostCompatible();

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
	public abstract H264<M> quality(int quality);

	/**
	 * @param compression
	 * @return
	 */
	public H264<M> compression(Compression compression) {
		return profile(compression.getProfile()).level(compression.getLevel());
	}

	// **********************************************

	/**
	 * Set the encoding preset (cf. x264 --fullhelp) (default "medium")
	 *
	 * @param preset
	 * @return
	 */
	public H264<M> preset(Preset preset) {
		parameter("-preset", preset.flag());
		return this;
	}

	/**
	 * Tune the encoding params (cf. x264 --fullhelp)
	 *
	 * @param tune
	 * @return
	 */
	public H264<M> tune(Tune tune) {
		parameter("-tune", tune.flag());
		return this;
	}

	/**
	 * Set profile restrictions (cf. x264 --fullhelp)
	 *
	 * @param profile
	 * @return
	 */
	public H264<M> profile(Profile profile) {
		parameter("-profile:v", profile.flag());
		return this;
	}

	/**
	 * Use fast settings when encoding first pass (from 0 to 1) (default 1)
	 *
	 * @param fastfirstpass
	 * @return
	 */
	public H264<M> fastfirstpass(boolean fastfirstpass) {
		parameter("-fastfirstpass", Integer.toString(fastfirstpass ? 1 : 0));
		return this;
	}

	/**
	 * Specify level (as defined by Annex A)
	 *
	 * @param level
	 * @return
	 */
	public H264<M> level(Level level) {
		parameter("-level", level.flag());
		return this;
	}

	/**
	 * Filename for 2 pass stats
	 *
	 * @param passlogfile
	 * @return
	 */
	public H264<M> passlogfile(String passlogfile) {
		parameter("-passlogfile", passlogfile);
		return this;
	}

	/**
	 * Weighted prediction for P-frames
	 *
	 * @param wpredp
	 * @return
	 */
	public H264<M> wpredp(String wpredp) {
		parameter("-wpredp", wpredp);
		return this;
	}

	/**
	 * X264 options
	 *
	 * @param x264opts
	 * @return
	 */
	public H264<M> x264opts(String x264opts) {
		parameter("-x264opts", x264opts);
		return this;
	}

	/**
	 * In CRF mode, prevents VBV from lowering quality beyond this point. (from -1 to FLT_MAX) (default -1)
	 *
	 * @param crfMax
	 * @return
	 */
	public H264<M> crfMax(float crfMax) {
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
	public H264<M> qp(int qp) {
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
	public H264<M> aqMode(AqMode aqMode) {
		if( ! aqMode.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + aqMode.io() + ": " + io);
		}
		parameter("-aq-mode", aqMode.flag());
		return this;
	}

	/**
	 * AQ strength. Reduces blocking and blurring in flat and textured areas. (from -1 to FLT_MAX) (default -1)
	 *
	 * @param aqStrength
	 * @return
	 */
	public H264<M> aqStrength(float aqStrength) {
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
	public H264<M> psy(int psy) {
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
	public H264<M> psyRd(String psyRd) {
		parameter("-psy-rd", psyRd);
		return this;
	}

	/**
	 * Number of frames to look ahead for frametype and ratecontrol (from -1 to INT_MAX) (default -1)
	 *
	 * @param rcLookahead
	 * @return
	 */
	public H264<M> rcLookahead(int rcLookahead) {
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
	public H264<M> weightb(int weightb) {
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
	public H264<M> weightp(Weightp weightp) {
		if( ! weightp.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + weightp.io() + ": " + io);
		}
		parameter("-weightp", weightp.flag());
		return this;
	}

	/**
	 * Calculate and print SSIM stats. (from -1 to 1) (default -1)
	 *
	 * @param ssim
	 * @return
	 */
	public H264<M> ssim(int ssim) {
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
	public H264<M> intraRefresh(int intraRefresh) {
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
	public H264<M> blurayCompat(int blurayCompat) {
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
	public H264<M> bBias(int bBias) {
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
	public H264<M> bPyramid(BPyramid bPyramid) {
		if( ! bPyramid.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + bPyramid.io() + ": " + io);
		}
		parameter("-b-pyramid", bPyramid.flag());
		return this;
	}

	/**
	 * One reference per partition, as opposed to one reference per macroblock (from -1 to 1) (default -1)
	 *
	 * @param mixedRefs
	 * @return
	 */
	public H264<M> mixedRefs(int mixedRefs) {
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
	public H264<M> _8x8dct(int _8x8dct) {
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
	public H264<M> fastPskip(int fastPskip) {
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
	public H264<M> aud(int aud) {
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
	public H264<M> mbtree(int mbtree) {
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
	public H264<M> deblock(String deblock) {
		parameter("-deblock", deblock);
		return this;
	}

	/**
	 * Reduce fluctuations in QP (before curve compression) (from -1 to FLT_MAX) (default -1)
	 *
	 * @param cplxblur
	 * @return
	 */
	public H264<M> cplxblur(float cplxblur) {
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
	public H264<M> partitions(String partitions) {
		parameter("-partitions", partitions);
		return this;
	}

	/**
	 * Direct MV prediction mode (from -1 to INT_MAX) (default -1)
	 *
	 * @param directPred
	 * @return
	 */
	public H264<M> directPred(DirectPred directPred) {
		if( ! directPred.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + directPred.io() + ": " + io);
		}
		parameter("-direct-pred", directPred.flag());
		return this;
	}

	/**
	 * Limit the size of each slice in bytes (from -1 to INT_MAX) (default -1)
	 *
	 * @param sliceMaxSize
	 * @return
	 */
	public H264<M> sliceMaxSize(int sliceMaxSize) {
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
	public H264<M> stats(String stats) {
		parameter("-stats", stats);
		return this;
	}

	/**
	 * Signal HRD information (requires vbv-bufsize; cbr not allowed in .mp4) (from -1 to INT_MAX) (default -1)
	 *
	 * @param nalHrd
	 * @return
	 */
	public H264<M> nalHrd(NalHrd nalHrd) {
		if( ! nalHrd.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + nalHrd.io() + ": " + io);
		}
		parameter("-nal-hrd", nalHrd.flag());
		return this;
	}

	/**
	 * AVC-Intra class 50/100/200 (from -1 to 200) (default -1)
	 *
	 * @param avcintraClass
	 * @return
	 */
	public H264<M> avcintraClass(Integer avcintraClass) {
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
	public H264<M> x264Params(String x264Params) {
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
