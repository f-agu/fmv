package org.fagu.fmv.mymedia.m3u;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * @author fagu
 * @created 7 mars 2021 15:59:34
 */
public class SearchDoubleInDiversAlphabetOrderBootstrap {

	public static void main(String... args) {

		Map<String, File> albumAuthors = new HashMap<>();
		File albumFolder = new File(args[0]);
		File[] albumFolders = albumFolder.listFiles(f -> f.isDirectory());
		if(albumFolders != null) {
			for(File folder : albumFolders) {
				listAuthorsAlbum(folder, albumAuthors);
			}
		}

		File diversFolder = new File(args[1]);
		File[] diversFolders = diversFolder.listFiles(f -> f.isDirectory());
		if(diversFolders != null) {
			for(File folder : diversFolders) {
				diversSearchIn(folder, albumAuthors);
			}
		}
	}

	private static void listAuthorsAlbum(File folder, Map<String, File> albumAuthors) {
		File[] folders = folder.listFiles(f -> f.isDirectory());
		if(folders != null) {
			for(File f : folders) {
				String baseName = FilenameUtils.getBaseName(f.getName());
				String author = StringUtils.substringBefore(baseName, " - ").trim();
				File prev = albumAuthors.put(author.toLowerCase(), f);
				if(prev != null) {
					System.out.println("Double with " + prev + " and " + f);
				}
			}
		}
	}

	private static void diversSearchIn(File folder, Map<String, File> albumAuthors) {
		Map<String, AtomicInteger> authorCountMap = new TreeMap<>();
		File[] mp3Files = folder.listFiles(f -> f.isFile() && "mp3".equalsIgnoreCase(FilenameUtils.getExtension(f.getName())));
		if(mp3Files != null) {
			for(File mp3File : mp3Files) {
				String baseName = FilenameUtils.getBaseName(mp3File.getName());
				String author = StringUtils.substringBefore(baseName, " - ").trim();
				authorCountMap.computeIfAbsent(author.toLowerCase(), k -> new AtomicInteger())
						.incrementAndGet();
			}

			authorCountMap.entrySet().stream()
					.filter(e -> e.getValue().get() > 1)
					.forEach(e -> System.out.println(e.getKey() + " : " + e.getValue()));
		}

		File[] folders = folder.listFiles(f -> f.isDirectory());
		if(folders != null) {
			Arrays.stream(folders)
					.map(File::getName)
					.map(String::toLowerCase)
					.forEach(author -> authorCountMap.computeIfAbsent(author.toLowerCase(), k -> new AtomicInteger())
							.incrementAndGet());
		}

		authorCountMap.keySet().stream()
				.forEach(s -> {
					File f = albumAuthors.get(s);
					if(f != null) {
						System.out.println("Should move \"" + s + "\" to " + f);
					}
				});
	}

}
