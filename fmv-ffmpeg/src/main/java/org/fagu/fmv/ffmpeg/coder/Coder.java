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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.fagu.fmv.ffmpeg.ElementParameterized;
import org.fagu.fmv.ffmpeg.flags.Debug;
import org.fagu.fmv.ffmpeg.flags.Flags;
import org.fagu.fmv.ffmpeg.flags.Flags2;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.flags.ThreadType;
import org.fagu.fmv.ffmpeg.format.IO;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.Processor;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public abstract class Coder<M> extends ElementParameterized<M> {

	// -----------------------------------------------

	/**
	 * (default 0)
	 * 
	 * @author f.agu
	 */
	public static class CFlags extends Flags<CFlags> {

		/**
		 * Allow decoders to produce unaligned output
		 */
		public static final CFlags UNALIGNED = new CFlags(0, "unaligned", IO.INPUT);

		/**
		 * Use four motion vectors per macroblock (MPEG-4)
		 */
		public static final CFlags MV4 = new CFlags(1, "mv4", IO.OUTPUT);

		/**
		 * Use 1/4-pel motion compensation
		 */
		public static final CFlags QPEL = new CFlags(2, "qpel", IO.OUTPUT);

		/**
		 * Use loop filter
		 */
		public static final CFlags LOOP = new CFlags(3, "loop", IO.OUTPUT);

		/**
		 * Use gmc
		 */
		public static final CFlags GMC = new CFlags(4, "gmc", IO.OUTPUT);

		/**
		 * Always try a mb with mv=<0,0>
		 */
		public static final CFlags MV0 = new CFlags(5, "mv0", IO.OUTPUT);

		/**
		 * Only decode/encode grayscale
		 */
		public static final CFlags GRAY = new CFlags(6, "gray", IO.INPUT_OUTPUT);

		/**
		 * Error[?] variables will be set during encoding
		 */
		public static final CFlags PSNR = new CFlags(7, "psnr", IO.OUTPUT);

		/**
		 * Normalize adaptive quantization
		 */
		public static final CFlags NAQ = new CFlags(8, "naq", IO.OUTPUT);

		/**
		 * Use interlaced DCT
		 */
		public static final CFlags ILDCT = new CFlags(9, "ildct", IO.OUTPUT);

		/**
		 * Force low delay
		 */
		public static final CFlags LOW_DELAY = new CFlags(10, "low_delay", IO.INPUT_OUTPUT);

		/**
		 * Place global headers in extradata instead of every keyframe
		 */
		public static final CFlags GLOBAL_HEADER = new CFlags(11, "global_header", IO.OUTPUT);

		/**
		 * Use only bitexact functions (except (I)DCT)
		 */
		public static final CFlags BITEXACT = new CFlags(12, "bitexact", IO.INPUT_OUTPUT);

		/**
		 * H.263 advanced intra coding / MPEG-4 AC prediction
		 */
		public static final CFlags AIC = new CFlags(13, "aic", IO.OUTPUT);

		/**
		 * Interlaced motion estimation
		 */
		public static final CFlags ILME = new CFlags(14, "ilme", IO.OUTPUT);

		/**
		 * Closed GOP
		 */
		public static final CFlags CGOP = new CFlags(15, "cgop", IO.OUTPUT);

		/**
		 * Output even potentially corrupted frames
		 */
		public static final CFlags OUTPUT_CORRUPT = new CFlags(16, "output_corrupt", IO.INPUT);

		protected CFlags(int index, String flag, IO io) {
			super(CFlags.class, index, flag, io);
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Idct {

		AUTO("auto", IO.INPUT_OUTPUT),
		INT("int", IO.INPUT_OUTPUT),
		SIMPLE("simple", IO.INPUT_OUTPUT),
		SIMPLEMMX("simplemmx", IO.INPUT_OUTPUT),
		ARM("arm", IO.INPUT_OUTPUT),
		ALTIVEC("altivec", IO.INPUT_OUTPUT),
		SH4("sh4", IO.INPUT_OUTPUT),
		SIMPLEARM("simplearm", IO.INPUT_OUTPUT),
		SIMPLEARMV5TE("simplearmv5te", IO.INPUT_OUTPUT),
		SIMPLEARMV6("simplearmv6", IO.INPUT_OUTPUT),
		SIMPLENEON("simpleneon", IO.INPUT_OUTPUT),
		SIMPLEALPHA("simplealpha", IO.INPUT_OUTPUT),
		IPP("ipp", IO.INPUT_OUTPUT),
		XVIDMMX("xvidmmx", IO.INPUT_OUTPUT),
		// floating point AAN IDCT
		FAANI("faani", IO.INPUT_OUTPUT),
		SIMPLEAUTO("simpleauto", IO.INPUT_OUTPUT);

		private final String flag;

		private final IO io;

		private Idct(String flag, IO io) {
			this.flag = flag;
			this.io = io;
		}

		public String flag() {
			return flag;
		}

		public IO io() {
			return io;
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Threads {

		// autodetect a suitable number of threads to use
		AUTO("auto", IO.INPUT_OUTPUT);

		private final String flag;

		private final IO io;

		private Threads(String flag, IO io) {
			this.flag = flag;
			this.io = io;
		}

		public String flag() {
			return flag;
		}

		public IO io() {
			return io;
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum ColorPrimaries {

		// BT.709
		BT709("bt709", IO.INPUT_OUTPUT),
		// Unspecified
		UNSPECIFIED("unspecified", IO.INPUT_OUTPUT),
		// BT.470 M
		BT470M("bt470m", IO.INPUT_OUTPUT),
		// BT.470 BG
		BT470BG("bt470bg", IO.INPUT_OUTPUT),
		// SMPTE 170 M
		SMPTE170M("smpte170m", IO.INPUT_OUTPUT),
		// SMPTE 240 M
		SMPTE240M("smpte240m", IO.INPUT_OUTPUT),
		// Film
		FILM("film", IO.INPUT_OUTPUT),
		// BT.2020
		BT2020("bt2020", IO.INPUT_OUTPUT);

		private final String flag;

		private final IO io;

		private ColorPrimaries(String flag, IO io) {
			this.flag = flag;
			this.io = io;
		}

		public String flag() {
			return flag;
		}

		public IO io() {
			return io;
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum ColorTrc {

		// BT.709
		BT709("bt709", IO.INPUT_OUTPUT),
		// Unspecified
		UNSPECIFIED("unspecified", IO.INPUT_OUTPUT),
		// Gamma 2.2
		GAMMA22("gamma22", IO.INPUT_OUTPUT),
		// Gamma 2.8
		GAMMA28("gamma28", IO.INPUT_OUTPUT),
		// SMPTE 170 M
		SMPTE170M("smpte170m", IO.INPUT_OUTPUT),
		// SMPTE 240 M
		SMPTE240M("smpte240m", IO.INPUT_OUTPUT),
		// Linear
		LINEAR("linear", IO.INPUT_OUTPUT),
		// SMPTE 240 M
		LOG("log", IO.INPUT_OUTPUT),
		// SMPTE 240 M
		LOG_SQRT("log_sqrt", IO.INPUT_OUTPUT),
		// SMPTE 240 M
		IEC61966_2_4("iec61966_2_4", IO.INPUT_OUTPUT),
		// BT.1361
		BT1361("bt1361", IO.INPUT_OUTPUT),
		// SMPTE 240 M
		IEC61966_2_1("iec61966_2_1", IO.INPUT_OUTPUT),
		// BT.2020 - 10 bit
		BT2020_10BIT("bt2020_10bit", IO.INPUT_OUTPUT),
		// BT.2020 - 12 bit
		BT2020_12BIT("bt2020_12bit", IO.INPUT_OUTPUT);

		private final String flag;

		private final IO io;

		private ColorTrc(String flag, IO io) {
			this.flag = flag;
			this.io = io;
		}

		public String flag() {
			return flag;
		}

		public IO io() {
			return io;
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Colorspace {

		// RGB
		RGB("rgb", IO.INPUT_OUTPUT),
		// BT.709
		BT709("bt709", IO.INPUT_OUTPUT),
		// Unspecified
		UNSPECIFIED("unspecified", IO.INPUT_OUTPUT),
		// FourCC
		FCC("fcc", IO.INPUT_OUTPUT),
		// BT.470 BG
		BT470BG("bt470bg", IO.INPUT_OUTPUT),
		// SMPTE 170 M
		SMPTE170M("smpte170m", IO.INPUT_OUTPUT),
		// SMPTE 240 M
		SMPTE240M("smpte240m", IO.INPUT_OUTPUT),
		// YCOCG
		YCOCG("ycocg", IO.INPUT_OUTPUT),
		// BT.2020 NCL
		BT2020_NCL("bt2020_ncl", IO.INPUT_OUTPUT),
		// BT.2020 CL
		BT2020_CL("bt2020_cl", IO.INPUT_OUTPUT);

		private final String flag;

		private final IO io;

		private Colorspace(String flag, IO io) {
			this.flag = flag;
			this.io = io;
		}

		public String flag() {
			return flag;
		}

		public IO io() {
			return io;
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum ColorRange {

		// Unspecified
		UNSPECIFIED("unspecified", IO.INPUT_OUTPUT),
		// MPEG (219*2^(n-8))
		MPEG("mpeg", IO.INPUT_OUTPUT),
		// JPEG (2^n-1)
		JPEG("jpeg", IO.INPUT_OUTPUT);

		private final String flag;

		private final IO io;

		private ColorRange(String flag, IO io) {
			this.flag = flag;
			this.io = io;
		}

		public String flag() {
			return flag;
		}

		public IO io() {
			return io;
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum FieldOrder {

		PROGRESSIVE("progressive", IO.INPUT_OUTPUT),
		TT("tt", IO.INPUT_OUTPUT),
		BB("bb", IO.INPUT_OUTPUT),
		TB("tb", IO.INPUT_OUTPUT),
		BT("bt",
				IO.INPUT_OUTPUT);

		private final String flag;

		private final IO io;

		private FieldOrder(String flag, IO io) {
			this.flag = flag;
			this.io = io;
		}

		public String flag() {
			return flag;
		}

		public IO io() {
			return io;
		}
	}

	// -----------------------------------------------

	private final Set<CFlags> cflagss;

	private final Set<Debug> debugs;

	private final Set<Flags2> flags2s;

	private final Set<ThreadType> threadTypes;

	protected final Type type;

	protected final IO io;

	protected Coder(Type type, String name, IO io) {
		super(name);
		this.type = type;
		this.io = io;

		cflagss = new HashSet<>();
		debugs = new HashSet<>();
		flags2s = new HashSet<>();
		threadTypes = new HashSet<>();
	}

	public Type type() {
		return type;
	}

	public IO io() {
		return io;
	}

	/**
	 * (default 0)
	 * 
	 * @param cflagss
	 * @return
	 */
	public M flags(CFlags... cflagss) {
		return flags(Arrays.asList(cflagss));
	}

	/**
	 * (default 0)
	 * 
	 * @param cflagss
	 * @return
	 */
	public M flags(Collection<CFlags> flagss) {
		flagss.stream().filter(f -> io.accept(f.io())).forEach(cflagss::add);
		return getMThis();
	}

	/**
	 * Set audio sampling rate (in Hz) (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param ar
	 * @return
	 */
	public M ar(int ar) {
		parameter("-ar", Integer.toString(ar));
		return getMThis();
	}

	/**
	 * Set number of audio channels (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param ac
	 * @return
	 */
	public M ac(int ac) {
		parameter("-ac", Integer.toString(ac));
		return getMThis();
	}

	/**
	 * How strictly to follow the standards (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param strict
	 * @return
	 */
	public M strict(Strict strict) {
		parameter("-strict", strict.flag());
		return getMThis();
	}

	/**
	 * Select IDCT implementation (from 0 to INT_MAX) (default 0)
	 * 
	 * @param idct
	 * @return
	 */
	public M idct(Idct idct) {
		parameter("-idct", idct.flag());
		return getMThis();
	}

	/**
	 * Print specific debug info (default 0)
	 * 
	 * @param debug
	 * @return
	 */
	public M debug(Debug... debugs) {
		return debug(Arrays.asList(debugs));
	}

	/**
	 * Print specific debug info (default 0)
	 * 
	 * @param debug
	 * @return
	 */
	public M debug(Collection<Debug> indebugs) {
		indebugs.stream()
				.filter(f -> io.accept(f.io()))
				.forEach(debugs::add);
		return getMThis();
	}

	/**
	 * (default 0)
	 * 
	 * @param flags2
	 * @return
	 */
	public M flags2(Flags2... flags2s) {
		return flags2(Arrays.asList(flags2s));
	}

	/**
	 * (default 0)
	 * 
	 * @param flags2
	 * @return
	 */
	public M flags2(Collection<Flags2> inflags2s) {
		inflags2s.stream()
				.filter(f -> io.accept(f.io()))
				.forEach(flags2s::add);
		return getMThis();
	}

	/**
	 * (from 0 to INT_MAX) (default 1)
	 * 
	 * @param threads
	 * @return
	 */
	public M threads(Threads threads) {
		parameter("-threads", threads.flag());
		return getMThis();
	}

	/**
	 * (from 0 to I64_MAX) (default 0)
	 * 
	 * @param channelLayout
	 * @return
	 */
	public M channelLayout(long channelLayout) {
		if(0 > channelLayout) {
			throw new IllegalArgumentException("channelLayout must be at least 0: " + channelLayout);
		}
		parameter("-channel_layout", Long.toString(channelLayout));
		return getMThis();
	}

	/**
	 * (from 1 to INT_MAX) (default 1)
	 * 
	 * @param ticksPerFrame
	 * @return
	 */
	public M ticksPerFrame(int ticksPerFrame) {
		if(1 > ticksPerFrame) {
			throw new IllegalArgumentException("ticksPerFrame must be at least 1: " + ticksPerFrame);
		}
		parameter("-ticks_per_frame", Integer.toString(ticksPerFrame));
		return getMThis();
	}

	/**
	 * Color primaries (from 1 to 9) (default 2)
	 * 
	 * @param colorPrimaries
	 * @return
	 */
	public M colorPrimaries(ColorPrimaries colorPrimaries) {
		parameter("-color_primaries", colorPrimaries.flag());
		return getMThis();
	}

	/**
	 * Color transfert characteristic (from 1 to 15) (default 2)
	 * 
	 * @param colorTrc
	 * @return
	 */
	public M colorTrc(ColorTrc colorTrc) {
		parameter("-color_trc", colorTrc.flag());
		return getMThis();
	}

	/**
	 * Colorspace (from 1 to 10) (default 2)
	 * 
	 * @param colorspace
	 * @return
	 */
	public M colorspace(Colorspace colorspace) {
		parameter("-colorspace", colorspace.flag());
		return getMThis();
	}

	/**
	 * Color range (from 0 to 2) (default 0)
	 * 
	 * @param colorRange
	 * @return
	 */
	public M colorRange(ColorRange colorRange) {
		parameter("-color_range", colorRange.flag());
		return getMThis();
	}

	/**
	 * (from 0 to 6) (default 0)
	 * 
	 * @param chromaSampleLocation
	 * @return
	 */
	public M chromaSampleLocation(int chromaSampleLocation) {
		if(0 > chromaSampleLocation || chromaSampleLocation > 6) {
			throw new IllegalArgumentException("chromaSampleLocation must be between 0 and 6: " + name);
		}
		parameter("-chroma_sample_location", Integer.toString(chromaSampleLocation));
		return getMThis();
	}

	/**
	 * Select multithreading type (default 3)
	 * 
	 * @param threadType
	 * @return
	 */
	public M threadType(ThreadType... threadTypes) {
		return threadType(Arrays.asList(threadTypes));
	}

	/**
	 * Select multithreading type (default 3)
	 * 
	 * @param threadType
	 * @return
	 */
	public M threadType(Collection<ThreadType> inthreadTypes) {
		inthreadTypes.stream()
				.filter(f -> io.accept(f.io()))
				.forEach(threadTypes::add);
		return getMThis();
	}

	/**
	 * Field order (from 0 to 5) (default 0)
	 * 
	 * @param fieldOrder
	 * @return
	 */
	public M fieldOrder(FieldOrder fieldOrder) {
		parameter("-field_order", fieldOrder.flag());
		return getMThis();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.IOEntity#eventAdded(org.fagu.fmv.ffmpeg.operation.Processor, IOEntity)
	 */
	@Override
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {
		super.eventAdded(processor, ioEntity);
		if( ! cflagss.isEmpty()) {
			parameter(processor, ioEntity, "-flags", cflagss);
		}
		if( ! debugs.isEmpty()) {
			parameter(processor, ioEntity, "-debug", debugs);
		}
		if( ! flags2s.isEmpty()) {
			parameter(processor, ioEntity, "-flags2", flags2s);
		}
		if( ! threadTypes.isEmpty()) {
			parameter(processor, ioEntity, "-thread_type", threadTypes);
		}
	}

}
