package org.fagu.fmv.soft.gs;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.fagu.fmv.soft.Resource;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.soft.exec.exception.FMVExecuteException;
import org.fagu.fmv.soft.exec.exception.NestedException;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class GSExceptionKnowAnalyzeTestCase {

	@Test
	// @Ignore
	public void testMerge() throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "gs-merge-test");
		FileUtils.deleteDirectory(folder);
		folder.mkdirs();
		try {
			try {
				Soft gsSoft = GS.search();
				File viewJpegPSFile = new File(new File(gsSoft.getFile().getParentFile().getParentFile(), "lib"), "viewjpeg.ps");

				File srcFile = Resource.extract(folder, "cheese.zip");
				File outFile = new File(srcFile.getPath() + ".pdf");

				List<String> parameters = new ArrayList<>();
				parameters.add("-sDEVICE=pdfwrite");
				parameters.add("-dPDFSETTINGS=/prepress");
				parameters.add("-o");
				parameters.add(outFile.getAbsolutePath());
				parameters.add(viewJpegPSFile.getAbsolutePath());
				parameters.add("-c");
				parameters.add("(" + srcFile.getName() + ") viewJPEG showpage");

				gsSoft.withParameters(parameters)
						.workingDirectory(srcFile.getParentFile())
						// .logCommandLine(System.out::println)
						.execute();
			} catch(FMVExecuteException e) {
				if(e.isKnown()) {
					assertEquals("Permission denied", e.getExceptionKnown().toString());
				} else {
					throw e;
				}
			}
		} finally {
			FileUtils.deleteDirectory(folder);
		}

	}

	/**
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void testPDFToImage() throws IOException {
		StringJoiner joiner = new StringJoiner(";");
		List<Consumer<SoftExecutor>> consumers = Arrays.asList(null, se -> se.ifExceptionIsKnownDo(ek -> ek.onMessage(joiner::add).doThrow()));
		// List<Consumer<SoftExecutor>> consumers = Arrays.asList(se -> se.ifExceptionIsKnownDo(ek -> {
		// System.out.println("================= " + ek.toString());
		// ek.getNestedException().messageToLines().forEach(System.out::println);
		// ek.onMessage(joiner::add).doThrow();
		// }));
		for(Consumer<SoftExecutor> consumer : consumers) {
			runPdfToImage(null, "Permission denied", consumer);
			runPdfToImage("cheese.zip", "Undefined format", consumer);
			runPdfToImage("mp4.mp4", "Undefined format", consumer);
		}
		assertEquals("Permission denied;Undefined format;Undefined format", joiner.toString());
	}

	// *************************************

	/**
	 * @param srcResource
	 * @param expectedMessage
	 * @param consumer
	 * @throws IOException
	 */
	private void runPdfToImage(String srcResource, String expectedMessage, Consumer<SoftExecutor> consumer) throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "gs-pdf2img-test");
		try {
			FileUtils.deleteDirectory(folder);
			folder.mkdirs();
			File srcFile = srcResource != null ? Resource.extract(folder, srcResource) : folder;
			File outFile = new File(srcFile.getPath() + ".jpg");
			try {
				Soft gsSoft = GS.search();

				List<String> list = new ArrayList<>();
				list.add("-sDEVICE=png16m");
				list.add("-sOutputFile=" + outFile.getPath());
				list.add("-r200");
				list.add("-dNOPAUSE");
				list.add("-dBATCH");
				list.add("-dSAFER");
				list.add("-dFirstPage=1");
				list.add("-dLastPage=1");
				list.add("-dUseCropBox");
				list.add(srcFile.getAbsolutePath());

				SoftExecutor customizeExecutor = gsSoft.withParameters(list)
						.workingDirectory(srcFile.getParentFile());
				// .logCommandLine(System.out::println);
				if(consumer != null) {
					consumer.accept(customizeExecutor);
				}
				customizeExecutor.execute();
				fail();
			} catch(FMVExecuteException e) {
				if(e.isKnown()) {
					assertEquals(expectedMessage, e.getExceptionKnown().toString());
				} else {
					new NestedException(e).messageToLines().forEach(System.out::println);
					throw e;
				}
			}
		} finally {
			FileUtils.deleteDirectory(folder);
		}
	}

}
