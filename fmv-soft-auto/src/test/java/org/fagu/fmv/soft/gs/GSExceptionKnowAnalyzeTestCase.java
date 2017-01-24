package org.fagu.fmv.soft.gs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import org.fagu.fmv.soft.FMVExecuteException;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftTestCase;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 24 janv. 2017 13:23:07
 */
public class GSExceptionKnowAnalyzeTestCase {

	/**
	 * 
	 */
	public GSExceptionKnowAnalyzeTestCase() {}

	/**
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void testMerge() throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "gs-merge-test");
		FileUtils.deleteDirectory(folder);
		folder.mkdirs();
		try {
			try {
				Soft gsSoft = GS.search();
				File viewJpegPSFile = new File(new File(gsSoft.getFile().getParentFile().getParentFile(), "lib"), "viewjpeg.ps");

				File srcFile = extractResource(folder, "cheese.zip");
				File outFile = new File(srcFile.getPath() + ".pdf");

				List<String> parameters = new ArrayList<>();
				parameters.add("-sDEVICE=pdfwrite");
				parameters.add("-dPDFSETTINGS=/prepress");
				parameters.add("-o");
				parameters.add(outFile.getAbsolutePath());
				parameters.add(viewJpegPSFile.getAbsolutePath());
				parameters.add("-c");
				parameters.add("(" + srcFile.getName() + ") viewJPEG showpage");

				gsSoft.withParameters(parameters)
						.customizeExecutor(exec -> exec.setWorkingDirectory(srcFile.getParentFile()))
						.logCommandLine(l -> {
							System.out.println(l);
						})
						.execute();
			} catch(FMVExecuteException e) {
				if(e.isKnown()) {
					assertEquals("ddd", e.getExceptionKnown().toString());
				} else {
					throw e;
				}
			}
		} finally {
			FileUtils.deleteDirectory(folder);
		}

	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testPDFToImage() throws IOException {
		runPdfToImage(null, "Permission denied");
		runPdfToImage("cheese.zip", "Undefined format");
		runPdfToImage("mp4.mp4", "Undefined format");
	}

	// *************************************

	/**
	 * @param srcResource
	 * @throws IOException
	 */
	private void runPdfToImage(String srcResource, String expectedMessage) throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "gs-pdf2img-test");
		try {
			FileUtils.deleteDirectory(folder);
			folder.mkdirs();
			File srcFile = srcResource != null ? extractResource(folder, srcResource) : folder;
			File outFile = new File(srcFile.getPath() + ".jpg");
			try {
				Soft gsSoft = GS.search();

				List<String> list = new ArrayList<>();
				list.add("-sDEVICE=png16m");
				list.add("-sOutputFile=" + outFile.getPath());
				list.add("-r200");
				list.add("-dNOPAUSE");
				list.add("-dBATCH");
				list.add("-dSAFER");
				list.add("-dFirstPage=1");
				list.add("-dLastPage=1");
				list.add("-dUseCropBox");
				list.add(srcFile.getAbsolutePath());

				gsSoft.withParameters(list)
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
