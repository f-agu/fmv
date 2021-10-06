package org.fagu.fmv.soft._7z;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.soft.ExecuteDelegateRepository;
import org.fagu.fmv.soft.LogExecuteDelegate;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.version.Version;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 20 sept. 2019 10:07:44
 */
class _7zSoftProviderTestCase {

	@Test
	@Disabled
	void testSearch() {
		ExecuteDelegateRepository.set(new LogExecuteDelegate(System.out::println));
		Soft s = _7z.search();
		System.out.println(s);
		s.getFounds().getFounds().forEach(sf -> {
			System.out.println(sf);
		});
	}

	@Test
	void test1() throws IOException {
		Parser parser = newParser();
		parser.readLine("");
		parser.readLine("7-Zip [64] 16.00 : Copyright (c) 1999-2016 Igor Pavlov : 2016-05-10");
		assertInfo(parser, new Version(16, 0));
	}

	@Test
	void test2() throws IOException {
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
