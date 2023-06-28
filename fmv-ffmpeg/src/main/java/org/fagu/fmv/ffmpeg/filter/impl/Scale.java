package org.fagu.fmv.ffmpeg.filter.impl;

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
import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.flags.SwsFlags;
import org.fagu.fmv.ffmpeg.format.IO;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class Scale extends AbstractFilter {

	// ----------------------------------------------

	public enum Interlacing {

		FORCE(1), // Force interlaced aware scaling
		NO(0), // Do not apply interlaced scaling (default)
		AUTO( - 1); // Select interlaced aware scaling depending on whether the source frames are flagged as interlaced
					// or not.

		private final int value;

		private Interlacing(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	// ----------------------------------------------

	public enum ColorMatrix {
		AUTO, // Choose automatically.
		BT709, // International Telecommunication Union (ITU) Recommendation BT.709.
		FCC, // United States Federal Communications Commission (FCC) Code of Federal Regulations (CFR) Title 47 (2003)
				// 73.682 (a).
		BT601, // ITU Radiocommunication Sector (ITU-R) Recommendation BT.601
				// ITU-R Rec. BT.470-6 (1998) Systems B, B1, and G
				// Society of Motion Picture and Television Engineers (SMPTE) ST 170:2004
		SMPTE240M // SMPTE ST 240:1999.
	}

	// ----------------------------------------------

	public enum ForceOriginalAspectRatio {
		DISABLE, DECREASE, INCREASE
	}

	// ----------------------------------------------

	public enum Range {

		AUTO("auto"), //
		FULL("full"), //
		JPEG("jpeg"), //
		MPEG("mpeg"), //
		TV("tv"), //
		PC("pc");

		private final String value;

		private Range(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	// -----------------------------------------------

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

		private SwsDither(String flag, IO io) {
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

	// ----------------------------------------------

	private Size size;

	private ScaleMode scaleMode;

	protected Scale() {
		super("scale");
	}

	protected Scale(Size size, ScaleMode mode) {
		this();
		set(size, mode);
	}

	public static Scale build() {
		return new Scale();
	}

	public static Scale to(Size size, ScaleMode mode) {
		return new Scale(size, mode);
	}

	public Scale set(Size size, ScaleMode scaleMode) {
		this.size = size;
		this.scaleMode = scaleMode;
		scaleMode.apply(this, size);
		return this;
	}

	public Size getSize() {
		return size;
	}

	public ScaleMode getScaleMode() {
		return scaleMode;
	}

	public Scale width(int width) {
		return width(Integer.toString(width));
	}

	public Scale width(String expr) {
		parameter("w", '\'' + expr + '\'');
		return this;
	}

	public Scale height(int height) {
		return height(Integer.toString(height));
	}

	public Scale height(String expr) {
		parameter("h", '\'' + expr + '\'');
		return this;
	}

	public Scale size(Size size) {
		return width(size.getWidth()).height(size.getHeight());
	}

	public Scale inputColorMatrix(ColorMatrix colorMatrix) {
		parameter("in_color_matrix", colorMatrix.name().toLowerCase());
		return this;
	}

	public Scale outputColorMatrix(ColorMatrix colorMatrix) {
		parameter("out_color_matrix", colorMatrix.name().toLowerCase());
		return this;
	}

	public Scale interlacing(Interlacing interlacing) {
		parameter("interl", Integer.toString(interlacing.getValue()));
		return this;
	}

	public Scale inputRange(Range range) {
		parameter("in_range", range.getValue());
		return this;
	}

	public Scale outputRange(Range range) {
		parameter("out_range", range.getValue());
		return this;
	}

	public Scale forceOriginalAspectRatio(ForceOriginalAspectRatio foar) {
		parameter("force_original_aspect_ratio", foar.name().toLowerCase());
		return this;
	}

	//
	// /**
	// * @param expr
	// * @return
	// */
	// public Scale flags(String expr) {
	// addArgument("flags", '\'' + expr + '\'');
	// return this;
	// }
	//
	// /**
	// * @param flags
	// * @return
	// */
	// public Scale flags(Flag... flags) {
	// return flags(Arrays.asList(flags));
	// }
	//
	// /**
	// * @param flags
	// * @return
	// */
	// public Scale flags(Collection<Flag> flags) {
	// if(flags.isEmpty()) {
	// return this;
	// }
	// return flags(StringUtils.join(flags, '+').toLowerCase());
	// }

	/**
	 * Input vertical chroma position in luma grid/256 (from -1 to 512) (default -1)
	 * 
	 * @param inVChrPos
	 * @return
	 */
	public Scale inVChrPos(int inVChrPos) {
		if( - 1 > inVChrPos || inVChrPos > 512) {
			throw new IllegalArgumentException("inVChrPos must be between -1 and 512: " + inVChrPos);
		}
		parameter("in_v_chr_pos", Integer.toString(inVChrPos));
		return this;
	}

	/**
	 * Input horizontal chroma position in luma grid/256 (from -1 to 512) (default -1)
	 * 
	 * @param inHChrPos
	 * @return
	 */
	public Scale inHChrPos(int inHChrPos) {
		if( - 1 > inHChrPos || inHChrPos > 512) {
			throw new IllegalArgumentException("inHChrPos must be between -1 and 512: " + inHChrPos);
		}
		parameter("in_h_chr_pos", Integer.toString(inHChrPos));
		return this;
	}

	/**
	 * Output vertical chroma position in luma grid/256 (from -1 to 512) (default -1)
	 * 
	 * @param outVChrPos
	 * @return
	 */
	public Scale outVChrPos(int outVChrPos) {
		if( - 1 > outVChrPos || outVChrPos > 512) {
			throw new IllegalArgumentException("outVChrPos must be between -1 and 512: " + outVChrPos);
		}
		parameter("out_v_chr_pos", Integer.toString(outVChrPos));
		return this;
	}

	/**
	 * Output horizontal chroma position in luma grid/256 (from -1 to 512) (default -1)
	 * 
	 * @param outHChrPos
	 * @return
	 */
	public Scale outHChrPos(int outHChrPos) {
		if( - 1 > outHChrPos || outHChrPos > 512) {
			throw new IllegalArgumentException("outHChrPos must be between -1 and 512: " + outHChrPos);
		}
		parameter("out_h_chr_pos", Integer.toString(outHChrPos));
		return this;
	}

	/**
	 * Scaler flags (default 4)
	 * 
	 * @param swsFlags
	 * @return
	 */
	public Scale flags(SwsFlags... swsFlagss) {
		return flags(Arrays.asList(swsFlagss));
	}

	/**
	 * Scaler flags (default 4)
	 * 
	 * @param swsFlags
	 * @return
	 */
	public Scale flags(Collection<SwsFlags> swsFlagss) {
		if( ! swsFlagss.isEmpty()) {
			parameter("flags", flagsToString(swsFlagss));
		}
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

}
