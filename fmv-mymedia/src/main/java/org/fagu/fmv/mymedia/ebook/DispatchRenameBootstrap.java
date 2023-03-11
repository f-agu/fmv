package org.fagu.fmv.mymedia.ebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * @author Utilisateur
 * @created 7 janv. 2023 11:54:29
 */
public class DispatchRenameBootstrap {

	private static final Path SRC = Paths.get(
			"D:\\A graver\\eBooks\\_Divers\\TODO2\\60 Ebooks en Français");

	private static final Path DEST = Paths.get("D:\\A graver\\eBooks\\Roman");

	private static final boolean DRY_RUN = false;

	public static void main(String... args) throws IOException {
		String suffix = getSuffix("   .epub");
		Files.walk(SRC)
				.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(".epub"))
				.forEach(p -> {
					final String origName = p.getFileName().toString();

					// suffix
					String name = origName;
					// name = suffix(origName, suffix);

					// title - author(invert)
					name = titleAuthorRevert(name);

					if(name.equals(origName)) {
						return;
					}

					try {
						DispatchTools.dispatch(p, DEST, name, DRY_RUN);
					} catch(IOException e) {
						e.printStackTrace();
					}
					// System.out.println(origName + " -> " + dest);
					// if( ! DRY_RUN) {
					// Path dest = p.getParent().resolve(name);
					// try {
					// Files.move(p, dest, StandardCopyOption.REPLACE_EXISTING);
					// } catch(IOException e) {
					// e.printStackTrace();
					// }
					// }
				});
	}

	private static String suffix(final String name, final String suffix) {
		String n = name;
		if(name.endsWith(suffix)) {
			n = StringUtils.substringBefore(name, suffix) + ".epub";
		}
		return n;
	}

	private static String titleAuthorRevert(final String name) {
		String baseName = FilenameUtils.getBaseName(name);
		String title = StringUtils.substringBefore(baseName, " - ");
		String author = StringUtils.substringAfter(baseName, " - ");
		String[] s = author.split(",");
		if(s.length == 2) {
			return s[1].trim() + ' ' + s[0].trim() + " - " + title + ".epub";
		}
		if(s.length < 2) {
			// return author + " - " + title + ".epub";
		}
		System.out.println(name + "   " + s.length);
		return name;
	}

	private static String getSuffix(final String s) {
		return s.endsWith(".epub") ? s : s + ".epub";
	}

}
