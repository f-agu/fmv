package org.fagu.fmv.soft.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * @author f.agu
 * @created 8 juil. 2019 16:32:43
 */
public class LogOutputStream extends OutputStream {

	private final Consumer<String> logger;

	private final OutputStream outputStream;

	private final String hexCode;

	private long pointer;

	public LogOutputStream(OutputStream outputStream, Consumer<String> logger) {
		this.outputStream = Objects.requireNonNull(outputStream);
		this.logger = Objects.requireNonNull(logger);
		this.hexCode = Log.reference(this);
	}

	@Override
	public void write(int b) throws IOException {
		logger.accept(pointerToString() + "write(" + Log.data(b) + ")");
		outputStream.write(b);
		++pointer;
	}

	@Override
	public void write(byte[] b) throws IOException {
		logger.accept(pointerToString() + "write(byte[" + b.length + "]) " + Log.data(b, 0, b.length));
		outputStream.write(b);
		pointer += b.length;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		logger.accept(pointerToString() + "write(byte[], " + off + ", " + len + ") " + Log.data(b, off, len));
		outputStream.write(b, off, len);
		pointer += len;
	}

	@Override
	public void flush() throws IOException {
		logger.accept(pointerToString() + "flush()");
		outputStream.flush();
	}

	@Override
	public void close() throws IOException {
		logger.accept(pointerToString() + "close()");
		outputStream.close();
	}
	// ********************************

	private String pointerToString() {
		return hexCode + " (@" + pointer + " / 0x" + Long.toHexString(pointer) + ") ";
	}

}
