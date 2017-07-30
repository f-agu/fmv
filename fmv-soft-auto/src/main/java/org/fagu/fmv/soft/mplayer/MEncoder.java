package org.fagu.fmv.soft.mplayer;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftSearch;


/**
 * @author fagu
 */
public class MEncoder {

	/**
	 * 
	 */
	private MEncoder() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * @return
	 */
	public static Soft search() {
		return Soft.search(new MEncoderSoftProvider());
	}

	/**
	 * @return
	 */
	public static SoftSearch searchWith() {
		return Soft.with(MEncoderSoftProvider::new);
	}

}
