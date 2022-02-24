package org.fagu.fmv.mymedia.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;


public class RenameSouthParkBootstrap {

	public static void main(String... args) throws Exception {
		Map<Integer, Map<Integer, Episode>> episodeMap = loadEpisodeInfos();
		Path root = Paths.get(args[0]);
		browse(root, episodeMap);
	}

	private static void browse(Path path, Map<Integer, Map<Integer, Episode>> episodeMap) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

			final Pattern pattern = Pattern.compile(".*S(\\d+)E(\\d+).*");

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String name = file.getFileName().toString();
				Matcher matcher = pattern.matcher(name);
				if(matcher.matches()) {
					int season = Integer.parseInt(matcher.group(1));
					int ep = Integer.parseInt(matcher.group(2));
					Map<Integer, Episode> seasonMap = episodeMap.get(season);
					if(seasonMap == null) {
						System.out.println("season not found: " + season);
						return FileVisitResult.TERMINATE;
					}
					Episode episode = seasonMap.get(ep);
					if(episode == null) {
						System.out.println("episode " + ep + " not found in season " + season);
						return FileVisitResult.TERMINATE;
					} ;

					String newName = new StringBuilder()
							.append('S').append(StringUtils.leftPad(Integer.toString(episode.season), 2, '0'))
							.append('E').append(StringUtils.leftPad(Integer.toString(episode.numInSeason), 2, '0'))
							.append(" - ").append(episode.title)
							.append('.').append(FilenameUtils.getExtension(name))
							.toString();
					if( ! name.equals(newName)) {
						System.out.println(file + " => " + newName);
						Files.move(file, file.getParent().resolve(newName));
					}
				} else {
					System.out.println("FAILED: " + file);
				}
				return FileVisitResult.CONTINUE;
			}

		});
	}

	private static Map<Integer, Map<Integer, Episode>> loadEpisodeInfos() throws Exception {
		Map<Integer, Map<Integer, Episode>> map = new HashMap<>();

		URL url = new URL("https://fr.wikipedia.org/wiki/Liste_des_%C3%A9pisodes_de_South_Park");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8))) {
			int lineNum = 0;
			String line = null;
			int saison = 0;
			while((line = reader.readLine()) != null) {
				++lineNum;
				String seasonNum = StringUtils.substringBetween(line, "<span class=\"mw-headline\" id=\"Saison_", "_");
				if(seasonNum != null) {
					saison = Integer.parseInt(seasonNum);
					continue;
				}
				String epNum = StringUtils.substringBetween(line, "<tr style=\"text-align:center;\"><td id=\"ep", "\"");
				if(epNum != null) {
					try {
						int numGlobal = Integer.parseInt(epNum);
						int numInSeason = Integer.parseInt(StringUtils.substringBetween(line, "</td><td>", "</td>"));
						String title = StringUtils.substringBetween(line, "title=\"", "\"");
						title = StringUtils.substringBetween(line, title + "\">", "</a>");
						if(title != null) {
							title = title
									.replace("&#160;", " ")
									.replace(" :", ",")
									.replace(":", ",")
									.replace(" ? ", ". ")
									.replace(" ?", "")
									.replace("?", "")
									.replace("  ", " ");
						} else {
							title = StringUtils.EMPTY;
						}

						LocalDate date = Optional.ofNullable(StringUtils.substringBetween(line, "datetime=\"", "\""))
								.map(LocalDate::parse)
								.orElse(null);
						map.computeIfAbsent(saison, k -> new HashMap<>())
								.put(numInSeason, new Episode(saison, numGlobal, numInSeason, title, date));
						// System.out.println(saison + " " + numGlobal + " " + numInSeason + " " + title + " " +
						// date);
					} catch(Exception e) {
						System.err.println(lineNum + " : " + line);
						throw e;
					}
				}

			}
		}
		return map;
	}

	// ---------------------------------------

	private static class Episode {

		private final int season;

		private final int numGlobal;

		private final int numInSeason;

		private final String title;

		private final LocalDate date;

		private Episode(int season, int numGlobal, int numInSeason, String title, LocalDate date) {
			this.season = season;
			this.numGlobal = numGlobal;
			this.numInSeason = numInSeason;
			this.title = title;
			this.date = date;
		}
	}
}
