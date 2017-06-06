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

	/**
	 * @see org.fagu.fmv.soft.exec.ProcessOperator#operate(java.lang.Process)
	 */
	@Override
	public Process operate(Process process) {
		if( ! SystemUtils.IS_OS_UNIX) {
			return process;
		}
		return new WrapProcess(process) {

			@Override
			public OutputStream getOutputStream() {
				OutputStream outputStream = super.getOutputStream();
				if( ! "java.lang.ProcessBuilder$NullOutputStream".equals(outputStream.getClass().getName())) {
					return outputStream;
				}
				return new FilterOutputStream(outputStream) {

					@Override
					public void write(int b) throws IOException {
						try {
							super.write(b);
						} catch(IOException e) {
							// ignore
						}
					}

					@Override
					public void write(byte[] b) throws IOException {
						try {
							super.write(b);
						} catch(IOException e) {
							// ignore
						}
					}

					@Override
					public void write(byte[] b, int off, int len) throws IOException {
						try {
							super.write(b, off, len);
						} catch(IOException e) {
							// ignore
						}
					}

					@Override
					public void flush() throws IOException {
						try {
							super.flush();
						} catch(IOException e) {
							// ignore
						}
					}

					@Override
					public void close() throws IOException {
						try {
							super.close();
						} catch(IOException e) {
							// ignore
						}
					}
				};
			}
		};
	}

}
