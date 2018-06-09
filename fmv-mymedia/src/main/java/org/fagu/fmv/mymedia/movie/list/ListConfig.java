package org.fagu.fmv.mymedia.movie.list;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.mymedia.file.PlaceHolderRootFile;
import org.fagu.fmv.mymedia.logger.Logger;


/**
 * @author Utilisateur
 * @created 9 juin 2018 13:47:13
 */
public class ListConfig {

	private final List<File> folders;

	private ListConfig(List<File> folders) {
		this.folders = Collections.unmodifiableList(folders);
	}

	public static ListConfig load(File file, Logger logger) throws IOException {
		logger.log("Load config file: " + file);
		List<File> folders = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(StringUtils.isBlank(line)) {
					continue;
				}
				PlaceHolderRootFile.findFile(line, logger, "  Folder").ifPresent(folders::add);
			}
		}
		return new ListConfig(folders);
	}

	public List<File> getFolders() {
		return folders;
	}
}
