package org.fagu.fmv.soft.java;

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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ParserFactory;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.utils.ImmutableProperties;
import org.fagu.version.Version;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class JavaSoftProviderTestCase {

	/**
	 * @throws IOException
	 */
	@Test
	public void testParse18OnWindows() throws IOException {
		Parser parser = newParser();
		parser.readLine("java version \"1.8.0_20\"");
		parser.readLine("Java(TM) SE Runtime Environment (build 1.8.0_20-b26)");
		parser.readLine("Java HotSpot(TM) 64-Bit Server VM (build 25.20-b23, mixed mode)");
		assertInfo(parser, new Version(1, 8, 0, 20));
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testParse18OnLinux() throws IOException {
		Parser parser = newParser();
		parser.readLine("openjdk version \"1.8.0_45\"");
		parser.readLine("OpenJDK Runtime Environment (build 1.8.0_45-b13)");
		parser.readLine("OpenJDK 64-Bit Server VM (build 25.45-b02, mixed mode)");
		assertInfo(parser, new Version(1, 8, 0, 45));
	}

	/**
	 * 
	 */
	@Test
	@Ignore
	public void testFind() {
		Soft java = Java.search();
		for(SoftFound softFound : java.getFounds()) {
			System.out.println(softFound);
		}
		System.out.println();
		System.out.println(java);
	}

	// *******************************************************

	/**
	 * @return
	 */
	private Parser newParser() {
		JavaSoftProvider softProvider = new JavaSoftProvider();
		ParserFactory parserFactory = ((ExecSoftFoundFactory)softProvider.createSoftFoundFactory(ImmutableProperties.of())).getParserFactory();
		return parserFactory.create(new File("."), softProvider.getSoftPolicy());
	}

	/**
	 * @param parser
	 * @param expectedVersion
	 * @throws IOException
	 */
	private void assertInfo(Parser parser, Version expectedVersion) throws IOException {
		SoftFound softFound = parser.closeAndParse("", 0);
		VersionSoftInfo softInfo = (VersionSoftInfo)softFound.getSoftInfo();
		assertEquals(expectedVersion, softInfo.getVersion().orElse(null));
	}

}
