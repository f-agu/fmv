package org.fagu.fmv.soft.mplayer;

import org.fagu.fmv.soft.Soft;


/**
 * @author fagu
 */
public class MPlayer {

	/**
	 * 
	 */
	private MPlayer() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * @return
	 */
	public static Soft search() {
		return Soft.search(new MPlayerSoftProvider());
	}

}
