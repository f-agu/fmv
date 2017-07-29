package org.fagu.fmv.soft.mplayer;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.SoftPolicy;


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
	 * @param softPolicy
	 * @return
	 */
	public static Soft search(SoftPolicy softPolicy) {
		return Soft.search(new MEncoderSoftProvider(softPolicy));
	}

}
