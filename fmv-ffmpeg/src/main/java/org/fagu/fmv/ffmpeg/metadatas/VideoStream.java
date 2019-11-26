package org.fagu.fmv.ffmpeg.metadatas;

import java.util.Map;
import java.util.Optional;

import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.utils.media.Ratio;
import org.fagu.fmv.utils.media.Rotation;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class VideoStream extends Stream {

	public VideoStream(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		super(movieMetadatas, map);
	}

	public int width() {
		return getInt("width").orElseThrow(RuntimeException::new);
	}

	public int height() {
		return getInt("height").orElseThrow(RuntimeException::new);
	}

	/**
	 * SAR
	 * 
	 * @return
	 */
	public Optional<Ratio> sampleAspectRatio() {
		return getRatio("sample_aspect_ratio");
	}

	/**
	 * DAR
	 * 
	 * @return
	 */
	public Optional<Ratio> displayAspectRatio() {
		return getRatio("display_aspect_ratio");
	}

	public Optional<PixelFormat> pixelFormat() {
		return getPixelFormat("pix_fmt");
	}

	public Optional<Integer> level() {
		return getInt("level");
	}

	public Optional<Integer> bitsPerRawSample() {
		return getInt("bits_per_raw_sample");
	}

	public Size size() {
		return Size.valueOf(width(), height());
	}

	@Override
	public Type type() {
		return Type.VIDEO;
	}

	public Rotation rotation() {
		Optional<Object> tag = tag("rotate");
		return tag.isPresent() ? Rotation.valueOf("R_" + tag.get()) : Rotation.R_0;
	}

	@Override
	public String toString() {
		return new StringBuilder(100)
				.append("VideoStream[").append(width()).append('x').append(height()).append(']')
				.toString();
	}

}
