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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.fagu.fmv.soft.im.Convert;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ArrayListOutputConsumer;


/**
 * @author f.agu
 */
public class IMReducer extends AbstractReducer {

	private static final double DEFAULT_QUALITY = 65D;

	private static final String DEFAULT_FORMAT = "jpg";

	private double quality = DEFAULT_QUALITY;

	private String format;

	/**
	 *
	 */
	public IMReducer() {
		Convert.search();
		try {
			quality = Double.parseDouble(System.getProperty("fmv.reduce.imagequality"));
		} catch(Exception e) {
			// ignore
		}
		format = System.getProperty("fmv.reduce.format", DEFAULT_FORMAT);
	}

	/**
	 * @see org.fagu.fmv.mymedia.reduce.Reducer#getName()
	 */
	@Override
	public String getName() {
		return "ImageMagick";
	}

	/**
	 * @see org.fagu.fmv.mymedia.reduce.Reducer#reduceMedia(java.io.File, String, Logger)
	 */
	@Override
	public File reduceMedia(File srcFile, String consolePrefixMessage, Logger logger) throws IOException {
		IMOperation op = new IMOperation();
		op.addImage();
		op.autoOrient();
		op.quality(quality);
		op.addImage();

		File destFile = getTempFile(srcFile, format);

		OverrideLogConvertCmd convertCmd = new OverrideLogConvertCmd(logger);
		ArrayListOutputConsumer outputConsumer = new ArrayListOutputConsumer();
		convertCmd.setOutputConsumer(outputConsumer);
		try {
			convertCmd.run(op, srcFile.getAbsolutePath() + "[0]", destFile.getAbsolutePath());
		} catch(IM4JavaException | InterruptedException e) {
			throw new IOException(e);
		}
		return destFile;
	}

	public static void main(String[] args) throws IOException {
		File f1 = new File("D:\\tmp\\u1.jpg");
		File f2 = new File("D:\\tmp\\u2.jpg");
		f2.delete();
		FileUtils.copyFile(f1, f2);

		IMReducer reducer = new IMReducer();
		reducer.reduceMedia(f2, "toto", Loggers.systemOut());
	}

}
