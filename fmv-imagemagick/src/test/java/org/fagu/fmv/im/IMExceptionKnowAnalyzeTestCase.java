package org.fagu.fmv.im;

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
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 24 janv. 2017 14:39:11
 */
public class IMExceptionKnowAnalyzeTestCase {

	/**
	 * 
	 */
	public IMExceptionKnowAnalyzeTestCase() {}

	/**
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		extractMetadatas(null, "identify: no decode delegate for this image format ''");
		extractMetadatas("cheese.zip", "identify: no decode delegate for this image format 'ZIP'");
	}

	// ****************************************************************

	/**
	 * @param resource
	 * @param expectedMessage
	 * @throws IOException
	 */
	private void extractMetadatas(String resource, String expectedMessage) throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "im-extractmetadatas-test");
		try {
			FileUtils.deleteDirectory(folder);
			folder.mkdirs();
			File file = resource != null ? extractResource(folder, resource) : folder;
			try {
				ImageMetadatas extract = ImageMetadatas.with(file).extract();
				fail(extract.toJSON());
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
		try (InputStream inputStream = Image.class.getResourceAsStream(resource);
				OutputStream outputStream = new FileOutputStream(file)) {
			IOUtils.copy(inputStream, outputStream);
		}
		return file;
	}
}
