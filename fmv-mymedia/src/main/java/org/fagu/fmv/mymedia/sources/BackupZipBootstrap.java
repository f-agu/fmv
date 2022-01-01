package org.fagu.fmv.mymedia.sources;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.textprogressbar.TextProgressBar.TextProgressBarBuilder;
import org.fagu.fmv.textprogressbar.part.SpinnerPart;


/**
 * @author f.agu
 * @created 1 janv. 2022 17:14:38
 */
public class BackupZipBootstrap {

	public static void main(String... args) throws IOException {
		if(args.length != 2) {
			System.out.println("Usage: java org.fagu.fmv.mymedia.sources.BackupZipBootstrap <file-list-folders|path-to-backup> <output-folder>");
			return;
		}
		List<Path> folders = loadFolders(args[0]);
		Path outputFolder = Paths.get(args[1]);
		Files.createDirectories(outputFolder);
		BackupZipBootstrap backupZipBootstrap = new BackupZipBootstrap();
		for(Path folder : folders) {
			backupZipBootstrap.searchProject(folder, outputFolder);
		}
	}

	private static List<Path> loadFolders(String foldersFile) throws IOException {
		Path path = Paths.get(foldersFile);
		if(Files.isDirectory(path)) {
			return Collections.singletonList(path);
		}
		return Files.lines(path)
				.filter(s -> ! s.isEmpty())
				.map(Paths::get)
				.collect(Collectors.toList());
	}

	private void searchProject(Path dir, Path outputFolder) throws IOException {
		Files.walkFileTree(dir, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes atts) throws IOException {
				Path pomPath = path.resolve("pom.xml");
				if(Files.exists(pomPath)) {
					backupProject(dir, path, outputFolder);
					return FileVisitResult.SKIP_SUBTREE;
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

	private void backupProject(Path rootPath, Path dir, Path outputFolder) throws IOException {
		Filter<Path> filter = GitIgnoreFilter.open(dir);
		Path outputFile = outputFolder.resolve(dir.getFileName().toString() + ".zip");
		Files.delete(outputFile);

		AtomicInteger countPath = new AtomicInteger();
		TextProgressBarBuilder builder = TextProgressBar.newBar()
				.fixWidth(40).withText(rootPath.relativize(dir).toString())
				.fixWidth(13).with(status -> countPath.toString() + " item" + (countPath.get() > 1 ? "s" : ""))
				.append(new SpinnerPart());

		TextProgressBar textProgressBar = builder.buildAndSchedule();
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(outputFile))) {
			zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
			browseProject(rootPath, dir, filter, zipOutputStream, countPath);
		} finally {
			textProgressBar.close();
		}
	}

	private void browseProject(Path rootPath, Path dir, DirectoryStream.Filter<Path> filter, ZipOutputStream zipOutputStream, AtomicInteger countPath)
			throws IOException {
		Files.walkFileTree(dir, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes atts) throws IOException {
				return filter.accept(path) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts) throws IOException {
				if(filter.accept(path)) {
					ZipEntry zipEntry = new ZipEntry(rootPath.relativize(path).toString());
					zipOutputStream.putNextEntry(zipEntry);
					if(Files.isRegularFile(path)) {
						Files.copy(path, zipOutputStream);
					}
					countPath.incrementAndGet();
				}
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
