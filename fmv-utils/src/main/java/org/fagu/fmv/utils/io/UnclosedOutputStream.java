package org.fagu.fmv.utils.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * @author f.agu
 */
public class UnclosedOutputStream extends FilterOutputStream {

	public UnclosedOutputStream(OutputStream out) {
		super(out);
	}

	@Override
	public void close() throws IOException {
		// DO NOTHING
	}

	public void closeForce() throws IOException {
		super.close();
	}

}
