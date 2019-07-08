package org.fagu.fmv.soft.exec;

import java.io.OutputStream;

import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.NullOutputStream;
import org.fagu.fmv.soft.io.StreamLog;
import org.fagu.fmv.soft.io.StreamLogConsumer;


/**
 * @author f.agu
 */
public class WritablePumpStreamHandler extends PumpStreamHandler {

	private OutputStream outputStream;

	/**
	 * 
	 */
	public WritablePumpStreamHandler() {
		super(NullOutputStream.NULL_OUTPUT_STREAM);
	}

	/**
	 * @see org.apache.commons.exec.PumpStreamHandler#setProcessInputStream(java.io.OutputStream)
	 */
	@Override
	public void setProcessInputStream(OutputStream outputStream) {
		this.outputStream = StreamLog.wrap(outputStream, StreamLogConsumer.in());
	}

	/**
	 * @return
	 */
	public OutputStream getProcessInputStream() {
		return outputStream;
	}

}
