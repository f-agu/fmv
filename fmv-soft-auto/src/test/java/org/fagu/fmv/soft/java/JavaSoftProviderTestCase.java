package org.fagu.fmv.soft.java;

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

import org.fagu.fmv.soft.ExecuteDelegateRepository;
import org.fagu.fmv.soft.LogExecuteDelegate;
import org.fagu.fmv.soft.Soft;
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
class JavaSoftProviderTestCase {

	@Test
	void testSearch() {
		ExecuteDelegateRepository.set(new LogExecuteDelegate(System.out::println));
		Java.search();
	}

	@Test
	void testParse18OnWindows() throws IOException {
		Parser parser = newParser();
		parser.readLine("java version \"1.8.0_20\"");
		parser.readLine("Java(TM) SE Runtime Environment (build 1.8.0_20-b26)");
		parser.readLine("Java HotSpot(TM) 64-Bit Server VM (build 25.20-b23, mixed mode)");
		assertInfo(parser, new Version(1, 8, 0, 20), null);
	}

	@Test
	void testParse18OnLinux() throws IOException {
		Parser parser = newParser();
		parser.readLine("openjdk version \"1.8.0_45\"");
		parser.readLine("OpenJDK Runtime Environment (build 1.8.0_45-b13)");
		parser.readLine("OpenJDK 64-Bit Server VM (build 25.45-b02, mixed mode)");
		assertInfo(parser, new Version(1, 8, 0, 45), null);
	}

	@Test
	void testParse18_262OnLinux() throws IOException {
		Parser parser = newParser();
		parser.readLine("openjdk version \"1.8.0_262\"");
		parser.readLine("OpenJDK Runtime Environment (build 1.8.0_262-b10)");
		parser.readLine("OpenJDK 64-Bit Server VM (build 25.262-b10, mixed mode)");
		assertInfo(parser, new Version(1, 8, 0, 262), null);
	}

	@Test
	void testParseOpenJDK9Internal() throws IOException {
		Parser parser = newParser();
		parser.readLine("openjdk version \"9-internal\"");
		parser.readLine("OpenJDK Runtime Environment (build 9-internal+0-2016-04-14-195246.buildd.src)");
		parser.readLine("OpenJDK 64-Bit Server VM (build 9-internal+0-2016-04-14-195246.buildd.src, mixed mode)");
		assertInfo(parser, new Version(9), null);
	}

	@Test
	void testParseOpenJDK1106() throws IOException {
		Parser parser = newParser();
		parser.readLine("openjdk version \"11.0.6\" 2020-01-14");
		parser.readLine("OpenJDK Runtime Environment (build 11.0.6+10-post-Ubuntu-1ubuntu118.04.1)");
		parser.readLine("OpenJDK 64-Bit Server VM (build 11.0.6+10-post-Ubuntu-1ubuntu118.04.1, mixed mode, sharing)");
		assertInfo(parser, new Version(11, 0, 6), LocalDate.of(2020, 1, 14));
	}

	@Test
	void testParseJava1302() throws IOException {
		Parser parser = newParser();
		parser.readLine("java version \"13.0.2\" 2020-01-14");
		parser.readLine("Java(TM) SE Runtime Environment (build 13.0.2+8)");
		parser.readLine("Java HotSpot(TM) 64-Bit Server VM (build 13.0.2+8, mixed mode, sharing)");
		assertInfo(parser, new Version(13, 0, 2), LocalDate.of(2020, 1, 14));
	}

	@Test
	void testParseJava1402() throws IOException {
		Parser parser = newParser();
		parser.readLine("java version \"14.0.2\" 2020-07-14");
		parser.readLine("Java(TM) SE Runtime Environment (build 14.0.2+12-46)");
		parser.readLine("Java HotSpot(TM) 64-Bit Server VM (build 14.0.2+12-46, mixed mode, sharing)");
		assertInfo(parser, new Version(14, 0, 2), LocalDate.of(2020, 7, 14));
	}

	@Test
	@Disabled
	void testFind() {
		Soft java = Java.search();
		for(SoftFound softFound : java.getFounds()) {
			System.out.println(softFound);
		}
		System.out.println();
		System.out.println(java);
	}

	// *******************************************************

	private Parser newParser() {
		JavaSoftProvider softProvider = new JavaSoftProvider();
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
