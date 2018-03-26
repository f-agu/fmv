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
import org.fagu.fmv.im.IMOperation;
import org.fagu.fmv.im.soft.Convert;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.soft.Soft;


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
	public Reduced reduceMedia(File srcFile, String consolePrefixMessage, Logger logger) throws IOException {
		File destFile = getTempFile(srcFile, format);
		IMOperation op = new IMOperation();
		op.image(srcFile, "[0]").autoOrient().quality(quality).image(destFile);

		Soft convertSoft = Convert.search();
		convertSoft.withParameters(op.toList())
				.logCommandLine(line -> logger.log("Exec: " + line))
				.execute();
		return new Reduced(destFile, false);
	}

	@Override
	public void close() throws IOException {
		// nothing
	}

	public static void main(String[] args) throws IOException {
		File f1 = new File("D:\\tmp\\u1.jpg");
		File f2 = new File("D:\\tmp\\u2.jpg");
		f2.delete();
		FileUtils.copyFile(f1, f2);

		try (IMReducer reducer = new IMReducer()) {
			reducer.reduceMedia(f2, "toto", Loggers.systemOut());
		}
	}

}
