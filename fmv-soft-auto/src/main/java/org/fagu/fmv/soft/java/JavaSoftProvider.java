package org.fagu.fmv.soft.java;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ExecSoftFoundFactoryBuilder;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.VersionDate;
import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.Locators;
import org.fagu.fmv.soft.find.SearchBehavior;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.utils.SearchPropertiesHelper;
import org.fagu.fmv.soft.utils.SearchPropertiesHelper.SearchMatching;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class JavaSoftProvider extends SoftProvider {

	private static final String DEFAULT_PATTERN_VERSION = "(.*) version \"(.*)\"(.*)";

	private static final String DEFAULT_PATTERN_DATE = ".*([0-9]{4}\\-[0-9]{1,2}\\-[0-9]{1,2}).*";

	public static final String NAME = "java";

	public JavaSoftProvider() {
		this(null);
	}

	public JavaSoftProvider(SoftPolicy softPolicy) {
		super(NAME, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(1, 8))));
	}

	JavaSoftProvider(String name, SoftPolicy softPolicy) {
		super(name, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(1, 8))));
	}

	@Override
	public Optional<String> getGroupTitle() {
		return Optional.of("Java");
	}

	@Override
	public SearchBehavior getSearchBehavior() {
		return SearchBehavior.versionAndDate();
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties, Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) {
		SearchMatching searchMatching = new SearchPropertiesHelper(searchProperties, this)
				.forMatchingVersion(DEFAULT_PATTERN_VERSION);
		return prepareBuilder(
				prepareSoftFoundFactory().withParameters("-version"),
				builderConsumer)
						.parseVersionDate(line -> searchMatching.ifMatches(line, matcher -> {
							Version version = VersionParserManager.parse(matcher.group(2));
							Date date = null;
							if(matcher.groupCount() > 2) {
								date = parseDate(searchProperties, matcher.group(3));
							}
							return Optional.of(new VersionDate(version, date));
						})
								.orElse(null))
						.build();
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = new SoftLocator(getName()) {

			@Override
			protected List<Locator> getLocators(Locators loc) {
				List<Locator> list = super.getLocators(loc);
				Locators locators = createLocators();
				list.add(0, locators.byPropertyPath("java.home"));
				if(SystemUtils.IS_OS_WINDOWS) {
					ProgramFilesLocatorSupplier.with(list::add, locators)
							.find(programFile -> {
								List<File> files = new ArrayList<>();
								File[] javaFolders = programFile.listFiles(f -> "java".equalsIgnoreCase(f.getName()));
								if(javaFolders != null) {
									for(File folder : javaFolders) {
										File[] subFolders = folder.listFiles();
										if(subFolders != null) {
											for(File subFolder : subFolders) {
												files.add(subFolder);
												files.add(new File(subFolder, "bin"));
											}
										}
									}
								}
								return files;
							})
							.supplyIn();
				}
				return list;
			}
		};
		softLocator.setSoftPolicy(getSoftPolicy());
		softLocator.setEnvName("JAVA_HOME");
		return softLocator;
	}

	@Override
	public String getDownloadURL() {
		return "https://www.java.com/download/";
	}

	// *****************************************************

	private Date parseDate(Properties searchProperties, String remainLine) {
		return new SearchPropertiesHelper(searchProperties, this)
				.forMatchingDate(DEFAULT_PATTERN_DATE)
				.ifMatches(remainLine, matcher -> {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					try {
						return Optional.ofNullable(dateFormat.parse(matcher.group(1)));
					} catch(Exception e) {
						// ignore
					}
					return Optional.empty();
				})
				.orElse(null);
	}

}
