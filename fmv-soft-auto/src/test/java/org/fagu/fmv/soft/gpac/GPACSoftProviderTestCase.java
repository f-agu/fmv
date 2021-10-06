package org.fagu.fmv.soft.gpac;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.soft.ExecuteDelegateRepository;
import org.fagu.fmv.soft.LogExecuteDelegate;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ParserFactory;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.utils.ImmutableProperties;
import org.fagu.version.Version;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 19 juin 2019 17:19:23
 */
class GPACSoftProviderTestCase {

	@Test
	@Disabled
	void testSearchMP4Box() {
		ExecuteDelegateRepository.set(new LogExecuteDelegate(System.out::println));
		Soft soft = MP4Box.search();
		System.out.println(soft.isFound());
		System.out.println(soft.getFile());
	}

	@Test
	@Disabled
	void testSearchMP42TS() {
		Soft soft = MP42TS.search();
		System.out.println(soft.isFound());
		System.out.println(soft.getFile());
	}

	@Test
	void testParseMP4Box() throws IOException {
		Parser parser = newParserMP4Box();
		parser.readLine("MP4Box - GPAC version 0.7.2-DEV-rev1167-g10c1f03b-master");
		parser.readLine("(c) Telecom ParisTech 2000-2018 - Licence LGPL v2");
		parser.readLine("GPAC Configuration: (static configuration file)");
		parser.readLine("Features: GPAC_64_BITS GPAC_MEMORY_TRACKING GPAC_HAS_SSL GPAC_HAS_SPIDERMONKEY GPAC_HAS_JPEG GPAC_HAS_PNG");
		assertInfo(parser, new Version(0, 7, 2));
	}

	// *******************************************************

	private Parser newParserMP4Box() {
		MP4BoxSoftProvider softProvider = new MP4BoxSoftProvider();
		ParserFactory parserFactory = ((ExecSoftFoundFactory)softProvider.createSoftFoundFactory(ImmutableProperties.of())).getParserFactory();
		return parserFactory.create(new File("."), softProvider.getSoftPolicy());
	}

	private void assertInfo(Parser parser, Version expectedVersion) throws IOException {
		SoftFound softFound = parser.closeAndParse("", 0);
		VersionSoftInfo softInfo = (VersionSoftInfo)softFound.getSoftInfo();
		assertEquals(expectedVersion, softInfo.getVersion().orElse(null));
	}

}
