package org.fagu.fmv.soft.xpdf;

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

import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.version.Version;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class PdfInfoSoftProviderTestCase {

	@Test
	void testParseOnLinux_original() throws IOException {
		Parser parser = newParser();
		parser.readLine("pdfinfo version 3.04");
		parser.readLine("Copyright 1996-2014 Glyph & Cog, LLC");
		assertInfo(parser, new Version(3, 4));
	}

	@Test
	void testParseOnLinux_popplers() throws IOException {
		Parser parser = newParser();
		parser.readLine("pdfinfo version 0.12.4");
		parser.readLine("Copyright 2005-2009 The Poppler Developers - http://poppler.freedesktop.org");
		parser.readLine("Copyright 1996-2004 Glyph & Cog, LLC");
		assertInfo(parser, new Version(0, 12, 4));
	}

	@Test
	void testParseOnWindows_original() throws IOException {
		Parser parser = newParser();
		parser.readLine("pdfinfo version 3.04");
		parser.readLine("Copyright 1996-2014 Glyph & Cog, LLC");
		assertInfo(parser, new Version(3, 4));
	}

	@Test
	void testParseOnWindows_xpdfreader() throws IOException {
		Parser parser = newParser();
		parser.readLine("pdfinfo version 4.03 [www.xpdfreader.com]");
		parser.readLine("Copyright 1996-2021 Glyph & Cog, LLC");
		assertInfo(parser, new Version(4, 3));
	}

	@Test
	void testSoftPolicy() throws IOException {
		PdfInfoSoftProvider softProvider = new PdfInfoSoftProvider();
		assertEquals("xpdf[>= v4] ; poppler[>= v0.12] ; All platforms[>= v0.12]", softProvider.getMinVersion());
	}

	// *******************************************************

	private Parser newParser() {
		PdfInfoSoftProvider softProvider = new PdfInfoSoftProvider();
		return softProvider.createParser(new File("."));
	}

	private void assertInfo(Parser parser, Version expectedVersion) throws IOException {
		SoftFound softFound = parser.closeAndParse("", 0);
		VersionSoftInfo softInfo = (VersionSoftInfo)softFound.getSoftInfo();
		assertEquals(expectedVersion, softInfo.getVersion().orElse(null));
	}

}
