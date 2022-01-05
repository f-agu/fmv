package org.fagu.fmv.im.soft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
import java.time.LocalDate;
import java.util.Optional;

import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ParserFactory;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionDateSoftInfo;
import org.fagu.fmv.soft.utils.ImmutableProperties;
import org.fagu.version.Version;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class IdentifySoftProviderTestCase {

	@Test
	@Disabled
	void testSearch() {
		// ExecuteDelegateRepository.set(new LogExecuteDelegate(System.out::println));
		Identify.search();
	}

	@Test
	void testParseWin10_7102() throws IOException {
		Parser parser = newParser();
		parser.readLine("Version: ImageMagick 7.1.0-18 Q16 x64 2021-12-18 https://imagemagick.org");
		parser.readLine("Copyright: (C) 1999-2021 ImageMagick Studio LLC");
		parser.readLine("License: https://imagemagick.org/script/license.php");
		parser.readLine("Features: Cipher DPC Modules OpenCL OpenMP(2.0)");
		parser.readLine(
				"Delegates (built-in): bzlib cairo flif freetype gslib heic jng jp2 jpeg jxl lcms lqr lzma openexr pangocairo png ps raqm raw rsvg tiff webp xml zip zlib");
		parser.readLine("Compiler: Visual Studio 2019 (192930137)");
		assertInfo(parser, new Version(7, 1, 0, 18), LocalDate.of(2021, 12, 18));
	}

	@Test
	void testParseK8S_7102() throws IOException {
		Parser parser = newParser();
		parser.readLine("Version: ImageMagick 7.1.0-2 Q16 x86_64 2021-06-25 https://imagemagick.org");
		parser.readLine("Copyright: (C) 1999-2021 ImageMagick Studio LLC");
		parser.readLine("License: https://imagemagick.org/script/license.php");
		parser.readLine("Features: Cipher DPC HDRI Modules OpenMP(3.1) ");
		parser.readLine(
				"Delegates (built-in): bzlib cairo djvu fftw flif fontconfig freetype gvc heic jbig jng jp2 jpeg lcms ltdl lzma openexr pangocairo png raqm raw rsvg tiff webp wmf x xml zip zlib");
		assertInfo(parser, new Version(7, 1, 0, 2), LocalDate.of(2021, 06, 25));
	}

	// *******************************************************

	private Parser newParser() {
		IdentifySoftProvider softProvider = new IdentifySoftProvider();
		ParserFactory parserFactory = ((ExecSoftFoundFactory)softProvider.createSoftFoundFactory(ImmutableProperties.of())).getParserFactory();
		return parserFactory.create(new File("."), softProvider.getSoftPolicy());
	}

	private void assertInfo(Parser parser, Version expectedVersion, LocalDate date) throws IOException {
		SoftFound softFound = parser.closeAndParse("", 0);
		VersionDateSoftInfo softInfo = (VersionDateSoftInfo)softFound.getSoftInfo();
		assertEquals(expectedVersion, softInfo.getVersion().orElse(null));
		Optional<LocalDate> dateOpt = softInfo.getLocalDate();
		if(date != null) {
			assertEquals(date, dateOpt.get());
		} else {
			assertFalse(dateOpt.isPresent());
		}
	}

}
