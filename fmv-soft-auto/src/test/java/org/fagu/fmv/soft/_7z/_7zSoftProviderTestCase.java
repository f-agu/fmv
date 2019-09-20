package org.fagu.fmv.soft._7z;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.version.Version;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 20 sept. 2019 10:07:44
 */
public class _7zSoftProviderTestCase {

	@Test
	@Ignore
	public void testSearch() {
		Soft s = _7z.search();
		System.out.println(s);
		s.getFounds().getFounds().forEach(sf -> {
			System.out.println(sf);
		});
	}

	@Test
	public void test1() throws IOException {
		Parser parser = newParser();
		parser.readLine("");
		parser.readLine("7-Zip [64] 16.00 : Copyright (c) 1999-2016 Igor Pavlov : 2016-05-10");
		assertInfo(parser, new Version(16, 0));
	}

	@Test
	public void test2() throws IOException {
		Parser parser = newParser();
		parser.readLine("");
		parser.readLine("7-Zip 17.01 beta (x64) : Copyright (c) 1999-2017 Igor Pavlov : 2017-08-28");
		assertInfo(parser, new Version(17, 1));
	}

	// *******************************************************

	private Parser newParser() {
		_7zSoftProvider softProvider = new _7zSoftProvider();
		return softProvider.createParser(new File("."));
	}

	private void assertInfo(Parser parser, Version expectedVersion) throws IOException {
		SoftFound softFound = parser.closeAndParse("", 0);
		VersionSoftInfo softInfo = (VersionSoftInfo)softFound.getSoftInfo();
		assertEquals(expectedVersion, softInfo.getVersion().orElse(null));
	}

}
