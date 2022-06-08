package org.fagu.fmv.mymedia.classify.image;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fagu.fmv.im.IMOperation;
import org.fagu.fmv.im.Image;
import org.fagu.fmv.im.soft.Convert;
import org.fagu.fmv.mymedia.classify.Converter;
import org.fagu.fmv.mymedia.classify.ConverterListener;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public abstract class AbstractImageConverter extends Converter<Image> {

	private final ExecutorService executorService;

	private final Soft convertSoft;

	public AbstractImageConverter(File destFolder) {
		this(destFolder, Runtime.getRuntime().availableProcessors());
	}

	public AbstractImageConverter(File destFolder, int nThreads) {
		super(destFolder);
		convertSoft = Convert.search();
		if(nThreads > 1) {
			executorService = Executors.newFixedThreadPool(nThreads);
		} else {
			executorService = null;
		}
	}

	@Override
	public String getFormat(String defaultValue) {
		return "jpg";
	}

	@Override
	public void convert(Image srcImage, FileFinder<Image>.InfosFile infosFile, File destFile, ConverterListener<Image> listener) throws IOException {
		Runnable runnable = create(srcImage, destFile, listener);
		if(executorService == null) {
			runnable.run();
			return;
		}
		executorService.submit(runnable);
	}

	@Override
	public void close() throws IOException {
		if(executorService != null) {
			executorService.shutdown();
		}
	}

	// **********************************************************

	abstract protected void populateOperation(IMOperation op);

	// **********************************************************

	private Runnable create(Image srcImage, File destFile, ConverterListener<Image> listener) {
		return () -> {
			if(listener != null) {
				listener.eventPreConvert(srcImage, destFile);
			}

			IMOperation op = new IMOperation();
			op.image(srcImage.getFile(), "[0]");
			populateOperation(op);
			op.image(destFile);

			try {
				convertSoft.withParameters(op.toList())
						.execute();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			if(listener != null) {
				listener.eventPostConvert(srcImage, destFile);
			}
		};
	}

}
