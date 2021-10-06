package org.fagu.fmv.im.soft;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 - 2015 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ParserFactory;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionDateSoftInfo;
import org.fagu.fmv.soft.utils.ImmutableProperties;
import org.fagu.version.Version;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class IMInfoTestCase {

	@Test
	void testParse_firstLines() throws IOException {
		assertInfo("Version: ImageMagick 6.6.0-4 2012-05-02 Q16 http://www.imagemagick.org", new Version(6, 6, 0, 4), d(2012, 5, 2), "6.6.0.4");
		assertInfo("Version: ImageMagick 6.7.9-10 2012-10-08 Q16 http://www.imagemagick.org", new Version(6, 7, 9, 10), d(2012, 10, 8), "6.7.9.10");
		assertInfo("Version: ImageMagick 6.8.7-1 2013-10-17 Q16 http://www.imagemagick.org", new Version(6, 8, 7, 1), d(2013, 10, 17), "6.8.7.1");
		assertInfo("Version: ImageMagick 6.9.2-0 Q16 x86_64 2015-09-10 http://www.imagemagick.org", new Version(6, 9, 2, 0), d(2015, 9, 10),
				"6.9.2.0");
		assertInfo("Version: ImageMagick 6.9.2-1 Q16 x86_64 2015-09-18 http://www.imagemagick.org", new Version(6, 9, 2, 1), d(2015, 9, 18),
				"6.9.2.1");
		assertInfo("Version: ImageMagick 6.9.7-4 Q16 x86_64 20170114 http://www.imagemagick.org", new Version(6, 9, 7, 4), d(2017, 01, 14),
				"6.9.7.4");
		assertInfo("Version: ImageMagick 6.9.7-4 Q16 x86_64 2017-01-14 http://www.imagemagick.org", new Version(6, 9, 7, 4), d(2017, 01, 14),
				"6.9.7.4");
		assertInfo("Version: ImageMagick 7.0.8-28 Q16 x86_64 2019-02-17 https://imagemagick.org", new Version(7, 0, 8, 28), d(2019, 2, 17),
				"7.0.8.28");
	}

	@Test
	void testParseOnWindows() throws IOException {
		Parser parser = newParser();
		parser.readLine("Version: ImageMagick 6.8.7-1 2013-10-17 Q16 http://www.imagemagick.org");
		parser.readLine("Copyright: Copyright (C) 1999-2013 ImageMagick Studio LLC");
		parser.readLine("Features: DPC OpenMP");
		parser.readLine("Delegates: bzlib freetype jbig jng jp2 jpeg lcms lqr png ps png tiff webp x xml zlib");
		assertInfo(parser, new Version(6, 8, 7, 1), d(2013, 10, 17), "6.8.7.1");
	}

	@Test
	void testParseOnMac() throws IOException {
		Parser parser = newParser();
		parser.readLine("Version: ImageMagick 6.9.3-7 Q16 x86_64 2016-03-27 http://www.imagemagick.org");
		parser.readLine("Copyright: Copyright (C) 1999-2016 ImageMagick Studio LLC");
		parser.readLine("License: http://www.imagemagick.org/script/license.php");
		parser.readLine("Features: Cipher DPC Modules ");
		parser.readLine("Delegates (built-in): bzlib freetype jng jpeg ltdl lzma png tiff xml zlib");

		assertInfo(parser, new Version(6, 9, 3, 7), d(2016, 3, 27), "6.9.3.7");
	}

	// *******************************************************

	private Parser newParser() {
		ConvertSoftProvider softProvider = new ConvertSoftProvider();
		ParserFactory parserFactory = ((ExecSoftFoundFactory)softProvider.createSoftFoundFactory(ImmutableProperties.of())).getParserFactory();
		return parserFactory.create(new File("."), softProvider.getSoftPolicy());
	}

	private Date d(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private void assertInfo(String firstLine, Version expectedVersion, Date expectedDate, String expectedInfo) throws IOException {
		Parser parser = newParser();
		parser.readLine(firstLine);
		assertInfo(parser, expectedVersion, expectedDate, expectedInfo);
	}

	private void assertInfo(Parser parser, Version expectedVersion, Date expectedDate, String expectedInfo) throws IOException {
		SoftFound softFound = parser.closeAndParse("", 0);
		VersionDateSoftInfo imInfo = (VersionDateSoftInfo)softFound.getSoftInfo();
		assertEquals(expectedVersion, imInfo.getVersion().orElse(null));
		assertEquals(expectedDate, imInfo.getDate().orElse(null));
		assertEquals(expectedInfo, imInfo.getInfo());
	}

}
