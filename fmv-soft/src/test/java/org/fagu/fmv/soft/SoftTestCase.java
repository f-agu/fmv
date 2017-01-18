package org.fagu.fmv.soft;

/*
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.junit.Test;


/**
 * @author f.agu
 */
public class SoftTestCase {

	/**
	 *
	 */
	public SoftTestCase() {}

	@Test
	// @Ignore
	public void test1() throws Exception {
		SoftFoundFactory ffSoftFoundFactory = ExecSoftFoundFactory.withParameters("-version").parseFactory(file -> new Parser() {

			private Integer build;

			@Override
			public void readLine(String line) {
				if(line.startsWith("ff")) {
					build = Integer.parseInt(StringUtils.substringBetween(line, "N-", "-"));
				}
			}

			@Override
			public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
				if(build == null) {
					return SoftFound.foundBadSoft(file);
				}
				// return SoftFound.found(file, new TestSoftInfo(build));
				return SoftFound.foundBadVersion(new TestSoftInfo(49), "85");
			}
		}).build();

		// SoftFoundFactory identifyFoundFactory = ExecSoftFoundFactory.withParameters("-version").parseFactory(file ->
		// new Parser() {
		//
		// @Override
		// public void readLine(String line) {}
		//
		// @Override
		// public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
		// return SoftFound.foundBadVersion(new TestSoftInfo(49), "85");
		// }
		// }).build();

		Soft soft = Soft.with(new TestSoftName("ffprout")).search(ffSoftFoundFactory);
		// Soft ffprobeSoft = Soft.with("ffprobe").search(ffSoftFoundFactory);
		// Soft identifySoft = Soft.withName("identify").search(identifyFoundFactory);

		SoftLogger softFormatter = new SoftLogger(Arrays.asList(soft));
		// SoftLogger softFormatter = new SoftLogger(Arrays.asList(ffprobeSoft, identifySoft, ffmpegSoft));
		softFormatter.logDetails(System.out::println);
		// System.out.println(soft.getFounds());
		// System.out.println(soft.getFile());
		// soft.withParameters("").execute();
	}

}
