package org.fagu.fmv.ffmpeg.soft;

import org.fagu.version.Version;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 24 janv. 2017 12:36:47
 */
public class BuildMappingTestCase {

	/**
	 * 
	 */
	public BuildMappingTestCase() {}

	@Test
	public void testDate() {
		System.out.println(BuildMapping.versionToLocalDate(new Version(0, 0, 0)));
	}

}
