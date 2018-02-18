package org.fagu.fmv.ffmpeg.coder;

import org.fagu.fmv.ffmpeg.Properties;
import org.fagu.fmv.utils.PropertyValues;

/**
 * ffmpeg -h encoder=libx264
 *
 * {@link https://trac.ffmpeg.org/wiki/Encode/H.264}<br>
 * {@link http://x264.janhum.alfahosting.org/fullhelp.txt}<br>
 * {@link http://mewiki.project357.com/wiki/X264_Settings#level}
 *
 * @author f.agu
 */
public class Libx264 extends H264<Libx264> {

	/**
	 *
	 */
	protected Libx264() {
		super("libx264");
	}

	/**
	 * @return
	 */
	public static Libx264 build() {
		return new Libx264();
	}

	/**
	 * @return
	 */
	@Override
	public Libx264 mostCompatible() {
		Preset preset = new Preset(PropertyValues.fromSystemProperties(Properties.H264_PRESET).toLowerCase());
		preset(preset);
		compression(Compression.ALL_DEVICES);
		// http://slhck.info/articles/crf
		return crf(PropertyValues.fromSystemProperties(Properties.H264_QUALITY));
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.coder.H264#quality(int)
	 */
	@Override
	public Libx264 quality(int quality) {
		return crf(quality);
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
		if (-1 > crf || crf > 51) {
			throw new IllegalArgumentException("crf must be between -1 and 51: " + name);
		}
		parameter("-crf", Integer.toString(crf));
		return this;
	}

}
