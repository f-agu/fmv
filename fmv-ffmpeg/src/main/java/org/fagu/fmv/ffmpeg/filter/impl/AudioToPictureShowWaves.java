package org.fagu.fmv.ffmpeg.filter.impl;

import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class AudioToPictureShowWaves extends AbstractFilter {

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
	protected AudioToPictureShowWaves() {
		super("showwavespic");
	}

	/**
	 * @return
	 */
	public static AudioToPictureShowWaves build() {
		return new AudioToPictureShowWaves();
	}

	/**
	 * Set video size (default "600x240")
	 * 
	 * @param size
	 * @return
	 */
	public AudioToPictureShowWaves size(Size size) {
		parameter("size", size.toString());
		return this;
	}

	/**
	 * Set video size (default "600x240")
	 * 
	 * @param s
	 * @return
	 */
	public AudioToPictureShowWaves s(Size s) {
		parameter("s", s.toString());
		return this;
	}

	/**
	 * Draw channels separately (default false)
	 * 
	 * @param splitChannels
	 * @return
	 */
	public AudioToPictureShowWaves splitChannels(boolean splitChannels) {
		parameter("split_channels", splitChannels ? "1" : "0");
		return this;
	}

	/**
	 * Set channels colors (default "red|green|blue|yellow|orange|lime|pink|magenta|brown")
	 * 
	 * @param colors
	 * @return
	 */
	public AudioToPictureShowWaves colors(String colors) {
		parameter("colors", colors);
		return this;
	}

	/**
	 * Set amplitude scale (from 0 to 3) (default lin)
	 * 
	 * @param scale
	 * @return
	 */
	public AudioToPictureShowWaves scale(Scale scale) {
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
