package org.fagu.fmv.soft.xpdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.fagu.fmv.soft.FMVExecuteException;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftTestCase;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 24 janv. 2017 13:23:07
 */
public class XpdfExceptionKnowAnalyzeTestCase {

	/**
	 * 
	 */
	public XpdfExceptionKnowAnalyzeTestCase() {}

	/**
	 * @throws IOException
	 */
	@Test
	public void testInfo() throws IOException {
		extract(null, "Couldn't open file");
		extract("cheese.zip", "May not be a PDF file");
		extract("mp4.mp4", "May not be a PDF file");
	}

	// *************************************

	/**
	 * @param srcResource
	 * @throws IOException
	 */
	private void extract(String srcResource, String expectedMessage) throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "xpdf-info-test");
		try {
			FileUtils.deleteDirectory(folder);
			folder.mkdirs();
			File srcFile = srcResource != null ? extractResource(folder, srcResource) : folder;
			try {
				Soft pdfInfoSoft = PdfInfo.search();
				pdfInfoSoft.withParameters(srcFile.getAbsolutePath())
						.customizeExecutor(exec -> exec.setWorkingDirectory(srcFile.getParentFile()))
						.execute();
				fail();
			} catch(FMVExecuteException e) {
				if(e.isKnown()) {
					assertEquals(expectedMessage, e.getExceptionKnown().toString());
				} else {
					throw e;
				}
			}
		} finally {
			FileUtils.deleteDirectory(folder);
		}
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
