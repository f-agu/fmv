package org.fagu.fmv.mymedia.sources;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Oodrive
 * @author f.agu
 * @created 27 juil. 2022 12:09:58
 */
class GitFileTools {

	GitFileTools() {}

	static Optional<String> findURL(Path path) {
		Path configPath = path.resolve(".git").resolve("config");
		if( ! Files.exists(configPath)) {
			return Optional.empty();
		}
		try (Stream<String> lines = Files.lines(configPath)) {
			return lines
					.filter(l -> l.trim().startsWith("url = "))
					.map(l -> StringUtils.substringAfter(l, "url = "))
					.map(u -> {
						Pattern pattern = Pattern.compile("(https://)(?:[\\w]+@)(.*)");
						Matcher matcher = pattern.matcher(u);
						if(matcher.matches()) {
							return matcher.group(1) + matcher.group(2);
						}
						return u;
					})
					.findFirst();
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
