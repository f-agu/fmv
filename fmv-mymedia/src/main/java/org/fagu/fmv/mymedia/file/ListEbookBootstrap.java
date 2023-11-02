package org.fagu.fmv.mymedia.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;


/**
 * @author Utilisateur
 * @created 2 nov. 2023 10:55:57
 */
public class ListEbookBootstrap {

	private static final Set<String> EXTENSIONS = getAcceptedExtensions();

	public static void main(String... args) throws IOException {
		Path path = Paths.get(args[0]);
		try (Stream<Path> stream = Files.walk(path)) {
			stream
					.filter(p -> EXTENSIONS.contains(FilenameUtils.getExtension(p.getFileName().toString())))
					.map(p -> FilenameUtils.getBaseName(p.getFileName().toString()))
					.distinct()
					.sorted()
					.forEach(f -> System.out.println(f));
		}
	}

	private static Set<String> getAcceptedExtensions() {
		Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		set.add("epub");
		set.add("pdf");
		return Collections.unmodifiableSet(set);
	}

}
