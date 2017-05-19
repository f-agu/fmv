package org.fagu.fmv.soft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;


/**
 * @author fagu
 */
public class Resource {

	/**
	 * 
	 */
	private Resource() {}

	/**
	 * @return
	 * @throws IOException
	 */
	public static File tmpFolder() throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "gs-test");
		FileUtils.deleteDirectory(folder);
		folder.mkdirs();
		return folder;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public static File extract_Pdf_Pdf() throws IOException {
		return extract("pdf.pdf");
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public static File extract_Salut_Pdf() throws IOException {
		return extract("salut.pdf");
	}

	/**
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static File extract(String resource) throws IOException {
		return extract(tmpFolder(), resource);
	}

	/**
	 * @param tmpFolder
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static File extract(File tmpFolder, String resource) throws IOException {
		File file = File.createTempFile(FilenameUtils.getBaseName(resource), "." + FilenameUtils.getExtension(resource), tmpFolder);
		try (InputStream inputStream = open(resource);
				OutputStream outputStream = new FileOutputStream(file)) {
			IOUtils.copy(inputStream, outputStream);
		}
		return file;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public static InputStream open_Salut_Pdf() throws IOException {
		return open("salut.pdf");
	}

	/**
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static InputStream open(String resource) throws IOException {
		return Resource.class.getResourceAsStream(resource);
	}

}
