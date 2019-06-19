package org.fagu.fmv.soft.mplayer;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftSearch;


/**
 * @author fagu
 */
public class MPlayer {

	private MPlayer() {
		throw new AssertionError("No instances for you!");
	}

	public static Soft search() {
		return Soft.search(new MPlayerSoftProvider());
	}

	public static SoftSearch searchWith() {
		return Soft.with(MPlayerSoftProvider::new);
	}

}
