package org.fagu.fmv.mymedia.sync;

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
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.sync.SyncConfig.SyncElement;
import org.fagu.fmv.mymedia.sync.file.FileStorage;
import org.fagu.fmv.mymedia.sync.impl.Synchronizers;
import org.fagu.fmv.mymedia.utils.AppVersion;
import org.fagu.fmv.mymedia.utils.ScannerHelper;


/**
 * @author f.agu
 */
public class Bootstrap {

	private static final String LOG_FILE_PROPERTY = "fmv.sync.logfile";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if(args.length == 0) {
			System.out.println("Usage: " + Bootstrap.class.getName() + " <sync-config-file1> [<sync-config-file2> <sync-config-file3> ...]");
			return;
		}
		try (Logger logger = openLogger()) {
			Logger forkLogger = Loggers.fork(logger, Loggers.systemOut());

			forkLogger.log("file.encoding: " + System.getProperty("file.encoding"));
			forkLogger.log("Default Charset=" + Charset.defaultCharset());

			AppVersion.logMyVersion(forkLogger::log);
			List<SyncConfig> configs = loadConfig(args, forkLogger);
			forkLogger.log("");
			displayConfig(configs, forkLogger);
			forkLogger.log("");
			if(ScannerHelper.yesNo("Continue with this configuration")) {
				synchronize(configs, logger);
			}
		}
	}

	/**
	 * @param args
	 * @param logger
	 * @return
	 * @throws IOException
	 */
	private static List<SyncConfig> loadConfig(String[] args, Logger logger) throws IOException {
		List<SyncConfig> configs = new LinkedList<>();
		for(String arg : args) {
			File confFile = new File(arg);
			if( ! confFile.exists()) {
				System.out.println("SyncConfigFile not found: " + arg);
				continue;
			}
			configs.add(SyncConfig.load(confFile, logger));
		}
		return configs;
	}

	/**
	 * @param configs
	 * @param logger
	 */
	private static void displayConfig(List<SyncConfig> configs, Logger logger) {
		logger.log("Synchronizations declared:");
		configs.stream()
				.flatMap(syncConfig -> syncConfig.getElements().stream())
				.forEach(syncElement -> {
					logger.log("        source: " + syncElement.getSourceFile());
					List<File> destFiles = syncElement.getDestFiles();
					logger.log("   destination:     " + destFiles.get(0));
					Iterator<File> iterator = destFiles.iterator();
					iterator.next();
					while(iterator.hasNext()) {
						logger.log("                    " + iterator.next());
					}
				});
	}

	/**
	 * @param configs
	 * @param logger
	 * @throws IOException
	 */
	private static void synchronize(List<SyncConfig> configs, Logger logger) throws IOException {
		logger.log("Synchronizing...");
		for(SyncConfig syncConfig : configs) {
			for(SyncElement syncElement : syncConfig.getElements()) {
				synchronize(logger, syncElement.getSourceFile(), syncElement.getDestFiles());
			}
		}
	}

	/**
	 * @param logger
	 * @param sourceFile
	 * @param destFiles
	 * @throws IOException
	 */
	private static void synchronize(Logger logger, File sourceFile, List<File> destFiles) throws IOException {
		List<Storage> destStorages = null;
		try (Storage sourceStrage = new FileStorage(sourceFile)) {
			destStorages = destFiles.stream()
					.map(FileStorage::new)
					.collect(Collectors.toList());
			Synchronizer synchronizer = Synchronizers.getDefault(logger);
			Sync sync = new Sync(sourceStrage, destStorages, synchronizer);
			sync.synchronize();
		} finally {
			if(destStorages != null) {
				for(Storage storage : destStorages) {
					try {
						storage.close();
					} catch(IOException ignore) {}
				}
			}
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private static Logger openLogger() throws IOException {
		String property = System.getProperty(LOG_FILE_PROPERTY);
		if(property == null) {
			property = "sync.log";
		}
		return LoggerFactory.openLogger(new File(property));
	}

}
