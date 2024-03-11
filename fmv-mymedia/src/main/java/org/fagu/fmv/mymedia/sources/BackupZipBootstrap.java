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
import java.util.List;
import java.util.NavigableSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
			System.out.println("Usage: java " + BackupZipBootstrap.class.getName() + " <file-list-folders|path-to-backup> <output-folder>");
			return;
		}
		List<Path> folders = FileListFolders.loadFolders(args[0]);
		Path outputFolder = Paths.get(args[1]);
		Files.createDirectories(outputFolder);
		NavigableSet<ProjectStats> projectStatsSet = new TreeSet<>();
		for(Path folder : folders) {
			ProjectWalker.searchProject(folder, path -> {
				Path pomPath = path.resolve("pom.xml");
				String group = folder.getFileName().toString();
				ReadPomXML readPomXML = new ReadPomXML("version");
				ProjectStats projectStats = new ProjectStats(group, path.getFileName().toString(), readPomXML.getInfo(pomPath));
				GitFileTools.findURL(path).ifPresent(projectStats::setGitUrl);
				backupProject(folder, path, outputFolder, projectStats);
				projectStatsSet.add(projectStats);
				return FileVisitResult.SKIP_SUBTREE;
			});
		}
		displayStats(projectStatsSet);
	}

	private static ProjectStats backupProject(Path rootPath, Path dir, Path outputFolder, ProjectStats projectStats) throws IOException {
		Filter<Path> filter = GitIgnoreFilter.open(dir);
		Path outputFile = outputFolder.resolve(projectStats.getName() + '_' + projectStats.getVersion() + ".zip");
		if(Files.exists(outputFile)) {
			Files.delete(outputFile);
		}

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

	private static void browseProject(Path rootPath, Path dir, DirectoryStream.Filter<Path> filter, ZipOutputStream zipOutputStream,
			AtomicInteger countPath,
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
				.add("Group")
				.add("Name")
				.add("Version")
				.add("Git")
				.add("Count modules")
				.add("Count files")
				.add("Count java files")
				.add("File size"));

		for(ProjectStats projectStats : projectStatsSet) {
			StringJoiner joiner = new StringJoiner("\t")
					.add(projectStats.getGroup())
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
