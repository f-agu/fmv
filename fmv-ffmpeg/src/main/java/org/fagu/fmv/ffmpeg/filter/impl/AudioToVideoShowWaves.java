/**
 * 
 */
package org.fagu.fmv.ffmpeg.filter.impl;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.media.Size;


/**
 * @author fagu
 */
public class AudioToVideoShowWaves extends AbstractFilter {

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Mode {
		// draw a point for each sample
		POINT("point"),
		// draw a line for each sample
		LINE("line"),
		// draw a line between samples
		P2P("p2p"),
		// draw a centered line for each sample
		CLINE("cline");

		private final String flag;

		/**
		 * @param flag
		 */
		private Mode(String flag) {
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
	public enum Scale {
		// linear
		LIN("lin"),
		// logarithmic
		LOG("log"),
		// square root
		SQRT("sqrt"),
		// cubic root
		CBRT("cbrt");

		private final String flag;

		/**
		 * @param flag
		 */
		private Scale(String flag) {
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
	* 
	*/
	protected AudioToVideoShowWaves() {
		super("showwaves");
	}

	/**
	 * @return
	 */
	public static AudioToVideoShowWaves build() {
		return new AudioToVideoShowWaves();
	}

	/**
	 * Set video size (default "600x240")
	 * 
	 * @param size
	 * @return
	 */
	public AudioToVideoShowWaves size(Size size) {
		parameter("size", size.toString());
		return this;
	}

	/**
	 * Set video size (default "600x240")
	 * 
	 * @param s
	 * @return
	 */
	public AudioToVideoShowWaves s(Size s) {
		parameter("s", s.toString());
		return this;
	}

	/**
	 * Select display mode (from 0 to 3) (default point)
	 * 
	 * @param mode
	 * @return
	 */
	public AudioToVideoShowWaves mode(Mode mode) {
		parameter("mode", mode.flag());
		return this;
	}

	/**
	 * Set how many samples to show in the same point (from 0 to INT_MAX) (default 0)
	 * 
	 * @param n
	 * @return
	 */
	public AudioToVideoShowWaves n(int n) {
		if(n < 0) {
			throw new IllegalArgumentException("n must be at least 0: " + n);
		}
		parameter("n", Integer.toString(n));
		return this;
	}

	/**
	 * Set video rate (default "25")
	 * 
	 * @param rate
	 * @return
	 */
	public AudioToVideoShowWaves rate(FrameRate rate) {
		parameter("rate", rate.toString());
		return this;
	}

	/**
	 * Set video rate (default "25")
	 * 
	 * @param r
	 * @return
	 */
	public AudioToVideoShowWaves r(FrameRate r) {
		parameter("r", r.toString());
		return this;
	}

	/**
	 * Draw channels separately (default false)
	 * 
	 * @param splitChannels
	 * @return
	 */
	public AudioToVideoShowWaves splitChannels(boolean splitChannels) {
		parameter("split_channels", splitChannels ? "1" : "0");
		return this;
	}

	/**
	 * Set channels colors (default "red|green|blue|yellow|orange|lime|pink|magenta|brown")
	 * 
	 * @param colors
	 * @return
	 */
	public AudioToVideoShowWaves colors(String colors) {
		parameter("colors", colors);
		return this;
	}

	/**
	 * Set amplitude scale (from 0 to 3) (default lin)
	 * 
	 * @param scale
	 * @return
	 */
	public AudioToVideoShowWaves scale(Scale scale) {
		parameter("scale", scale.flag());
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}
}
