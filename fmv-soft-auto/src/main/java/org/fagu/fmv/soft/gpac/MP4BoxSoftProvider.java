package org.fagu.fmv.soft.gpac;

import org.fagu.fmv.soft.find.SoftPolicy;


/**
 * @author f.agu
 */
public class MP4BoxSoftProvider extends GPACSoftProvider {

	public static final String NAME = "mp4box";

	public MP4BoxSoftProvider() {
		this(null);
	}

	public MP4BoxSoftProvider(SoftPolicy softPolicy) {
		super(NAME, softPolicy, "-version");
	}

}
