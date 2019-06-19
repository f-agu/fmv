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

	private static final String PDF_PDF = "pdf.pdf";

	private static final String PDF_SALUT = "salut.pdf";

	private static final String PDF_KENWOOD = "kenwood.pdf";

	private Resource() {}

	public static File tmpFolder() throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "gs-test");
		FileUtils.deleteDirectory(folder);
		folder.mkdirs();
		return folder;
	}

	public static File extract_Pdf_Pdf() throws IOException {
		return extract(PDF_PDF);
	}

	public static File extract_Salut_Pdf() throws IOException {
		return extract(PDF_SALUT);
	}

	public static File extract_kenwood_Pdf() throws IOException {
		return extract(PDF_KENWOOD);
	}

	public static File extract(String resource) throws IOException {
		return extract(tmpFolder(), resource);
	}

	public static File extract(File tmpFolder, String resource) throws IOException {
		File file = File.createTempFile(FilenameUtils.getBaseName(resource), "." + FilenameUtils.getExtension(resource), tmpFolder);
		try (InputStream inputStream = open(resource);
				OutputStream outputStream = new FileOutputStream(file)) {
			IOUtils.copy(inputStream, outputStream);
		}
		return file;
	}

	public static InputStream open_Salut_Pdf() throws IOException {
		return open(PDF_SALUT);
	}

	public static InputStream open_kenwood_Pdf() throws IOException {
		return open(PDF_KENWOOD);
	}

	public static InputStream open(String resource) throws IOException {
		return Resource.class.getResourceAsStream(resource);
	}

}
