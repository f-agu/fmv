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


/**
 * ffmpeg -h encoder=libx265
 * 
 * {@link https://trac.ffmpeg.org/wiki/Encode/H.265}<br>
 * 
 * @author f.agu
 */
public class Libx265 extends Encoder<Libx265> {

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum AqMode {
		DISABLED(IO.OUTPUT),
		// Variance AQ (complexity mask)
		ENABLED(IO.OUTPUT),
		// Auto-variance AQ (experimental)
		AUTOVARIANCE(IO.OUTPUT);

		private final IO io;

		/**
		 * @param flag
		 * @param io
		 */
		private AqMode(IO io) {
			this.io = io;
		}

		/**
		 * @return
		 */
		public int flag() {
			return ordinal();
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
	public enum Tune {

		PSNR("psnr"), //
		SSIM("ssim"), //
		GRAIN("grain"), //
		ZEROLATENCY("zero-latency"),
		FASTDECODE("fast-decode"); //

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
	 * 
	 */
	protected Libx265() {
		super(Type.VIDEO, "libx265");
	}

	/**
	 * @return
	 */
	public static Libx265 build() {
		return new Libx265();
	}

	// ****************************************

	/**
	 * @param preset
	 * @return
	 */
	public Libx265 preset(Preset preset) {
		parameter("-preset", preset.flag());
		return this;
	}

	/**
	 * @param tune
	 * @return
	 */
	public Libx265 tune(Tune tune) {
		parameter("-tune", tune.flag());
		return this;
	}

	/**
	 * @param profile
	 * @return
	 */
	public Libx265 profile(Profile profile) {
		parameter("-profile:v", profile.flag());
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
	public Libx265 crf(int crf) {
		if( - 1 > crf || crf > 51) {
			throw new IllegalArgumentException("crf must be between -1 and 51: " + name);
		}
		parameter("-crf", Integer.toString(crf));
		return this;
	}

	/**
	 * Override the x265 configuration using a :-separated list of key=value parameters
	 * 
	 * @param x265Params
	 * @return
	 */
	public Libx265 x265Params(String x265Params) {
		parameter("-x265-params", x265Params);
		return this;
	}

}
