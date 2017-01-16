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

import org.fagu.fmv.ffmpeg.flags.Bug;
import org.fagu.fmv.ffmpeg.flags.Ec;
import org.fagu.fmv.ffmpeg.flags.ErrDetect;
import org.fagu.fmv.ffmpeg.flags.SubCharencMode;
import org.fagu.fmv.ffmpeg.flags.Vismv;
import org.fagu.fmv.ffmpeg.format.IO;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.Processor;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.AudioSampleFormat;


/**
 * @author f.agu
 */
public abstract class Decoder<M> extends Coder<M> {

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum SkipLoopFilter {
		// discard no frame
		NONE("none", IO.INPUT),
		// discard useless frames
		DEFAULT("default", IO.INPUT),
		// discard all non-reference frames
		NOREF("noref", IO.INPUT),
		// discard all bidirectional frames
		BIDIR("bidir", IO.INPUT),
		// discard all frames except keyframes
		NOKEY("nokey", IO.INPUT),
		// discard all frames except I frames
		NOINTRA("nointra", IO.INPUT),
		// discard all frames
		ALL("all", IO.INPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private SkipLoopFilter(String flag, IO io) {
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
	public enum SkipIdct {
		// discard no frame
		NONE("none", IO.INPUT),
		// discard useless frames
		DEFAULT("default", IO.INPUT),
		// discard all non-reference frames
		NOREF("noref", IO.INPUT),
		// discard all bidirectional frames
		BIDIR("bidir", IO.INPUT),
		// discard all frames except keyframes
		NOKEY("nokey", IO.INPUT),
		// discard all frames except I frames
		NOINTRA("nointra", IO.INPUT),
		// discard all frames
		ALL("all", IO.INPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private SkipIdct(String flag, IO io) {
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
	public enum SkipFrame {
		// discard no frame
		NONE("none", IO.INPUT),
		// discard useless frames
		DEFAULT("default", IO.INPUT),
		// discard all non-reference frames
		NOREF("noref", IO.INPUT),
		// discard all bidirectional frames
		BIDIR("bidir", IO.INPUT),
		// discard all frames except keyframes
		NOKEY("nokey", IO.INPUT),
		// discard all frames except I frames
		NOINTRA("nointra", IO.INPUT),
		// discard all frames
		ALL("all", IO.INPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private SkipFrame(String flag, IO io) {
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

	private final Set<Bug> bugs;

	private final Set<ErrDetect> errDetects;

	private final Set<Ec> ecs;

	private final Set<Vismv> vismvs;

	private final Set<SubCharencMode> subCharencModes;

	/**
	 * @param type
	 * @param name
	 */
	public Decoder(Type type, String name) {
		super(type, name, IO.INPUT);

		bugs = new HashSet<>();
		errDetects = new HashSet<>();
		ecs = new HashSet<>();
		vismvs = new HashSet<>();
		subCharencModes = new HashSet<>();
	}

	/**
	 * Work around not autodetected encoder bugs (default 1)
	 * 
	 * @param bug
	 * @return
	 */
	public M bug(Bug... bugs) {
		return bug(Arrays.asList(bugs));
	}

	/**
	 * Work around not autodetected encoder bugs (default 1)
	 * 
	 * @param bug
	 * @return
	 */
	public M bug(Collection<Bug> bugs) {
		bugs.stream().filter(f -> io.accept(f.io())).forEach(f -> this.bugs.add(f));
		return getMThis();
	}

	/**
	 * Set error detection flags (default 0)
	 * 
	 * @param errDetect
	 * @return
	 */
	public M errDetect(ErrDetect... errDetects) {
		return errDetect(Arrays.asList(errDetects));
	}

	/**
	 * Set error detection flags (default 0)
	 * 
	 * @param errDetect
	 * @return
	 */
	public M errDetect(Collection<ErrDetect> errDetects) {
		errDetects.stream().filter(f -> io.accept(f.io())).forEach(f -> this.errDetects.add(f));
		return getMThis();
	}

	/**
	 * Set error concealment strategy (default 3)
	 * 
	 * @param ec
	 * @return
	 */
	public M ec(Ec... ecs) {
		return ec(Arrays.asList(ecs));
	}

	/**
	 * Set error concealment strategy (default 3)
	 * 
	 * @param ec
	 * @return
	 */
	public M ec(Collection<Ec> ecs) {
		ecs.stream().filter(f -> io.accept(f.io())).forEach(f -> this.ecs.add(f));
		return getMThis();
	}

	/**
	 * Visualize motion vectors (MVs) (default 0)
	 * 
	 * @param vismv
	 * @return
	 */
	public M vismv(Vismv... vismvs) {
		return vismv(Arrays.asList(vismvs));
	}

	/**
	 * Visualize motion vectors (MVs) (default 0)
	 * 
	 * @param vismv
	 * @return
	 */
	public M vismv(Collection<Vismv> vismvs) {
		vismvs.stream().filter(f -> io.accept(f.io())).forEach(f -> this.vismvs.add(f));
		return getMThis();
	}

	/**
	 * Number of macroblock rows at the top which are skipped (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param skipTop
	 * @return
	 */
	public M skipTop(int skipTop) {
		parameter("-skip_top", Integer.toString(skipTop));
		return getMThis();
	}

	/**
	 * Number of macroblock rows at the bottom which are skipped (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param skipBottom
	 * @return
	 */
	public M skipBottom(int skipBottom) {
		parameter("-skip_bottom", Integer.toString(skipBottom));
		return getMThis();
	}

	/**
	 * Decode at 1= 1/2, 2=1/4, 3=1/8 resolutions (from 0 to INT_MAX) (default 0)
	 * 
	 * @param lowres
	 * @return
	 */
	public M lowres(int lowres) {
		if(lowres < 0) {
			throw new IllegalArgumentException("lowres must be at least 0: " + lowres);
		}
		parameter("-lowres", Integer.toString(lowres));
		return getMThis();
	}

	/**
	 * Skip loop filtering process for the selected frames (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param skipLoopFilter
	 * @return
	 */
	public M skipLoopFilter(SkipLoopFilter skipLoopFilter) {
		if( ! skipLoopFilter.io().isInput()) {
			throw new IllegalArgumentException("IO is wrong: " + skipLoopFilter.io() + ": " + io);
		}
		parameter("-skip_loop_filter", skipLoopFilter.flag());
		return getMThis();
	}

	/**
	 * Skip IDCT/dequantization for the selected frames (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param skipIdct
	 * @return
	 */
	public M skipIdct(SkipIdct skipIdct) {
		if( ! skipIdct.io().isInput()) {
			throw new IllegalArgumentException("IO is wrong: " + skipIdct.io() + ": " + io);
		}
		parameter("-skip_idct", skipIdct.flag());
		return getMThis();
	}

	/**
	 * Skip decoding for the selected frames (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param skipFrame
	 * @return
	 */
	public M skipFrame(SkipFrame skipFrame) {
		if( ! skipFrame.io().isInput()) {
			throw new IllegalArgumentException("IO is wrong: " + skipFrame.io() + ": " + io);
		}
		parameter("-skip_frame", skipFrame.flag());
		return getMThis();
	}

	/**
	 * Set desired number of audio channels (from 0 to INT_MAX) (default 0)
	 * 
	 * @param requestChannels
	 * @return
	 */
	public M requestChannels(int requestChannels) {
		if(requestChannels < 0) {
			throw new IllegalArgumentException("requestChannels must be at least 0: " + requestChannels);
		}
		parameter("-request_channels", Integer.toString(requestChannels));
		return getMThis();
	}

	/**
	 * (from 0 to I64_MAX) (default 0)
	 * 
	 * @param requestChannelLayout
	 * @return
	 */
	public M requestChannelLayout(long requestChannelLayout) {
		if(requestChannelLayout < 0) {
			throw new IllegalArgumentException("requestChannelLayout must be at least 0: " + requestChannelLayout);
		}
		parameter("-request_channel_layout", Long.toString(requestChannelLayout));
		return getMThis();
	}

	/**
	 * Sample format audio decoders should prefer (default none)
	 * 
	 * @param requestSampleFmt
	 * @return
	 */
	public M requestSampleFmt(AudioSampleFormat requestSampleFmt) {
		parameter("-request_sample_fmt", requestSampleFmt.getName());
		return getMThis();
	}

	/**
	 * Set input text subtitles character encoding
	 * 
	 * @param subCharenc
	 * @return
	 */
	public M subCharenc(String subCharenc) {
		parameter("-sub_charenc", subCharenc);
		return getMThis();
	}

	/**
	 * Set input text subtitles character encoding mode (default 0)
	 * 
	 * @param subCharencMode
	 * @return
	 */
	public M subCharencMode(SubCharencMode... subCharencModes) {
		return subCharencMode(Arrays.asList(subCharencModes));
	}

	/**
	 * Set input text subtitles character encoding mode (default 0)
	 * 
	 * @param subCharencMode
	 * @return
	 */
	public M subCharencMode(Collection<SubCharencMode> subCharencModes) {
		subCharencModes.stream().filter(f -> io.accept(f.io())).forEach(f -> this.subCharencModes.add(f));
		return getMThis();
	}

	/**
	 * (from 0 to 1) (default 0)
	 * 
	 * @param refcountedFrames
	 * @return
	 */
	public M refcountedFrames(boolean refcountedFrames) {
		parameter("-refcounted_frames", Integer.toString(refcountedFrames ? 1 : 0));
		return getMThis();
	}

	/**
	 * Skip processing alpha (from 0 to 1) (default 0)
	 * 
	 * @param skipAlpha
	 * @return
	 */
	public M skipAlpha(boolean skipAlpha) {
		parameter("-skip_alpha", Integer.toString(skipAlpha ? 1 : 0));
		return getMThis();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.IOEntity#eventAdded(org.fagu.fmv.ffmpeg.operation.Processor, IOEntity)
	 */
	@Override
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {
		super.eventAdded(processor, ioEntity);
		if( ! bugs.isEmpty()) {
			parameter(processor, ioEntity, "-bug", bugs);
		}
		if( ! errDetects.isEmpty()) {
			parameter(processor, ioEntity, "-err_detect", errDetects);
		}
		if( ! ecs.isEmpty()) {
			parameter(processor, ioEntity, "-ec", ecs);
		}
		if( ! vismvs.isEmpty()) {
			parameter(processor, ioEntity, "-vismv", vismvs);
		}
		if( ! subCharencModes.isEmpty()) {
			parameter(processor, ioEntity, "-sub_charenc_mode", subCharencModes);
		}
	}
}
