package org.fagu.fmv.mymedia.reduce;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 fagu
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.fagu.fmv.im.IMOperation;
import org.fagu.fmv.im.soft.Convert;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.reduce.cb.ComicBook;
import org.fagu.fmv.mymedia.reduce.cb.ComicBookFactory;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.textprogressbar.part.ProgressPart;
import org.fagu.fmv.utils.io.UnclosedOutputStream;


/**
 * @author f.agu
 */
public class CBReducer extends AbstractReducer {

	private static final double DEFAULT_QUALITY = 65D;

	private final Soft convertSoft;

	private final IMOperation imOperation;

	public CBReducer() {
		this.convertSoft = Convert.search();
		this.imOperation = new IMOperation()
				.image("-[0]")
				.autoOrient()
				.quality(DEFAULT_QUALITY)
				.image("jpg:-");
	}

	@Override
	public String getName() {
		return "CBZ / ImageMagick";
	}

	@Override
	public Reduced reduceMedia(File srcFile, String consolePrefixMessage, Logger logger) throws IOException {
		ComicBookFactory comicBookFactory = new ComicBookFactory();
		ComicBook comicBook = comicBookFactory.create(srcFile);
		if(comicBook == null) {
			return null;
		}
		File tempCbrFile = getTempFile(srcFile, "cbz");
		try (ComicBook cb = comicBook) {
			int countTotal = comicBook.countEntry();
			AtomicInteger currentCount = new AtomicInteger();

			IntSupplier progressInPercent = () -> countTotal > 0 ? 100 * currentCount.get() / countTotal : 0;
			try (TextProgressBar bar = TextProgressBar.newBar()
					.fixWidth(60)
					.withText(consolePrefixMessage)
					.appendText("  ")
					.append(ProgressPart.width(32).build())
					.appendText("  " + comicBook.getType() + "   ")
					.append(status -> currentCount.get() + "/" + countTotal)
					.buildAndSchedule(progressInPercent)) {

				try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(new FileOutputStream(tempCbrFile))) {
					comicBook.reduce(
							(zipArchiveEntry, inputStream) -> appendEntry(zipArchiveOutputStream, currentCount, zipArchiveEntry, inputStream));
				}
			}
		}
		return new Reduced(tempCbrFile, false);
	}

	@Override
	public void close() throws IOException {
		// nothing
	}

	public static void main(String[] args) throws IOException {
		File srcfile = new File("F:\\Personnel\\n\\n.cbr");
		try (CBReducer reducer = new CBReducer()) {
			reducer.reduceMedia(srcfile, "toto", Loggers.systemOut());
		}
	}

	// ***************************************************

	private void appendEntry(ZipArchiveOutputStream zipArchiveOutputStream, AtomicInteger currentCount, ZipArchiveEntry outEntry,
			InputStream inputStream) throws IOException {
		zipArchiveOutputStream.putArchiveEntry(outEntry);
		convertSoft.withParameters(imOperation.toList())
				// .logCommandLine(cmdLine -> System.out.println(cmdLine))
				.input(inputStream)
				.output(new UnclosedOutputStream(zipArchiveOutputStream))
				.execute();
		zipArchiveOutputStream.closeArchiveEntry();
		currentCount.incrementAndGet();
	}

}
