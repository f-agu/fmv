package org.fagu.fmv.ffmpeg.format;

import org.fagu.fmv.ffmpeg.operation.MediaOutput;


/**
 * @author Oodrive
 * @author f.agu
 * @created 18 juil. 2019 16:20:09
 */
public class CustomMuxer extends Muxer<CustomMuxer> {

	public CustomMuxer(String name, MediaOutput mediaOutput) {
		super(name, mediaOutput);
	}

}
