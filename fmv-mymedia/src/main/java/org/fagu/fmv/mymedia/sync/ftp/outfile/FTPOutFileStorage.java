package org.fagu.fmv.mymedia.sync.ftp.outfile;

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

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.Objects;

import org.apache.commons.net.ftp.FTPClient;
import org.fagu.fmv.mymedia.sync.Item;
import org.fagu.fmv.mymedia.sync.ftp.FTPStorage;


/**
 * @author f.agu
 */
public class FTPOutFileStorage extends FTPStorage {

	private final File outFile;

	public FTPOutFileStorage(FTPClient ftpClient, String ftpPath, File outFile) {
		super(ftpClient, ftpPath);
		this.outFile = Objects.requireNonNull(outFile);
	}

	public FTPOutFileStorage(String host, int port, String login, String password, String ftpPath, File outFile) throws SocketException, IOException {
		super(host, port, login, password, ftpPath);
		this.outFile = outFile;
	}

	@Override
	public Item getRoot() throws IOException {
		return new FTPOutFileItem(ftpClient, ftpPath, ftpClient.mlistFile(ftpPath), outFile);
	}

}
