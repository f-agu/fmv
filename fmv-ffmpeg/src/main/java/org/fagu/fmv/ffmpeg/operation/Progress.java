package org.fagu.fmv.ffmpeg.operation;

import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public interface Progress {

	/**
	 * @return
	 */
	int getFrame();

	/**
	 * @return
	 */
	int getFps();

	/**
	 * @return
	 */
	int getQ();

	/**
	 * @return
	 */
	int getSizeKb();

	/**
	 * @return
	 */
	Time getTime();

	/**
	 * @return
	 */
	Double getBitRateKb();

	/**
	 * @return
	 */
	Integer getDup();

	/**
	 * @return
	 */
	Integer getDrop();

	/**
	 * @return
	 */
	Float getSpeed();

}
