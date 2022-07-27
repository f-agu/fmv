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
import java.util.NavigableSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.textprogressbar.TextProgressBar.TextProgressBarBuilder;
import org.fagu.fmv.textprogressbar.part.SpinnerPart;
import org.fagu.fmv.utils.ByteSize;


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
		NavigableSet<ProjectStats> projectStatsSet = new TreeSet<>();
		for(Path folder : folders) {
			backupZipBootstrap.searchProject(folder, outputFolder, projectStatsSet::add);
		}
		displayStats(projectStatsSet);
	}

	private static List<Path> loadFolders(String foldersFile) throws IOException {
		Path path = Paths.get(foldersFile);
		if(Files.isDirectory(path)) {
			return Collections.singletonList(path);
		}
		try (Stream<String> lines = Files.lines(path)) {
			return lines
					.map(String::trim)
					.filter(s -> ! s.isEmpty() && ! s.startsWith("#"))
					.map(Paths::get)
					.collect(Collectors.toList());
		}
	}

	private void searchProject(Path dir, Path outputFolder, Consumer<ProjectStats> projectStatsConsumer) throws IOException {
		System.out.println(dir);
		Files.walkFileTree(dir, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes atts) throws IOException {
				Path pomPath = path.resolve("pom.xml");
				if(Files.exists(pomPath)) {
					ReadPomXML readPomXML = new ReadPomXML("version");
					ProjectStats stats = backupProject(dir, path, outputFolder, readPomXML.getInfo(pomPath));
					projectStatsConsumer.accept(stats);
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

	private ProjectStats backupProject(Path rootPath, Path dir, Path outputFolder, String projectVersion) throws IOException {
		Filter<Path> filter = GitIgnoreFilter.open(dir);
		String name = dir.getFileName().toString();
		Path outputFile = outputFolder.resolve(name + '_' + projectVersion + ".zip");
		if(Files.exists(outputFile)) {
			Files.delete(outputFile);
		}
		ProjectStats projectStats = new ProjectStats(name, projectVersion);
		GitTools.findURL(dir).ifPresent(projectStats::setGitUrl);

		AtomicInteger countPath = new AtomicInteger();
		AtomicReference<String> endText = new AtomicReference<>(StringUtils.EMPTY);
		TextProgressBarBuilder builder = TextProgressBar.newBar()
				.appendText("  ")
				.fixWidth(40).withText(rootPath.relativize(dir).toString())
				.fixWidth(13).with(status -> countPath.toString() + " item" + (countPath.get() > 1 ? "s" : ""))
				.append(new SpinnerPart())
				.append(status -> endText.get());

		try (TextProgressBar textProgressBar = builder.buildAndSchedule()) {
			try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(outputFile))) {
				zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
				browseProject(rootPath, dir, filter, zipOutputStream, countPath, projectStats);
			} finally {
				endText.set("    " + ByteSize.formatSize(Files.size(outputFile)));
			}
		}
		System.out.println();
		return projectStats;
	}

	private void browseProject(Path rootPath, Path dir, DirectoryStream.Filter<Path> filter, ZipOutputStream zipOutputStream, AtomicInteger countPath,
			ProjectStats projectStats)
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

						String fileName = path.getFileName().toString();
						projectStats.addFile(Files.size(path), FilenameUtils.getExtension(fileName));
						if("pom.xml".equals(fileName)) {
							projectStats.incrementModule();
						}
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

	private static void displayStats(NavigableSet<ProjectStats> projectStatsSet) {
		System.out.println();
		System.out.println();
		System.out.println(new StringJoiner("\t")
				.add("Name")
				.add("Version")
				.add("Git")
				.add("Count modules")
				.add("Count files")
				.add("Count java files")
				.add("File size"));

		for(ProjectStats projectStats : projectStatsSet) {
			StringJoiner joiner = new StringJoiner("\t")
					.add(projectStats.getName())
					.add(projectStats.getVersion())
					.add(projectStats.getGitUrl().orElse(""))
					.add(Integer.toString(projectStats.getCountModules()))
					.add(Integer.toString(projectStats.getCountFiles()))
					.add(projectStats.getCountFiles("java").map(i -> i.toString()).orElse(""))
					.add(ByteSize.formatSize(projectStats.getSizeFiles()));
			System.out.println(joiner);
		}
	}

}
