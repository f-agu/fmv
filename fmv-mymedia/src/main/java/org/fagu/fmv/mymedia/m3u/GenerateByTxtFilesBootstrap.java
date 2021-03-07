package org.fagu.fmv.mymedia.m3u;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 * @created 17 janv. 2021 11:04:06
 */
public class GenerateByTxtFilesBootstrap {

	public static void main(String... args) throws Exception {
		if(args.length < 2) {
			System.out.println("Usage: java " + GenerateByTxtFilesBootstrap.class.getName()
					+ " <source-folder> <txt-file-or-folder> [txt-file-or-folder2] [txt-file-or-folder3]...");
			return;
		}
		Iterator<Path> pathIterator = Arrays.stream(args)
				.map(Paths::get)
				.iterator();
		Path rootPath = pathIterator.next();
		NavigableMap<String, Path> foundFiles = searchFiles(rootPath);
		while(pathIterator.hasNext()) {
			doAnything(rootPath, pathIterator.next(), foundFiles);
		}
	}

	private static NavigableMap<String, Path> searchFiles(Path folder) throws IOException {
		SearchVisitor searchVisitor = new SearchVisitor();
		Files.walkFileTree(folder, searchVisitor);
		return searchVisitor.foundFiles;
	}

	private static void doAnything(Path rootPath, Path path, NavigableMap<String, Path> foundFiles) throws IOException {
		if(Files.isRegularFile(path)) {
			doTxtFile(rootPath, path, foundFiles);
		} else {
			try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
					path,
					p -> p.getFileName().toString().toLowerCase().endsWith(".txt"))) {
				for(Path sp : directoryStream) {
					doTxtFile(rootPath, sp, foundFiles);
				}
			}
		}
	}

	private static void doTxtFile(Path rootPath, Path file, NavigableMap<String, Path> foundFiles) throws IOException {
		int baseLength = rootPath.getParent().toString().length() + 1;
		File m3uFile = new File(file.toFile().getParentFile(), "Michel Sardou - " + file.getFileName().toString().replace(".txt", ".m3u8"));
		System.out.println("======= " + m3uFile);
		try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()));
				M3U8Writer m3u8Writer = new M3U8Writer(new FileOutputStream(m3uFile))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(line.isEmpty()) {
					continue;
				}
				String origTitle = line.split("\t")[1];
				String title = simplifyName(origTitle);
				Path path = foundFiles.get(title);
				if(path == null) {
					System.out.println(file.getFileName().toString() + " / " + title + "    " + (path != null ? path : "?"));
					System.out.println("   " + foundFiles.lowerEntry(title));
					System.out.println("   " + foundFiles.higherEntry(title));
				} else {
					m3u8Writer.addTitle(origTitle, path.toString().substring(baseLength));
				}
			}
		}
	}

	private static String simplifyName(final String name) {
		String s = name.toLowerCase();
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		s = s
				.replace("-", "")
				.replace("'", "")
				.replace("’", "")
				.replace(".", "")
				.replace(" ", "")
				.replace("?", "")
				.replace(",", "");

		return s;
	}

	// ------------------------------------------------------

	private static class SearchVisitor extends SimpleFileVisitor<Path> {

		// private final Path rootPath;

		private final NavigableMap<String, Path> foundFiles;

		private SearchVisitor() {
			// this.rootPath = Objects.requireNonNull(rootPath);
			foundFiles = new TreeMap<>();
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			String name = dir.getFileName().toString().toLowerCase();
			if("inedits".equals(name) || "karaoke".equals(name)) {
				return FileVisitResult.SKIP_SUBTREE;
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			String name = file.getFileName().toString().toLowerCase();
			if(name.endsWith(".mp3")) {
				name = StringUtils.substringBeforeLast(name, ".mp3");
				name = name.contains("- ") ? StringUtils.substringAfter(name, "- ") : name;
				String simplifyName = simplifyName(name);
				Path previous = foundFiles.put(simplifyName, file);
				if(previous != null) {
					throw new RuntimeException("Already defined: " + simplifyName + " => " + previous + "  &  " + file);
				}
			}
			return FileVisitResult.CONTINUE;
		}
	}
}
