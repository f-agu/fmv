package org.fagu.fmv.soft;

import static org.junit.jupiter.api.Assertions.fail;

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
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.SoftExecutor.Executed;
import org.fagu.fmv.soft.exec.exception.FMVExecuteException;
import org.fagu.fmv.soft.exec.exception.TimeoutExecuteException;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.Lines;
import org.fagu.fmv.soft.find.Locators;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SysoutCmdLineFMVExecListener;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;


/**
 * @author f.agu
 */
class SoftTestCase {

	@Test
	@Disabled
	void test1() throws Exception {
		SoftFoundFactory ffSoftFoundFactory = ExecSoftFoundFactory.forProvider(new TestSoftProvider("ffprout"))
				.withParameters("-version")
				.parseFactory((file, softPolicy) -> new Parser() {

					private Integer build;

					@Override
					public void readLine(String line) {
						if(line.startsWith("ff")) {
							build = Integer.parseInt(StringUtils.substringBetween(line, "N-", "-"));
						}
					}

					@Override
					public SoftFound closeAndParse(String cmdLineStr, int exitValue, Lines lines) throws IOException {
						if(build == null) {
							return SoftFound.foundBadSoft(file);
						}
						// return SoftFound.found(file, new TestSoftInfo(build));
						return SoftFound.foundBadVersion(new TestSoftInfo(49), "85");
					}
				})
				.build();

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

		Soft soft = Soft.with(new TestSoftProvider("ffprout")).search(ffSoftFoundFactory);
		// Soft ffprobeSoft = Soft.with("ffprobe").search(ffSoftFoundFactory);
		// Soft identifySoft = Soft.withName("identify").search(identifyFoundFactory);

		SoftLogger softChecker = new SoftLogger(Collections.singletonList(soft));

		// SoftLogger softFormatter = new SoftLogger(Arrays.asList(ffprobeSoft, identifySoft, ffmpegSoft));
		softChecker.logDetails(System.out::println);
		// System.out.println(soft.getFounds());
		// System.out.println(soft.getFile());
		soft.withParameters("")
				.execute();

	}

	@Test
	@Disabled
	void testTimeout() throws Exception {
		TestSoftProvider softProvider = new TestSoftProvider("pause") {

			@Override
			public SoftLocator getSoftLocator() {
				SoftLocator softLocator = super.getSoftLocator();
				Locators locators = softLocator.createLocators();
				softLocator.addDefaultLocator();
				softLocator.addLocator(locators.byPath("c:\\tmp"));
				return softLocator;
			}

		};
		SoftFoundFactory ffSoftFoundFactory = ExecSoftFoundFactory.forProvider(softProvider)
				// .withParameters("/?")
				.withoutParameter()
				.timeOut(1_000)
				.customizeExecutor(e -> e.addListener(new SysoutCmdLineFMVExecListener()))
				.parseFactory((file, softPolicy) -> new Parser() {

					@Override
					public void readLine(String line) { // ignore
					}

					@Override
					public SoftFound closeAndParse(String cmdLineStr, int exitValue, Lines lines) throws IOException {
						throw new RuntimeException();
					}
				})
				.build();

		Soft soft = Soft.with(softProvider).search(ffSoftFoundFactory);
		System.out.println(soft);
	}

	@Test
	@EnabledOnOs(OS.LINUX)
	void testTimeout2() throws Exception {
		Soft soft = Soft.withExecFile("timeout");
		System.out.println(soft);
		System.out.println(soft.getFounds());
		System.out.println(1);
		soft.withParameters("10", "2>", "nul")
				.timeOut(2000)
				.input(System.in)
				.output(System.out)
				.err(System.out)
				.execute();
		System.out.println(2);
	}

	@Test
	@EnabledOnOs(OS.WINDOWS)
	void testTimeout3() throws Exception {
		Soft soft = Soft.withExecFileBuilder("ping")
				.addSoftLocator(sl -> sl.addSearchListener(new LogSearchListener(System.out::println)))
				.build()
				.getSoft();

		// System.out.println(soft);
		// System.out.println(soft.getFounds());
		// System.out.println("executing...");

		try {
			soft.withParameters("-t", "127.0.0.1")
					.timeOut(2_000)
					.input(System.in)
					.output(System.out)
					.err(System.out)
					.execute();
		} catch(FMVExecuteException e) {
			if(e.getCause() instanceof TimeoutExecuteException) {
				return;
			}
			fail("Cause not TimeoutExecuteException: " + e.getCause());
		}
		fail("TimeoutExecuteException not throw");
	}

	@Test
	@Disabled
	void test2() throws Exception {
		Soft soft = Soft.withExecFile("cmd");
		System.out.println(soft);
		System.out.println(soft.getFounds());
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		System.out.println(1);
		soft.withoutParameter()
				.ifExceptionDo(e -> {
					System.out.println("error exec");
				})
				.execute();
		System.out.println(2);
		Future<Executed> future = soft.withoutParameter()
				.ifExceptionDo(e -> {
					System.out.println("error exec bg");
				})
				.executeInBackground(executorService);
		System.out.println(new Date());
		System.out.println(future.get());
	}

}
