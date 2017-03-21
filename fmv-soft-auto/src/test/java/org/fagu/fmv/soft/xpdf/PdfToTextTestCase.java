package org.fagu.fmv.soft.xpdf;

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
import org.fagu.fmv.soft.SoftTestCase;
import org.junit.Test;


/**
 * @author f.agu
 * @created 21 mars 2017 11:07:39
 */
public class PdfToTextTestCase {

	/**
	 * 
	 */
	public PdfToTextTestCase() {}

	@Test
	public void test() throws IOException {
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
					// .addCommonReadLine(System.out::println)
					.execute();
			assertEquals(1, output.size());
			assertEquals("Salut, E\u00E9\u00E9\u00E9 de test", output.get(0));
			System.out.println((int)'é');
		} finally {
			FileUtils.deleteDirectory(folder);
		}
	}

	// ******************************************

	/**
	 * @return
	 */
	private List<String> getParameters(String... parameters) {
		List<String> params = new ArrayList<>(4);
		if(SystemUtils.IS_OS_WINDOWS) {
			params.add("-enc");
			params.add("UTF-8");
		}
		for(String param : parameters) {
			params.add(param);
		}
		return params;
	}

	/**
	 * @param tmpFolder
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	private File extractResource(File tmpFolder, String resource) throws IOException {
		File file = File.createTempFile(FilenameUtils.getBaseName(resource), "." + FilenameUtils.getExtension(resource), tmpFolder);
		try (InputStream inputStream = SoftTestCase.class.getResourceAsStream(resource);
				OutputStream outputStream = new FileOutputStream(file)) {
			IOUtils.copy(inputStream, outputStream);
		}
		return file;
	}

}
