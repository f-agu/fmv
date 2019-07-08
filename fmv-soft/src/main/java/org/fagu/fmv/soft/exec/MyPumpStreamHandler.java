package org.fagu.fmv.soft.exec;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.exec.PumpStreamHandler;
import org.fagu.fmv.soft.io.StreamLog;
import org.fagu.fmv.soft.io.StreamLogConsumer;


/**
 * @author Oodrive
 * @author f.agu
 * @created 8 juil. 2019 16:55:29
 */
class MyPumpStreamHandler extends PumpStreamHandler {

	public MyPumpStreamHandler() {}

	public MyPumpStreamHandler(OutputStream outAndErr) {
		super(outAndErr);
	}

	public MyPumpStreamHandler(OutputStream out, OutputStream err) {
		super(out, err);
	}

	public MyPumpStreamHandler(OutputStream out, OutputStream err, InputStream input) {
		super(out, err, input);
	}

	@Override
	protected Thread createPump(final InputStream is, final OutputStream os) {
		return createPump(is, os, true);
	}

	@Override
	public void setProcessInputStream(OutputStream os) {
		super.setProcessInputStream(StreamLog.wrap(os, StreamLogConsumer.in()));
	}

	@Override
	public void setProcessOutputStream(InputStream is) {
		super.setProcessOutputStream(StreamLog.wrap(is, StreamLogConsumer.out()));
	}

	@Override
	public void setProcessErrorStream(InputStream is) {
		super.setProcessErrorStream(StreamLog.wrap(is, StreamLogConsumer.err()));
	}

}
