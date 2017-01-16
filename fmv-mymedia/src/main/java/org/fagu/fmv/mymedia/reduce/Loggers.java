package org.fagu.fmv.mymedia.reduce;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2016 fagu
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


import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author f.agu
 */
public class Loggers {

	private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 *
	 */
	private Loggers() {}

	/**
	 * @return
	 */
	public static Logger systemOut() {
		return printStream(System.out);
	}

	/**
	 * @return
	 */
	public static Logger systemErr() {
		return printStream(System.err);
	}

	/**
	 * @param printStream
	 * @return
	 */
	public static Logger printStream(PrintStream printStream) {
		return new Logger() {

			/**
			 * @see java.io.Closeable#close()
			 */
			@Override
			public void close() throws IOException {
				printStream.close();
			}

			/**
			 * @see org.fagu.fmv.mymedia.reduce.Logger#log(java.lang.Throwable)
			 */
			@Override
			public void log(Throwable throwable) {
				throwable.printStackTrace(printStream);
			}

			/**
			 * @see org.fagu.fmv.mymedia.reduce.Logger#log(java.lang.String)
			 */
			@Override
			public void log(String message) {
				printStream.println(message);
			}
		};
	}

	/**
	 * @param logger
	 * @return
	 * @throws IOException
	 */
	public static Logger timestamp(Logger logger) throws IOException {
		return timstamp(logger, null);
	}

	/**
	 * @param logger
	 * @param dateTimeFormatter
	 * @return
	 * @throws IOException
	 */
	public static Logger timstamp(Logger logger, DateTimeFormatter dateTimeFormatter) throws IOException {
		final DateTimeFormatter DTF = dateTimeFormatter == null ? DEFAULT_DATE_TIME_FORMATTER : dateTimeFormatter;
		return new Logger() {

			/**
			 * @param throwable
			 */
			@Override
			public void log(Throwable throwable) {
				logger.log(throwable);
			}

			/**
			 * @param message
			 */
			@Override
			public void log(String message) {
				StringBuilder buf = new StringBuilder(20 + message.length());
				buf.append(LocalDateTime.now().format(DTF)).append(' ').append(message);
				logger.log(message);
			}

			/**
			 * @see java.io.Closeable#close()
			 */
			@Override
			public void close() throws IOException {
				logger.close();
			}
		};
	}
}
