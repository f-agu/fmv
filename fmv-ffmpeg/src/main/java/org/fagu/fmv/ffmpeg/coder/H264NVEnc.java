package org.fagu.fmv.ffmpeg.coder;

import org.fagu.fmv.ffmpeg.Properties;
import org.fagu.fmv.utils.PropertyValues;

/**
 * ffmpeg -h encoder=h264_nvenc
 * {@link https://trac.ffmpeg.org/wiki/HWAccelIntro}
 *
 * @author f.agu
 */
public class H264NVEnc extends H264<H264NVEnc> {

	/**
	 *
	 */
	protected H264NVEnc() {
		super("h264_nvenc");
	}

	/**
	 * @return
	 */
	public static H264NVEnc build() {
		return new H264NVEnc();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.coder.H264#quality(int)
	 */
	@Override
	public H264NVEnc quality(int quality) {
		return cq(quality);
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
	public H264NVEnc cq(int quality) {
		if (-1 > quality || quality > 51) {
			throw new IllegalArgumentException("crf must be between -1 and 51: " + name);
		}
		parameter("-cq", Integer.toString(quality));
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.coder.H264#mostCompatible()
	 */
	@Override
	public H264NVEnc mostCompatible() {
		Preset preset = new Preset(PropertyValues.fromSystemProperties(Properties.H264_PRESET).toLowerCase());
		preset(preset);
		profile(Profile.BASELINE);
		// level(Level.L_3_1); -> auto
		return cq(PropertyValues.fromSystemProperties(Properties.H264_QUALITY));
	}

}
