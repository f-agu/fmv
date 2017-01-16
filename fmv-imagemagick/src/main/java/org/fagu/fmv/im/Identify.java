package org.fagu.fmv.im;

import java.util.Objects;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;


/**
 * @author fagu
 */
public class Identify {

	private final Soft identifySoft;

	/**
	 * @param identifySoft
	 */
	public Identify(Soft identifySoft) {
		this.identifySoft = Objects.requireNonNull(identifySoft);
		if( ! identifySoft.isFound()) {
			// TODO
		}
	}

	public static void main(String[] args) {
		Soft soft = org.fagu.fmv.soft.im.Identify.search();
		VersionSoftInfo versionSoftInfo = (VersionSoftInfo)soft.getFirstFound().getSoftInfo();
		System.out.println(versionSoftInfo.getVersion());
		System.out.println(soft.getFile());

		soft.withParameters("rr");
	}

}
