package org.fagu.fmv.soft.exec;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
