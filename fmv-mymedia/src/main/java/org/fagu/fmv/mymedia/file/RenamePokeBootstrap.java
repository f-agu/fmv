package org.fagu.fmv.mymedia.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;


public class RenamePokeBootstrap {

	public static void main(String... args) throws Exception {
		browse(Paths.get("..."), Paths.get("..."));
	}

	private static void browse(Path path, Path dest) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String name = file.getFileName().toString();
				String extension = FilenameUtils.getExtension(name);
				String newName = null;
				if(name.startsWith("Pocket.Monsters.")) {
					newName = StringUtils.substringAfter(name, "Pocket.Monsters.");
					newName = StringUtils.substringBefore(newName, ".") + " Pokémon." + extension;
				} else if(name.startsWith("Pokémon - 00")) {
					newName = StringUtils.substringAfter(name, "Pokémon - 00");
					String num = StringUtils.substringBefore(newName, " - ");
					String title = StringUtils.substringAfter(newName, " - ");
					title = StringUtils.substringBefore(title, " FRENCH");
					newName = "S01E" + num + " Pokémon - " + title + "." + extension;
				}
				if(newName != null) {
					Path renamed = dest.resolve(newName);
					Files.move(file, renamed);
					System.out.println(name + " => " + renamed);
				}
				return FileVisitResult.CONTINUE;
			}

		});
	}

}
