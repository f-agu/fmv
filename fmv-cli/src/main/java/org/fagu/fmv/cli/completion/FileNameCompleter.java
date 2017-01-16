package org.fagu.fmv.cli.completion;

/*
 * #%L
 * fmv-cli
 * %%
 * Copyright (C) 2014 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static jline.internal.Preconditions.checkNotNull;

import java.io.File;
import java.util.List;

import jline.console.completer.Completer;
import jline.internal.Configuration;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class FileNameCompleter implements Completer {

	// TODO: Handle files with spaces in them

	private static final boolean OS_IS_WINDOWS;

	static {
		String os = Configuration.getOsName();
		OS_IS_WINDOWS = os.contains("windows");
	}

	private final boolean caseSensitive;

	/**
	 *
	 */
	public FileNameCompleter() {
		caseSensitive = ! OS_IS_WINDOWS;
	}

	/**
	 * @see jline.console.completer.Completer#complete(java.lang.String, int, java.util.List)
	 */
	@Override
	public int complete(String buffer, final int cursor, final List<CharSequence> candidates) {
		// System.out.println("complete(" + buffer + ", " + cursor + ", " + candidates + ")");

		// buffer can be null
		checkNotNull(candidates);

		if(buffer == null) {
			buffer = "";
		}

		if(OS_IS_WINDOWS) {
			buffer = buffer.replace('/', '\\');
		}

		String translated = buffer;

		// System.out.println("translated(1): " + translated);

		File homeDir = getUserHome();

		// Special character: ~ maps to the user's home directory
		if(translated.startsWith("~" + separator())) {
			translated = homeDir.getPath() + translated.substring(1);
		} else if(translated.startsWith("~")) {
			translated = homeDir.getParentFile().getAbsolutePath();
		} else if( ! (new File(translated).isAbsolute())) {
			String cwd = getUserDir().getAbsolutePath();
			if(cwd.endsWith(separator())) {
				translated = cwd + translated;
			} else {
				translated = cwd + separator() + translated;
			}
		}

		File file = new File(translated);
		final File dir;

		if(translated.endsWith(separator())) {
			dir = file;
		} else {
			dir = file.getParentFile();
		}

		File[] entries = dir == null ? new File[0] : dir.listFiles();

		return matchFiles(buffer, translated, entries, candidates);
	}

	// ******************************************************

	/**
	 * @return
	 */
	protected String separator() {
		return File.separator;
	}

	/**
	 * @return
	 */
	protected File getUserHome() {
		return Configuration.getUserHome();
	}

	/**
	 * @return
	 */
	protected File getUserDir() {
		return new File(".");
		// return new File("c:\\");
	}

	/**
	 * @param buffer
	 * @param translated
	 * @param root
	 * @param files
	 * @param candidates
	 * @return
	 */
	protected int matchFiles(String buffer, String translated, File[] files, List<CharSequence> candidates) {
		// System.out.println("mathches(" + buffer + ", " + translated + ", " + (files != null ? files.length : "null")
		// + ",...)");
		if(files == null) {
			return - 1;
		}

		int matches = 0;

		// first pass: just count the matches
		for(File file : files) {
			if(startsWith(file, translated)) {
				++matches;
				// System.out.println("  startsWith " + file);
			} else {
				// System.out.println("  not startsWith " + file);
			}
		}
		int index = buffer.lastIndexOf(separator()) + separator().length();
		File lastFile = null;
		for(File file : files) {
			if(startsWith(file, translated)) {
				lastFile = file;
				CharSequence name = null;
				if(matches == 1 && file.isDirectory()) {
					name = file.getName() + separator();
				} else {
					name = file.getName() + " ";
				}
				candidates.add(render(file, name).toString());
			}
		}
		if(candidates.size() == 1) {
			if(StringUtils.containsWhitespace(lastFile.getName()) && ! buffer.startsWith("\"")) {
				index = 0;
				String candidate = translated + (String)candidates.get(0);
				candidates.clear();
				candidates.add(candidate.replace(" ", "\\ "));
			}
		}

		return index;
	}

	/**
	 * @param file
	 * @param name
	 * @return
	 */
	protected CharSequence render(final File file, final CharSequence name) {
		return name;
	}

	/**
	 * @param file
	 * @param find
	 * @return
	 */
	protected boolean startsWith(File file, String find) {
		if(caseSensitive) {
			return file.getAbsolutePath().startsWith(find);
		}
		return file.getAbsolutePath().toLowerCase().startsWith(find.toLowerCase());
	}
}
