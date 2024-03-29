package org.fagu.fmv.soft.gs;

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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author fagu
 */
@Disabled
class WatermarkTestCase {

	@Test
	void testWatermark_File2File() throws IOException {
		File srcFile = Resource.extract_Pdf_Pdf();
		String name = srcFile.getName();
		File destFile = new File(srcFile.getParentFile(), FilenameUtils.getBaseName(name) + "-watermarked.pdf");
		try {
			GS.search()
					.withParameters(getParameters("été ici et là", srcFile.getPath(), destFile.getPath()))
					.execute();
		} finally {
			srcFile.delete();
		}
	}

	@Test
	void testWatermark_InputStream2File() throws IOException {
		File destFile = new File(Resource.tmpFolder(), "inputstream-watermarked.pdf");
		try (InputStream inputStream = Resource.open_Salut_Pdf()) {

			GS.search()
					.withParameters(getParameters("été ici et là", "-_", destFile.getPath()))
					.input(inputStream)
					.execute();

		}
	}

	@Test
	void testWatermark_InputStream2OutputStream() throws IOException {
		String text = "\377\376\155\157";

		File destFile = new File(Resource.tmpFolder(), "inputstream-outputstream-watermarked.pdf");
		// long startTime = System.currentTimeMillis();
		try (InputStream inputStream = Resource.open_kenwood_Pdf();
				OutputStream outputStream = new FileOutputStream(destFile)) {

			GS.search()
					.withParameters(getParameters(text, "-_", "-"))
					.input(inputStream)
					.output(outputStream)
					.execute();

		}
		// System.out.println(System.currentTimeMillis() - startTime + " ms");
	}

	// **********************************************

	// private String toOctal(String s) {
	// StringBuilder buf = new StringBuilder();
	// for(char c : s.toCharArray()) {
	// buf.append("\\0").append(Integer.toString(c, 8));
	// }
	// return buf.toString();
	// }

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

	private String getWatermarkScript(String watermarckText) {
		// for encoding :
		// http://www.acumentraining.com/Acumen_Journal/AcumenJournal_Nov2001.zip
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
			writer.println("    watermarkFont");
			writer.println("    watermarkColor");
			writer.println("    pageWidth .5 mul pageHeight .5 mul translate");
			writer.println("    0 0 moveto");
			writer.println("    watermarkText false charpath flattenpath pathbbox");
			writer.println("    4 2 roll pop pop");
			writer.println("    0 0 moveto");
			writer.println("    watermarkAngle rotate");
			writer.println("    -.5 mul exch -.5 mul exch");
			writer.println("    rmoveto");
			// https://ghostscript.com/doc/current/Language.htm#Transparency
			writer.println("    0.5 .setopacityalpha");
			writer.println("    watermarkText show");
			writer.println("   grestore");
			writer.println("   true ");
			writer.println("  } ifelse");
			writer.println(" } bind");
			writer.println(">> setpagedevice");
		}
		return stringWriter.toString();
	}

}
