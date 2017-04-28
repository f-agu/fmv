package org.fagu.fmv.soft.java;

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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.soft.SoftName;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.Locators;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.find.policy.VersionPolicy;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class JavaSoftProvider extends SoftProvider {

	public static final String NAME = "java";

	/**
	 * 
	 */
	public JavaSoftProvider() {
		super(NAME);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory()
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory() {
		return ExecSoftFoundFactory.withParameters("-version")
				.parseFactory((file, softPolicy) -> createParser(getSoftName(), file, softPolicy))
				.build();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftLocator()
	 */
	@Override
	public SoftLocator getSoftLocator() {
		return new SoftLocator() {

			/**
			 * @see org.fagu.fmv.soft.find.SoftLocator#getLocators(org.fagu.fmv.soft.SoftName,
			 *      org.fagu.fmv.soft.find.Locators)
			 */
			@Override
			protected List<Locator> getLocators(SoftName softName, Locators loc, FileFilter defaultFileFilter) {
				List<Locator> list = super.getLocators(softName, loc, defaultFileFilter);
				list.add(0, createLocators(softName, defaultFileFilter).byPropertyPath("java.home"));
				return list;
			}
		};
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getDownloadURL()
	 */
	@Override
	public String getDownloadURL() {
		return "https://www.java.com/download/";
	}

	/**
	 * @return
	 */
	@Override
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		return new VersionPolicy() //
				.onAllPlatforms().minVersion(new Version(1, 1));
	}

	// ***********************************************************************

	/**
	 * @param softName
	 * @param file
	 * @param softPolicy
	 * @return
	 */
	Parser createParser(SoftName softName, File file, SoftPolicy<?, ?, ?> softPolicy) {
		return new Parser() {

			private final Pattern pattern = Pattern.compile("(.*) version \"(.*)\"");

			private Version version;

			@Override
			public void readLine(String line) {
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches()) {
					version = VersionParserManager.parse(matcher.group(2));
				}
			}

			@Override
			public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
				return getSoftPolicy().toSoftFound(new VersionSoftInfo(file, softName, version));
			}

		};
	}
}
