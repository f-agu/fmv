package org.fagu.fmv.mymedia.sources;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author f.agu
 * @created 1 janv. 2022 16:03:26
 */
public class GitIgnoreFilter implements DirectoryStream.Filter<Path> {

	private final List<DirectoryStream.Filter<Path>> filters;

	private GitIgnoreFilter(List<DirectoryStream.Filter<Path>> filters) {
		this.filters = filters;
	}

	public static DirectoryStream.Filter<Path> open(Path path) throws IOException {
		Path p = path;
		if(Files.isDirectory(path)) {
			p = p.resolve(".gitignore");
		}

		if( ! Files.exists(p)) {
			return new GitIgnoreFilter(Collections.singletonList(gitFilter()));
		}

		List<Filter<Path>> filters = Files.lines(p)
				.map(String::trim)
				.filter(s -> ! s.startsWith("#") && ! s.isEmpty())
				.map(s -> {
					if(s.endsWith("/")) { // folder
						if(s.startsWith("*")) {
							return new EndsWithFolderFilter(s.substring(1, s.length() - 1));
						}
						return new EqualsFolderFilter(s.substring(0, s.length() - 1));
					} else { // file
						if(s.startsWith("*")) {
							return new EndsWithFileFilter(s.substring(1));
						}
						return new EqualsFileFilter(s);
					}
				})
				.collect(Collectors.toList());
		filters.add(gitFilter());
		return new GitIgnoreFilter(Collections.unmodifiableList(filters));
	}

	@Override
	public boolean accept(Path entry) throws IOException {
		for(DirectoryStream.Filter<Path> filter : filters) {
			if(filter.accept(entry)) {
				return false;
			}
		}
		return true;
	}

	// **************************************************************

	private static DirectoryStream.Filter<Path> gitFilter() {
		return new DirectoryStream.Filter<Path>() {

			@Override
			public boolean accept(Path p) throws IOException {
				String name = p.getFileName().toString();
				return Files.isDirectory(p) && ".git".equals(name)
						|| Files.isRegularFile(p)
								&& (".gitignore".equals(name)
										|| ".gitattributes".equals(name));
			}

			@Override
			public String toString() {
				return ".git element";
			}
		};
	}

	// -------------------------------------------------------------

	private static class EqualsFilter implements DirectoryStream.Filter<Path> {

		private final String name;

		private EqualsFilter(String name) {
			this.name = Objects.requireNonNull(name);
		}

		@Override
		public boolean accept(Path entry) throws IOException {
			return name.equals(entry.getFileName().toString());
		}

		@Override
		public String toString() {
			return name;
		}

	}

	// -------------------------------------------------------------

	private static class EndsWithFilter implements DirectoryStream.Filter<Path> {

		private final String endsWith;

		private EndsWithFilter(String endsWith) {
			this.endsWith = Objects.requireNonNull(endsWith);
		}

		@Override
		public boolean accept(Path entry) throws IOException {
			return entry.getFileName().toString().endsWith(endsWith);
		}

		@Override
		public String toString() {
			return "*." + endsWith;
		}

	}

	// -------------------------------------------------------------

	private static class EqualsFileFilter extends EqualsFilter {

		private EqualsFileFilter(String name) {
			super(name);
		}

		@Override
		public boolean accept(Path entry) throws IOException {
			return Files.isRegularFile(entry) && super.accept(entry);
		}

		@Override
		public String toString() {
			return "Is file && " + super.toString();
		}

	}

	// -------------------------------------------------------------

	private static class EqualsFolderFilter extends EqualsFilter {

		private EqualsFolderFilter(String name) {
			super(name);
		}

		@Override
		public boolean accept(Path entry) throws IOException {
			return Files.isDirectory(entry) && super.accept(entry);
		}

		@Override
		public String toString() {
			return "Is folder && " + super.toString();
		}

	}
	// -------------------------------------------------------------

	private static class EndsWithFileFilter extends EndsWithFilter {

		private EndsWithFileFilter(String name) {
			super(name);
		}

		@Override
		public boolean accept(Path entry) throws IOException {
			return Files.isRegularFile(entry) && super.accept(entry);
		}

		@Override
		public String toString() {
			return "Is file && " + super.toString();
		}
	}

	// -------------------------------------------------------------

	private static class EndsWithFolderFilter extends EndsWithFilter {

		private EndsWithFolderFilter(String name) {
			super(name);
		}

		@Override
		public boolean accept(Path entry) throws IOException {
			return Files.isDirectory(entry) && super.accept(entry);
		}

		@Override
		public String toString() {
			return "Is folder && " + super.toString();
		}
	}

}
