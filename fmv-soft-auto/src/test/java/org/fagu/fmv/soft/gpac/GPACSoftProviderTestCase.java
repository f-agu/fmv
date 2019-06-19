package org.fagu.fmv.soft.gpac;

import org.fagu.fmv.soft.Soft;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 * @created 19 juin 2019 17:19:23
 */
public class GPACSoftProviderTestCase {

	@Test
	@Ignore
	public void testSearchMP4Box() {
		Soft soft = MP4Box.search();
		System.out.println(soft.isFound());
		System.out.println(soft.getFile());
	}

	@Test
	@Ignore
	public void testSearchMP42TS() {
		Soft soft = MP42TS.search();
		System.out.println(soft.isFound());
		System.out.println(soft.getFile());
	}

}
