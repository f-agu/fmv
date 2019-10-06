package org.fagu.fmv.soft.xpdf;

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
