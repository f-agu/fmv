package org.fagu.fmv.soft.win32;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 * @created 24 sept. 2019 09:12:22
 */
public class BinaryVersionInfoTestCase {

	@Test
	@Ignore
	public void test() {
		if( ! SystemUtils.IS_OS_WINDOWS) {
			return;
		}
		File exeFile = new File(System.getenv("windir"), "regedit.exe");
		BinaryVersionInfo.getInfo(exeFile)
				.ifPresent(m -> m.entrySet().forEach(System.out::println));
	}

}
