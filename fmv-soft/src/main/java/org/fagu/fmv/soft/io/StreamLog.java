package org.fagu.fmv.soft.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;


/**
 * @author f.agu
 * @created 8 juil. 2019 17:35:09
 */
public class StreamLog {

	private static boolean enabled = false;

	private StreamLog() {}

	public static void debug(boolean enabled) {
		StreamLog.enabled = enabled;
	}

	public static InputStream wrap(InputStream inputStream, Consumer<String> logger) {
		if(enabled) {
			return new LogInputStream(inputStream, logger);
		}
		return inputStream;
	}

	public static OutputStream wrap(OutputStream outputStream, Consumer<String> logger) {
		if(enabled) {
			return new LogOutputStream(outputStream, logger);
		}
		return outputStream;
	}

}
