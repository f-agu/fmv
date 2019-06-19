package org.fagu.fmv.soft.mplayer;

import org.fagu.fmv.soft.find.SoftPolicy;


/**
 * @author fagu
 */
public class MEncoderSoftProvider extends MSoftProvider {

	public static final String NAME = "mencoder";

	public MEncoderSoftProvider() {
		super(NAME);
	}

	public MEncoderSoftProvider(SoftPolicy softPolicy) {
		super(NAME, softPolicy);
	}

}
