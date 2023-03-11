package org.fagu.fmv.mymedia.ebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;


/**
 * @author Utilisateur
 * @created 12 janv. 2023 09:02:09
 */
public class ExistsIn {

	private static final Collection<String> FRENCH_STOP_WORDS = createStopWords(Arrays.asList(
			"au", "aux", "avec", "ce", "ces",
			"dans", "de", "des", "du", "elle", "en", "et", "eux", "il", "je",
			"la", "le", "leur", "lui", "ma", "mais", "me", "même", "mes",
			"moi", "mon", "ne", "nos", "notre", "nous", "on", "ou", "par",
			"pas", "pour", "que", "qui", "sa", "se", "ses", "son", "sur", "ta",
			"te", "tes", "toi", "ton", "tu", "un", "une", "vos", "votre",
			"vous", "à", "y", "été", "étée", "étées", "étés", "étant", "suis",
			"es", "est", "sommes", "êtes", "sont", "serai", "seras", "sera",
			"serons", "serez", "seront", "serais", "serait", "serions",
			"seriez", "seraient", "étais", "était", "étions", "étiez",
			"étaient", "fus", "fut", "fûmes", "fûtes", "furent", "sois",
			"soit", "soyons", "soyez", "soient", "fusse", "fusses", "fût",
			"fussions", "fussiez", "fussent", "ayant", "eu", "eue", "eues",
			"eus", "ai", "as", "avons", "avez", "ont", "aurai", "auras",
			"aura", "aurons", "aurez", "auront", "aurais", "aurait", "aurions",
			"auriez", "auraient", "avais", "avait", "avions", "aviez",
			"avaient", "eut", "eûmes", "eûtes", "eurent", "aie", "aies", "ait",
			"ayons", "ayez", "aient", "eusse", "eusses", "eût", "eussions",
			"eussiez", "eussent", "ceci", "cela", "celà", "cet", "cette",
			"ici", "ils", "les", "leurs", "quel", "quels", "quelle", "quelles",
			"sans", "soi", "qu", "c", "d", "j", "l", "m", "n", "s", "t"));

	private ExistsIn() {}

	static boolean exists(Path parent, String title) throws IOException {
		List<String> words = removeStopWords(removeDiacriticals(title));
		NavigableMap<Integer, List<Path>> near = new TreeMap<>(Collections.reverseOrder());
		Files.walk(parent)
				.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(".epub"))
				.forEach(p -> {
					List<String> names = removeStopWords(removeDiacriticals(FilenameUtils.getBaseName(p.getFileName().toString())));
					int count = (int)names.stream().filter(words::contains).count();
					if(count > 0) {
						near.computeIfAbsent(count, k -> new ArrayList<>()).add(p);
					}
				});
		if(near.size() > 0) {
			return true;
		}
		// near.forEach((k, v) -> System.out.println(k + " " + words + " : " + v));
		return false;
	}

	// ***************************************************

	private static String removeDiacriticals(String s) {
		return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
	}

	private static List<String> removeStopWords(String s) {
		List<String> allWords = Stream.of(s.toLowerCase().split(" "))
				.collect(Collectors.toList());
		allWords.removeAll(FRENCH_STOP_WORDS);
		return allWords;
	}

	private static Set<String> createStopWords(List<String> baseList) {
		Set<String> set = new HashSet<>(baseList.size() * 2);
		set.addAll(baseList);
		baseList.stream()
				.map(ExistsIn::removeDiacriticals)
				.forEach(set::add);
		return set;
	}

}
