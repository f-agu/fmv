package org.fagu.fmv.mymedia.movie.list.column;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.fagu.fmv.mymedia.movie.list.column.AgesCache.AgesElement;


/**
 * @author Utilisateur
 * @created 4 mai 2018 14:39:24
 */
public class AgesFilm {

	private final Map<String, Optional<Ages>> notInCache = new TreeMap<>();

	private final AgesCache agesCache;

	public AgesFilm(AgesCache agesCache) {
		this.agesCache = Objects.requireNonNull(agesCache);
	}

	public Optional<Ages> getAges(String movieTitle) {
		return search(movieTitle);
	}

	public Map<String, Optional<Ages>> getNotInCache() {
		return Collections.unmodifiableMap(notInCache);
	}

	// **********************************************

	private Optional<Ages> search(String text) {
		Optional<AgesElement> element = agesCache.find(text);
		if(element.isPresent()) {
			return element.get().getAges();
		}

		System.out.println("SEARCH AGES ### not in cache for: " + text);

		Optional<Ages> ages = searchBy(text);
		if( ! ages.isPresent()) {
			ages = searchBy(stripAccents(text).replaceAll("[^A-Za-z ]", " "));
		}
		notInCache.put(text, ages);
		agesCache.put(text, ages.orElse(null));
		return ages;
	}

	private Optional<Ages> searchBy(String text) {
		Set<Ages> ages = new TreeSet<>();
		int page = 1;
		try {
			while(search(text, page, ages)) {
				++page;
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		if(ages.isEmpty()) {
			return Optional.empty();
		}
		if(ages.size() > 1) {
			System.out.println("SEARCH AGES ### multiple found: " + ages);
		}
		return Optional.of(ages.iterator().next()); // TODO re-filter
	}

	private boolean search(String text, int page, Collection<Ages> ages) throws IOException {
		String lcTxt = text.toLowerCase();
		StringBuilder sb = new StringBuilder();
		sb.append("http://www.filmages.ch/films/recherche/search/").append(URLEncoder.encode(lcTxt, "UTF-8")).append(".html");
		if(page > 1) {
			sb.append("?page=").append(page);
		}
		URL url = new URL(sb.toString());
		URLConnection connection = url.openConnection();
		boolean hasMorePage = false;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String line;
			boolean found = false;
			double score = 0;
			String title = null;
			Integer legal = null;
			Integer suggested = null;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(found) {
					if("</tr>".equals(line)) {
						if(legal != null || suggested != null) {
							ages.add(new Ages(title, legal, suggested, score));
						}
						found = false;
						title = null;
					} else if(line.startsWith("<td class=\"field age_legal\"")) {
						legal = parseAge(StringUtils.substringBetween(line, "<td class=\"field age_legal\">", " ans</td>"));
					} else if(line.startsWith("<td class=\"field age_suggested\"")) {
						suggested = parseAge(StringUtils.substringBetween(line, "<td class=\"field age_suggested\">", " ans</td>"));
					}
				} else {
					if(line.startsWith("<td class=\"field title_french\">")) {
						String name = StringUtils.substringBetween(line, "ment\">", "</a></td>");
						score = new JaroWinklerDistance().apply(name, text);
						if(score > 0.9D) {
							found = true;
							title = name;
							continue;
						}
					} else if(line.startsWith("<li><a href=\"films/recherche/search/to.html?page=")) {
						hasMorePage = true;
					}
				}
			}
		}
		return hasMorePage;
	}

	public static String stripAccents(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}

	private Integer parseAge(String text) {
		try {
			return Integer.parseInt(text);
		} catch(Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		AgesFilm agesFilm = new AgesFilm(AgesCache.getInstance());
		System.out.println(agesFilm.getAges("Rrrrrrr !!!"));
	}
}
