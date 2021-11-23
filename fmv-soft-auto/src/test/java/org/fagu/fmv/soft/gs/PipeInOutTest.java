package org.fagu.fmv.soft.gs;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fagu.fmv.soft.Resource;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.exec.exception.FMVExecuteException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author Utilisateur
 * @created 13 avr. 2018 16:37:32
 */
class PipeInOutTest {

	/**
	 * https://stackoverflow.com/questions/23909077/count-pdf-pages-from-stdin-with-ghostscript-postscript
	 * 
	 * You cannot work with PDF files from stdin, as the PDF format makes it more or less essential to be able to have
	 * random access to all parts of the file.
	 * 
	 * In the cases where Ghostscript reads a PDF file from stdin it first copies it to a local file then works on that,
	 * so it isn't working from stdin anyway.
	 * 
	 * In short, this can't be done.
	 * 
	 * @throws IOException
	 */
	@Test
	@Disabled
	void testIn() throws IOException {
		File folder = Files.createTempDirectory("gs-pipe-in").toFile();
		Soft gsSoft = GS.search();
		try {
			File srcFile = Resource.extract(folder, "salut.pdf");
			File viewJpegPSFile = new File(new File(gsSoft.getFile().getParentFile().getParentFile(), "lib"), "viewjpeg.ps");
			File outFile = new File(srcFile.getPath() + ".pdf");

			List<String> parameters = new ArrayList<>();
			parameters.add("-sDEVICE=pdfwrite");
			parameters.add("-dPDFSETTINGS=/prepress");
			parameters.add("-o");
			parameters.add(outFile.getAbsolutePath());
			parameters.add(viewJpegPSFile.getAbsolutePath());
			parameters.add("-c");
			parameters.add("(-) viewJPEG showpage");

			try (InputStream inputStream = new FileInputStream(srcFile)) {
				gsSoft.withParameters(parameters)
						.workingDirectory(srcFile.getParentFile())
						.logCommandLine(System.out::println)
						.input(inputStream)
						.execute();
			} catch(FMVExecuteException e) {
				if(e.isKnown()) {
					assertEquals("ddd", e.getExceptionKnown().toString());
				} else {
					throw e;
				}
			}
		} finally {
			FileUtils.deleteQuietly(folder);
		}
	}

	@Test
	@Disabled
	void testOut() throws IOException {
		File folder = Files.createTempDirectory("gs-pipe-out").toFile();
		Soft gsSoft = GS.search();
		try {
			File srcFile = Resource.extract(folder, "salut.pdf");
			File viewJpegPSFile = new File(new File(gsSoft.getFile().getParentFile().getParentFile(), "lib"), "viewjpeg.ps");
			File outFile = new File(srcFile.getPath() + ".pdf");

			List<String> parameters = new ArrayList<>();
			parameters.add("-sDEVICE=pdfwrite");
			parameters.add("-dPDFSETTINGS=/prepress");
			parameters.add("-sOutputFile=%stdout");
			// parameters.add(viewJpegPSFile.getAbsolutePath());
			parameters.add("-c");
			parameters.add("(" + srcFile.getName() + ") viewJPEG showpage");

			try (OutputStream outputStream = new FileOutputStream(outFile)) {
				gsSoft.withParameters(parameters)
						.workingDirectory(srcFile.getParentFile())
						.logCommandLine(System.out::println)
						.output(outputStream)
						.execute();
			} catch(FMVExecuteException e) {
				// if(e.isKnown()) {
				// assertEquals("ddd", e.getExceptionKnown().toString());
				// } else {
				throw e;
				// }
			}
		} finally {
			FileUtils.deleteQuietly(folder);
		}
	}
	// -sOutputFile=%stdout

}
