package org.fagu.fmv.soft.exec;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.utils.order.Order;


/**
 * @author f.agu
 * @created 6 juin 2017 09:31:28
 */
@Order(0)
public class IgnoreNullOutputStreamProcessOperator implements ProcessOperator {

	@Override
	public Process operate(Process process) {
		return new WrapProcess(process) {

			@Override
			public OutputStream getOutputStream() {
				return new FilterOutputStream(super.getOutputStream()) {

					@Override
					public void write(byte[] b) throws IOException {
						out.write(b);
					}

					@Override
					public void write(byte[] b, int off, int len) throws IOException {
						out.write(b, off, len);
					}

					@Override
					public void flush() throws IOException {
						try {
							super.flush();
						} catch(IOException e) {
							manageIOException(e);
						}
					}

					@Override
					public void close() throws IOException {
						try {
							super.close();
						} catch(IOException e) {
							manageIOException(e);
						}
					}

					// **********************************

					private void manageIOException(IOException e) throws IOException {

						// Windows
						if(SystemUtils.IS_OS_WINDOWS
								&& "java.io.FileOutputStream".equals(e.getStackTrace()[0].getClassName())
								&& e.getClass().equals(IOException.class)) {
							return;
						}

						// Unix like
						if(SystemUtils.IS_OS_UNIX
								&& "java.lang.ProcessBuilder$NullOutputStream".equals(e.getStackTrace()[0].getClassName())
								&& "Stream closed".equals(e.getMessage())) {
							// ignore
							return;
						}
						throw e;
					}
				};
			}
		};
	}

}
