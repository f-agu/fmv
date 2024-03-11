package org.fagu.fmv.mymedia.sources;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;


/**
 * @author Oodrive
 * @author f.agu
 * @created 22 déc. 2023 15:55:52
 */
public class ProjectWalker {

	private ProjectWalker() {}

	// ----------------------------------------------------

	interface Visitor {

		FileVisitResult visit(Path path) throws IOException;
	}

	// ----------------------------------------------------

	static void searchProject(Path dir, Visitor visitor) throws IOException {
		System.out.println(dir);
		Files.walkFileTree(dir, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes atts) throws IOException {
				Path pomPath = path.resolve("pom.xml");
				if(Files.exists(pomPath)) {
					return visitor.visit(path);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path path, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path path, IOException exc) throws IOException {
				return path.equals(dir) ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
			}

		});
	}
}
