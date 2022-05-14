package org.fagu.fmv.mymedia.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.lang3.StringUtils;


public class RenameBootstrap {

	public static void main(String... args) throws Exception {
		browse(Paths.get("/folder/folder/..."));
	}

	private static void browse(Path path) throws IOException {
		final String endsWith = ".boo.bar.dat";

		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String name = file.getFileName().toString();
				if(name.endsWith(endsWith)) {
					name = StringUtils.substringBefore(name, endsWith) + ".avi";
					Path renamed = file.getParent().resolve(name);
					Files.move(file, renamed);
					System.out.println(name);
				}
				return FileVisitResult.CONTINUE;
			}

		});
	}

}
