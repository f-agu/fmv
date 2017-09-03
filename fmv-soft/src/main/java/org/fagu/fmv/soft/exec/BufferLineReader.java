package org.fagu.fmv.soft.exec;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author fagu
 */
class BufferLineReader extends FilterReader implements Runnable {

	// TODO
	// can stop schedule
	// throw correct exception when destroy process

	private final ScheduledExecutorService scheduledExecutorService;

	private final LookReader lookReader;

	private StringBuilder buf;

	private CanContinue canContinue = () -> {};

	BufferLineReader(Reader in, LookReader lookReader) {
		super(in);
		this.lookReader = lookReader;
		startNewLine();
		if(lookReader != null) {
			scheduledExecutorService = Executors.newScheduledThreadPool(1);
			long period = 400;
			scheduledExecutorService.scheduleAtFixedRate(this, period, period, TimeUnit.MILLISECONDS);
		} else {
			scheduledExecutorService = null;
		}
	}

	@Override
	public int read() throws IOException {
		canContinue.can();
		int read = super.read();
		if(read >= 0) {
			buf.append((char)read);
		}
		return read;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		canContinue.can();
		int read = super.read(cbuf, off, len);
		if(read > 0) {
			buf.append(cbuf, off, read);
		}
		return read;
	}

	@Override
	public int read(char[] cbuf) throws IOException {
		canContinue.can();
		int read = super.read(cbuf);
		if(read > 0) {
			buf.append(cbuf, 0, read);
		}
		return read;
	}

	public void startNewLine() {
		buf = new StringBuilder();
	}

	@Override
	public void run() {
		if(buf.length() > 0) {
			try {
				if( ! lookReader.look(toString())) {
					stopLookReader();
				}
			} catch(IOException e) {
				canContinue = () -> {
					throw e;
				};
				throw new UncheckedIOException(e);
			}
		}
	}

	public void stopLookReader() {
		if(scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
		}
	}

	@Override
	public void close() throws IOException {
		try {
			stopLookReader();
		} finally {
			super.close();
		}
	}

	@Override
	public String toString() {
		return buf.toString();
	}

	// ------------------------------------------

	@FunctionalInterface
	private interface CanContinue {

		void can() throws IOException;
	}

}
