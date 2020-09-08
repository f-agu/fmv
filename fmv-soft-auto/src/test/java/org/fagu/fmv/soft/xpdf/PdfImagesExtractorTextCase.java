package org.fagu.fmv.soft.xpdf;

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
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.junit.Test;


/**
 * @author fagu
 * @created 6 oct. 2019 16:25:05
 */
public class PdfImagesExtractorTextCase {

	@Test
	public void test() throws IOException {
		File inFile = null;
		List<File> extractedFiles = null;
		try {
			inFile = File.createTempFile(getClass().getSimpleName() + '-', ".pdf");
			try (InputStream inputStream = getClass().getResourceAsStream("example_041.pdf")) {
				Objects.requireNonNull(inputStream, "Resource not found");
				FileUtils.copyInputStreamToFile(inputStream, inFile);
			}

			PdfImagesExtractor pdfImagesExtractor = PdfImagesExtractor.create();
			extractedFiles = pdfImagesExtractor.extract(inFile);
			extractedFiles.forEach(System.out::println);
		} finally {
			if(inFile != null) {
				inFile.delete();
			}
			if(extractedFiles != null) {
				extractedFiles.forEach(File::delete);
			}
		}
	}
}
