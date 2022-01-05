package org.fagu.fmv.soft.mediainfo;

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

import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ParserFactory;
import org.fagu.fmv.soft.find.Lines;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.utils.ImmutableProperties;
import org.fagu.version.Version;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class MediaInfoProviderTestCase {

	@Test
	void testParse() throws IOException {
		Lines lines = new Lines();
		lines.addOut("MediaInfo Command line,");
		lines.addOut("MediaInfoLib - v18.03.1");
		assertInfo(lines, new Version(18, 3, 1));
	}

	// *******************************************************

	private Parser newParser() {
		MediaInfoSoftProvider softProvider = new MediaInfoSoftProvider();
		ParserFactory parserFactory = ((ExecSoftFoundFactory)softProvider.createSoftFoundFactory(ImmutableProperties.of())).getParserFactory();
		return parserFactory.create(new File("."), softProvider.getSoftPolicy());
	}

	private void assertInfo(Lines lines, Version expectedVersion) throws IOException {
		Parser parser = newParser();
		parser.read(lines);
		SoftFound softFound = parser.closeAndParse("", 0, lines);
		VersionSoftInfo softInfo = (VersionSoftInfo)softFound.getSoftInfo();
		assertEquals(expectedVersion, softInfo.getVersion().orElse(null));
	}

}
