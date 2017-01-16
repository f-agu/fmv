package org.fagu.fmv.ffmpeg;

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

import org.fagu.fmv.ffmpeg.flags.Flags;
import org.fagu.fmv.ffmpeg.flags.SwrFlags;
import org.fagu.fmv.ffmpeg.format.IO;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.Processor;
import org.fagu.fmv.ffmpeg.utils.AudioSampleFormat;
import org.fagu.fmv.ffmpeg.utils.ChannelLayout;


/**
 * @author f.agu
 */
public class Resampler extends Element<Resampler> {

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum DitherMethod {
		// select rectangular dither
		RECTANGULAR("rectangular"),
		// select triangular dither
		TRIANGULAR("triangular"),
		// select triangular dither with high pass
		TRIANGULAR_HP("triangular_hp"),
		// select lipshitz noise shaping dither
		LIPSHITZ("lipshitz"),
		// select shibata noise shaping dither
		SHIBATA("shibata"),
		// select low shibata noise shaping dither
		LOW_SHIBATA("low_shibata"),
		// select high shibata noise shaping dither
		HIGH_SHIBATA("high_shibata"),
		// select f-weighted noise shaping dither
		F_WEIGHTED("f_weighted"),
		// select modified-e-weighted noise shaping dither
		MODIFIED_E_WEIGHTED("modified_e_weighted"),
		// select improved-e-weighted noise shaping dither
		IMPROVED_E_WEIGHTED("improved_e_weighted");

		private final String flag;

		/**
		 * @param flag
		 */
		private DitherMethod(String flag) {
			this.flag = flag;
		}

		/**
		 * @return
		 */
		public String flag() {
			return flag;
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum SelectResampler {
		// select SW Resampler
		SWR("swr"),
		// select SoX Resampler
		SOXR("soxr");

		private final String flag;

		/**
		 * @param flag
		 */
		private SelectResampler(String flag) {
			this.flag = flag;
		}

		/**
		 * @return
		 */
		public String flag() {
			return flag;
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum MatrixEncoding {
		// select none
		NONE("none"),
		// select Dolby
		DOLBY("dolby"),
		// select Dolby Pro Logic II
		DPLII("dplii");

		private final String flag;

		/**
		 * @param flag
		 */
		private MatrixEncoding(String flag) {
			this.flag = flag;
		}

		/**
		 * @return
		 */
		public String flag() {
			return flag;
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum FilterType {
		// select cubic
		CUBIC("cubic"),
		// select Blackman Nuttall Windowed Sinc
		BLACKMAN_NUTTALL("blackman_nuttall"),
		// select Kaiser Windowed Sinc
		KAISER("kaiser");

		private final String flag;

		/**
		 * @param flag
		 */
		private FilterType(String flag) {
			this.flag = flag;
		}

		/**
		 * @return
		 */
		public String flag() {
			return flag;
		}
	}

	// -----------------------------------------------

	/**
	 * Set flags (default 0)
	 * 
	 * @author f.agu
	 */
	public static class RFlags extends Flags<RFlags> {

		/**
		 * Force resampling
		 */
		public static final RFlags RES = new RFlags(0, "res", null);

		/**
		 * @param index
		 * @param flag
		 * @param io
		 */
		protected RFlags(int index, String flag, IO io) {
			super(Flags.class, index, flag, io);
		}
	}

	// -----------------------------------------------

	private final Set<RFlags> rflagss;

	private final Set<SwrFlags> swrFlagss;

	/**
	 * 
	 */
	private Resampler() {
		super(null);
		rflagss = new HashSet<>();
		swrFlagss = new HashSet<>();
	}

	/**
	 * @return
	 */
	public static Resampler build() {
		return new Resampler();
	}

	/**
	 * Set input channel count (from 0 to 32) (default 0)
	 * 
	 * @param ich
	 * @return
	 */
	public Resampler ich(int ich) {
		if(0 > ich || ich > 32) {
			throw new IllegalArgumentException("ich must be between 0 and 32: " + ich);
		}
		parameter("-ich", Integer.toString(ich));
		return getMThis();
	}

	/**
	 * Set input channel count (from 0 to 32) (default 0)
	 * 
	 * @param inChannelCount
	 * @return
	 */
	public Resampler inChannelCount(int inChannelCount) {
		if(0 > inChannelCount || inChannelCount > 32) {
			throw new IllegalArgumentException("inChannelCount must be between 0 and 32: " + inChannelCount);
		}
		parameter("-in_channel_count", Integer.toString(inChannelCount));
		return getMThis();
	}

	/**
	 * Set output channel count (from 0 to 32) (default 0)
	 * 
	 * @param och
	 * @return
	 */
	public Resampler och(int och) {
		if(0 > och || och > 32) {
			throw new IllegalArgumentException("och must be between 0 and 32: " + och);
		}
		parameter("-och", Integer.toString(och));
		return getMThis();
	}

	/**
	 * Set output channel count (from 0 to 32) (default 0)
	 * 
	 * @param outChannelCount
	 * @return
	 */
	public Resampler outChannelCount(int outChannelCount) {
		if(0 > outChannelCount || outChannelCount > 32) {
			throw new IllegalArgumentException("outChannelCount must be between 0 and 32: " + outChannelCount);
		}
		parameter("-out_channel_count", Integer.toString(outChannelCount));
		return getMThis();
	}

	/**
	 * Set used channel count (from 0 to 32) (default 0)
	 * 
	 * @param uch
	 * @return
	 */
	public Resampler uch(int uch) {
		if(0 > uch || uch > 32) {
			throw new IllegalArgumentException("uch must be between 0 and 32: " + uch);
		}
		parameter("-uch", Integer.toString(uch));
		return getMThis();
	}

	/**
	 * Set used channel count (from 0 to 32) (default 0)
	 * 
	 * @param usedChannelCount
	 * @return
	 */
	public Resampler usedChannelCount(int usedChannelCount) {
		if(0 > usedChannelCount || usedChannelCount > 32) {
			throw new IllegalArgumentException("usedChannelCount must be between 0 and 32: " + usedChannelCount);
		}
		parameter("-used_channel_count", Integer.toString(usedChannelCount));
		return getMThis();
	}

	/**
	 * Set input sample rate (from 0 to INT_MAX) (default 0)
	 * 
	 * @param isr
	 * @return
	 */
	public Resampler isr(int isr) {
		if(isr < 0) {
			throw new IllegalArgumentException("isr must be at least 0: " + isr);
		}
		parameter("-isr", Integer.toString(isr));
		return getMThis();
	}

	/**
	 * Set input sample rate (from 0 to INT_MAX) (default 0)
	 * 
	 * @param inSampleRate
	 * @return
	 */
	public Resampler inSampleRate(int inSampleRate) {
		if(inSampleRate < 0) {
			throw new IllegalArgumentException("inSampleRate must be at least 0: " + inSampleRate);
		}
		parameter("-in_sample_rate", Integer.toString(inSampleRate));
		return getMThis();
	}

	/**
	 * Set output sample rate (from 0 to INT_MAX) (default 0)
	 * 
	 * @param osr
	 * @return
	 */
	public Resampler osr(int osr) {
		if(osr < 0) {
			throw new IllegalArgumentException("osr must be at least 0: " + osr);
		}
		parameter("-osr", Integer.toString(osr));
		return getMThis();
	}

	/**
	 * Set output sample rate (from 0 to INT_MAX) (default 0)
	 * 
	 * @param outSampleRate
	 * @return
	 */
	public Resampler outSampleRate(int outSampleRate) {
		if(outSampleRate < 0) {
			throw new IllegalArgumentException("outSampleRate must be at least 0: " + outSampleRate);
		}
		parameter("-out_sample_rate", Integer.toString(outSampleRate));
		return getMThis();
	}

	/**
	 * Set input sample format (default none)
	 * 
	 * @param isf
	 * @return
	 */
	public Resampler isf(AudioSampleFormat isf) {
		parameter("-isf", isf.getName());
		return getMThis();
	}

	/**
	 * Set input sample format (default none)
	 * 
	 * @param inSampleFmt
	 * @return
	 */
	public Resampler inSampleFmt(AudioSampleFormat inSampleFmt) {
		parameter("-in_sample_fmt", inSampleFmt.getName());
		return getMThis();
	}

	/**
	 * Set output sample format (default none)
	 * 
	 * @param osf
	 * @return
	 */
	public Resampler osf(AudioSampleFormat osf) {
		parameter("-osf", osf.getName());
		return getMThis();
	}

	/**
	 * Set output sample format (default none)
	 * 
	 * @param outSampleFmt
	 * @return
	 */
	public Resampler outSampleFmt(AudioSampleFormat outSampleFmt) {
		parameter("-out_sample_fmt", outSampleFmt.getName());
		return getMThis();
	}

	/**
	 * Set internal sample format (default none)
	 * 
	 * @param tsf
	 * @return
	 */
	public Resampler tsf(AudioSampleFormat tsf) {
		parameter("-tsf", tsf.getName());
		return getMThis();
	}

	/**
	 * Set internal sample format (default none)
	 * 
	 * @param internalSampleFmt
	 * @return
	 */
	public Resampler internalSampleFmt(AudioSampleFormat internalSampleFmt) {
		parameter("-internal_sample_fmt", internalSampleFmt.getName());
		return getMThis();
	}

	/**
	 * Set input channel layout (default 0x0)
	 * 
	 * @param icl
	 * @return
	 */
	public Resampler icl(ChannelLayout icl) {
		parameter("-icl", icl.getName());
		return getMThis();
	}

	/**
	 * Set input channel layout (default 0x0)
	 * 
	 * @param inChannelLayout
	 * @return
	 */
	public Resampler inChannelLayout(ChannelLayout inChannelLayout) {
		parameter("-in_channel_layout", inChannelLayout.getName());
		return getMThis();
	}

	/**
	 * Set output channel layout (default 0x0)
	 * 
	 * @param ocl
	 * @return
	 */
	public Resampler ocl(ChannelLayout ocl) {
		parameter("-ocl", ocl.getName());
		return getMThis();
	}

	/**
	 * Set output channel layout (default 0x0)
	 * 
	 * @param outChannelLayout
	 * @return
	 */
	public Resampler outChannelLayout(ChannelLayout outChannelLayout) {
		parameter("-out_channel_layout", outChannelLayout.getName());
		return getMThis();
	}

	/**
	 * Set center mix level (from -32 to 32) (default 0.707107)
	 * 
	 * @param clev
	 * @return
	 */
	public Resampler clev(float clev) {
		if( - 32.0 > clev || clev > 32.0) {
			throw new IllegalArgumentException("clev must be between -32.0 and 32.0: " + clev);
		}
		parameter("-clev", Float.toString(clev));
		return getMThis();
	}

	/**
	 * Set center mix level (from -32 to 32) (default 0.707107)
	 * 
	 * @param centerMixLevel
	 * @return
	 */
	public Resampler centerMixLevel(float centerMixLevel) {
		if( - 32.0 > centerMixLevel || centerMixLevel > 32.0) {
			throw new IllegalArgumentException("centerMixLevel must be between -32.0 and 32.0: " + centerMixLevel);
		}
		parameter("-center_mix_level", Float.toString(centerMixLevel));
		return getMThis();
	}

	/**
	 * Set surround mix level (from -32 to 32) (default 0.707107)
	 * 
	 * @param slev
	 * @return
	 */
	public Resampler slev(float slev) {
		if( - 32.0 > slev || slev > 32.0) {
			throw new IllegalArgumentException("slev must be between -32.0 and 32.0: " + slev);
		}
		parameter("-slev", Float.toString(slev));
		return getMThis();
	}

	/**
	 * Set surround mix Level (from -32 to 32) (default 0.707107)
	 * 
	 * @param surroundMixLevel
	 * @return
	 */
	public Resampler surroundMixLevel(float surroundMixLevel) {
		if( - 32.0 > surroundMixLevel || surroundMixLevel > 32.0) {
			throw new IllegalArgumentException("surroundMixLevel must be between -32.0 and 32.0: " + surroundMixLevel);
		}
		parameter("-surround_mix_level", Float.toString(surroundMixLevel));
		return getMThis();
	}

	/**
	 * Set LFE mix level (from -32 to 32) (default 0)
	 * 
	 * @param lfeMixLevel
	 * @return
	 */
	public Resampler lfeMixLevel(float lfeMixLevel) {
		if( - 32.0 > lfeMixLevel || lfeMixLevel > 32.0) {
			throw new IllegalArgumentException("lfeMixLevel must be between -32.0 and 32.0: " + lfeMixLevel);
		}
		parameter("-lfe_mix_level", Float.toString(lfeMixLevel));
		return getMThis();
	}

	/**
	 * Set rematrix volume (from -1000 to 1000) (default 1)
	 * 
	 * @param rmvol
	 * @return
	 */
	public Resampler rmvol(float rmvol) {
		if( - 1000.0 > rmvol || rmvol > 1000.0) {
			throw new IllegalArgumentException("rmvol must be between -1000.0 and 1000.0: " + rmvol);
		}
		parameter("-rmvol", Float.toString(rmvol));
		return getMThis();
	}

	/**
	 * Set rematrix volume (from -1000 to 1000) (default 1)
	 * 
	 * @param rematrixVolume
	 * @return
	 */
	public Resampler rematrixVolume(float rematrixVolume) {
		if( - 1000.0 > rematrixVolume || rematrixVolume > 1000.0) {
			throw new IllegalArgumentException("rematrixVolume must be between -1000.0 and 1000.0: " + rematrixVolume);
		}
		parameter("-rematrix_volume", Float.toString(rematrixVolume));
		return getMThis();
	}

	/**
	 * Set rematrix maxval (from 0 to 1000) (default 0)
	 * 
	 * @param rematrixMaxval
	 * @return
	 */
	public Resampler rematrixMaxval(float rematrixMaxval) {
		if(0.0 > rematrixMaxval || rematrixMaxval > 1000.0) {
			throw new IllegalArgumentException("rematrixMaxval must be between 0.0 and 1000.0: " + rematrixMaxval);
		}
		parameter("-rematrix_maxval", Float.toString(rematrixMaxval));
		return getMThis();
	}

	/**
	 * Set flags (default 0)
	 * 
	 * @param rflags
	 * @return
	 */
	public Resampler flags(RFlags... rflagss) {
		return flags(Arrays.asList(rflagss));
	}

	/**
	 * Set flags (default 0)
	 * 
	 * @param rflags
	 * @return
	 */
	public Resampler flags(Collection<RFlags> rflagss) {
		rflagss.stream().filter(f -> IO.OUTPUT.accept(f.io())).forEach(f -> this.rflagss.add(f));
		return this;
	}

	/**
	 * Set flags (default 0)
	 * 
	 * @param swrFlags
	 * @return
	 */
	public Resampler swrFlags(SwrFlags... swrFlagss) {
		return swrFlags(Arrays.asList(swrFlagss));
	}

	/**
	 * Set flags (default 0)
	 * 
	 * @param swrFlags
	 * @return
	 */
	public Resampler swrFlags(Collection<SwrFlags> swrFlagss) {
		swrFlagss.stream().filter(f -> IO.OUTPUT.accept(f.io())).forEach(f -> this.swrFlagss.add(f));
		return this;
	}

	/**
	 * Set dither scale (from 0 to INT_MAX) (default 1)
	 * 
	 * @param ditherScale
	 * @return
	 */
	public Resampler ditherScale(float ditherScale) {
		if(0.0 > ditherScale || ditherScale > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("ditherScale must be between 0.0 and 2.14748365E9: " + ditherScale);
		}
		parameter("-dither_scale", Float.toString(ditherScale));
		return getMThis();
	}

	/**
	 * Set dither method (from 0 to 71) (default 0)
	 * 
	 * @param ditherMethod
	 * @return
	 */
	public Resampler ditherMethod(DitherMethod ditherMethod) {
		parameter("-dither_method", ditherMethod.flag());
		return getMThis();
	}

	/**
	 * Set swr resampling filter size (from 0 to INT_MAX) (default 32)
	 * 
	 * @param filterSize
	 * @return
	 */
	public Resampler filterSize(int filterSize) {
		if(filterSize < 0) {
			throw new IllegalArgumentException("filterSize must be at least 0: " + filterSize);
		}
		parameter("-filter_size", Integer.toString(filterSize));
		return getMThis();
	}

	/**
	 * Set swr resampling phase shift (from 0 to 24) (default 10)
	 * 
	 * @param phaseShift
	 * @return
	 */
	public Resampler phaseShift(int phaseShift) {
		if(0 > phaseShift || phaseShift > 24) {
			throw new IllegalArgumentException("phaseShift must be between 0 and 24: " + phaseShift);
		}
		parameter("-phase_shift", Integer.toString(phaseShift));
		return getMThis();
	}

	/**
	 * Enable linear interpolation (from 0 to 1) (default 0)
	 * 
	 * @param linearInterp
	 * @return
	 */
	public Resampler linearInterp(boolean linearInterp) {
		parameter("-linear_interp", Integer.toString(linearInterp ? 1 : 0));
		return getMThis();
	}

	/**
	 * Set cutoff frequency ratio (from 0 to 1) (default 0)
	 * 
	 * @param cutoff
	 * @return
	 */
	public Resampler cutoff(boolean cutoff) {
		parameter("-cutoff", Integer.toString(cutoff ? 1 : 0));
		return getMThis();
	}

	/**
	 * Set cutoff frequency ratio (from 0 to 1) (default 0)
	 * 
	 * @param resampleCutoff
	 * @return
	 */
	public Resampler resampleCutoff(boolean resampleCutoff) {
		parameter("-resample_cutoff", Integer.toString(resampleCutoff ? 1 : 0));
		return getMThis();
	}

	/**
	 * Set resampling Engine (from 0 to 1) (default 0)
	 * 
	 * @param resampler
	 * @return
	 */
	public Resampler resampler(boolean resampler) {
		parameter("-resampler", Integer.toString(resampler ? 1 : 0));
		return getMThis();
	}

	/**
	 * Set soxr resampling precision (in bits) (from 15 to 33) (default 20)
	 * 
	 * @param precision
	 * @return
	 */
	public Resampler precision(double precision) {
		if(15.0 > precision || precision > 33.0) {
			throw new IllegalArgumentException("precision must be between 15.0 and 33.0: " + precision);
		}
		parameter("-precision", Double.toString(precision));
		return getMThis();
	}

	/**
	 * Enable soxr Chebyshev passband & higher-precision irrational ratio approximation (from 0 to 1) (default 0)
	 * 
	 * @param cheby
	 * @return
	 */
	public Resampler cheby(boolean cheby) {
		parameter("-cheby", Integer.toString(cheby ? 1 : 0));
		return getMThis();
	}

	/**
	 * Set minimum difference between timestamps and audio data (in seconds) below which no timestamp compensation of
	 * either kind is applied (from 0 to FLT_MAX) (default FLT_MAX)
	 * 
	 * @param minComp
	 * @return
	 */
	public Resampler minComp(float minComp) {
		if(minComp < 0.0) {
			throw new IllegalArgumentException("minComp must be at least 0.0: " + minComp);
		}
		parameter("-min_comp", Float.toString(minComp));
		return getMThis();
	}

	/**
	 * Set minimum difference between timestamps and audio data (in seconds) to trigger padding/trimming the data. (from
	 * 0 to INT_MAX) (default 0.1)
	 * 
	 * @param minHardComp
	 * @return
	 */
	public Resampler minHardComp(float minHardComp) {
		if(0.0 > minHardComp || minHardComp > 2.14748365E9) {
			throw new IllegalArgumentException("minHardComp must be between 0.0 and 2.14748365E9: " + minHardComp);
		}
		parameter("-min_hard_comp", Float.toString(minHardComp));
		return getMThis();
	}

	/**
	 * Set duration (in seconds) over which data is stretched/squeezed to make it match the timestamps. (from 0 to
	 * INT_MAX) (default 1)
	 * 
	 * @param compDuration
	 * @return
	 */
	public Resampler compDuration(float compDuration) {
		if(0.0 > compDuration || compDuration > 2.14748365E9) {
			throw new IllegalArgumentException("compDuration must be between 0.0 and 2.14748365E9: " + compDuration);
		}
		parameter("-comp_duration", Float.toString(compDuration));
		return getMThis();
	}

	/**
	 * Set maximum factor by which data is stretched/squeezed to make it match the timestamps. (from INT_MIN to INT_MAX)
	 * (default 0)
	 * 
	 * @param maxSoftComp
	 * @return
	 */
	public Resampler maxSoftComp(float maxSoftComp) {
		if( - 2.14748365E9 > maxSoftComp || maxSoftComp > 2.14748365E9) {
			throw new IllegalArgumentException("maxSoftComp must be between -2.14748365E9 and 2.14748365E9: " + maxSoftComp);
		}
		parameter("-max_soft_comp", Float.toString(maxSoftComp));
		return getMThis();
	}

	/**
	 * Simplified 1 parameter audio timestamp matching, 0(disabled), 1(filling and trimming), >1(maximum stretch/squeeze
	 * in samples per second) (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param async
	 * @return
	 */
	public Resampler async(float async) {
		if( - 2.14748365E9 > async || async > 2.14748365E9) {
			throw new IllegalArgumentException("async must be between -2.14748365E9 and 2.14748365E9: " + async);
		}
		parameter("-async", Float.toString(async));
		return getMThis();
	}

	/**
	 * Assume the first pts should be this value (in samples). (from I64_MIN to I64_MAX) (default I64_MIN)
	 * 
	 * @param firstPts
	 * @return
	 */
	public Resampler firstPts(long firstPts) {
		parameter("-first_pts", Long.toString(firstPts));
		return getMThis();
	}

	/**
	 * Set matrixed stereo encoding (from 0 to 6) (default 0)
	 * 
	 * @param matrixEncoding
	 * @return
	 */
	public Resampler matrixEncoding(MatrixEncoding matrixEncoding) {
		parameter("-matrix_encoding", matrixEncoding.flag());
		return getMThis();
	}

	/**
	 * Select swr filter type (from 0 to 2) (default 2)
	 * 
	 * @param filterType
	 * @return
	 */
	public Resampler filterType(FilterType filterType) {
		parameter("-filter_type", filterType.flag());
		return getMThis();
	}

	/**
	 * Set swr Kaiser Window Beta (from 2 to 16) (default 9)
	 * 
	 * @param kaiserBeta
	 * @return
	 */
	public Resampler kaiserBeta(int kaiserBeta) {
		if(2 > kaiserBeta || kaiserBeta > 16) {
			throw new IllegalArgumentException("kaiserBeta must be between 2 and 16: " + kaiserBeta);
		}
		parameter("-kaiser_beta", Integer.toString(kaiserBeta));
		return getMThis();
	}

	/**
	 * Set swr number of output sample bits (from 0 to 64) (default 0)
	 * 
	 * @param outputSampleBits
	 * @return
	 */
	public Resampler outputSampleBits(int outputSampleBits) {
		if(0 > outputSampleBits || outputSampleBits > 64) {
			throw new IllegalArgumentException("outputSampleBits must be between 0 and 64: " + outputSampleBits);
		}
		parameter("-output_sample_bits", Integer.toString(outputSampleBits));
		return getMThis();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.IOEntity#eventAdded(org.fagu.fmv.ffmpeg.operation.Processor, IOEntity)
	 */
	@Override
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {
		super.eventAdded(processor, ioEntity);
		if( ! rflagss.isEmpty()) {
			parameter(processor, ioEntity, "-flags", rflagss);
		}
		if( ! swrFlagss.isEmpty()) {
			parameter(processor, ioEntity, "-swr_flags", swrFlagss);
		}
	}
}
