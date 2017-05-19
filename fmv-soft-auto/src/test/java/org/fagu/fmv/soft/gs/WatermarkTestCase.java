package org.fagu.fmv.soft.gs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.soft.Resource;
import org.junit.Test;


/**
 * @author fagu
 */
public class WatermarkTestCase {

	@Test
	public void testWatermark_File2File() throws IOException {
		File srcFile = Resource.extract_Pdf_Pdf();
		String name = srcFile.getName();
		File destFile = new File(srcFile.getParentFile(), FilenameUtils.getBaseName(name) + "-watermarked.pdf");
		System.out.println(destFile);
		try {
			GS.search()
					.withParameters(getParameters("été ici et là", srcFile.getPath(), destFile.getPath()))
					.execute();
		} finally {
			srcFile.delete();
		}
	}

	@Test
	public void testWatermark_InputStream2File() throws IOException {
		File destFile = new File(Resource.tmpFolder(), "inputstream-watermarked.pdf");
		System.out.println(destFile);
		try (InputStream inputStream = Resource.open_Salut_Pdf()) {

			GS.search()
					.withParameters(getParameters("été ici et là", "-_", destFile.getPath()))
					.input(inputStream)
					.execute();

		}
	}

	@Test
	public void testWatermark_InputStream2OutputStream() throws IOException {
		String text = "\u00E9té ici 4 et là";

		File destFile = new File(Resource.tmpFolder(), "inputstream-outputstream-watermarked.pdf");
		System.out.println(destFile);
		// try (InputStream inputStream = Resource.open_Salut_Pdf();
		try (InputStream inputStream = new FileInputStream("D:\\tmp\\nexity\\signed\\a.pdf");
				OutputStream outputStream = new FileOutputStream(destFile)) {

			GS.search()
					.withParameters(getParameters(text, "-_", "-"))
					.input(inputStream)
					.out(outputStream)
					.execute();

		}
	}

	// **********************************************

	/**
	 * @param watermarckText
	 * @param input
	 * @param output
	 * @return
	 */
	private List<String> getParameters(String watermarckText, String input, String output) {
		List<String> params = new ArrayList<>();
		params.add("-dBATCH");
		params.add("-dNOPAUSE");
		params.add("-dNOPAUSE");
		params.add("-sDEVICE=pdfwrite");
		params.add("-sOutputFile=" + output);
		params.add("-c");
		params.add(getWatermarkScript(watermarckText));
		params.add("-f");
		params.add(input);
		return params;
	}

	/**
	 * @param watermarckText
	 * @return
	 */
	private String getWatermarkScript(String watermarckText) {
		StringWriter stringWriter = new StringWriter();
		try (PrintWriter writer = new PrintWriter(stringWriter)) {
			writer.println("/watermarkText { (" + watermarckText + ") } def");
			writer.println("/watermarkFont { /Helvetica-Bold 72 selectfont } def");
			writer.println("/watermarkColor { .75 setgray } def");
			writer.println("/watermarkAngle { 45 } def");
			writer.println("");
			writer.println("/pageWidth { currentpagedevice /PageSize get 0 get } def");
			writer.println("/pageHeight { currentpagedevice /PageSize get 1 get } def");
			writer.println("");
			writer.println("<<");
			writer.println(" /EndPage {");
			writer.println(" 2 eq { pop false }");
			writer.println("  {");
			writer.println("   gsave");
			writer.println("   watermarkFont");
			writer.println("   watermarkColor");
			writer.println("   pageWidth .5 mul pageHeight .5 mul translate");
			writer.println("   0 0 moveto");
			writer.println("   watermarkText false charpath flattenpath pathbbox");
			writer.println(" 4 2 roll pop pop");
			writer.println(" 0 0 moveto");
			writer.println("   watermarkAngle rotate");
			writer.println("   -.5 mul exch -.5 mul exch");
			writer.println(" rmoveto");
			writer.println(" watermarkText show");
			writer.println("   grestore");
			writer.println("   true ");
			writer.println("  } ifelse");
			writer.println(" } bind");
			writer.println(">> setpagedevice");
		}
		return stringWriter.toString();
	}

}
