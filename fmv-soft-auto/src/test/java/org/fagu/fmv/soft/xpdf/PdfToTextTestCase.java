package org.fagu.fmv.soft.xpdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftTestCase;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class PdfToTextTestCase {

	@Test
	// @Disabled
	void testSearch() {
		// ExecuteDelegateRepository.set(new LogExecuteDelegate(System.out::println));
		Soft soft = PdfToText.search();
		soft.getFounds().getFounds().forEach(softFound -> {
			System.out.println(softFound.getFile() + "   " + ((XPdfVersionSoftInfo)softFound.getSoftInfo()).getProvider());
		});
		XPdfVersionSoftInfo softInfo = (XPdfVersionSoftInfo)soft.getFirstInfo();
		System.out.println(softInfo.getProvider());
	}

	@Test
	void testExtractText() throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "xpdf-pdftotext");
		try {
			FileUtils.deleteDirectory(folder);
			folder.mkdirs();
			File srcFile = extractResource(folder, "salut.pdf");

			List<String> output = new ArrayList<>();
			Soft soft = PdfToText.search();
			soft
					.withParameters(getParameters(soft, srcFile.getPath(), "-"))
					.logCommandLine(System.out::println)
					.addCommonReadLine(output::add)
					.charset(StandardCharsets.UTF_8)
					// .addCommonReadLine(System.out::println)
					.execute();
			// output.forEach(l -> System.out.println("[" + l + "]"));
			int size = output.size();

			if(size == 1) { // provider = XPDF
				assertEquals("Salut, E\u00E9\u00E9\u00E9 de test", output.get(0));

			} else if(size == 3) { // provider = POPPLER
				String s = output.get(2);
				assertEquals("Salut,", output.get(0));
				assertEquals("", output.get(1));
				assertEquals("E\u00E9\u00E9\u00E9 de test", s);

			} else {
				fail("Output size should be 1 or 3: " + size);
			}

		} finally {
			FileUtils.deleteDirectory(folder);
		}
	}

	// ******************************************

	private List<String> getParameters(Soft soft, String... parameters) {
		XPdfVersionSoftInfo softInfo = (XPdfVersionSoftInfo)soft.getFirstFound().getSoftInfo();
		List<String> params = new ArrayList<>(4);
		params.addAll(softInfo.getProvider().getDefaultOptionParameters());
		for(String param : parameters) {
			params.add(param);
		}
		return params;
	}

	private File extractResource(File tmpFolder, String resource) throws IOException {
		File file = File.createTempFile(FilenameUtils.getBaseName(resource), "." + FilenameUtils.getExtension(resource), tmpFolder);
		try (InputStream inputStream = SoftTestCase.class.getResourceAsStream(resource);
				OutputStream outputStream = new FileOutputStream(file)) {
			IOUtils.copy(inputStream, outputStream);
		}
		return file;
	}

}
