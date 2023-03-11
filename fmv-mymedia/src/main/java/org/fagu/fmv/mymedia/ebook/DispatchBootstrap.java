package org.fagu.fmv.mymedia.ebook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Utilisateur
 * @created 7 janv. 2023 11:54:29
 */
public class DispatchBootstrap {

	private static final File SRC = new File(
			"a path");

	private static final File DEST = new File("another path");

	public static void main(String... args) throws IOException {
		for(File file : SRC.listFiles()) {
			String name = file.getName();
			String author = StringUtils.substringBefore(name, " - ");
			if(author == null) {
				System.out.println("Author not found : " + file);
				continue;
			}
			File destFolder = new File(DEST, Character.toString(Character.toUpperCase(author.charAt(0))));
			destFolder = new File(destFolder, author);
			if(destFolder.exists()) {
				// System.out.println("Author exists : " + destFolder);
				// System.out.println(" > " + name);
			} else {
				destFolder.mkdirs();
			}
			File d = new File(destFolder, name);
			System.out.println(file + " -> " + d);
			Files.move(file.toPath(), d.toPath());
		}
	}

}
