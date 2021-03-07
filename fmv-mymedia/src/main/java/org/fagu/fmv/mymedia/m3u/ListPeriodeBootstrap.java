package org.fagu.fmv.mymedia.m3u;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FilenameUtils;


/**
 * @author f.agu
 * @created 27 août 2020 10:34:40
 */
public class ListPeriodeBootstrap {

	public static void main(String... args) {
		if(args.length != 1) {
			System.out.println("Usage: java " + ListPeriodeBootstrap.class + " <period-folder>");
			return;
		}
		File periodFolder = new File(args[0]);

		File[] listFolders = periodFolder.listFiles(f -> f.isDirectory());
		if(listFolders == null) {
			System.out.println("Folders not found in " + periodFolder);
			return;
		}
		for(File folder : listFolders) {
			System.out.println(folder);
			findAndWriteZipFiles(folder, periodFolder);
			findAndWriteMP3Files(folder, periodFolder, null);
		}
		findAndWriteZipFiles(periodFolder, periodFolder);
	}

	private static void findAndWriteMP3Files(File folder, File outputFolder, String prefix) {
		File[] mp3Files = folder.listFiles(f -> f.isFile() && "mp3".equalsIgnoreCase(FilenameUtils.getExtension(f.getName())));
		String baseName = prefix != null ? prefix : folder.getName();
		if(mp3Files != null) {
			String fileName = baseName + ".txt";
			try (PrintStream printStream = new PrintStream(new File(outputFolder, fileName))) {
				if(mp3Files != null && mp3Files.length > 0) {
					Arrays.stream(mp3Files).forEach(f -> printStream.println(f.getName()));
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		File[] directories = folder.listFiles(f -> f.isDirectory());
		if(directories != null) {
			for(File directory : directories) {
				findAndWriteMP3Files(directory, outputFolder, baseName + "-" + directory.getName());
			}
		}
	}

	private static void findAndWriteZipFiles(File folder, File outputFolder) {
		File[] zipFiles = folder.listFiles(f -> f.isFile() && "zip".equalsIgnoreCase(FilenameUtils.getExtension(f.getName())));
		if(zipFiles != null) {
			for(File zipFile : zipFiles) {
				try {
					List<String> list = extractZipEntryNamesApache(zipFile);
					if( ! list.isEmpty()) {
						try (PrintStream printStream = new PrintStream(
								new File(outputFolder, FilenameUtils.getBaseName(zipFile.getName()) + ".txt"))) {
							list.forEach(printStream::println);
						} catch(IOException e) {
							e.printStackTrace();
						}
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static List<String> extractZipEntryNamesApache(File zipFile) throws IOException {
		System.out.println(zipFile.getName() + "...");
		List<String> list = new ArrayList<>();
		try (ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(
				new FileInputStream(zipFile),
				"UTF8",
				true,
				true)) {
			ZipArchiveEntry zipArchiveEntry = null;
			while((zipArchiveEntry = zipArchiveInputStream.getNextZipEntry()) != null) {
				list.add(FilenameUtils.getName(zipArchiveEntry.getName()));
			}
		}
		return list;
	}

}
