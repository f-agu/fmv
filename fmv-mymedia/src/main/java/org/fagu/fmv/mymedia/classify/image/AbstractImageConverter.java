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

import org.fagu.fmv.image.Image;
import org.fagu.fmv.mymedia.classify.Converter;
import org.fagu.fmv.mymedia.classify.ConverterListener;
import org.fagu.fmv.soft.im.Convert;
import org.fagu.fmv.utils.file.FileFinder;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ArrayListOutputConsumer;


/**
 * @author f.agu
 */
public abstract class AbstractImageConverter extends Converter<Image> {

	private final ExecutorService executorService;

	/**
	 * @param destFolder
	 */
	public AbstractImageConverter(File destFolder) {
		this(destFolder, Runtime.getRuntime().availableProcessors());
	}

	/**
	 * @param destFolder
	 * @param nThreads
	 */
	public AbstractImageConverter(File destFolder, int nThreads) {
		super(destFolder);
		Convert.search();
		if(nThreads > 1) {
			executorService = Executors.newFixedThreadPool(nThreads);
		} else {
			executorService = null;
		}
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.Converter#getFormat(java.lang.String)
	 */
	@Override
	public String getFormat(String defaultValue) {
		return "jpg";
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.Converter#convert(org.fagu.fmv.media.Media,
	 *      org.fagu.fmv.utils.file.FileFinder.InfosFile, java.io.File, org.fagu.fmv.mymedia.classify.ConverterListener)
	 */
	@Override
	public void convert(Image srcImage, FileFinder<Image>.InfosFile infosFile, File destFile, ConverterListener<Image> listener) throws IOException {
		Runnable runnable = create(srcImage, destFile, listener);
		if(executorService == null) {
			runnable.run();
			return;
		}
		executorService.submit(runnable);
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if(executorService != null) {
			executorService.shutdown();
		}
	}

	// **********************************************************

	/**
	 * @param op
	 */
	abstract protected void populateOperation(IMOperation op);

	// **********************************************************

	/**
	 * @param srcImage
	 * @param destFile
	 * @param listener
	 * @return
	 */
	private Runnable create(Image srcImage, File destFile, ConverterListener<Image> listener) {
		return () -> {
			if(listener != null) {
				listener.eventPreConvert(srcImage, destFile);
			}

			IMOperation op = new IMOperation();
			op.addImage();
			populateOperation(op);
			op.addImage();

			ConvertCmd convertCmd = new ConvertCmd();
			ArrayListOutputConsumer outputConsumer = new ArrayListOutputConsumer();
			convertCmd.setOutputConsumer(outputConsumer);
			try {
				convertCmd.run(op, srcImage.getFile().getAbsolutePath() + "[0]", destFile.getAbsolutePath());
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			if(listener != null) {
				listener.eventPostConvert(srcImage, destFile);
			}
		};
	}

}
