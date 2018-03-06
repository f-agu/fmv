package org.fagu.fmv.ffmpeg.soft;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.ffmpeg.exception.FFExceptionKnownAnalyzer;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.version.Version;
import org.fagu.version.VersionParseException;
import org.fagu.version.VersionParserManager;
import org.fagu.version.VersionUnit;


/**
 * @author f.agu
 */
public abstract class FFSoftProvider extends SoftProvider {

	private static final Pattern LIBVERSION_PATTERN = Pattern.compile("([\\w]+)\\ +\\ ([0-9]+\\.\\ *[0-9]+\\.[0-9]+).*");

	private static final Pattern NVERSION_PATTERN = Pattern.compile("[N|n]-?(.+)-[a-zA-Z0-9]+");

	private static final Pattern BUILD_PATTERN = Pattern.compile(".*built on (.*) with gcc.*");

	private static final Pattern FF_FIRSTLINE_PATTERN = Pattern.compile("(ff[a-z]+) version (.*)");

	/**
	 * @param name
	 */
	public FFSoftProvider(String name) {
		super(name, null);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory()
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory() {
		return prepareSoftFoundFactory()
				.withParameters("-version")
				.parseFactory((file, softPolicy) -> createParser(file))
				.build();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftLocator()
	 */
	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		softLocator.enableCacheInSameFolderOfGroup(getGroupName());
		if(SystemUtils.IS_OS_WINDOWS) {
			softLocator.addDefaultLocator();
			ProgramFilesLocatorSupplier.with(softLocator)
					.find(programFile -> {
						List<File> files = new ArrayList<>();
						File[] folders = programFile.listFiles(f -> f.getName().toLowerCase().startsWith("ffmpeg"));
						if(folders != null) {
							for(File folder : folders) {
								files.add(folder);
								files.add(new File(folder, "bin"));
							}
						}
						return files;
					})
					.supplyIn();
		}
		return softLocator;
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getDownloadURL()
	 */
	@Override
	public String getDownloadURL() {
		return "http://ffmpeg.org/download.html";
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getMinVersion()
	 */
	@Override
	public String getMinVersion() {
		StringBuilder buf = new StringBuilder();
		buf.append('v').append(FFSoftPolicy.MIN_VERSION).append(" or built ").append(FFSoftPolicy.MIN_BUILD_VERSION);
		return buf.toString();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getExceptionKnownAnalyzerClass()
	 */
	@Override
	public Class<? extends ExceptionKnownAnalyzer> getExceptionKnownAnalyzerClass() {
		return FFExceptionKnownAnalyzer.class;
	}

	// ***********************************************************************

	/**
	 * @param file
	 * @return
	 */
	Parser createParser(File file) {
		return new Parser() {

			private static final String CONFIGURATION_START_PATTERN = "configuration:";

			private Version version = null;

			private Date builtDate = null;

			private Set<String> configuration = null; // configuration

			private Map<String, Version> libVersions = new HashMap<>(); // lib versions

			private SoftFound softFound;

			@Override
			public void readLine(final String lineIn) {
				if(StringUtils.isBlank(lineIn) || softFound != null) {
					return;
				}
				String line = lineIn.trim();

				// version
				Matcher matcher = FF_FIRSTLINE_PATTERN.matcher(line);
				if(matcher.matches()) {
					String parsedTool = matcher.group(1).trim();
					if(parsedTool.equalsIgnoreCase(getName())) {
						version = getVersion(matcher.group(2).trim());
						return;
					}
					softFound = SoftFound.foundError(file, "Wrong tool, need " + getName() + " and found " + parsedTool + ": " + line);
				}

				// configuration
				if(line.startsWith(CONFIGURATION_START_PATTERN)) {
					configuration = getConfiguration(StringUtils.substringAfter(line, CONFIGURATION_START_PATTERN).trim());
					return;
				}

				// built
				matcher = BUILD_PATTERN.matcher(line);
				if(matcher.matches()) {
					builtDate = getBuiltDate(matcher);
					return;
				}

				// lib versions
				matcher = LIBVERSION_PATTERN.matcher(line);
				if(matcher.matches()) {
					addLibVersions(libVersions, matcher);
				}
			}

			@Override
			public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
				if(softFound != null) {
					return softFound;
				}

				Integer builtVersion = null;
				if(version != null && version.size() == 1) {
					final int minBuildVersion = 100;
					int major = version.getFieldValue(VersionUnit.VF_0_MAJOR, 0);
					// N-70767-gd24af70
					if(major > minBuildVersion) {
						builtVersion = major;
						version = null;
					}
				}

				FFInfo ffInfo = new FFInfo(file, version, getName(), builtDate, builtVersion, configuration, libVersions);
				return FFSoftPolicy.toSoftFound(ffInfo);
			}
		};

	}

	/**
	 * @param matcher
	 * @return
	 */
	static Date getBuiltDate(Matcher matcher) {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
		String strDate = matcher.group(1);
		try {
			return simpleDateFormat.parse(strDate);
		} catch(ParseException pe) {
			// ignore
		}
		return null;
	}

	/**
	 * @param line
	 * @return
	 */
	static Version getVersion(String line) {
		String sver = StringUtils.substringBefore(line, " ");
		Matcher matcher = NVERSION_PATTERN.matcher(sver);
		if(matcher.matches()) {
			sver = matcher.group(1);
		}
		try {
			return VersionParserManager.parse(sver);
		} catch(VersionParseException e) {
			// ignore
		}
		return null;
	}

	/**
	 * @param line
	 * @return
	 */
	static Set<String> getConfiguration(String line) {
		Set<String> set = new LinkedHashSet<>(32);

		boolean inMark = false;
		StringBuilder currentBuf = new StringBuilder();

		for(char c : line.toCharArray()) {
			switch(c) {
				case '\'':
					inMark = ! inMark;
					break;

				case ' ':
					if( ! inMark) {
						String s = currentBuf.toString();
						if(StringUtils.isNotBlank(s)) {
							set.add(s);
						}
						currentBuf = new StringBuilder();
						break;
					}

				default:
					currentBuf.append(c);
			}
		}

		return set;
	}

	/**
	 * @param map
	 * @param matcher
	 */
	static void addLibVersions(Map<String, Version> map, Matcher matcher) {
		String sver = matcher.group(2).replaceAll(" ", "");
		try {
			Version version = VersionParserManager.parse(sver);
			map.put(matcher.group(1), version);
		} catch(VersionParseException e) {
			// ignore
		}
	}
}
