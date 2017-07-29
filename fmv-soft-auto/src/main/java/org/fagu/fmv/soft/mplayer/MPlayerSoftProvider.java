package org.fagu.fmv.soft.mplayer;

import org.fagu.fmv.soft.find.SoftPolicy;


/**
 * @author fagu
 */
public class MPlayerSoftProvider extends MSoftProvider {

	public static final String NAME = "mplayer";

	/**
	 * 
	 */
	public MPlayerSoftProvider() {
		super(NAME);
	}

	/**
	 * @param softPolicy
	 */
	public MPlayerSoftProvider(SoftPolicy softPolicy) {
		super(NAME, softPolicy);
	}

}
