package org.fagu.fmv.mymedia.rip.dvd;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.fagu.fmv.ffmpeg.operation.FFMPEGProgressReadLine;


/**
 * @author Oodrive
 * @author f.agu
 * @created 5 juin 2017 16:38:40
 */
public class FFMpegProgress extends FFMPEGProgressReadLine {

	private final AtomicInteger progressEncode;

	private final int numberOfFrames;

	/**
	 * @param progressEncode
	 * @param numberOfFrames
	 */
	public FFMpegProgress(AtomicInteger progressEncode, int numberOfFrames) {
		this.progressEncode = Objects.requireNonNull(progressEncode);
		this.numberOfFrames = numberOfFrames;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.FFMPEGProgressReadLine#read(java.lang.String)
	 */
	@Override
	public void read(String line) {
		super.read(line);
		System.out.println(line);
		progressEncode.set(100 * getFrame() / numberOfFrames);
	}

}
