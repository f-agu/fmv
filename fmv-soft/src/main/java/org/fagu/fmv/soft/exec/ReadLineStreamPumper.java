package org.fagu.fmv.soft.exec;

/*
 * #%L
 * fmv-utils
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

import java.io.InputStream;
import java.util.Scanner;


/**
 * @author f.agu
 */
public class ReadLineStreamPumper implements Runnable {

	private final InputStream inputStream;

	private final ReadLine readLine;

	private boolean finished;

	/**
	 * @param inputStream
	 * @param readLine
	 */
	public ReadLineStreamPumper(InputStream inputStream, ReadLine readLine) {
		this.inputStream = inputStream;
		this.readLine = readLine;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		synchronized(this) {
			// Just in case this object is reused in the future
			finished = false;
		}
		// Use Scanner, I don't why it doesn't work with a BufferedReader and an InputStreamReader...
		try (Scanner scanner = new Scanner(inputStream)) {
			while(scanner.hasNext()) {
				readLine.read(scanner.nextLine());
			}
		} catch(final Exception e) {
			// nothing to do - happens quite often with watchdog
		} finally {
			synchronized(this) {
				finished = true;
				notifyAll();
			}
		}
	}

	/**
	 * Tells whether the end of the stream has been reached.
	 * 
	 * @return true is the stream has been exhausted.
	 */
	public synchronized boolean isFinished() {
		return finished;
	}

	/**
	 * This method blocks until the stream pumper finishes.
	 * 
	 * @exception InterruptedException if any thread interrupted the current thread before or while the current thread
	 *            was waiting for a notification.
	 * @see #isFinished()
	 */
	public synchronized void waitFor() throws InterruptedException {
		while( ! isFinished()) {
			wait();
		}
	}

}
