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

import java.io.IOException;
import java.net.SocketException;
import java.util.Objects;

import org.apache.commons.net.ftp.FTPClient;
import org.fagu.fmv.mymedia.sync.Item;
import org.fagu.fmv.mymedia.sync.Storage;


/**
 * @author f.agu
 */
public class FTPStorage implements Storage {

	protected final FTPClient ftpClient;

	protected final String host;

	protected final String ftpPath;

	/**
	 * @param ftpClient
	 * @param ftpPath
	 */
	public FTPStorage(FTPClient ftpClient, String ftpPath) {
		this.ftpClient = Objects.requireNonNull(ftpClient);
		this.ftpPath = Objects.requireNonNull(ftpPath);
		host = "?";
	}

	/**
	 * @param host
	 * @param login
	 * @param password
	 * @param ftpPath
	 * @throws SocketException
	 * @throws IOException
	 */
	public FTPStorage(String host, String login, String password, String ftpPath) throws SocketException, IOException {
		ftpClient = new FTPClient();
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.connect(host);
		ftpClient.login(login, password);
		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		this.host = host;
		this.ftpPath = ftpPath;
	}

	/**
	 * @see org.fagu.sync.Storage#getRoot()
	 */
	@Override
	public Item getRoot() throws IOException {
		return new FTPItem(ftpClient, ftpPath, ftpClient.mlistFile(ftpPath));
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			ftpClient.logout();
		} finally {
			ftpClient.disconnect();
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ftp://" + host + ftpPath;
	}
}
