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
import org.fagu.fmv.soft.find.Lines;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.version.Version;
import org.fagu.version.VersionUnit;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class PdfInfoSoftProviderTestCase {

	@Test
	void testParseOnLinux_original() throws IOException {
		Lines lines = new Lines();
		lines.addOut("pdfinfo version 3.04");
		lines.addOut("Copyright 1996-2014 Glyph & Cog, LLC");
		assertInfo(lines, new Version(3, 4));
	}

	@Test
	void testParseOnLinux_popplers() throws IOException {
		Lines lines = new Lines();
		lines.addOut("pdfinfo version 0.12.4");
		lines.addOut("Copyright 2005-2009 The Poppler Developers - http://poppler.freedesktop.org");
		lines.addOut("Copyright 1996-2004 Glyph & Cog, LLC");
		assertInfo(lines, new Version(0, 12, 4));
	}

	@Test
	void testParseOnWindows_original() throws IOException {
		Lines lines = new Lines();
		lines.addOut("pdfinfo version 3.04");
		lines.addOut("Copyright 1996-2014 Glyph & Cog, LLC");
		assertInfo(lines, new Version(3, 4));
	}

	@Test
	void testParseOnWindows_xpdfreader() throws IOException {
		Lines lines = new Lines();
		lines.addOut("pdfinfo version 4.03 [www.xpdfreader.com]");
		lines.addOut("Copyright 1996-2021 Glyph & Cog, LLC");
		assertInfo(lines, new Version(4, 3));
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

	private void assertInfo(Lines lines, Version expectedVersion) throws IOException {
		Parser parser = newParser();
		parser.read(lines);
		SoftFound softFound = parser.closeAndParse("", 0, lines);
		VersionSoftInfo softInfo = (VersionSoftInfo)softFound.getSoftInfo();
		assertEquals(expectedVersion, softInfo.getVersion()
				.map(v -> v.cut(VersionUnit.parse(expectedVersion.size() - 1)))
				.orElse(null));
	}

}
