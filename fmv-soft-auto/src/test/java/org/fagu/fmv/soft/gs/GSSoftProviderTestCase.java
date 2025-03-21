package org.fagu.fmv.soft.gs;

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

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ParserFactory;
import org.fagu.fmv.soft.find.Lines;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionDateSoftInfo;
import org.fagu.fmv.soft.utils.ImmutableProperties;
import org.fagu.version.Version;
import org.fagu.version.VersionUnit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class GSSoftProviderTestCase {

	@Test
	@Disabled
	void testSearch() {
		// ExecuteDelegateRepository.set(new LogExecuteDelegate(System.out::println));
		Soft soft = GS.search();
		System.out.println(soft);
	}

	@Test
	void testParseOnWindows() throws IOException {
		Lines lines = new Lines();
		lines.addOut("GPL Ghostscript 9.16 (2015-03-30)");
		lines.addOut("Copyright (C) 2015 Artifex Software, Inc.  All rights reserved.");
		assertInfo(lines, new Version(9, 16), d(2015, 03, 30));
	}

	@Test
	void testParseOnMac() throws IOException {
		Lines lines = new Lines();
		lines.addOut("GPL Ghostscript 9.18 (2015-10-05)");
		lines.addOut("Copyright (C) 2015 Artifex Software, Inc.  All rights reserved.");
		assertInfo(lines, new Version(9, 18), d(2015, 10, 05));
	}

	// *******************************************************

	private Parser newParser() {
		GSSoftProvider softProvider = new GSSoftProvider();
		ParserFactory parserFactory = ((ExecSoftFoundFactory)softProvider.createSoftFoundFactory(ImmutableProperties.of())).getParserFactory();
		return parserFactory.create(new File("."), softProvider.getSoftPolicy());
	}

	private Date d(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private void assertInfo(Lines lines, Version expectedVersion, Date expectedDate) throws IOException {
		Parser parser = newParser();
		parser.read(lines);
		SoftFound softFound = parser.closeAndParse("", 0, lines);
		VersionDateSoftInfo softInfo = (VersionDateSoftInfo)softFound.getSoftInfo();
		assertEquals(expectedVersion, softInfo.getVersion()
				.map(v -> v.cut(VersionUnit.parse(expectedVersion.size() - 1)))
				.orElse(null));
		assertEquals(expectedDate, softInfo.getDate().orElse(null));
	}

}
