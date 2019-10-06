package org.fagu.fmv.soft.xpdf;

import java.io.File;
import java.io.IOException;
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

	public List<File> extract(File pdfFile) throws IOException {
		FileUtils.forceMkdir(extractFolder);
		SoftExecutor softExecutor = pdfImagesSoft.withParameters(
				pdfFile.getAbsolutePath(),
				extractFolder.getAbsolutePath() + File.separatorChar + prefix);
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

	// **************************************************

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
