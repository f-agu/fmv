package org.fagu.fmv.mymedia.sources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author f.agu
 * @created 22 déc. 2023 15:54:49
 */
public class FileListFolders {

	private FileListFolders() {}

	static List<Path> loadFolders(String foldersFile) throws IOException {
		Path path = Paths.get(foldersFile);
		if(Files.isDirectory(path)) {
			return Collections.singletonList(path);
		}
		try (Stream<String> lines = Files.lines(path)) {
			return lines
					.map(String::trim)
					.filter(s -> ! s.isEmpty() && ! s.startsWith("#"))
					.map(Paths::get)
					.collect(Collectors.toList());
		}
	}
}
