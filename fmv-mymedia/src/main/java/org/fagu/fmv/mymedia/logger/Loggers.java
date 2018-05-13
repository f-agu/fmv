package org.fagu.fmv.mymedia.logger;

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
import java.util.Arrays;

import org.fagu.fmv.mymedia.utils.UnclosedPrintStream;


/**
 * @author f.agu
 */
public class Loggers {

	private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final Logger NO_OPERATION = new Logger() {

		@Override
		public void close() throws IOException {
			// do nothing
		}

		@Override
		public void log(Throwable throwable) {
			// do nothing
		}

		@Override
		public void log(String message) {
			// do nothing
		}
	};

	private static final Logger SYSTEM_OUT = printStream(new UnclosedPrintStream(System.out), "system-out");

	private static final Logger SYSTEM_ERR = printStream(new UnclosedPrintStream(System.err), "system-err");

	private Loggers() {}

	/**
	 * @return
	 */
	public static Logger noOperation() {
		return NO_OPERATION;
	}

	/**
	 * @return
	 */
	public static Logger systemOut() {
		return SYSTEM_OUT;
	}

	/**
	 * @return
	 */
	public static Logger systemErr() {
		return SYSTEM_ERR;
	}

	/**
	 * @param printStream
	 * @return
	 */
	public static Logger printStream(PrintStream printStream) {
		return printStream(printStream, null);
	}

	/**
	 * @param printStream
	 * @return
	 */
	public static Logger printStream(PrintStream printStream, String toString) {
		return new Logger() {

			@Override
			public void close() throws IOException {
				printStream.close();
			}

			@Override
			public void log(Throwable throwable) {
				throwable.printStackTrace(printStream);
			}

			@Override
			public void log(String message) {
				printStream.println(message);
			}

			@Override
			public String toString() {
				return toString != null ? toString : super.toString();
			}
		};
	}

	/**
	 * @param logger
	 * @return
	 */
	public static Logger fork(Logger... loggers) {
		return new Logger() {

			@Override
			public void close() throws IOException {
				IOException exception = null;
				for(Logger logger : loggers) {
					try {
						logger.close();
					} catch(IOException e) {
						exception = e;
					}
				}
				if(exception != null) {
					throw exception;
				}
			}

			@Override
			public void log(Throwable throwable) {
				Arrays.stream(loggers).forEach(l -> l.log(throwable));
			}

			@Override
			public void log(String message) {
				Arrays.stream(loggers).forEach(l -> l.log(message));
			}

			@Override
			public String toString() {
				return Arrays.asList(loggers).toString();
			}
		};
	}

	/**
	 * @param logger
	 * @return
	 * @throws IOException
	 */
	public static Logger timestamp(Logger logger) throws IOException {
		return timestamp(logger, null);
	}

	/**
	 * @param logger
	 * @param dateTimeFormatter
	 * @return
	 * @throws IOException
	 */
	public static Logger timestamp(Logger logger, DateTimeFormatter dateTimeFormatter) throws IOException {
		final DateTimeFormatter dateFormatter = dateTimeFormatter == null ? DEFAULT_DATE_TIME_FORMATTER : dateTimeFormatter;
		return new Logger() {

			@Override
			public void log(Throwable throwable) {
				logger.log(throwable);
			}

			@Override
			public void log(String message) {
				StringBuilder buf = new StringBuilder(20 + message.length());
				buf.append(LocalDateTime.now().format(dateFormatter)).append(' ').append(message);
				logger.log(buf.toString());
			}

			@Override
			public void close() throws IOException {
				logger.close();
			}

			@Override
			public String toString() {
				return "timestamp[" + logger.toString() + ']';
			}
		};
	}
}
