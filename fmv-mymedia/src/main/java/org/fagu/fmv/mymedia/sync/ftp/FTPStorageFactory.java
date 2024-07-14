package org.fagu.fmv.mymedia.sync.ftp;

import java.util.Properties;

import org.fagu.fmv.mymedia.sync.Storage;
import org.fagu.fmv.mymedia.sync.StorageFactory;


/**
 * @author f.agu
 */
public class FTPStorageFactory extends StorageFactory {

	private static final String NAME = "ftp";

	public FTPStorageFactory() {
		super(NAME);
	}

	@Override
	public Storage create(Properties properties) {
		String host = properties.getProperty("host");
		Integer port = Integer.parseInt(properties.getProperty("port"));
		String login = properties.getProperty("login");
		String password = properties.getProperty("password");
		String path = properties.getProperty("path");
		try {
			return new FTPStorage(host, port, login, password, path);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
