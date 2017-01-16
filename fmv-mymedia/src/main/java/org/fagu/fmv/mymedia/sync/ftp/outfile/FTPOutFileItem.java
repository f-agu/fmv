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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.fagu.fmv.mymedia.sync.ftp.FTPItem;


/**
 * @author f.agu
 */
public class FTPOutFileItem extends FTPItem {

	private final File outFile;

	/**
	 * @param ftpClient
	 * @param ftpPath
	 * @param ftpFile
	 * @param outFile
	 */
	public FTPOutFileItem(FTPClient ftpClient, String ftpPath, FTPFile ftpFile, File outFile) {
		super(ftpClient, ftpPath, ftpFile);
		this.outFile = Objects.requireNonNull(outFile);
	}

	/**
	 * @see org.fagu.sync.ftp.FTPItem#openOutputStream()
	 */
	@Override
	public OutputStream openOutputStream() throws IOException {
		outFile.getParentFile().mkdirs();
		return new FileOutputStream(outFile);
	}

	/**
	 * @see org.fagu.sync.ftp.FTPItem#create(org.apache.commons.net.ftp.FTPClient, java.lang.String, java.lang.String,
	 *      org.apache.commons.net.ftp.FTPFile)
	 */
	@Override
	protected FTPItem create(FTPClient ftpClient, String ftpPath, String fileName, FTPFile ftpFile) {
		return new FTPOutFileItem(ftpClient, ftpPath, ftpFile, new File(outFile, fileName));
	}

}
