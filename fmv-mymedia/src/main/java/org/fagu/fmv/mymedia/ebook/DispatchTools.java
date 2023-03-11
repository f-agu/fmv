package org.fagu.fmv.mymedia.ebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * @author Utilisateur
 * @created 16 janv. 2023 22:48:51
 */
public class DispatchTools {

	private static final boolean DELETE_IGNORE = true;

	private DispatchTools() {}

	public static void dispatch(Path sourceFile, Path destinationPath, boolean dryRun) throws IOException {
		dispatch(sourceFile, destinationPath, null, dryRun);
	}

	public static void dispatch(Path sourceFile, Path destinationPath, String newName, boolean dryRun) throws IOException {
		final String name = StringUtils.defaultString(newName, sourceFile.toString().toString());
		String author = StringUtils.substringBefore(name, " - ");
		if(author == null) {
			System.out.println("Author not found : " + sourceFile);
			return;
		}
		String title = null;
		if(author.equals(name)) {
			author = FilenameUtils.getBaseName(name);
			// System.out.println(author);
		} else {
			title = StringUtils.substringAfter(name, " - ");
			title = title.substring(0, title.length() - 5); // "remove ".epub"
		}
		// System.out.println(author);
		Path destFolder = destinationPath.resolve(Character.toString(Character.toUpperCase(author.charAt(0))));
		destFolder = destFolder.resolve(author);
		if(Files.exists(destFolder)) {
			// System.out.println("Author exists : " + destFolder);
			if(ExistsIn.exists(destFolder, title)) {
				System.out.println("IGNORE : " + name);
				if(DELETE_IGNORE && ! dryRun) {
					Files.delete(sourceFile);
				}
			} else {
				move(destFolder, name, sourceFile, dryRun, "APPEND");
			}
			// System.out.println(" > " + name);
		} else {
			// System.out.println("Author not exists : " + destFolder);
			if( ! dryRun) {
				Files.createDirectories(destFolder);
			}
			move(destFolder, name, sourceFile, dryRun, "NEW   ");
		}
	}

	private static void move(Path destFolder, String name, Path file, boolean dryRun, String state) throws IOException {
		Path d = destFolder.resolve(name);
		System.out.println(state + " : " + file.getFileName().toString() + " -> " + d);
		if( ! dryRun) {
			Files.move(file, d);
		}
	}
}
