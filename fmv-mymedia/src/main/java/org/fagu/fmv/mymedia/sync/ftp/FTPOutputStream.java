package org.fagu.fmv.mymedia.sync.ftp;

/*
 * #%L
 * fmv-mymedia
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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import org.apache.commons.net.ftp.FTPClient;


/**
 * @author f.agu
 */
public class FTPOutputStream extends FilterOutputStream {

	private final FTPClient ftpClient;

	/**
	 * @param outputStream
	 * @param ftpClient
	 */
	public FTPOutputStream(OutputStream outputStream, FTPClient ftpClient) {
		super(outputStream);
		this.ftpClient = Objects.requireNonNull(ftpClient);
	}

	/**
	 * @see java.io.FilterOutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		super.close();
		if( ! ftpClient.completePendingCommand()) {
			throw new IOException("File transfer failed");
		}
	}

}
