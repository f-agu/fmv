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

import org.fagu.fmv.ffmpeg.flags.SwsFlags;
import org.fagu.fmv.ffmpeg.format.IO;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.Processor;


/**
 * @author f.agu
 */
public class Scaler extends Element<Scaler> {

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum SwsDither {
		// leave choice to sws
		AUTO("auto", IO.OUTPUT),
		// bayer dither
		BAYER("bayer", IO.OUTPUT),
		// error diffusion
		ED("ed", IO.OUTPUT),
		// arithmetic addition dither
		A_DITHER("a_dither", IO.OUTPUT),
		// arithmetic xor dither
		X_DITHER("x_dither", IO.OUTPUT);

		private final String flag;

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private SwsDither(String flag, IO io) {
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

	private final Set<SwsFlags> swsFlagss;

	/**
	 * 
	 */
	private Scaler() {
		super(null);
		swsFlagss = new HashSet<>();
	}

	/**
	 * @return
	 */
	public static Scaler build() {
		return new Scaler();
	}

	/**
	 * Scaler flags (default 4)
	 * 
	 * @param swsFlags
	 * @return
	 */
	public Scaler swsFlags(SwsFlags... swsFlagss) {
		return swsFlags(Arrays.asList(swsFlagss));
	}

	/**
	 * Scaler flags (default 4)
	 * 
	 * @param swsFlags
	 * @return
	 */
	public Scaler swsFlags(Collection<SwsFlags> swsFlagss) {
		swsFlagss.stream().filter(f -> IO.OUTPUT.accept(f.io())).forEach(swsFlagss::add);
		return this;
	}

	/**
	 * Source width (from 1 to INT_MAX) (default 16)
	 * 
	 * @param srcw
	 * @return
	 */
	public Scaler srcw(int srcw) {
		if(srcw < 1) {
			throw new IllegalArgumentException("srcw must be at least 1: " + srcw);
		}
		parameter("-srcw", Integer.toString(srcw));
		return getMThis();
	}

	/**
	 * Source height (from 1 to INT_MAX) (default 16)
	 * 
	 * @param srch
	 * @return
	 */
	public Scaler srch(int srch) {
		if(srch < 1) {
			throw new IllegalArgumentException("srch must be at least 1: " + srch);
		}
		parameter("-srch", Integer.toString(srch));
		return getMThis();
	}

	/**
	 * Destination width (from 1 to INT_MAX) (default 16)
	 * 
	 * @param dstw
	 * @return
	 */
	public Scaler dstw(int dstw) {
		if(dstw < 1) {
			throw new IllegalArgumentException("dstw must be at least 1: " + dstw);
		}
		parameter("-dstw", Integer.toString(dstw));
		return getMThis();
	}

	/**
	 * Destination height (from 1 to INT_MAX) (default 16)
	 * 
	 * @param dsth
	 * @return
	 */
	public Scaler dsth(int dsth) {
		if(dsth < 1) {
			throw new IllegalArgumentException("dsth must be at least 1: " + dsth);
		}
		parameter("-dsth", Integer.toString(dsth));
		return getMThis();
	}

	/**
	 * Source format (from 0 to 332) (default 0)
	 * 
	 * @param srcFormat
	 * @return
	 */
	public Scaler srcFormat(int srcFormat) {
		if(0 > srcFormat || srcFormat > 332) {
			throw new IllegalArgumentException("srcFormat must be between 0 and 332: " + srcFormat);
		}
		parameter("-src_format", Integer.toString(srcFormat));
		return getMThis();
	}

	/**
	 * Destination format (from 0 to 332) (default 0)
	 * 
	 * @param dstFormat
	 * @return
	 */
	public Scaler dstFormat(int dstFormat) {
		if(0 > dstFormat || dstFormat > 332) {
			throw new IllegalArgumentException("dstFormat must be between 0 and 332: " + dstFormat);
		}
		parameter("-dst_format", Integer.toString(dstFormat));
		return getMThis();
	}

	/**
	 * Source range (from 0 to 1) (default 0)
	 * 
	 * @param srcRange
	 * @return
	 */
	public Scaler srcRange(boolean srcRange) {
		parameter("-src_range", Integer.toString(srcRange ? 1 : 0));
		return getMThis();
	}

	/**
	 * Destination range (from 0 to 1) (default 0)
	 * 
	 * @param dstRange
	 * @return
	 */
	public Scaler dstRange(boolean dstRange) {
		parameter("-dst_range", Integer.toString(dstRange ? 1 : 0));
		return getMThis();
	}

	/**
	 * Scaler param 0 (from INT_MIN to INT_MAX) (default 123456)
	 * 
	 * @param param0
	 * @return
	 */
	public Scaler param0(double param0) {
		if( - 2.147483648E9 > param0 || param0 > 2.147483647E9) {
			throw new IllegalArgumentException("param0 must be between -2.147483648E9 and 2.147483647E9: " + param0);
		}
		parameter("-param0", Double.toString(param0));
		return getMThis();
	}

	/**
	 * Scaler param 1 (from INT_MIN to INT_MAX) (default 123456)
	 * 
	 * @param param1
	 * @return
	 */
	public Scaler param1(double param1) {
		if( - 2.147483648E9 > param1 || param1 > 2.147483647E9) {
			throw new IllegalArgumentException("param1 must be between -2.147483648E9 and 2.147483647E9: " + param1);
		}
		parameter("-param1", Double.toString(param1));
		return getMThis();
	}

	/**
	 * Source vertical chroma position in luma grid/256 (from -1 to 512) (default -1)
	 * 
	 * @param srcVChrPos
	 * @return
	 */
	public Scaler srcVChrPos(int srcVChrPos) {
		if( - 1 > srcVChrPos || srcVChrPos > 512) {
			throw new IllegalArgumentException("srcVChrPos must be between -1 and 512: " + srcVChrPos);
		}
		parameter("-src_v_chr_pos", Integer.toString(srcVChrPos));
		return getMThis();
	}

	/**
	 * Source horizontal chroma position in luma grid/256 (from -1 to 512) (default -1)
	 * 
	 * @param srcHChrPos
	 * @return
	 */
	public Scaler srcHChrPos(int srcHChrPos) {
		if( - 1 > srcHChrPos || srcHChrPos > 512) {
			throw new IllegalArgumentException("srcHChrPos must be between -1 and 512: " + srcHChrPos);
		}
		parameter("-src_h_chr_pos", Integer.toString(srcHChrPos));
		return getMThis();
	}

	/**
	 * Destination vertical chroma position in luma grid/256 (from -1 to 512) (default -1)
	 * 
	 * @param dstVChrPos
	 * @return
	 */
	public Scaler dstVChrPos(int dstVChrPos) {
		if( - 1 > dstVChrPos || dstVChrPos > 512) {
			throw new IllegalArgumentException("dstVChrPos must be between -1 and 512: " + dstVChrPos);
		}
		parameter("-dst_v_chr_pos", Integer.toString(dstVChrPos));
		return getMThis();
	}

	/**
	 * Destination horizontal chroma position in luma grid/256 (from -1 to 512) (default -1)
	 * 
	 * @param dstHChrPos
	 * @return
	 */
	public Scaler dstHChrPos(int dstHChrPos) {
		if( - 1 > dstHChrPos || dstHChrPos > 512) {
			throw new IllegalArgumentException("dstHChrPos must be between -1 and 512: " + dstHChrPos);
		}
		parameter("-dst_h_chr_pos", Integer.toString(dstHChrPos));
		return getMThis();
	}

	/**
	 * Set dithering algorithm (from 0 to 6) (default 1)
	 * 
	 * @param swsDither
	 * @return
	 */
	public Scaler swsDither(SwsDither swsDither) {
		if( ! swsDither.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + swsDither.io() + ": OUTPUT");
		}
		parameter("-sws_dither", swsDither.flag());
		return getMThis();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.IOEntity#eventAdded(org.fagu.fmv.ffmpeg.operation.Processor, IOEntity)
	 */
	@Override
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {
		super.eventAdded(processor, ioEntity);
		if( ! swsFlagss.isEmpty()) {
			parameter(processor, ioEntity, "-sws_flags", swsFlagss);
		}
	}
}
