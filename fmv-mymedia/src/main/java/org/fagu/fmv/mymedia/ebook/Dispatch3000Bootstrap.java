package org.fagu.fmv.mymedia.ebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Utilisateur
 * @created 7 janv. 2023 11:54:29
 */
public class Dispatch3000Bootstrap {

	private static final Path SRC = Paths.get(
			"D:\\A graver\\eBooks\\_Divers\\TODO2\\60 Ebooks en Français");

	private static final Path DEST = Paths.get("D:\\A graver\\eBooks\\Roman");

	private static final boolean DRY_RUN = false;

	public static void main(String... args) throws IOException {
		Files.walk(SRC)
				.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(".epub"))
				.forEach(p -> {
					try {
						DispatchTools.dispatch(p, DEST, DRY_RUN);
					} catch(IOException e) {
						e.printStackTrace();
					}
				});
	}
}
