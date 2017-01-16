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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.fagu.fmv.mymedia.sync.Item;


/**
 * @author f.agu
 */
public class FTPItem implements Item {

	private final FTPClient ftpClient;

	private final String ftpPath;

	private FTPFile ftpFile;

	/**
	 * @param ftpClient
	 * @param ftpPath
	 * @param ftpFile
	 */
	public FTPItem(FTPClient ftpClient, String ftpPath, FTPFile ftpFile) {
		this.ftpClient = Objects.requireNonNull(ftpClient);
		this.ftpPath = Objects.requireNonNull(ftpPath);
		this.ftpFile = ftpFile;
	}

	/**
	 * @see org.fagu.sync.Item#isFile()
	 */
	@Override
	public boolean isFile() {
		return getFTPFile().isFile();
	}

	/**
	 * @see org.fagu.sync.Item#isDirectory()
	 */
	@Override
	public boolean isDirectory() {
		return getFTPFile().isDirectory();
	}

	/**
	 * @see org.fagu.sync.Item#getName()
	 */
	@Override
	public String getName() {
		return getFTPFile().getName();
	}

	/**
	 * @see org.fagu.sync.Item#size()
	 */
	@Override
	public long size() {
		return getFTPFile().getSize();
	}

	/**
	 * @see org.fagu.sync.Item#listChildren()
	 */
	@Override
	public Map<String, Item> listChildren() throws IOException {
		FTPFile[] listFiles = ftpClient.listFiles(ftpPath);
		Map<String, Item> items = new TreeMap<>(Collections.reverseOrder());
		for(FTPFile ftpFile : listFiles) {
			items.put(ftpFile.getName(), create(ftpClient, ftpPath + '/' + ftpFile.getName(), ftpFile.getName(), ftpFile));
		}
		return items;
	}

	/**
	 * @see org.fagu.sync.Item#mkdir(java.lang.String)
	 */
	@Override
	public Item mkdir(String name) throws IOException {
		String path = ftpPath + '/' + name;
		if(ftpClient.makeDirectory(path)) {
			return create(ftpClient, path, name, ftpClient.mlistFile(path));
		}
		return null;
	}

	/**
	 * @see org.fagu.sync.Item#createFile(java.lang.String)
	 */
	@Override
	public Item createFile(String name) throws IOException {
		String path = ftpPath + '/' + name;
		return create(ftpClient, path, name, null);
	}

	/**
	 * @see org.fagu.sync.Item#openInputStream()
	 */
	@Override
	public InputStream openInputStream() throws IOException {
		return ftpClient.retrieveFileStream(ftpPath);
	}

	/**
	 * @see org.fagu.sync.Item#openOutputStream()
	 */
	@Override
	public OutputStream openOutputStream() throws IOException {
		OutputStream storeFileStream = ftpClient.storeFileStream(ftpPath);
		// if (!FTPReply.isPositiveIntermediate(ftpClient.getReplyCode())) {
		// throw new IOException("File transfer failed");
		// }
		return new FTPOutputStream(storeFileStream, ftpClient);
	}

	/**
	 * @see org.fagu.sync.Item#delete()
	 */
	@Override
	public boolean delete() throws IOException {
		return delete(getFTPFile(), ftpPath);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ftpPath;
	}

	// ******************************

	/**
	 * @param ftpClient
	 * @param ftpPath
	 * @param fileName
	 * @param ftpFile
	 * @return
	 */
	protected FTPItem create(FTPClient ftpClient, String ftpPath, String fileName, FTPFile ftpFile) {
		return new FTPItem(ftpClient, ftpPath, ftpFile);
	}

	// ******************************

	/**
	 * @param ftpFile
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private boolean delete(FTPFile ftpFile, String path) throws IOException {
		if(ftpFile.isDirectory()) {
			for(FTPFile file : ftpClient.listFiles(path)) {
				delete(file, path + '/' + file.getName());
			}
			return ftpClient.removeDirectory(path);
		}
		return ftpClient.deleteFile(path);
	}

	/**
	 * 
	 */
	private FTPFile getFTPFile() {
		if(ftpFile != null) {
			return ftpFile;
		}
		try {
			FTPFile[] listFiles = ftpClient.listFiles(ftpPath);
			if(listFiles != null && listFiles.length == 1 && listFiles[0] != null) {
				ftpFile = listFiles[0];
				return ftpFile;
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		throw new RuntimeException();
	}

}
