package org.fagu.fmv.soft.gs;

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
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.SoftName;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.info.VersionDateSoftInfo;
import org.fagu.fmv.soft.find.policy.VersionPolicy;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class GSSoftProvider extends SoftProvider {

	public static final String NAME = "gs";

	/**
	 * 
	 */
	public GSSoftProvider() {
		super(NAME);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory()
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory() {
		return ExecSoftFoundFactory.withParameters("-version", "-q") //
				.parseFactory(file -> createParser(getSoftName(), file)) //
				.build();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSearchFileFilter()
	 */
	@Override
	public FileFilter getSearchFileFilter() {
		if(SystemUtils.IS_OS_WINDOWS) {
			return f -> {
				String name = f.getName();
				String baseName = FilenameUtils.getBaseName(name);
				return ("gswin32c".equals(baseName) || "gswin64c".equals(baseName)) && "exe".equalsIgnoreCase(FilenameUtils.getExtension(name));
			};
		}
		return super.getSearchFileFilter();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getDownloadURL()
	 */
	@Override
	public String getDownloadURL() {
		return "http://ghostscript.com/download/";
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftPolicy()
	 */
	@Override
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		return new VersionPolicy() //
				.onAllPlatforms().minVersion(new Version(9, 15));
	}

	// ***********************************************************************

	/**
	 * @param softName
	 * @param file
	 * @return
	 */
	Parser createParser(SoftName softName, File file) {
		return new Parser() {

			private Pattern pattern = Pattern.compile("GPL Ghostscript ([0-9\\.\\-]+) \\(([0-9\\-]+)\\)");

			private Version version;

			private boolean firstPass;

			private Date date = null;

			@Override
			public void readLine(String line) {
				if(firstPass) {
					return;
				}
				firstPass = true;
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches()) {
					version = VersionParserManager.parse(matcher.group(1));
				}

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				try {
					date = dateFormat.parse(matcher.group(2));
				} catch(Exception e) {
					// ignore
				}
			}

			@Override
			public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
				SoftPolicy<?, ?, ?> softPolicy = getSoftPolicy();
				return softPolicy.toSoftFound(new VersionDateSoftInfo(file, softName, version, date));
			}

		};
	}
}
