package org.fagu.fmv.soft.xpdf;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.ExecuteDelegateRepository;
import org.fagu.fmv.soft.LogExecuteDelegate;
import org.fagu.fmv.soft.SoftTestCase;
import org.junit.Test;


/**
 * @author f.agu
 */
public class PdfToTextTestCase {

	@Test
	public void testSearch() {
		ExecuteDelegateRepository.set(new LogExecuteDelegate(System.out::println));
		PdfToText.search();
	}

	@Test
	public void testExtractText() throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "xpdf-pdftotext");
		try {
			FileUtils.deleteDirectory(folder);
			folder.mkdirs();
			File srcFile = extractResource(folder, "salut.pdf");

			List<String> output = new ArrayList<>();
			PdfToText.search()
					.withParameters(getParameters(srcFile.getPath(), "-"))
					// .logCommandLine(System.out::println)
					.addCommonReadLine(output::add)
					.addCommonReadLine(System.out::println)
					.execute();
			assertEquals(1, output.size());
			// System.out.println(output.get(0));
			assertEquals("Salut, E\u00E9\u00E9\u00E9 de test", output.get(0));
		} finally {
			FileUtils.deleteDirectory(folder);
		}
	}

	// ******************************************

	private List<String> getParameters(String... parameters) {
		List<String> params = new ArrayList<>(4);
		if(SystemUtils.IS_OS_WINDOWS) {
			// params.add("-enc");
			// params.add("UTF-8");
		}
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
