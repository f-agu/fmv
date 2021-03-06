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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;


/**
 * @author fagu
 * @created 6 oct. 2019 16:00:20
 */
public class PdfImagesExtractor {
	// ------------------------------------------------

	public static class Builder {

		private File extractFolder;

		private Soft pdfImagesSoft;

		private Consumer<SoftExecutor> executorConsumer;

		public Builder withExtractFolder(File extractFolder) {
			this.extractFolder = extractFolder;
			return this;
		}

		public Builder withPdfImagesSoft(Soft pdfImagesSoft) {
			this.pdfImagesSoft = pdfImagesSoft;
			return this;
		}

		public Builder withExecutorConsumer(Consumer<SoftExecutor> executorConsumer) {
			this.executorConsumer = executorConsumer;
			return this;
		}

		public PdfImagesExtractor build() {
			return new PdfImagesExtractor(this);
		}
	}

	// ------------------------------------------------

	private final File extractFolder;

	private final Soft pdfImagesSoft;

	private final Consumer<SoftExecutor> executorConsumer;

	private final String prefix;

	private PdfImagesExtractor(Builder builder) {
		this.extractFolder = getExtractFolder(builder.extractFolder);
		this.pdfImagesSoft = getPdfImagesSoft(builder.pdfImagesSoft);
		this.executorConsumer = builder.executorConsumer;
		this.prefix = RandomStringUtils.randomAlphanumeric(20);
	}

	public static PdfImagesExtractor create() {
		return new Builder().build();
	}

	public Soft getPdfImagesSoft() {
		return pdfImagesSoft;
	}

	public String getPrefix() {
		return prefix;
	}

	public File getExtractFolder() {
		return extractFolder;
	}

	public List<File> extract(InputStream inputStream) throws IOException {
		FileUtils.forceMkdir(extractFolder);
		SoftExecutor softExecutor = pdfImagesSoft.withParameters(
				"-",
				extractFolder.getAbsolutePath() + File.separatorChar + prefix)
				.input(inputStream);
		return extract(softExecutor);
	}

	public List<File> extract(File pdfFile) throws IOException {
		FileUtils.forceMkdir(extractFolder);
		SoftExecutor softExecutor = pdfImagesSoft.withParameters(
				pdfFile.getAbsolutePath(),
				extractFolder.getAbsolutePath() + File.separatorChar + prefix);
		return extract(softExecutor);
	}

	// **************************************************

	private List<File> extract(SoftExecutor softExecutor) throws IOException {
		if(executorConsumer != null) {
			executorConsumer.accept(softExecutor);
		}
		softExecutor.execute();

		File[] files = extractFolder.listFiles(f -> f.getName().startsWith(prefix) && f.isFile());
		List<File> list;
		if(files != null) {
			list = new ArrayList<>(Arrays.asList(files));
		} else {
			list = new ArrayList<>(0);
		}
		Collections.sort(list);
		return list;
	}

	private static File getExtractFolder(File folder) {
		if(folder != null) {
			return folder;
		}
		return new File(System.getProperty("java.io.tmpdir"));
	}

	private static Soft getPdfImagesSoft(Soft pdfImagesSoft) {
		if(pdfImagesSoft != null) {
			return pdfImagesSoft;
		}
		return PdfImages.search();
	}

}
