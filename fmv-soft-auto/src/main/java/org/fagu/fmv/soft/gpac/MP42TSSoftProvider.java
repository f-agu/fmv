package org.fagu.fmv.soft.gpac;

import org.fagu.fmv.soft.find.SoftPolicy;


/**
 * @author f.agu
 */
public class MP42TSSoftProvider extends GPACSoftProvider {

	public static final String NAME = "mp42ts";

	public MP42TSSoftProvider() {
		this(null);
	}

	public MP42TSSoftProvider(SoftPolicy softPolicy) {
		super(NAME, softPolicy, "-h");
	}

}
