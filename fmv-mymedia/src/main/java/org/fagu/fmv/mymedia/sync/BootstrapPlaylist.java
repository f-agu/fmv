package org.fagu.fmv.mymedia.sync;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClientConfig;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.sync.file.FileStorage;
import org.fagu.fmv.mymedia.sync.ftp.FTPStorage;
import org.fagu.fmv.mymedia.sync.impl.Synchronizers;
import org.fagu.fmv.mymedia.utils.ScannerHelper;
import org.fagu.fmv.mymedia.utils.ScannerHelper.Answer;


/**
 * @author Utilisateur
 * @created 14 juil. 2024 16:54:45
 */
public class BootstrapPlaylist {

	private static final String LOG_FILE_PROPERTY = "fmv.sync.logfile";

	public static void main(String... args) throws IOException {
		System.setProperty("org.apache.commons.net.ftp.systemType", FTPClientConfig.SYST_UNIX);

		String login = args[0];
		String password = args[1];
		String path = "";
		int port = 2221;

		Answer defaultAnswer = () -> "27";
		String value = ScannerHelper.ask("192.168.0.", List.of(defaultAnswer), defaultAnswer).getValue();
		String host = "192.168.0." + value;

		Storage sourceStorage = new FileStorage(new File("F:\\Play list vv"));
		Storage destinationStorage = new FTPStorage(host, port, login, password, path);

		try (Logger logger = openLogger()) {
			Synchronizer synchronizer = Synchronizers.getDefault(logger);
			Sync sync = new Sync(sourceStorage, destinationStorage, synchronizer);
			sync.synchronize();
		}
	}

	private static Logger openLogger() throws IOException {
		if(1 + 1 == 2) {
			return Loggers.systemOut();
		}

		String property = System.getProperty(LOG_FILE_PROPERTY);
		if(property == null) {
			property = "sync.log";
		}
		return LoggerFactory.openLogger(new File(property));
	}
}
