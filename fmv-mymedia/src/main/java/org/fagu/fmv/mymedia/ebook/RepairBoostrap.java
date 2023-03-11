package org.fagu.fmv.mymedia.ebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


/**
 * @author Utilisateur
 * @created 13 janv. 2023 00:17:06
 */
public class RepairBoostrap {

	public static void main(String... args) throws IOException {
		Path path = Paths.get(args[0]);
		try (Stream<Path> stream = Files.walk(path)) {
			stream
					.filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".epub"))
					.forEach(p -> {
						try {
							long size = Files.size(p);
							if(size > 5_000_000) {
								System.out.println(size + " \t " + p);
							}
						} catch(IOException e) {
							e.printStackTrace();
						}
					});
		}
	}

}
